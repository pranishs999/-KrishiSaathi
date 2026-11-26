from django.db import models
from apps.crops.models import Crop, Topic, Season, Region

class Guide(models.Model):
    id = models.CharField(max_length=100, primary_key=True)
    crop = models.ForeignKey(Crop, on_delete=models.CASCADE, db_column='crop_id', related_name='guides')
    title_ne = models.CharField(max_length=255)
    title_en = models.CharField(max_length=255)
    summary_ne = models.TextField()
    topic = models.ForeignKey(Topic, on_delete=models.SET_NULL, null=True, blank=True, db_column='topic_id')
    season = models.ForeignKey(Season, on_delete=models.SET_NULL, null=True, blank=True, db_column='season_id')
    region = models.ForeignKey(Region, on_delete=models.SET_NULL, null=True, blank=True, db_column='region_id')
    is_published = models.BooleanField(default=True)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    class Meta:
        db_table = 'guides'
        managed = False
        verbose_name = 'Cultivation Guide'
        verbose_name_plural = 'Cultivation Guides'
        ordering = ['title_ne']

    def __str__(self):
        return f"{self.title_ne} ({self.crop.name_ne})"

class GuideStep(models.Model):
    id = models.CharField(max_length=100, primary_key=True)
    guide = models.ForeignKey(Guide, on_delete=models.CASCADE, db_column='guide_id', related_name='steps')
    step_number = models.IntegerField()
    title_ne = models.CharField(max_length=200)
    title_en = models.CharField(max_length=200)
    content_ne = models.TextField()
    content_en = models.TextField(null=True, blank=True)
    tips_ne = models.TextField(null=True, blank=True)
    image_url = models.CharField(max_length=500, null=True, blank=True)
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        db_table = 'guide_steps'
        managed = False
        verbose_name = 'Guide Step'
        verbose_name_plural = 'Guide Steps'
        ordering = ['step_number']

    def __str__(self):
        return f"{self.guide.title_ne} - Step {self.step_number}: {self.title_ne}"
