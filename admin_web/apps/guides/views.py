from django.shortcuts import render, redirect, get_object_or_404
from django.contrib.auth.decorators import login_required
from django.contrib import messages
from apps.guides.models import Guide, GuideStep
from apps.crops.models import Crop, Topic, Season, Region
from apps.accounts.models import AdminActivityLog

@login_required
def guide_list(request):
    crop_id = request.GET.get('crop')
    guides = Guide.objects.all().select_related('crop', 'topic', 'season', 'region')
    if crop_id:
        guides = guides.filter(crop_id=crop_id)
    
    crops = Crop.objects.all()
    return render(request, 'guides/guide_list.html', {
        'guides': guides,
        'crops': crops,
        'selected_crop': crop_id,
    })

@login_required
def guide_create(request):
    crops = Crop.objects.all()
    topics = Topic.objects.all()
    seasons = Season.objects.all()
    regions = Region.objects.all()

    if request.method == 'POST':
        guide_id = request.POST.get('id', '').strip()
        crop_id = request.POST.get('crop_id')
        title_ne = request.POST.get('title_ne', '').strip()
        title_en = request.POST.get('title_en', '').strip()
        summary_ne = request.POST.get('summary_ne', '').strip()
        topic_id = request.POST.get('topic_id')
        season_id = request.POST.get('season_id')
        region_id = request.POST.get('region_id')
        is_published = request.POST.get('is_published') == 'on'

        crop = get_object_or_404(Crop, id=crop_id)
        if not guide_id:
            guide_id = f"guide_{crop.name_en.lower().replace(' ', '_')}"

        guide = Guide.objects.create(
            id=guide_id,
            crop=crop,
            title_ne=title_ne,
            title_en=title_en,
            summary_ne=summary_ne,
            topic=Topic.objects.filter(id=topic_id).first() if topic_id else None,
            season=Season.objects.filter(id=season_id).first() if season_id else None,
            region=Region.objects.filter(id=region_id).first() if region_id else None,
            is_published=is_published,
        )
        AdminActivityLog.objects.create(
            admin_email=request.user.email or request.user.username,
            action='CREATE_GUIDE',
            target_type='Guide',
            target_id=guide.id,
            details=f"Created guide {guide.title_ne}"
        )
        messages.success(request, f"नयाँ निर्देशिका '{title_ne}' थपियो।")
        return redirect('guides:detail', guide_id=guide.id)

    return render(request, 'guides/guide_form.html', {
        'crops': crops,
        'topics': topics,
        'seasons': seasons,
        'regions': regions,
    })

@login_required
def guide_detail(request, guide_id):
    guide = get_object_or_404(Guide.objects.select_related('crop', 'topic', 'season', 'region'), id=guide_id)
    steps = guide.steps.all().order_by('step_number')
    return render(request, 'guides/guide_detail.html', {'guide': guide, 'steps': steps})

@login_required
def guide_step_create(request, guide_id):
    guide = get_object_or_404(Guide, id=guide_id)
    if request.method == 'POST':
        step_number = int(request.POST.get('step_number', 1))
        step_id = f"step_{guide.id}_{step_number}"
        title_ne = request.POST.get('title_ne', '').strip()
        title_en = request.POST.get('title_en', '').strip()
        content_ne = request.POST.get('content_ne', '').strip()
        tips_ne = request.POST.get('tips_ne', '').strip()

        GuideStep.objects.update_or_create(
            id=step_id,
            defaults={
                'guide': guide,
                'step_number': step_number,
                'title_ne': title_ne,
                'title_en': title_en,
                'content_ne': content_ne,
                'tips_ne': tips_ne,
            }
        )
        messages.success(request, f"चरण {step_number} सफलतापूर्वक थपियो/अद्यावधिक भयो।")
        return redirect('guides:detail', guide_id=guide.id)

    next_step = guide.steps.count() + 1
    return render(request, 'guides/step_form.html', {'guide': guide, 'next_step': next_step})

@login_required
def guide_toggle_publish(request, guide_id):
    guide = get_object_or_404(Guide, id=guide_id)
    guide.is_published = not guide.is_published
    guide.save()
    status_text = "प्रकाशित (Published)" if guide.is_published else "अप्रकाशित (Draft)"
    messages.success(request, f"निर्देशिका '{guide.title_ne}' को स्थिति '{status_text}' बनाइयो।")
    return redirect('guides:list')
