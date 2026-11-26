from django.db import models

class CropCategory(models.Model):
    id = models.CharField(max_length=50, primary_key=True)
    name_ne = models.CharField(max_length=100)
    name_en = models.CharField(max_length=100)
    icon_name = models.CharField(max_length=50, null=True, blank=True)
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        db_table = 'crop_categories'
        managed = False
        verbose_name = 'Crop Category'
        verbose_name_plural = 'Crop Categories'
        ordering = ['name_en']

    def __str__(self):
        return f"{self.name_ne} ({self.name_en})"

class Season(models.Model):
    id = models.CharField(max_length=50, primary_key=True)
    name_ne = models.CharField(max_length=100)
    name_en = models.CharField(max_length=100)
    months_ne = models.CharField(max_length=100)

    class Meta:
        db_table = 'seasons'
        managed = False
        verbose_name = 'Season'
        verbose_name_plural = 'Seasons'

    def __str__(self):
        return f"{self.name_ne} ({self.months_ne})"

class Topic(models.Model):
    id = models.CharField(max_length=50, primary_key=True)
    name_ne = models.CharField(max_length=100)
    name_en = models.CharField(max_length=100)

    class Meta:
        db_table = 'topics'
        managed = False
        verbose_name = 'Topic'
        verbose_name_plural = 'Topics'

    def __str__(self):
        return self.name_ne

class Region(models.Model):
    id = models.CharField(max_length=50, primary_key=True)
    name_ne = models.CharField(max_length=150)
    name_en = models.CharField(max_length=150)

    class Meta:
        db_table = 'regions'
        managed = False
        verbose_name = 'Region'
        verbose_name_plural = 'Regions'

    def __str__(self):
        return self.name_ne

class Crop(models.Model):
    id = models.CharField(max_length=100, primary_key=True)
    category = models.ForeignKey(CropCategory, on_delete=models.SET_NULL, null=True, blank=True, db_column='category_id', related_name='crops')
    name_ne = models.CharField(max_length=150)
    name_en = models.CharField(max_length=150)
    scientific_name = models.CharField(max_length=150)
    description_ne = models.TextField()
    description_en = models.TextField(null=True, blank=True)
    growth_duration_days_min = models.IntegerField(default=90)
    growth_duration_days_max = models.IntegerField(default=150)
    optimal_temp_celsius = models.CharField(max_length=50, null=True, blank=True)
    soil_type = models.CharField(max_length=150, null=True, blank=True)
    thumbnail_url = models.CharField(max_length=500, null=True, blank=True)
    is_published = models.BooleanField(default=True)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    class Meta:
        db_table = 'crops'
        managed = False
        verbose_name = 'Crop'
        verbose_name_plural = 'Crops'
        ordering = ['name_ne']

    def __str__(self):
        return f"{self.name_ne} ({self.name_en})"
