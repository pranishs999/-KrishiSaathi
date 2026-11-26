package com.krishisaathi.app.domain.model

/**
 * Livestock domain models for Bakaiya Krishi Saathi पशुपालन module.
 * Maps directly to Supabase schema tables (migration 003/004).
 */

data class AnimalCategory(
    val id: String,
    val nameNe: String,
    val nameEn: String,
    val descriptionNe: String? = null,
    val iconName: String? = null,
    val sortOrder: Int = 0
)

data class Animal(
    val id: String,
    val categoryId: String,
    val nameNe: String,
    val nameEn: String,
    val scientificName: String? = null,
    val speciesCode: String,
    val summaryNe: String,
    val purposeTagsNe: String? = null,
    val bakaiyaRelevanceNe: String? = null,
    val climateSuitabilityNe: String? = null,
    val altitudeRangeNe: String? = null,
    val recommendedBreedsSummaryNe: String? = null,
    val safetyNoticeNe: String = "पशु स्वास्थ्य वा औषधि मात्रा सम्बन्धी अन्तिम निर्णय नजिकको पशु सेवा प्राविधिक वा पशु चिकित्सकको प्रत्यक्ष जाँचपछि मात्र लिनुहोस्।",
    val sourceReference: String = "",
    val thumbnailUrl: String? = null,
    val reviewStatus: String = "draft",
    val isVerifiedContent: Boolean = false,
    val isPublished: Boolean = false,
    val sortOrder: Int = 0
)

data class AnimalBreed(
    val id: String,
    val animalId: String,
    val breedNameNe: String,
    val breedNameEn: String? = null,
    val originType: String, // indigenous, improved, crossbred, exotic
    val purposeNe: String,
    val climateSuitabilityNe: String? = null,
    val characteristicsNe: String,
    val notesNe: String? = null,
    val sourceReference: String,
    val sortOrder: Int = 0
)

data class AnimalHousingGuidance(
    val id: String,
    val animalId: String,
    val shedDesignNe: String,
    val floorSpaceStandardNe: String? = null,
    val ventilationLightNe: String,
    val beddingDrainageNe: String,
    val sanitationRoutineNe: String,
    val biosecurityNotesNe: String,
    val sourceReference: String
)

data class AnimalFeedingGuidance(
    val id: String,
    val animalId: String,
    val greenFodderForageNe: String,
    val dryRoughageNe: String,
    val concentrateFeedNe: String? = null,
    val mineralSaltNe: String? = null,
    val cleanWaterNe: String,
    val rationQuantityNoteNe: String? = null,
    val feedingFrequencyNe: String,
    val sourceReference: String
)

data class AnimalBreedingGuidance(
    val id: String,
    val animalId: String,
    val breedingAgeWeightNe: String? = null,
    val estrusHeatSignsNe: String? = null,
    val matingAiGuidanceNe: String? = null,
    val pregnancyIncubationPeriodNe: String? = null,
    val pregnantCareNe: String,
    val birthingHatchingCareNe: String,
    val newbornChickFryCareNe: String,
    val weaningBroodingNe: String? = null,
    val sourceReference: String
)

data class AnimalHealthGuidance(
    val id: String,
    val animalId: String,
    val dailyHygieneNe: String,
    val biosecurityIsolationNe: String,
    val vaccinationAwarenessNe: String,
    val dewormingParasiteControlNe: String,
    val warningSignsNe: String,
    val vetReferralGuidanceNe: String,
    val sourceReference: String
)

data class AnimalDisease(
    val id: String,
    val animalId: String,
    val nameNe: String,
    val nameEn: String? = null,
    val pathogenType: String? = null,
    val symptomsNe: String,
    val preventionBiosecurityNe: String,
    val immediateFarmerActionNe: String,
    val veterinaryCautionNe: String,
    val sourceReference: String,
    val sortOrder: Int = 0
)

data class AnimalProductionGuidance(
    val id: String,
    val animalId: String,
    val primaryProductsNe: String,
    val cleanProductionPracticeNe: String,
    val postHarvestHandlingNe: String,
    val manureWasteManagementNe: String,
    val marketingNotesNe: String? = null,
    val sourceReference: String
)

data class AnimalImage(
    val id: String,
    val animalId: String,
    val imageUrl: String,
    val mediumUrl: String? = null,
    val thumbUrl: String? = null,
    val captionNe: String,
    val stageCategory: String = "general",
    val creatorName: String? = null,
    val sourceUrl: String? = null,
    val licenseType: String? = null,
    val sortOrder: Int = 0
)

data class AnimalContentSection(
    val id: String,
    val animalId: String,
    val sectionNumber: Int,
    val sectionKey: String,
    val titleNe: String,
    val titleEn: String,
    val bodyNe: String? = null,
    val bodyEn: String? = null,
    val isApplicable: Boolean = true,
    val sourceReference: String = "",
    val reviewStatus: String = "draft",
    val isPublished: Boolean = false,
    val sortOrder: Int = 0
)

data class VideoPlaylist(
    val id: String,
    val targetType: String, // "crop", "animal", "general"
    val cropId: String? = null,
    val animalId: String? = null,
    val titleNe: String,
    val titleEn: String,
    val descriptionNe: String? = null
)

/**
 * Publication lifecycle status (applies to crops, animals, sections, media).
 */
enum class PublicationStatus(val id: String, val labelNe: String) {
    DRAFT("draft", "मस्यौदा"),
    NEEDS_SOURCE("needs_source", "स्रोत आवश्यक"),
    IN_REVIEW("in_review", "समीक्षामा"),
    APPROVED("approved", "स्वीकृत"),
    PUBLISHED("published", "प्रकाशित"),
    NEEDS_REREVIEW("needs_rereview", "पुन: समीक्षा"),
    ARCHIVED("archived", "अभिलेखित");

    companion object {
        fun fromId(id: String): PublicationStatus? = entries.find { it.id == id }
    }
}
