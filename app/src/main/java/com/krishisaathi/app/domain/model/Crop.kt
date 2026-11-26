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

/**
 * Standard ecological suitability zones across Nepal's topography.
 */
enum class NepalEcologicalZone(
    val id: String,
    val nameNe: String,
    val nameEn: String,
    val elevationNoteNe: String
) {
    TERAI("terai", "तराई", "Terai", "६० - ३०० मि."),
    INNER_TERAI("inner_terai", "भित्री मधेस", "Inner Terai", "१५० - ६०० मि."),
    MID_HILL("mid_hill", "मध्य पहाड", "Mid-Hill", "६०० - २००० मि."),
    HIGH_HILL("high_hill", "उच्च पहाड", "High-Hill", "२००० - ३००० मि."),
    HIMALAYAN("himalayan", "हिमाली क्षेत्र", "Himalayan", "३०००+ मि.");

    companion object {
        fun parseList(csv: String?): List<NepalEcologicalZone> {
            if (csv.isNullOrBlank()) return emptyList()
            return csv.split(",").mapNotNull { token ->
                val trimmed = token.trim()
                entries.find { it.id.equals(trimmed, ignoreCase = true) }
            }
        }
    }
}

/**
 * Structured, reusable environmental and agricultural requirements model.
 * Uses nullable fields for unverified metrics instead of inventing placeholder defaults,
 * and distinguishes optimal ranges from minimum/maximum tolerance.
 */
data class CropGrowingCondition(
    // A. Altitude / Elevation (meters above sea level)
    val altitudeMinMeters: Int? = null,
    val altitudeOptimalMinMeters: Int? = null,
    val altitudeOptimalMaxMeters: Int? = null,
    val altitudeMaxMeters: Int? = null,
    val altitudeSummaryNe: String? = null,
    val altitudeGrowthNotesNe: String? = null,

    // B. Nepal Geographical Suitability
    val suitableZones: List<NepalEcologicalZone> = emptyList(),
    val suitableRegionsNe: String? = null,

    // C. Soil Requirements
    val soilTypesNe: String? = null,
    val soilTextureNe: String? = null,
    val soilPhMin: Double? = null,
    val soilPhMax: Double? = null,
    val soilDrainageNe: String? = null,
    val soilFertilityNe: String? = null,
    val soilOrganicMatterNe: String? = null,

    // D. Climate Requirements
    val tempMin: Double? = null,
    val tempOptimalMin: Double? = null,
    val tempOptimalMax: Double? = null,
    val tempMax: Double? = null,
    val humidityMin: Double? = null,
    val humidityMax: Double? = null,
    val rainfallMinMm: Int? = null,
    val rainfallMaxMm: Int? = null,
    val rainfallPeriodLabelNe: String? = null,
    val waterRequirementNe: String? = null,
    val sunlightNe: String? = null,

    // E. Cultivation & Timing Information
    val plantingSeasonNe: String? = null,
    val sowingPeriodNe: String? = null,
    val transplantingPeriodNe: String? = null,
    val growingDurationNe: String? = null,
    val harvestingPeriodNe: String? = null,
    val irrigationGuidanceNe: String? = null,
    val specialConsiderationsNe: String? = null,

    // F. Source & Reliability
    val sourceReference: String = "",
    val lastUpdatedDate: String? = null
)

data class CropVarietyRequirement(
    val id: String,
    val cropId: String,
    val cropNameNe: String,
    val cropNameEn: String,
    val varietyNameNe: String,
    val varietyNameEn: String?,
    val notesNe: String? = null,
    val growingConditions: CropGrowingCondition
)

data class CropCategory(
    val id: String,
    val nameNe: String,
    val nameEn: String,
    val iconName: String?,
    val thumbnailUrl: String? = null
)

data class BakaiyaCrop(
    val id: String,
    val categoryId: String,
    val nameNe: String,
    val nameEn: String,
    val scientificName: String,
    val descriptionNe: String,
    val varieties: String?,
    val climateOverview: String?,
    val growingConditions: CropGrowingCondition,

    // Cultivation Instructions
    val landPreparation: String?,
    val seedSelection: String?,
    val spacing: String?,
    val fertilizer: String?,
    val cropCare: String?,
    val pestDiseaseControl: String?,
    val harvesting: String?,
    val storage: String?,

    // Data Integrity & Provenance
    val sourceReference: String,
    val isVerifiedContent: Boolean,
    val thumbnailUrl: String?,
    val isPublished: Boolean
) {
    // Convenience accessors preserving compatibility across list/home screens
    val plantingSeason: String? get() = growingConditions.plantingSeasonNe
    val altitude: String? get() = growingConditions.altitudeSummaryNe
    val tempMin: Double? get() = growingConditions.tempMin
    val tempMax: Double? get() = growingConditions.tempMax
    val humidityMin: Double? get() = growingConditions.humidityMin
    val humidityMax: Double? get() = growingConditions.humidityMax
    val soilTypes: String? get() = growingConditions.soilTypesNe
    val soilPhMin: Double? get() = growingConditions.soilPhMin
    val soilPhMax: Double? get() = growingConditions.soilPhMax
    val sunlight: String? get() = growingConditions.sunlightNe
    val waterRequirement: String? get() = growingConditions.waterRequirementNe
    val irrigation: String? get() = growingConditions.irrigationGuidanceNe
}

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

object BakaiyaMunicipality {
    const val DEFAULT_NAME = "बकैया गाउँपालिका, मकवानपुर"
    val WARDS: List<Int> = (1..12).toList()
}

data class FarmerRegistration(
    val fullName: String,
    val municipality: String = BakaiyaMunicipality.DEFAULT_NAME,
    val wardNumber: Int,
    val phoneNumber: String,
    val registeredAt: Long = 0L
)
