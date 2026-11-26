from django.contrib import admin
from django.shortcuts import redirect
from django.urls import include, path
from cms import views as cms_views

urlpatterns = [
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
