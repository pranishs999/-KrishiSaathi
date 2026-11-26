import uuid
from django.db import models

class FarmerProfile(models.Model):
    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    phone = models.CharField(max_length=20, unique=True)
    full_name = models.CharField(max_length=150)
    mpin_hash = models.CharField(max_length=255)
    municipality = models.CharField(max_length=100, default='Hetauda')
    ward_number = models.IntegerField(default=1)
    latitude = models.FloatField(null=True, blank=True)
    longitude = models.FloatField(null=True, blank=True)
    land_unit_preference = models.CharField(max_length=20, default='ropani')
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    class Meta:
        db_table = 'farmer_profiles'
        managed = False
        verbose_name = 'Farmer Profile'
        verbose_name_plural = 'Farmer Profiles'

    def __str__(self):
        return f"{self.full_name} ({self.phone})"

class Farm(models.Model):
    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    farmer = models.ForeignKey(FarmerProfile, on_delete=models.CASCADE, db_column='farmer_id', related_name='farms')
    name = models.CharField(max_length=150)
    location = models.CharField(max_length=255)
    total_area = models.FloatField(default=0.0)
    area_unit = models.CharField(max_length=30, default='ropani')
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    class Meta:
        db_table = 'farms'
        managed = False
        verbose_name = 'Farm'
        verbose_name_plural = 'Farms'

    def __str__(self):
        return f"{self.name} - {self.farmer.full_name}"

class FarmPlot(models.Model):
    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    farm = models.ForeignKey(Farm, on_delete=models.CASCADE, db_column='farm_id', related_name='plots')
    plot_name = models.CharField(max_length=100)
    area = models.FloatField(default=0.0)
    area_unit = models.CharField(max_length=30, default='ropani')
    current_crop_id = models.CharField(max_length=100, null=True, blank=True)
    soil_type = models.CharField(max_length=100, null=True, blank=True)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    class Meta:
        db_table = 'farm_plots'
        managed = False
        verbose_name = 'Farm Plot'
        verbose_name_plural = 'Farm Plots'

    def __str__(self):
        return f"{self.plot_name} ({self.farm.name})"

class AdminActivityLog(models.Model):
    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    admin_email = models.CharField(max_length=150)
    action = models.CharField(max_length=100)
    target_type = models.CharField(max_length=50)
    target_id = models.CharField(max_length=100)
    details = models.TextField(null=True, blank=True)
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        db_table = 'admin_activity_logs'
        managed = False
        verbose_name = 'Admin Activity Log'
        verbose_name_plural = 'Admin Activity Logs'
        ordering = ['-created_at']

    def __str__(self):
        return f"[{self.created_at}] {self.admin_email} - {self.action} on {self.target_type}"
