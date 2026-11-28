from django.apps import AppConfig


class CoreConfig(AppConfig):
    """Single application configuration for Bakaiya Krishi Saathi Admin CMS."""

    default_auto_field = "django.db.models.BigAutoField"
    name = "core"
    verbose_name = "Bakaiya Krishi Saathi Core"
