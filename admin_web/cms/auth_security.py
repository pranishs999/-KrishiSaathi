"""Security middleware, login rate-limiting, and RBAC decorators for Bakaiya Krishi Saathi Admin."""

from functools import wraps
from django.core.cache import cache
from django.http import HttpResponse, HttpResponseForbidden
from django.shortcuts import redirect
from .workflow import get_user_role, has_cms_permission


class CmsSecurityHeadersMiddleware:
    """Adds baseline CSP, Referrer-Policy, and Cache-Control headers on admin routes."""

    def __init__(self, get_response):
        self.get_response = get_response

    def __call__(self, request):
        response = self.get_response(request)
        response["X-Content-Type-Options"] = "nosniff"
        response["X-Frame-Options"] = "DENY"
        response["Referrer-Policy"] = "strict-origin-when-cross-origin"
        if request.path.startswith("/cms/") or request.path.startswith("/django-admin/"):
            response["Cache-Control"] = "no-store, no-cache, must-revalidate, max-age=0"
        return response


def check_login_rate_limit(request, max_attempts: int = 8, window_seconds: int = 300) -> bool:
    """Returns True if login attempt is permitted; False when throttled."""
    ip = get_client_ip(request) or "unknown"
    key = f"cms_login_attempts:{ip}"
    attempts = cache.get(key, 0)
    if attempts >= max_attempts:
        return False
    return True


def record_failed_login(request, window_seconds: int = 300) -> None:
    ip = get_client_ip(request) or "unknown"
    key = f"cms_login_attempts:{ip}"
    attempts = cache.get(key, 0)
    cache.set(key, attempts + 1, timeout=window_seconds)


def clear_failed_logins(request) -> None:
    ip = get_client_ip(request) or "unknown"
    cache.delete(f"cms_login_attempts:{ip}")


def get_client_ip(request) -> str | None:
    forwarded = request.META.get("HTTP_X_FORWARDED_FOR")
    if forwarded:
        return forwarded.split(",")[0].strip()
    return request.META.get("REMOTE_ADDR")


def cms_role_required(permission: str | None = None):
    """Requires authenticated admin session plus an active CMS role (and optional permission)."""

    def decorator(view_func):
        @wraps(view_func)
        def _wrapped(request, *args, **kwargs):
            if not request.user.is_authenticated:
                return redirect("cms:login")
            role = get_user_role(request.user)
            if not role:
                return HttpResponseForbidden("Forbidden: Active CMS role required.")
            if permission and not has_cms_permission(request.user, permission):
                return HttpResponseForbidden(f"Forbidden: Missing permission '{permission}'.")
            return view_func(request, *args, **kwargs)

        return _wrapped

    return decorator


def rate_limit_exceeded_response():
    return HttpResponse(
        "Too many failed login attempts. Please wait 5 minutes before retrying.",
        status=429,
    )
