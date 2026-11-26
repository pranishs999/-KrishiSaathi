from django.urls import path
from apps.crops import views

app_name = 'crops'

urlpatterns = [
    path('', views.crop_list, name='list'),
    path('create/', views.crop_create, name='create'),
    path('<str:crop_id>/edit/', views.crop_edit, name='edit'),
    path('<str:crop_id>/toggle-publish/', views.crop_toggle_publish, name='toggle_publish'),
    path('<str:crop_id>/delete/', views.crop_delete, name='delete'),
]

