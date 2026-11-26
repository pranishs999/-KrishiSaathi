"""
Django settings for Bakaiya Krishi Saathi Admin CMS project.
Zero hardcoded database secrets or API credentials; all sensitive values come from environment variables.
"""

from pathlib import Path
import os
import sys
from urllib.parse import parse_qs, urlparse

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


def _parse_database_url(db_url: str) -> dict:
    if not db_url or db_url.startswith("sqlite:"):
        sqlite_name = db_url.replace("sqlite:///", "") if db_url.startswith("sqlite:///") else str(BASE_DIR / "db.sqlite3")
        return {
            "ENGINE": "django.db.backends.sqlite3",
            "NAME": sqlite_name,
        }
    parsed = urlparse(db_url)
    query = parse_qs(parsed.query)
    sslmode = (query.get("sslmode") or ["require"])[0]
    return {
        "ENGINE": "django.db.backends.postgresql",
        "NAME": parsed.path.lstrip("/") or "postgres",
        "USER": parsed.username or "postgres",
        "PASSWORD": parsed.password or "",
        "HOST": parsed.hostname or "localhost",
        "PORT": str(parsed.port or 5432),
        "CONN_MAX_AGE": 600,
        "OPTIONS": {"sslmode": sslmode},
    }


DEBUG = os.environ.get("DJANGO_DEBUG", os.environ.get("DEBUG", "false")).lower() == "true"
SECRET_KEY = os.environ.get(
    "SECRET_KEY",
    os.environ.get("DJANGO_SECRET_KEY", "dev-only-local-ephemeral-key-set-SECRET_KEY-in-prod"),
)

_allowed_env = os.environ.get("ALLOWED_HOSTS", "")
ALLOWED_HOSTS = (
    [h.strip() for h in _allowed_env.split(",") if h.strip()]
    if _allowed_env
    else ["localhost", "127.0.0.1", "testserver"]
)

INSTALLED_APPS = [
    "django.contrib.admin",
    "django.contrib.auth",
    "django.contrib.contenttypes",
    "django.contrib.sessions",
    "django.contrib.messages",
    "django.contrib.staticfiles",
    # Production English Admin CMS
    "cms",
    # Legacy app modules retained for compatibility
    "apps.accounts",
    "apps.crops",
    "apps.guides",
    "apps.dashboard",
]

MIDDLEWARE = [
    "django.middleware.security.SecurityMiddleware",
    "django.contrib.sessions.middleware.SessionMiddleware",
    "django.middleware.common.CommonMiddleware",
    "django.middleware.csrf.CsrfViewMiddleware",
    "django.contrib.auth.middleware.AuthenticationMiddleware",
    "django.contrib.messages.middleware.MessageMiddleware",
    "django.middleware.clickjacking.XFrameOptionsMiddleware",
    "cms.auth_security.CmsSecurityHeadersMiddleware",
]

ROOT_URLCONF = "config.urls"

TEMPLATES = [
    {
        "BACKEND": "django.template.backends.django.DjangoTemplates",
        "DIRS": [BASE_DIR / "templates"],
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

# Database configuration: env-driven DATABASE_URL (Supabase PostgreSQL).
# Automated tests stay on local SQLite so they never write to the cloud database.
_argv = " ".join(sys.argv)
_running_tests = "test" in sys.argv or "pytest" in _argv
DATABASES = {
    "default": _parse_database_url(
        "" if _running_tests else os.environ.get("DATABASE_URL", "")
    )
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
STATICFILES_DIRS = [BASE_DIR / "static"]
STATIC_ROOT = BASE_DIR / "staticfiles"
MEDIA_URL = "/media/"
MEDIA_ROOT = BASE_DIR / "media"

LOGIN_URL = "cms:login"
LOGIN_REDIRECT_URL = "cms:dashboard"
LOGOUT_REDIRECT_URL = "cms:login"

SESSION_COOKIE_HTTPONLY = True
SESSION_COOKIE_SAMESITE = "Lax"
CSRF_COOKIE_HTTPONLY = True
SESSION_EXPIRE_AT_BROWSER_CLOSE = True
if not DEBUG:
    SESSION_COOKIE_SECURE = True
    CSRF_COOKIE_SECURE = True

DEFAULT_AUTO_FIELD = "django.db.models.BigAutoField"
