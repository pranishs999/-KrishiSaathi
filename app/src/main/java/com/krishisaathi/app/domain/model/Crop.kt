package com.krishisaathi.app.domain.model

/**
 * 8 Supported Topic-Based Educational Video Playlists (Section 8).
 */
enum class VideoTopic(val id: Int, val titleNe: String) {
    SEED_SELECTION(1, "बीउ छनोट तथा तयारी"),
    LAND_PREP_PLANTING(2, "जग्गा तयारी तथा रोपाइँ"),
    FERTILIZER_MANAGEMENT(3, "मलखाद व्यवस्थापन"),
    IRRIGATION_MANAGEMENT(4, "सिँचाइ व्यवस्थापन"),
    PEST_DISEASE_CONTROL(5, "रोग तथा कीरा नियन्त्रण"),
    CROP_CARE(6, "बालीको हेरचाह"),
    HARVESTING(7, "कटानी गर्ने तरिका"),
    YIELD_AND_STORAGE(8, "उत्पादन तथा भण्डारण");

    companion object {
        fun fromId(id: Int): VideoTopic? = entries.find { it.id == id }
    }
}

data class CropCategory(
    val id: String,
    val nameNe: String,
    val nameEn: String,
    val iconName: String?
)

data class BakaiyaCrop(
    val id: String,
    val categoryId: String,
    val nameNe: String,
    val nameEn: String,
    val scientificName: String,
    val descriptionNe: String,
    val varieties: String?,
    val plantingSeason: String?,
    val climateOverview: String?,
    val altitude: String?,

    // Environmental Requirements (nullable so missing/unverified values are never invented)
    val tempMin: Double?,
    val tempMax: Double?,
    val humidityMin: Double?,
    val humidityMax: Double?,
    val soilTypes: String?,
    val soilPhMin: Double?,
    val soilPhMax: Double?,
    val sunlight: String?,
    val waterRequirement: String?,

    // Cultivation Instructions
    val landPreparation: String?,
    val seedSelection: String?,
    val spacing: String?,
    val fertilizer: String?,
    val irrigation: String?,
    val cropCare: String?,
    val pestDiseaseControl: String?,
    val harvesting: String?,
    val storage: String?,

    // Data Integrity & Provenance
    val sourceReference: String,
    val isVerifiedContent: Boolean,
    val thumbnailUrl: String?,
    val isPublished: Boolean
)

data class CropImage(
    val id: String,
    val cropId: String,
    val imageUrl: String,
    val captionNe: String,
    val stageCategory: String,
    val isSampleImage: Boolean
)

data class EducationalVideo(
    val id: String,
    val cropId: String,
    val topicId: Int,
    val titleNe: String,
    val descriptionNe: String,
    val channelName: String,
    val thumbnailUrl: String,
    val videoUrl: String,
    val duration: String?,
    val provider: String,
    val downloadPermission: Boolean,
    val downloadableFileUrl: String?,
    val localFilePath: String?,
    val fileSizeBytes: Long?,
    val isDownloaded: Boolean,
    val isSampleMedia: Boolean
)

data class HelplineContact(
    val id: String,
    val nameNe: String,
    val designationNe: String,
    val wardNe: String,
    val wardNumber: Int,
    val mobileNumber: String,
    val email: String?,
    val isVerified: Boolean
)

data class FarmerRegistration(
    val fullName: String,
    val wardNumber: Int,
    val phoneNumber: String
)
