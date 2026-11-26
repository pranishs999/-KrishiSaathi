from django.shortcuts import render, redirect, get_object_or_404
from django.contrib.auth.decorators import login_required
from django.contrib import messages
from apps.crops.models import Crop, CropCategory
from apps.accounts.models import AdminActivityLog

@login_required
def crop_list(request):
    category_id = request.GET.get('category')
    search_query = request.GET.get('q', '').strip()
    
    crops = Crop.objects.all()
    if category_id:
        crops = crops.filter(category_id=category_id)
    if search_query:
        crops = crops.filter(name_ne__icontains=search_query) | crops.filter(name_en__icontains=search_query)
    
    categories = CropCategory.objects.all()
    return render(request, 'crops/crop_list.html', {
        'crops': crops,
        'categories': categories,
        'selected_category': category_id,
        'search_query': search_query,
    })

@login_required
def crop_create(request):
    categories = CropCategory.objects.all()
    if request.method == 'POST':
        crop_id = request.POST.get('id', '').strip()
        name_ne = request.POST.get('name_ne', '').strip()
        name_en = request.POST.get('name_en', '').strip()
        scientific_name = request.POST.get('scientific_name', '').strip()
        category_id = request.POST.get('category_id')
        description_ne = request.POST.get('description_ne', '').strip()
        duration_min = int(request.POST.get('growth_duration_days_min', 90))
        duration_max = int(request.POST.get('growth_duration_days_max', 150))
        optimal_temp = request.POST.get('optimal_temp_celsius', '').strip()
        soil_type = request.POST.get('soil_type', '').strip()
        is_published = request.POST.get('is_published') == 'on'

        if not crop_id:
            crop_id = f"crop_{name_en.lower().replace(' ', '_')}"

        if Crop.objects.filter(id=crop_id).exists():
            messages.error(request, f"बाली आइडी '{crop_id}' पहिले नै दर्ता भइसकेको छ।")
        else:
            category = CropCategory.objects.filter(id=category_id).first() if category_id else None
            crop = Crop.objects.create(
                id=crop_id,
                name_ne=name_ne,
                name_en=name_en,
                scientific_name=scientific_name,
                category=category,
                description_ne=description_ne,
                growth_duration_days_min=duration_min,
                growth_duration_days_max=duration_max,
                optimal_temp_celsius=optimal_temp,
                soil_type=soil_type,
                is_published=is_published,
            )
            AdminActivityLog.objects.create(
                admin_email=request.user.email or request.user.username,
                action='CREATE_CROP',
                target_type='Crop',
                target_id=crop.id,
                details=f"Created crop {crop.name_ne}"
            )
            messages.success(request, f"नयाँ बाली '{name_ne}' सफलतापूर्वक थपियो।")
            return redirect('crops:list')

    return render(request, 'crops/crop_form.html', {'categories': categories})

@login_required
def crop_edit(request, crop_id):
    crop = get_object_or_404(Crop, id=crop_id)
    categories = CropCategory.objects.all()

    if request.method == 'POST':
        crop.name_ne = request.POST.get('name_ne', crop.name_ne).strip()
        crop.name_en = request.POST.get('name_en', crop.name_en).strip()
        crop.scientific_name = request.POST.get('scientific_name', crop.scientific_name).strip()
        category_id = request.POST.get('category_id')
        crop.category = CropCategory.objects.filter(id=category_id).first() if category_id else None
        crop.description_ne = request.POST.get('description_ne', crop.description_ne).strip()
        crop.growth_duration_days_min = int(request.POST.get('growth_duration_days_min', crop.growth_duration_days_min))
        crop.growth_duration_days_max = int(request.POST.get('growth_duration_days_max', crop.growth_duration_days_max))
        crop.optimal_temp_celsius = request.POST.get('optimal_temp_celsius', crop.optimal_temp_celsius).strip()
        crop.soil_type = request.POST.get('soil_type', crop.soil_type).strip()
        crop.is_published = request.POST.get('is_published') == 'on'
        crop.save()

        AdminActivityLog.objects.create(
            admin_email=request.user.email or request.user.username,
            action='UPDATE_CROP',
            target_type='Crop',
            target_id=crop.id,
            details=f"Updated crop {crop.name_ne}"
        )
        messages.success(request, f"बाली '{crop.name_ne}' विवरण सफलतापूर्वक अद्यावधिक भयो।")
        return redirect('crops:list')

    return render(request, 'crops/crop_form.html', {'crop': crop, 'categories': categories})

@login_required
def crop_toggle_publish(request, crop_id):
    crop = get_object_or_404(Crop, id=crop_id)
    crop.is_published = not crop.is_published
    crop.save()
    status_text = "प्रकाशित (Published)" if crop.is_published else "अप्रकाशित (Draft)"
    AdminActivityLog.objects.create(
        admin_email=request.user.email or request.user.username,
        action='TOGGLE_PUBLISH_CROP',
        target_type='Crop',
        target_id=crop.id,
        details=f"Crop {crop.name_ne} set to {status_text}"
    )
    messages.success(request, f"बाली '{crop.name_ne}' को स्थिति '{status_text}' बनाइयो।")
    return redirect('crops:list')

@login_required
def crop_delete(request, crop_id):
    crop = get_object_or_404(Crop, id=crop_id)
    name = crop.name_ne
    crop.delete()
    AdminActivityLog.objects.create(
        admin_email=request.user.email or request.user.username,
        action='DELETE_CROP',
        target_type='Crop',
        target_id=crop_id,
        details=f"Deleted crop {name}"
    )
    messages.success(request, f"बाली '{name}' हटाइयो।")
    return redirect('crops:list')
