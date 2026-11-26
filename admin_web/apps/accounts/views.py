from django.shortcuts import render, redirect
from django.contrib.auth import authenticate, login, logout
from django.contrib.auth.decorators import login_required
from django.contrib import messages
from apps.accounts.models import FarmerProfile, Farm

def login_view(request):
    if request.user.is_authenticated:
        return redirect('dashboard:home')
    
    if request.method == 'POST':
        username = request.POST.get('username', '').strip()
        password = request.POST.get('password', '').strip()
        user = authenticate(request, username=username, password=password)
        if user is not None:
            login(request, user)
            messages.success(request, f"स्वागत छ, {user.username}!")
            return redirect('dashboard:home')
        else:
            messages.error(request, "गलत प्रयोगकर्ता नाम वा पासवर्ड। (Invalid username or password)")
    
    return render(request, 'accounts/login.html')

def logout_view(request):
    logout(request)
    messages.info(request, "तपाईं सफलतापूर्वक लगआउट हुनुभयो।")
    return redirect('accounts:login')

@login_required
def farmer_list(request):
    farmers = FarmerProfile.objects.all().prefetch_related('farms')
    return render(request, 'accounts/farmers.html', {'farmers': farmers})
