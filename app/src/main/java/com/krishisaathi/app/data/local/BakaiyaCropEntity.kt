package com.krishisaathi.app.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "crop_categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "name_ne") val nameNe: String,
    @ColumnInfo(name = "name_en") val nameEn: String,
    @ColumnInfo(name = "icon_name") val iconName: String? = null
)

@Entity(tableName = "crops")
data class BakaiyaCropEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "category_id") val categoryId: String,
    @ColumnInfo(name = "name_ne") val nameNe: String,
    @ColumnInfo(name = "name_en") val nameEn: String,
    @ColumnInfo(name = "scientific_name") val scientificName: String,
    @ColumnInfo(name = "description_ne") val descriptionNe: String,
    @ColumnInfo(name = "varieties") val varieties: String?,
    @ColumnInfo(name = "planting_season") val plantingSeason: String?,
    @ColumnInfo(name = "climate_overview") val climateOverview: String?,
    @ColumnInfo(name = "suitable_altitude") val altitude: String?,

    // Environmental Requirements
    @ColumnInfo(name = "temp_min") val tempMin: Double?,
    @ColumnInfo(name = "temp_max") val tempMax: Double?,
    @ColumnInfo(name = "humidity_min") val humidityMin: Double?,
    @ColumnInfo(name = "humidity_max") val humidityMax: Double?,
    @ColumnInfo(name = "soil_types") val soilTypes: String?,
    @ColumnInfo(name = "soil_ph_min") val soilPhMin: Double?,
    @ColumnInfo(name = "soil_ph_max") val soilPhMax: Double?,
    @ColumnInfo(name = "sunlight") val sunlight: String?,
    @ColumnInfo(name = "water_requirement") val waterRequirement: String?,

    // Cultivation Sections
    @ColumnInfo(name = "land_preparation") val landPreparation: String?,
    @ColumnInfo(name = "seed_selection") val seedSelection: String?,
    @ColumnInfo(name = "spacing") val spacing: String?,
    @ColumnInfo(name = "fertilizer_management") val fertilizer: String?,
    @ColumnInfo(name = "irrigation_management") val irrigation: String?,
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

@Entity(tableName = "crop_images")
data class CropImageEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "crop_id") val cropId: String,
    @ColumnInfo(name = "image_url") val imageUrl: String,
    @ColumnInfo(name = "caption_ne") val captionNe: String,
    @ColumnInfo(name = "stage_category") val stageCategory: String,
    @ColumnInfo(name = "is_sample_image") val isSampleImage: Boolean = false
)

@Entity(tableName = "educational_videos")
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
    @ColumnInfo(name = "ward_number") val wardNumber: Int,
    @ColumnInfo(name = "phone_number") val phoneNumber: String
)

/**
 * Simple farm income/expense record for "फार्मको हिसाब कसरी राख्ने?" feature.
 * Local-only Room storage, no Supabase sync for this prototype.
 */
@Entity(tableName = "farm_records")
data class FarmRecordEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "type") val type: String, // "income" or "expense"
    @ColumnInfo(name = "category_ne") val categoryNe: String,
    @ColumnInfo(name = "amount") val amount: Double,
    @ColumnInfo(name = "description_ne") val descriptionNe: String,
    @ColumnInfo(name = "crop_name_ne") val cropNameNe: String? = null,
    @ColumnInfo(name = "date_millis") val dateMillis: Long,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)
