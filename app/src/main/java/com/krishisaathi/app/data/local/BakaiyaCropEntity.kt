package com.krishisaathi.app.data.local

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "crop_categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "name_ne") val nameNe: String,
    @ColumnInfo(name = "name_en") val nameEn: String,
    @ColumnInfo(name = "icon_name") val iconName: String? = null
)

/**
 * Reusable embedded Room column set for structured environmental & agronomic growing conditions.
 */
data class GrowingConditionsEmbedded(
    @ColumnInfo(name = "altitude_min_m") val altitudeMinMeters: Int? = null,
    @ColumnInfo(name = "altitude_opt_min_m") val altitudeOptimalMinMeters: Int? = null,
    @ColumnInfo(name = "altitude_opt_max_m") val altitudeOptimalMaxMeters: Int? = null,
    @ColumnInfo(name = "altitude_max_m") val altitudeMaxMeters: Int? = null,
    @ColumnInfo(name = "suitable_altitude") val altitudeSummaryNe: String? = null,
    @ColumnInfo(name = "altitude_growth_notes_ne") val altitudeGrowthNotesNe: String? = null,
    @ColumnInfo(name = "suitable_zones_csv") val suitableZonesCsv: String? = null,
    @ColumnInfo(name = "suitable_regions_ne") val suitableRegionsNe: String? = null,

    @ColumnInfo(name = "soil_types") val soilTypesNe: String? = null,
    @ColumnInfo(name = "soil_texture_ne") val soilTextureNe: String? = null,
    @ColumnInfo(name = "soil_ph_min") val soilPhMin: Double? = null,
    @ColumnInfo(name = "soil_ph_max") val soilPhMax: Double? = null,
    @ColumnInfo(name = "soil_drainage_ne") val soilDrainageNe: String? = null,
    @ColumnInfo(name = "soil_fertility_ne") val soilFertilityNe: String? = null,
    @ColumnInfo(name = "soil_organic_matter_ne") val soilOrganicMatterNe: String? = null,

    @ColumnInfo(name = "temp_min") val tempMin: Double? = null,
    @ColumnInfo(name = "temp_opt_min") val tempOptimalMin: Double? = null,
    @ColumnInfo(name = "temp_opt_max") val tempOptimalMax: Double? = null,
    @ColumnInfo(name = "temp_max") val tempMax: Double? = null,
    @ColumnInfo(name = "humidity_min") val humidityMin: Double? = null,
    @ColumnInfo(name = "humidity_max") val humidityMax: Double? = null,
    @ColumnInfo(name = "rainfall_min_mm") val rainfallMinMm: Int? = null,
    @ColumnInfo(name = "rainfall_max_mm") val rainfallMaxMm: Int? = null,
    @ColumnInfo(name = "rainfall_period_label_ne") val rainfallPeriodLabelNe: String? = null,
    @ColumnInfo(name = "water_requirement") val waterRequirementNe: String? = null,
    @ColumnInfo(name = "sunlight") val sunlightNe: String? = null,

    @ColumnInfo(name = "planting_season") val plantingSeasonNe: String? = null,
    @ColumnInfo(name = "sowing_period_ne") val sowingPeriodNe: String? = null,
    @ColumnInfo(name = "transplanting_period_ne") val transplantingPeriodNe: String? = null,
    @ColumnInfo(name = "growing_duration_ne") val growingDurationNe: String? = null,
    @ColumnInfo(name = "harvesting_period_ne") val harvestingPeriodNe: String? = null,
    @ColumnInfo(name = "irrigation_management") val irrigationGuidanceNe: String? = null,
    @ColumnInfo(name = "special_considerations_ne") val specialConsiderationsNe: String? = null,

    @ColumnInfo(name = "last_updated_date") val lastUpdatedDate: String? = null
)

@Entity(
    tableName = "crops",
    indices = [
        Index(value = ["category_id"]),
        Index(value = ["is_published"])
    ]
)
data class BakaiyaCropEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "category_id") val categoryId: String,
    @ColumnInfo(name = "name_ne") val nameNe: String,
    @ColumnInfo(name = "name_en") val nameEn: String,
    @ColumnInfo(name = "scientific_name") val scientificName: String,
    @ColumnInfo(name = "description_ne") val descriptionNe: String,
    @ColumnInfo(name = "varieties") val varieties: String?,
    @ColumnInfo(name = "climate_overview") val climateOverview: String?,
    @Embedded val growingConditions: GrowingConditionsEmbedded,

    // Cultivation Sections
    @ColumnInfo(name = "land_preparation") val landPreparation: String?,
    @ColumnInfo(name = "seed_selection") val seedSelection: String?,
    @ColumnInfo(name = "spacing") val spacing: String?,
    @ColumnInfo(name = "fertilizer_management") val fertilizer: String?,
    @ColumnInfo(name = "crop_care") val cropCare: String?,
    @ColumnInfo(name = "pest_disease_control") val pestDiseaseControl: String?,
    @ColumnInfo(name = "harvesting") val harvesting: String?,
    @ColumnInfo(name = "storage") val storage: String?,

    // Data Provenance
    @ColumnInfo(name = "source_reference") val sourceReference: String,
    @ColumnInfo(name = "is_verified_content") val isVerifiedContent: Boolean = true,
    @ColumnInfo(name = "thumbnail_url") val thumbnailUrl: String? = null,
    @ColumnInfo(name = "is_published") val isPublished: Boolean = true
)

/**
 * Variety-specific growing condition entry accommodating multiple varieties per crop.
 */
@Entity(
    tableName = "crop_variety_requirements",
    indices = [Index(value = ["crop_id"])]
)
data class CropVarietyRequirementEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "crop_id") val cropId: String,
    @ColumnInfo(name = "crop_name_ne") val cropNameNe: String,
    @ColumnInfo(name = "crop_name_en") val cropNameEn: String,
    @ColumnInfo(name = "variety_name_ne") val varietyNameNe: String,
    @ColumnInfo(name = "variety_name_en") val varietyNameEn: String? = null,
    @ColumnInfo(name = "notes_ne") val notesNe: String? = null,
    @ColumnInfo(name = "source_reference") val sourceReference: String,
    @Embedded val growingConditions: GrowingConditionsEmbedded
)

@Entity(
    tableName = "crop_images",
    indices = [Index(value = ["crop_id"])]
)
data class CropImageEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "crop_id") val cropId: String,
    @ColumnInfo(name = "image_url") val imageUrl: String,
    @ColumnInfo(name = "caption_ne") val captionNe: String,
    @ColumnInfo(name = "stage_category") val stageCategory: String,
    @ColumnInfo(name = "is_sample_image") val isSampleImage: Boolean = false
)

@Entity(
    tableName = "educational_videos",
    indices = [
        Index(value = ["crop_id"]),
        Index(value = ["topic_id"])
    ]
)
data class EducationalVideoEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "crop_id") val cropId: String,
    @ColumnInfo(name = "topic_id") val topicId: Int,
    @ColumnInfo(name = "title_ne") val titleNe: String,
    @ColumnInfo(name = "description_ne") val descriptionNe: String,
    @ColumnInfo(name = "channel_name") val channelName: String,
    @ColumnInfo(name = "thumbnail_url") val thumbnailUrl: String,
    @ColumnInfo(name = "video_url") val videoUrl: String,
    @ColumnInfo(name = "duration") val duration: String? = null,
    @ColumnInfo(name = "provider") val provider: String = "youtube",
    @ColumnInfo(name = "download_permission") val downloadPermission: Boolean = false,
    @ColumnInfo(name = "downloadable_file_url") val downloadableFileUrl: String? = null,
    @ColumnInfo(name = "local_file_path") val localFilePath: String? = null,
    @ColumnInfo(name = "file_size_bytes") val fileSizeBytes: Long? = null,
    @ColumnInfo(name = "is_downloaded") val isDownloaded: Boolean = false,
    @ColumnInfo(name = "is_sample_media") val isSampleMedia: Boolean = false
)

@Entity(tableName = "helpline_contacts")
data class HelplineContactEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "name_ne") val nameNe: String,
    @ColumnInfo(name = "designation_ne") val designationNe: String,
    @ColumnInfo(name = "ward_ne") val wardNe: String,
    @ColumnInfo(name = "ward_number") val wardNumber: Int,
    @ColumnInfo(name = "mobile_number") val mobileNumber: String,
    @ColumnInfo(name = "email") val email: String? = null,
    @ColumnInfo(name = "is_verified") val isVerified: Boolean = true
)

@Entity(tableName = "farmer_registration")
data class FarmerRegistrationEntity(
    @PrimaryKey val id: String = "primary_farmer",
    @ColumnInfo(name = "full_name") val fullName: String,
    @ColumnInfo(name = "municipality") val municipality: String = "बकैया गाउँपालिका, मकवानपुर",
    @ColumnInfo(name = "ward_number") val wardNumber: Int,
    @ColumnInfo(name = "phone_number") val phoneNumber: String,
    @ColumnInfo(name = "registered_at") val registeredAt: Long = System.currentTimeMillis()
)
