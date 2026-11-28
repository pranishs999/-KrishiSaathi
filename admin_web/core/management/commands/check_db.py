"""Management command to run safe, read-only database connectivity diagnostics."""

from django.core.management.base import BaseCommand, CommandError

from core.db_diagnostics import run_db_diagnostics


class Command(BaseCommand):
    help = "Verify env loading, DNS, TCP reachability, PostgreSQL auth, query, and migration status."

    def add_arguments(self, parser):
        parser.add_argument(
            "--timeout",
            type=int,
            default=5,
            help="Socket and PostgreSQL connect timeout in seconds (default: 5).",
        )

    def handle(self, *args, **options):
        report = run_db_diagnostics(timeout=options["timeout"])
        for step in report.steps:
            status = "PASS" if step.ok else "FAIL"
            style = self.style.SUCCESS if step.ok else self.style.ERROR
            self.stdout.write(style(f"[{status}] {step.name} ({step.category})"))
            self.stdout.write(f"       {step.detail}")
            if step.hint:
                self.stdout.write(self.style.WARNING(f"       Hint: {step.hint}"))

        if not report.passed:
            raise CommandError("Database connectivity check failed.")
        self.stdout.write(self.style.SUCCESS("All database connectivity checks passed."))
