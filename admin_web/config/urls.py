"""Project-level URL configuration for Bakaiya Krishi Saathi Admin CMS."""

from django.contrib import admin
from django.urls import include, path

urlpatterns = [
    path("django-admin/", admin.site.urls),
    path("", include("core.urls")),
]
