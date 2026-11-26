from django.urls import path
from . import views

app_name = "cms"

urlpatterns = [
    path("login/", views.cms_login, name="login"),
    path("logout/", views.cms_logout, name="logout"),
    path("", views.dashboard, name="dashboard"),
    path("crops/", views.crops_list, name="crops_list"),
    path("crops/create/", views.crop_create, name="crop_create"),
    path("crops/<str:crop_id>/edit/", views.crop_edit, name="crop_edit"),
    path("crops/<str:crop_id>/transition/", views.crop_transition, name="crop_transition"),
    path("livestock/", views.livestock_list, name="livestock_list"),
    path("livestock/<str:animal_id>/", views.livestock_detail, name="livestock_detail"),
    path("livestock/<str:animal_id>/transition/", views.livestock_transition, name="livestock_transition"),
    path("media/", views.media_library, name="media_library"),
    path("helpline/", views.helpline_list, name="helpline_list"),
]
