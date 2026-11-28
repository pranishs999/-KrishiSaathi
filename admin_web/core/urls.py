"""Application URL routing for Bakaiya Krishi Saathi Core."""

from django.urls import path

from . import views

app_name = "core"

urlpatterns = [
    # Health & entry redirect
    path("healthz", views.healthz, name="healthz"),
    path("", views.root_redirect, name="root"),
    # Primary English Admin CMS
    path("cms/login/", views.cms_login, name="login"),
    path("cms/logout/", views.cms_logout, name="logout"),
    
    # Role-specific dashboards
    path("cms/", views.dashboard, name="dashboard"),
    path("cms/dashboard/super-admin/", views.super_admin_dashboard, name="super_admin_dashboard"),
    path("cms/dashboard/staff-manager/", views.staff_manager_dashboard, name="staff_manager_dashboard"),
    path("cms/dashboard/staff/", views.staff_dashboard, name="staff_dashboard"),
    
    # Crop Management
    path("cms/crops/", views.crops_list, name="crops_list"),
    path("cms/crops/create/", views.crop_create, name="crop_create"),
    path("cms/crops/<str:crop_id>/edit/", views.crop_edit, name="crop_edit"),
    path("cms/crops/<str:crop_id>/transition/", views.crop_transition, name="crop_transition"),
    path("cms/crops/<str:crop_id>/detail/", views.crop_detail, name="crop_detail"),
    path("cms/crops/<str:crop_id>/delete/", views.crop_delete, name="crop_delete"),
    
    # Livestock Management
    path("cms/livestock/", views.livestock_list, name="livestock_list"),
    path("cms/livestock/<str:animal_id>/", views.livestock_detail, name="livestock_detail"),
    path("cms/livestock/<str:animal_id>/entry-detail/", views.livestock_entry_detail, name="livestock_entry_detail"),
    path("cms/livestock/<str:animal_id>/transition/", views.livestock_transition, name="livestock_transition"),
    
    # Media Library
    path("cms/media/", views.media_library, name="media_library"),
    path("cms/media/<str:asset_id>/detail/", views.media_asset_detail, name="media_asset_detail"),
    path("cms/media/<str:asset_id>/delete/", views.media_asset_delete, name="media_asset_delete"),
    
    # Helpline
    path("cms/helpline/", views.helpline_list, name="helpline_list"),
    path("cms/helpline/<str:contact_id>/detail/", views.helpline_contact_detail, name="helpline_contact_detail"),
    path("cms/helpline/<str:contact_id>/delete/", views.helpline_contact_delete, name="helpline_contact_delete"),
    
    # Farmers
    path("cms/farmers/", views.farmers_list, name="farmers_list"),
    
    # Public Read-Only Verified Content APIs for Farmer Clients
    path("api/v1/public/crops/", views.public_crops_api, name="public_crops_api"),
    path("api/v1/public/livestock/", views.public_livestock_api, name="public_livestock_api"),
    path("api/v1/public/helpline/", views.public_helpline_api, name="public_helpline_api"),
    
    # User Management - Super Admin
    path("cms/users/staff-managers/", views.staff_manager_list, name="staff_manager_list"),
    path("cms/users/staff-managers/create/", views.staff_manager_create, name="staff_manager_create"),
    path("cms/users/staff-managers/<int:manager_id>/edit/", views.staff_manager_edit, name="staff_manager_edit"),
    path("cms/users/staff-managers/<int:manager_id>/suspend/", views.staff_manager_suspend, name="staff_manager_suspend"),
    path("cms/users/staff-managers/<int:manager_id>/reactivate/", views.staff_manager_reactivate, name="staff_manager_reactivate"),
    path("cms/users/staff-managers/<int:manager_id>/archive/", views.staff_manager_archive, name="staff_manager_archive"),
    
    # User Management - Staff Manager (their team)
    path("cms/users/staff/", views.staff_list, name="staff_list"),
    path("cms/users/staff/create/", views.staff_create, name="staff_create"),
    path("cms/users/staff/<int:staff_id>/edit/", views.staff_edit, name="staff_edit"),
    path("cms/users/staff/<int:staff_id>/suspend/", views.staff_suspend, name="staff_suspend"),
    path("cms/users/staff/<int:staff_id>/reactivate/", views.staff_reactivate, name="staff_reactivate"),
    path("cms/users/staff/<int:staff_id>/deactivate/", views.staff_deactivate, name="staff_deactivate"),
    
    # Super Admin: All Staff
    path("cms/users/all-staff/", views.all_staff_list, name="all_staff_list"),
    
    # Profile
    path("cms/profile/", views.my_profile, name="my_profile"),
    path("cms/profile/edit/", views.edit_my_profile, name="edit_my_profile"),
    
    # Public Read-Only Verified Content APIs for Farmer Clients
    path("api/v1/public/crops/", views.public_crops_api, name="public_crops_api"),
    path("api/v1/public/livestock/", views.public_livestock_api, name="public_livestock_api"),
    path("api/v1/public/helpline/", views.public_helpline_api, name="public_helpline_api"),
    
    # Legacy Portal Routes (preserved for compatibility)
    path("accounts/login/", views.legacy_login, name="legacy_login"),
    path("accounts/logout/", views.legacy_logout, name="legacy_logout"),
    path("accounts/farmers/", views.legacy_farmer_list, name="legacy_farmers"),
    path("dashboard/", views.legacy_dashboard, name="legacy_dashboard"),
    path("crops/", views.legacy_crop_list, name="legacy_crops_list"),
    path("crops/create/", views.legacy_crop_create, name="legacy_crop_create"),
    path("crops/<str:crop_id>/edit/", views.legacy_crop_edit, name="legacy_crop_edit"),
    path("crops/<str:crop_id>/toggle-publish/", views.legacy_crop_toggle_publish, name="legacy_crop_toggle_publish"),
    path("crops/<str:crop_id>/delete/", views.legacy_crop_delete, name="legacy_crop_delete"),
    path("guides/", views.legacy_guide_list, name="legacy_guides_list"),
    path("guides/create/", views.legacy_guide_create, name="legacy_guide_create"),
    path("guides/<str:guide_id>/", views.legacy_guide_detail, name="legacy_guide_detail"),
    path("guides/<str:guide_id>/step/create/", views.legacy_guide_step_create, name="legacy_guide_step_create"),
    path("guides/<str:guide_id>/toggle-publish/", views.legacy_guide_toggle_publish, name="legacy_guide_toggle_publish"),
]
