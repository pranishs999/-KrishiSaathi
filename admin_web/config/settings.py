"""
Django settings for Bakaiya Krishi Saathi Admin CMS project.
Zero hardcoded database secrets or API credentials; all sensitive values come from environment variables.
"""

from pathlib import Path
import os
import sys
from urllib.parse import parse_qs, unquote, urlparse
from django.core.exceptions import ImproperlyConfigured

BASE_DIR = Path(__file__).resolve().parent.parent


def _load_dotenv(env_path: Path) -> None:
    if not env_path.exists():
        return
    for raw_line in env_path.read_text(encoding="utf-8").splitlines():
        line = raw_line.strip()
        if not line or line.startswith("#") or "=" not in line:
            continue
        key, val = line.split("=", 1)
        os.environ.setdefault(key.strip(), val.strip().strip('"').strip("'"))


_load_dotenv(BASE_DIR / ".env")

IS_VERCEL = os.environ.get("VERCEL", "").lower() in ("1", "true", "yes") or bool(os.environ.get("VERCEL_ENV"))
IS_RENDER = bool(os.environ.get("RENDER"))
IS_PRODUCTION = IS_VERCEL or IS_RENDER


def _env_bool(name: str, default: bool = False) -> bool:
    val = os.environ.get(name)
    if val is None or not val.strip():
        return default
    return val.strip().lower() in ("1", "true", "yes", "on")


def _env_int(name: str, default: int) -> int:
    val = os.environ.get(name)
    if val is None or not val.strip():
        return default
    return int(val.strip())


def _safe_sslmode(raw_sslmode: str | None, host: str) -> str:
    sslmode = (raw_sslmode or "require").strip().lower()
    if not sslmode:
        return "require"
    # Never disable SSL verification for remote PostgreSQL hosts.
    if sslmode == "disable" and host not in ("localhost", "127.0.0.1", "::1", ""):
        return "require"
    return sslmode


def _finalize_postgres_config(
    *,
    engine: str,
    name: str,
    user: str,
    password: str,
    host: str,
    port: str,
    sslmode: str,
) -> dict:
    pool_mode = os.environ.get("DB_POOL_MODE", "").strip().lower()
    is_transaction_pooler = port == "6543" or pool_mode == "transaction"
    default_conn_max_age = 0 if (IS_VERCEL or is_transaction_pooler) else 60
    conn_max_age = _env_int("DB_CONN_MAX_AGE", default_conn_max_age)
    connect_timeout = _env_int("DB_CONNECT_TIMEOUT", 10)

    disable_cursors_raw = os.environ.get("DB_DISABLE_SERVER_SIDE_CURSORS", "").strip().lower()
    if disable_cursors_raw in ("1", "true", "yes", "on"):
        disable_server_side_cursors = True
    elif disable_cursors_raw in ("0", "false", "no", "off"):
        disable_server_side_cursors = False
    else:
        disable_server_side_cursors = is_transaction_pooler

    config = {
        "ENGINE": engine or "django.db.backends.postgresql",
        "NAME": name or "postgres",
        "USER": user,
        "PASSWORD": password,
        "HOST": host,
        "PORT": port or "5432",
        "CONN_MAX_AGE": conn_max_age,
        "OPTIONS": {
            "sslmode": _safe_sslmode(sslmode, host),
            "connect_timeout": connect_timeout,
        },
    }
    if conn_max_age > 0:
        config["CONN_HEALTH_CHECKS"] = True
    if disable_server_side_cursors:
        config["DISABLE_SERVER_SIDE_CURSORS"] = True
    return config


def _parse_database_url(db_url: str) -> dict:
    if db_url.startswith("sqlite:"):
        sqlite_name = db_url.replace("sqlite:///", "") if db_url.startswith("sqlite:///") else str(BASE_DIR / "db.sqlite3")
        return {
            "ENGINE": "django.db.backends.sqlite3",
            "NAME": sqlite_name,
        }
    parsed = urlparse(db_url)
    query = parse_qs(parsed.query)
    sslmode = (query.get("sslmode") or [os.environ.get("DB_SSLMODE") or "require"])[0]
    host = parsed.hostname or ""
    port = str(parsed.port or os.environ.get("DB_PORT") or "5432")
    return _finalize_postgres_config(
        engine=os.environ.get("DB_ENGINE", "django.db.backends.postgresql"),
        name=parsed.path.lstrip("/") or os.environ.get("DB_NAME", "postgres"),
        user=unquote(parsed.username) if parsed.username else os.environ.get("DB_USER", "postgres"),
        password=unquote(parsed.password) if parsed.password else os.environ.get("DB_PASSWORD", ""),
        host=host,
        port=port,
        sslmode=sslmode,
    )


def build_database_config(running_tests: bool = False) -> dict:
    if running_tests:
        return {
            "ENGINE": "django.db.backends.sqlite3",
            "NAME": str(BASE_DIR / "db.sqlite3"),
        }

    # Require DB configuration for production/non-test environments
    # Check for required environment variables
    required_vars = ["DB_NAME", "DB_USER", "DB_PASSWORD"]
    missing_vars = [var for var in required_vars if not os.environ.get(var, "").strip()]
    
    if missing_vars and not os.environ.get("DATABASE_URL", "").strip():
        # Build a helpful error message
        raise ImproperlyConfigured(
            f"Missing required database environment variables: {', '.join(missing_vars)}. "
            f"Set DB_NAME, DB_USER, DB_PASSWORD, DB_HOST, DB_PORT, DB_SSLMODE "
            f"or provide DATABASE_URL. For Vercel, use Transaction Pooler (port 6543)."
        )

    db_host = os.environ.get("DB_HOST", "").strip()
    if db_host:
        return _finalize_postgres_config(
            engine=os.environ.get("DB_ENGINE", "django.db.backends.postgresql").strip(),
            name=os.environ.get("DB_NAME", "postgres").strip(),
            user=os.environ.get("DB_USER", "").strip(),
            password=os.environ.get("DB_PASSWORD", ""),
            host=db_host,
            port=os.environ.get("DB_PORT", "5432").strip(),
            sslmode=os.environ.get("DB_SSLMODE", "require").strip(),
        )

    db_url = os.environ.get("DATABASE_URL", "").strip()
    if db_url:
        return _parse_database_url(db_url)

    # Fallback with validation
    return _finalize_postgres_config(
        engine=os.environ.get("DB_ENGINE", "django.db.backends.postgresql").strip(),
        name=os.environ.get("DB_NAME", "postgres").strip(),
        user=os.environ.get("DB_USER", "").strip(),
        password=os.environ.get("DB_PASSWORD", ""),
        host="",
        port=os.environ.get("DB_PORT", "5432").strip(),
        sslmode=os.environ.get("DB_SSLMODE", "require").strip(),
    )


if IS_VERCEL and os.environ.get("VERCEL_ENV", "production") == "production":
    DEBUG = False
else:
    DEBUG = _env_bool("DJANGO_DEBUG", _env_bool("DEBUG", False))

SECRET_KEY = (
    os.environ.get("SECRET_KEY")
    or os.environ.get("DJANGO_SECRET_KEY")
    or "django-insecure-dev-only-set-SECRET_KEY-in-env"
)

_allowed_env = os.environ.get("ALLOWED_HOSTS", "")
ALLOWED_HOSTS = [h.strip() for h in _allowed_env.split(",") if h.strip() and h.strip() != "*"]
_render_hostname = os.environ.get("RENDER_EXTERNAL_HOSTNAME", "").strip()
_vercel_url = os.environ.get("VERCEL_URL", "").strip().removeprefix("https://").removeprefix("http://").rstrip("/")
_vercel_prod_url = os.environ.get("VERCEL_PROJECT_PRODUCTION_URL", "").strip().removeprefix("https://").removeprefix("http://").rstrip("/")

# Explicitly allow production domain
_production_domain = "krishi-saathi-phi.vercel.app"

_default_hosts = [
    "localhost",
    "127.0.0.1",
    "testserver",
    ".onrender.com",
    "bakaiya-krishi-saathi-cms.onrender.com",
    _render_hostname,
    _vercel_url,
    _vercel_prod_url,
    _production_domain,
]
if IS_VERCEL:
    _default_hosts.append(".vercel.app")
for _default_host in _default_hosts:
    if _default_host and _default_host != "*" and _default_host not in ALLOWED_HOSTS:
        ALLOWED_HOSTS.append(_default_host)

INSTALLED_APPS = [
    "django.contrib.admin",
    "django.contrib.auth",
    "django.contrib.contenttypes",
    "django.contrib.sessions",
    "django.contrib.messages",
    "django.contrib.staticfiles",
    # Single consolidated application
    "core",
]

MIDDLEWARE = [
    "django.middleware.security.SecurityMiddleware",
    "whitenoise.middleware.WhiteNoiseMiddleware",
    "django.contrib.sessions.middleware.SessionMiddleware",
    "django.middleware.common.CommonMiddleware",
    "django.middleware.csrf.CsrfViewMiddleware",
    "django.contrib.auth.middleware.AuthenticationMiddleware",
    "django.contrib.messages.middleware.MessageMiddleware",
    "django.middleware.clickjacking.XFrameOptionsMiddleware",
    "core.services.CmsSecurityHeadersMiddleware",
]

ROOT_URLCONF = "config.urls"

TEMPLATES = [
    {
        "BACKEND": "django.template.backends.django.DjangoTemplates",
        "DIRS": [],
        "APP_DIRS": True,
        "OPTIONS": {
            "context_processors": [
                "django.template.context_processors.request",
                "django.contrib.auth.context_processors.auth",
                "django.contrib.messages.context_processors.messages",
            ],
        },
    },
]

WSGI_APPLICATION = "config.wsgi.application"

# Database configuration: discrete DB_* env vars (Supabase Session/Transaction Pooler)
# with DATABASE_URL fallback. Automated tests stay on local SQLite so they never write to Supabase.
_argv = " ".join(sys.argv)
_running_tests = "test" in sys.argv or "pytest" in _argv
DATABASES = {
    "default": build_database_config(running_tests=_running_tests),
}

AUTH_PASSWORD_VALIDATORS = [
    {"NAME": "django.contrib.auth.password_validation.UserAttributeSimilarityValidator"},
    {"NAME": "django.contrib.auth.password_validation.MinimumLengthValidator", "OPTIONS": {"min_length": 10}},
    {"NAME": "django.contrib.auth.password_validation.CommonPasswordValidator"},
    {"NAME": "django.contrib.auth.password_validation.NumericPasswordValidator"},
]

LANGUAGE_CODE = "en-us"
TIME_ZONE = "Asia/Kathmandu"
USE_I18N = True
USE_TZ = True

STATIC_URL = "/static/"
STATICFILES_DIRS = []
STATIC_ROOT = BASE_DIR / "staticfiles"
MEDIA_URL = "/media/"
MEDIA_ROOT = BASE_DIR / "media"

_csrf_env = os.environ.get("CSRF_TRUSTED_ORIGINS", "")
CSRF_TRUSTED_ORIGINS = [origin.strip() for origin in _csrf_env.split(",") if origin.strip()]
_production_domain = "krishi-saathi-phi.vercel.app"
_default_csrf_origins = [
    "https://*.onrender.com",
    f"https://{_production_domain}",
]
if _render_hostname:
    _default_csrf_origins.append(f"https://{_render_hostname}")
if _vercel_url:
    _default_csrf_origins.append(f"https://{_vercel_url}")
if _vercel_prod_url:
    _default_csrf_origins.append(f"https://{_vercel_prod_url}")
if _vercel_prod_url != _production_domain:
    _default_csrf_origins.append(f"https://{_production_domain}")
for _origin in _default_csrf_origins:
    if _origin not in CSRF_TRUSTED_ORIGINS:
        CSRF_TRUSTED_ORIGINS.append(_origin)

if not DEBUG or IS_VERCEL or IS_RENDER:
    SECURE_PROXY_SSL_HEADER = ("HTTP_X_FORWARDED_PROTO", "https")
if IS_PRODUCTION and not DEBUG:
    SECURE_SSL_REDIRECT = _env_bool("SECURE_SSL_REDIRECT", True)
    SECURE_HSTS_SECONDS = _env_int("SECURE_HSTS_SECONDS", 31536000)
    SECURE_HSTS_INCLUDE_SUBDOMAINS = True
    SECURE_HSTS_PRELOAD = True
if not DEBUG:
    STORAGES = {
        "default": {"BACKEND": "django.core.files.storage.FileSystemStorage"},
        "staticfiles": {"BACKEND": "whitenoise.storage.CompressedStaticFilesStorage"},
    }

LOGIN_URL = "core:login"
LOGIN_REDIRECT_URL = "core:dashboard"
LOGOUT_REDIRECT_URL = "core:login"

SESSION_COOKIE_HTTPONLY = True
SESSION_COOKIE_SAMESITE = "Lax"
CSRF_COOKIE_HTTPONLY = True
SESSION_EXPIRE_AT_BROWSER_CLOSE = True
if not DEBUG:
    SESSION_COOKIE_SECURE = True
    CSRF_COOKIE_SECURE = True

DEFAULT_AUTO_FIELD = "django.db.models.BigAutoField"

# Logging configuration
LOGGING = {
    "version": 1,
    "disable_existing_loggers": False,
    "formatters": {
        "verbose": {
            "format": "{levelname} {asctime} {module} {process:d} {thread:d} {message}",
            "style": "{",
        },
        "simple": {
            "format": "{levelname} {message}",
            "style": "{",
        },
    },
    "handlers": {
        "console": {
            "class": "logging.StreamHandler",
            "formatter": "verbose",
        },
    },
    "root": {
        "handlers": ["console"],
        "level": "INFO" if not DEBUG else "DEBUG",
    },
    "loggers": {
        "django": {
            "handlers": ["console"],
            "level": "INFO",
            "propagate": False,
        },
        "django.request": {
            "handlers": ["console"],
            "level": "WARNING",
            "propagate": False,
        },
        "django.db.backends": {
            "handlers": ["console"],
            "level": "WARNING",
            "propagate": False,
        },
        "core": {
            "handlers": ["console"],
            "level": "INFO",
            "propagate": False,
        },
    },
}
