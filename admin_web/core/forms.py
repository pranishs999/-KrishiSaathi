"""Django form definitions and server-side validation for Bakaiya Krishi Saathi."""

from django import forms
from django.contrib.auth import get_user_model
from django.contrib.auth.forms import UserCreationForm, SetPasswordForm, PasswordChangeForm

from .models import AdminProfile

User = get_user_model()


class LoginForm(forms.Form):
    username = forms.CharField(max_length=150)
    password = forms.CharField(widget=forms.PasswordInput)

    def clean_username(self):
        return self.cleaned_data["username"].strip()


class CropEntryForm(forms.Form):
    name_en = forms.CharField(max_length=120)
    name_ne = forms.CharField(max_length=120)
    scientific_name = forms.CharField(max_length=150, required=False)
    category_id = forms.CharField(max_length=64, required=False)
    description_ne = forms.CharField(widget=forms.Textarea)
    varieties = forms.CharField(required=False)
    planting_season_ne = forms.CharField(max_length=200, required=False)
    altitude_summary_ne = forms.CharField(max_length=200, required=False)
    soil_types_ne = forms.CharField(max_length=255, required=False)
    source_reference = forms.CharField(max_length=255, required=False)
    thumbnail_url = forms.URLField(required=False)

    def clean_category_id(self):
        return self.cleaned_data.get("category_id", "").strip() or "cat-cereal"


class LivestockSectionForm(forms.Form):
    section_number = forms.IntegerField(min_value=1, max_value=25)
    body_ne = forms.CharField(required=False)
    body_en = forms.CharField(required=False)
    source_reference = forms.CharField(max_length=255, required=False)
    is_applicable = forms.BooleanField(required=False)


class MediaAssetUploadForm(forms.Form):
    title_en = forms.CharField(max_length=200)
    caption_ne = forms.CharField(max_length=255, required=False)
    target_kind = forms.ChoiceField(
        choices=[("crop", "Crop"), ("livestock", "Livestock"), ("general", "General")],
        required=False,
    )
    creator_name = forms.CharField(max_length=200)
    license_type = forms.CharField(max_length=120)
    source_url = forms.URLField(required=False)
    image_file = forms.FileField()

    def clean_target_kind(self):
        return self.cleaned_data.get("target_kind") or "general"


class HelplineContactForm(forms.Form):
    name_ne = forms.CharField(max_length=150)
    designation_ne = forms.CharField(max_length=200)
    department_en = forms.CharField(max_length=200)
    ward_ne = forms.CharField(max_length=200)
    mobile_number = forms.CharField(max_length=32)
    email = forms.EmailField(required=False)
    source_reference = forms.CharField(max_length=255)


class LegacyCropForm(forms.Form):
    id = forms.CharField(max_length=100, required=False)
    name_ne = forms.CharField(max_length=150)
    name_en = forms.CharField(max_length=150)
    scientific_name = forms.CharField(max_length=150)
    category_id = forms.CharField(max_length=50, required=False)
    description_ne = forms.CharField(widget=forms.Textarea)
    growth_duration_days_min = forms.IntegerField(required=False, initial=90)
    growth_duration_days_max = forms.IntegerField(required=False, initial=150)
    optimal_temp_celsius = forms.CharField(max_length=50, required=False)
    soil_type = forms.CharField(max_length=150, required=False)
    is_published = forms.BooleanField(required=False)


class LegacyGuideForm(forms.Form):
    id = forms.CharField(max_length=100, required=False)
    crop_id = forms.CharField(max_length=100)
    title_ne = forms.CharField(max_length=255)
    title_en = forms.CharField(max_length=255)
    summary_ne = forms.CharField(widget=forms.Textarea)
    topic_id = forms.CharField(max_length=50, required=False)
    season_id = forms.CharField(max_length=50, required=False)
    region_id = forms.CharField(max_length=50, required=False)
    is_published = forms.BooleanField(required=False)


class LegacyGuideStepForm(forms.Form):
    step_number = forms.IntegerField(min_value=1, max_value=50)
    title_ne = forms.CharField(max_length=200)
    title_en = forms.CharField(max_length=200)
    content_ne = forms.CharField(widget=forms.Textarea)
    tips_ne = forms.CharField(required=False)


# ============================================================
# User Management Forms (Super Admin → Staff Manager → Staff)
# ============================================================

class StaffManagerCreationForm(forms.Form):
    """Form for Super Admin to create Staff Manager accounts."""
    username = forms.CharField(max_length=150)
    email = forms.EmailField()
    full_name = forms.CharField(max_length=150)
    mobile_number = forms.CharField(max_length=32, required=False)
    password = forms.CharField(widget=forms.PasswordInput)
    password_confirm = forms.CharField(widget=forms.PasswordInput, label="Confirm Password")

    def clean_username(self):
        username = self.cleaned_data["username"].strip()
        if User.objects.filter(username__iexact=username).exists():
            raise forms.ValidationError("A user with this username already exists.")
        return username

    def clean_email(self):
        email = self.cleaned_data["email"].strip()
        if User.objects.filter(email__iexact=email).exists():
            raise forms.ValidationError("A user with this email already exists.")
        return email

    def clean(self):
        cleaned_data = super().clean()
        password = cleaned_data.get("password")
        password_confirm = cleaned_data.get("password_confirm")
        if password and password_confirm and password != password_confirm:
            raise forms.ValidationError("Passwords do not match.")
        return cleaned_data


class StaffCreationForm(forms.Form):
    """Form for Staff Manager to create Staff accounts."""
    username = forms.CharField(max_length=150)
    email = forms.EmailField()
    full_name = forms.CharField(max_length=150)
    mobile_number = forms.CharField(max_length=32, required=False)
    password = forms.CharField(widget=forms.PasswordInput)
    password_confirm = forms.CharField(widget=forms.PasswordInput, label="Confirm Password")

    def __init__(self, *args, manager=None, **kwargs):
        self.manager = manager
        super().__init__(*args, **kwargs)

    def clean_username(self):
        username = self.cleaned_data["username"].strip()
        if User.objects.filter(username__iexact=username).exists():
            raise forms.ValidationError("A user with this username already exists.")
        return username

    def clean_email(self):
        email = self.cleaned_data["email"].strip()
        if User.objects.filter(email__iexact=email).exists():
            raise forms.ValidationError("A user with this email already exists.")
        return email

    def clean(self):
        cleaned_data = super().clean()
        password = cleaned_data.get("password")
        password_confirm = cleaned_data.get("password_confirm")
        if password and password_confirm and password != password_confirm:
            raise forms.ValidationError("Passwords do not match.")
        return cleaned_data


class AdminProfileForm(forms.ModelForm):
    """Form for editing AdminProfile details."""
    class Meta:
        model = AdminProfile
        fields = [
            "full_name",
            "department",
            "mobile_number",
            "email",
            "profile_image",
            "status",
            "is_active_editor",
        ]
        widgets = {
            "full_name": forms.TextInput(attrs={"class": "form-input"}),
            "department": forms.TextInput(attrs={"class": "form-input"}),
            "mobile_number": forms.TextInput(attrs={"class": "form-input"}),
            "email": forms.EmailInput(attrs={"class": "form-input"}),
            "status": forms.Select(attrs={"class": "form-select"}),
            "is_active_editor": forms.CheckboxInput(attrs={"class": "form-checkbox"}),
        }


class StaffProfileForm(forms.ModelForm):
    """Form for Staff Manager to edit their staff's profiles."""
    class Meta:
        model = AdminProfile
        fields = [
            "full_name",
            "department",
            "mobile_number",
            "email",
            "profile_image",
            "status",
            "is_active_editor",
        ]
        widgets = {
            "full_name": forms.TextInput(attrs={"class": "form-input"}),
            "department": forms.TextInput(attrs={"class": "form-input"}),
            "mobile_number": forms.TextInput(attrs={"class": "form-input"}),
            "email": forms.EmailInput(attrs={"class": "form-input"}),
            "status": forms.Select(attrs={"class": "form-select"}),
            "is_active_editor": forms.CheckboxInput(attrs={"class": "form-checkbox"}),
        }


class PasswordResetRequestForm(forms.Form):
    """Form for requesting password reset."""
    email = forms.EmailField()


class PasswordResetConfirmForm(forms.Form):
    """Form for confirming password reset."""
    new_password = forms.CharField(widget=forms.PasswordInput)
    confirm_password = forms.CharField(widget=forms.PasswordInput)

    def clean(self):
        cleaned_data = super().clean()
        if cleaned_data.get("new_password") != cleaned_data.get("confirm_password"):
            raise forms.ValidationError("Passwords do not match.")
        return cleaned_data
