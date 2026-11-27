from django.contrib import admin
from django.db import DatabaseError, connection
from django.http import JsonResponse
from django.shortcuts import redirect
from django.urls import include, path
from cms import views as cms_views


def healthz(_request):
    try:
        with connection.cursor() as cursor:
            cursor.execute("SELECT 1")
            cursor.fetchone()
    except DatabaseError:
        return JsonResponse({"status": "error"}, status=503)
    return JsonResponse({"status": "ok"})


urlpatterns = [
    path("healthz", healthz, name="healthz"),
    path("django-admin/", admin.site.urls),
    path("", lambda request: redirect("cms:dashboard")),
    path("cms/", include("cms.urls")),
    # Public Read-Only Verified Content APIs for Farmer Clients
    path("api/v1/public/crops/", cms_views.public_crops_api, name="public_crops_api"),
    path("api/v1/public/livestock/", cms_views.public_livestock_api, name="public_livestock_api"),
    path("api/v1/public/helpline/", cms_views.public_helpline_api, name="public_helpline_api"),
    # Legacy routes
    path("dashboard/", include("apps.dashboard.urls")),
    path("crops/", include("apps.crops.urls")),
    path("guides/", include("apps.guides.urls")),
    path("accounts/", include("apps.accounts.urls")),
]
