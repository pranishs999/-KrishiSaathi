"""Safe, non-destructive PostgreSQL/Supabase connectivity diagnostics."""

from __future__ import annotations

import errno
import ipaddress
import re
import socket
from dataclasses import dataclass, field
from typing import Any

import psycopg2
from django.conf import settings
from django.db import DatabaseError, connection
from django.db.migrations.loader import MigrationLoader
from django.db.migrations.recorder import MigrationRecorder


@dataclass
class DiagnosticStep:
    name: str
    ok: bool
    category: str
    detail: str
    hint: str = ""


@dataclass
class DiagnosticReport:
    steps: list[DiagnosticStep] = field(default_factory=list)

    @property
    def passed(self) -> bool:
        return all(step.ok for step in self.steps)


def sanitize_error_text(text: str, password: str = "") -> str:
    """Strips passwords or embedded URI credentials before logging or displaying."""
    cleaned = text
    if password:
        cleaned = cleaned.replace(password, "<REDACTED>")
    cleaned = re.sub(r"(postgres(?:ql)?://[^:/\s]+:)([^@\s]+)(@)", r"\1<REDACTED>\3", cleaned)
    return cleaned


def classify_socket_error(exc: OSError, family: int) -> tuple[str, str]:
    """Distinguishes IPv6, IPv4, DNS, timeout, and availability errors."""
    if isinstance(exc, socket.gaierror):
        return "dns_failure", "DNS resolution failed for database host."
    if isinstance(exc, (TimeoutError, socket.timeout)) or exc.errno == errno.ETIMEDOUT:
        return "connection_timeout", "TCP connection timed out."
    if isinstance(exc, ConnectionRefusedError) or exc.errno == errno.ECONNREFUSED:
        return "project_unavailable", "TCP connection refused by remote host/port."
    if exc.errno in (errno.ENETUNREACH, errno.EHOSTUNREACH, errno.EADDRNOTAVAIL):
        if family == socket.AF_INET6:
            return "ipv6_unreachable", "IPv6 network is unreachable from this host."
        return "ipv4_unreachable", "IPv4 network is unreachable from this host."
    return "network_error", f"Network error (errno={exc.errno}): {exc}"


def _contains_ipv6_address(text: str) -> bool:
    for token in re.findall(r"[0-9a-fA-F:]+", text):
        if ":" not in token:
            continue
        try:
            ipaddress.IPv6Address(token)
            return True
        except ValueError:
            continue
    return False


def classify_psycopg2_error(exc: Exception, password: str = "") -> tuple[str, str]:
    """Classifies PostgreSQL OperationalError without treating network issues as auth failures."""
    msg = sanitize_error_text(str(exc), password)
    lower = msg.lower()
    pgcode = getattr(exc, "pgcode", None)

    if "network is unreachable" in lower:
        if _contains_ipv6_address(msg):
            return "ipv6_unreachable", msg
        return "network_unreachable", msg
    if "could not translate host name" in lower or "name or service not known" in lower:
        return "dns_failure", msg
    if "timeout" in lower or "timed out" in lower:
        return "connection_timeout", msg
    if "connection refused" in lower or "not currently accepting connections" in lower:
        return "project_unavailable", msg
    if (
        pgcode in ("28P01", "28000")
        or "password authentication failed" in lower
        or "tenant or user not found" in lower
    ):
        return "auth_failed", msg
    if "ssl" in lower or "certificate" in lower:
        return "ssl_error", msg
    if "does not exist" in lower and "database" in lower:
        return "database_not_found", msg
    return "postgres_error", msg


def _check_env_config(db_cfg: dict[str, Any]) -> DiagnosticStep:
    engine = db_cfg.get("ENGINE", "")
    host = db_cfg.get("HOST", "")
    port = str(db_cfg.get("PORT", ""))
    name = db_cfg.get("NAME", "")
    user = db_cfg.get("USER", "")
    password = db_cfg.get("PASSWORD", "")
    sslmode = (db_cfg.get("OPTIONS") or {}).get("sslmode", "")
    conn_max_age = db_cfg.get("CONN_MAX_AGE", None)
    disable_cursors = db_cfg.get("DISABLE_SERVER_SIDE_CURSORS", False)

    summary = (
        f"engine={engine}, host={host or '<missing>'}, port={port or '<missing>'}, "
        f"name={name or '<missing>'}, user={user or '<missing>'}, "
        f"password={'<set>' if password else '<missing>'}, sslmode={sslmode or '<missing>'}, "
        f"conn_max_age={conn_max_age}, disable_server_side_cursors={disable_cursors}"
    )

    if "postgresql" not in engine:
        return DiagnosticStep(
            name="1. Environment & Django DB config",
            ok=False,
            category="config_error",
            detail=summary,
            hint="Configure PostgreSQL via DB_ENGINE/DB_HOST/DB_USER/DB_PASSWORD or DATABASE_URL.",
        )
    if not host or not user or not password:
        return DiagnosticStep(
            name="1. Environment & Django DB config",
            ok=False,
            category="missing_credentials",
            detail=summary,
            hint="Set DB_HOST, DB_USER, and DB_PASSWORD in .env (local) or Vercel env vars (production).",
        )
    if not port.isdigit():
        return DiagnosticStep(
            name="1. Environment & Django DB config",
            ok=False,
            category="config_error",
            detail=summary,
            hint="DB_PORT must be a valid integer (5432 for Session Pooler, 6543 for Transaction Pooler).",
        )
    if host.startswith("db.") and host.endswith(".supabase.co"):
        return DiagnosticStep(
            name="1. Environment & Django DB config",
            ok=True,
            category="direct_host_warning",
            detail=f"{summary} (note: direct Supabase db.*.supabase.co host is typically IPv6-only)",
            hint="Prefer the Supabase Session Pooler hostname (port 5432) locally if IPv6 is unavailable.",
        )
    return DiagnosticStep(
        name="1. Environment & Django DB config",
        ok=True,
        category="ok",
        detail=summary,
    )


def _check_dns(host: str, port: int) -> tuple[DiagnosticStep, list[tuple[int, str]]]:
    try:
        infos = socket.getaddrinfo(host, port, type=socket.SOCK_STREAM)
    except socket.gaierror as exc:
        cat, msg = classify_socket_error(exc, socket.AF_UNSPEC)
        return (
            DiagnosticStep(
                name="2. Hostname DNS resolution",
                ok=False,
                category=cat,
                detail=f"{msg} ({exc})",
                hint="Verify DB_HOST spelling from Supabase Dashboard -> Connect -> Session/Transaction Pooler.",
            ),
            [],
        )

    seen: list[tuple[int, str]] = []
    for family, _socktype, _proto, _canon, sockaddr in infos:
        ip = sockaddr[0]
        if (family, ip) not in seen:
            seen.append((family, ip))

    ipv4 = [ip for fam, ip in seen if fam == socket.AF_INET]
    ipv6 = [ip for fam, ip in seen if fam == socket.AF_INET6]
    return (
        DiagnosticStep(
            name="2. Hostname DNS resolution",
            ok=True,
            category="ok",
            detail=f"Resolved {host}:{port} -> IPv4={ipv4 or 'none'}, IPv6={ipv6 or 'none'}",
        ),
        seen,
    )


def _check_tcp(addrs: list[tuple[int, str]], port: int, timeout: float = 5.0) -> DiagnosticStep:
    ordered = sorted(addrs, key=lambda item: 0 if item[0] == socket.AF_INET else 1)
    failures: list[tuple[str, str]] = []
    for family, ip in ordered:
        sock = socket.socket(family, socket.SOCK_STREAM)
        sock.settimeout(timeout)
        try:
            sock.connect((ip, port))
            proto = "IPv4" if family == socket.AF_INET else "IPv6"
            return DiagnosticStep(
                name="3. TCP connectivity",
                ok=True,
                category="ok",
                detail=f"TCP connection succeeded via {proto} ({ip}:{port}).",
            )
        except OSError as exc:
            cat, reason = classify_socket_error(exc, family)
            failures.append((cat, f"{ip}:{port} -> {reason}"))
        finally:
            sock.close()

    primary_cat = failures[0][0] if failures else "network_error"
    hint = ""
    if primary_cat == "ipv6_unreachable":
        hint = (
            "Host resolved only to IPv6 and this network has no IPv6 route. "
            "Switch DB_HOST/DB_USER to the IPv4-compatible Supabase Session Pooler endpoint."
        )
    elif primary_cat == "connection_timeout":
        hint = "Check firewall outbound rules on port 5432/6543 or verify Supabase project is active (not paused)."
    elif primary_cat == "project_unavailable":
        hint = "Check Supabase project status in dashboard and verify port (5432 Session / 6543 Transaction)."

    return DiagnosticStep(
        name="3. TCP connectivity",
        ok=False,
        category=primary_cat,
        detail="; ".join(msg for _cat, msg in failures),
        hint=hint,
    )


def _check_postgres_auth(db_cfg: dict[str, Any], timeout: int = 5) -> DiagnosticStep:
    password = db_cfg.get("PASSWORD", "")
    sslmode = (db_cfg.get("OPTIONS") or {}).get("sslmode", "require")
    try:
        conn = psycopg2.connect(
            dbname=db_cfg.get("NAME", "postgres"),
            user=db_cfg.get("USER", ""),
            password=password,
            host=db_cfg.get("HOST", ""),
            port=int(db_cfg.get("PORT", 5432)),
            sslmode=sslmode,
            connect_timeout=timeout,
        )
        conn.close()
        return DiagnosticStep(
            name="4. PostgreSQL authentication & SSL",
            ok=True,
            category="ok",
            detail=f"Authenticated as '{db_cfg.get('USER', '')}' with sslmode={sslmode}.",
        )
    except Exception as exc:
        cat, msg = classify_psycopg2_error(exc, password=password)
        hint = ""
        if cat == "auth_failed":
            hint = (
                "Verify DB_USER (for Supabase poolers this must include project ref: "
                "postgres.<project-ref>) and DB_PASSWORD."
            )
        elif cat == "ssl_error":
            hint = "Ensure DB_SSLMODE=require and system CA certificates are available."
        return DiagnosticStep(
            name="4. PostgreSQL authentication & SSL",
            ok=False,
            category=cat,
            detail=msg,
            hint=hint,
        )


def _check_django_query(password: str = "") -> DiagnosticStep:
    try:
        with connection.cursor() as cursor:
            cursor.execute("SELECT 1, current_database(), current_user")
            row = cursor.fetchone()
        return DiagnosticStep(
            name="5. Django SQL query",
            ok=True,
            category="ok",
            detail=f"SELECT 1 succeeded (database={row[1]}, user={row[2]}).",
        )
    except DatabaseError as exc:
        return DiagnosticStep(
            name="5. Django SQL query",
            ok=False,
            category="query_error",
            detail=sanitize_error_text(str(exc), password),
        )


def _check_migrations_status(password: str = "") -> DiagnosticStep:
    try:
        recorder = MigrationRecorder(connection)
        has_table = recorder.has_table()
        loader = MigrationLoader(connection, ignore_no_migrations=True)
        applied_count = len(loader.applied_migrations)
        total_count = len(loader.graph.nodes)
        pending_count = max(0, total_count - applied_count)
        return DiagnosticStep(
            name="6. Django migration status",
            ok=True,
            category="ok",
            detail=(
                f"django_migrations_table={'present' if has_table else 'absent'}, "
                f"applied={applied_count}, pending={pending_count}"
            ),
        )
    except DatabaseError as exc:
        return DiagnosticStep(
            name="6. Django migration status",
            ok=False,
            category="migration_check_error",
            detail=sanitize_error_text(str(exc), password),
        )


def run_db_diagnostics(timeout: int = 5) -> DiagnosticReport:
    """Runs all 6 non-destructive database connectivity checks in sequence."""
    report = DiagnosticReport()
    db_cfg = settings.DATABASES["default"]
    password = db_cfg.get("PASSWORD", "")

    env_step = _check_env_config(db_cfg)
    report.steps.append(env_step)
    if not env_step.ok:
        return report

    host = db_cfg["HOST"]
    port = int(db_cfg.get("PORT", 5432))

    dns_step, addrs = _check_dns(host, port)
    report.steps.append(dns_step)
    if not dns_step.ok:
        return report

    tcp_step = _check_tcp(addrs, port, timeout=float(timeout))
    report.steps.append(tcp_step)
    if not tcp_step.ok:
        return report

    auth_step = _check_postgres_auth(db_cfg, timeout=timeout)
    report.steps.append(auth_step)
    if not auth_step.ok:
        return report

    query_step = _check_django_query(password=password)
    report.steps.append(query_step)
    if not query_step.ok:
        return report

    mig_step = _check_migrations_status(password=password)
    report.steps.append(mig_step)
    return report
