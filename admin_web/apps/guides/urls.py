from django.urls import path
from apps.guides import views

app_name = 'guides'

urlpatterns = [
    path('', views.guide_list, name='list'),
    path('create/', views.guide_create, name='create'),
    path('<str:guide_id>/', views.guide_detail, name='detail'),
    path('<str:guide_id>/step/create/', views.guide_step_create, name='step_create'),
    path('<str:guide_id>/toggle-publish/', views.guide_toggle_publish, name='toggle_publish'),
]

