from django.shortcuts import render
from django.contrib.auth.decorators import login_required
from apps.crops.models import Crop, CropCategory
from apps.guides.models import Guide, GuideStep
from apps.accounts.models import FarmerProfile, Farm, AdminActivityLog

@login_required
def home(request):
    total_crops = Crop.objects.count()
    published_crops = Crop.objects.filter(is_published=True).count()
    total_guides = Guide.objects.count()
    published_guides = Guide.objects.filter(is_published=True).count()
    total_farmers = FarmerProfile.objects.count()
    total_farms = Farm.objects.count()
    recent_logs = AdminActivityLog.objects.all()[:8]
    recent_crops = Crop.objects.order_by('-created_at')[:5]

    context = {
        'total_crops': total_crops,
        'published_crops': published_crops,
        'total_guides': total_guides,
        'published_guides': published_guides,
        'total_farmers': total_farmers,
        'total_farms': total_farms,
        'recent_logs': recent_logs,
        'recent_crops': recent_crops,
    }
    return render(request, 'dashboard/index.html', context)
