package com.krishisaathi.app.data.remote

import com.krishisaathi.app.domain.model.FarmerRegistration
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

data class RemoteFarmerProfileSnapshot(
    val fullName: String? = null,
    val phoneNumber: String? = null,
    val municipality: String? = null,
    val wardNumber: Int? = null,
    val toleVillage: String? = null
)

sealed interface RegistrationUploadResult {
    data object Success : RegistrationUploadResult
    data object AlreadyRegistered : RegistrationUploadResult
    data class ServerError(val message: String) : RegistrationUploadResult
    data class NetworkError(val exception: Exception) : RegistrationUploadResult
}

@Serializable
private data class SupabaseFarmerProfilePayload(
    val phone: String,
    @SerialName("full_name") val fullName: String,
    @SerialName("mpin_hash") val mpinHash: String = "bakaiya_app_no_auth",
    val municipality: String,
    @SerialName("ward_number") val wardNumber: Int,
    @SerialName("tole_village") val toleVillage: String = "",
    @SerialName("consent_status") val consentStatus: Boolean = false,
    @SerialName("registered_at") val registeredAt: Long = 0
)

// ---- Remote Content Sync DTOs (strictly matching Supabase public schema) ----

@Serializable
data class SupabaseCropCategoryDto(
    val id: String,
    @SerialName("name_ne") val nameNe: String,
    @SerialName("name_en") val nameEn: String,
    @SerialName("icon_name") val iconName: String? = null,
    @SerialName("thumbnail_url") val thumbnailUrl: String? = null,
    @SerialName("sort_order") val sortOrder: Int = 0
)

@Serializable
data class SupabaseCropDto(
    val id: String,
    @SerialName("category_id") val categoryId: String? = null,
    @SerialName("name_ne") val nameNe: String,
    @SerialName("name_en") val nameEn: String,
    @SerialName("scientific_name") val scientificName: String,
    @SerialName("description_ne") val descriptionNe: String,
    val varieties: String? = null,
    @SerialName("climate_overview") val climateOverview: String? = null,
    @SerialName("altitude_min_m") val altitudeMinM: Int? = null,
    @SerialName("altitude_opt_min_m") val altitudeOptMinM: Int? = null,
    @SerialName("altitude_opt_max_m") val altitudeOptMaxM: Int? = null,
    @SerialName("altitude_max_m") val altitudeMaxM: Int? = null,
    @SerialName("suitable_altitude") val suitableAltitude: String? = null,
    @SerialName("altitude_growth_notes_ne") val altitudeGrowthNotesNe: String? = null,
    @SerialName("suitable_zones_csv") val suitableZonesCsv: String? = null,
    @SerialName("suitable_regions_ne") val suitableRegionsNe: String? = null,
    @SerialName("soil_types") val soilTypes: String? = null,
    @SerialName("soil_texture_ne") val soilTextureNe: String? = null,
    @SerialName("soil_ph_min") val soilPhMin: Double? = null,
    @SerialName("soil_ph_max") val soilPhMax: Double? = null,
    @SerialName("soil_drainage_ne") val soilDrainageNe: String? = null,
    @SerialName("soil_fertility_ne") val soilFertilityNe: String? = null,
    @SerialName("soil_organic_matter_ne") val soilOrganicMatterNe: String? = null,
    @SerialName("temp_min") val tempMin: Double? = null,
    @SerialName("temp_opt_min") val tempOptMin: Double? = null,
    @SerialName("temp_opt_max") val tempOptMax: Double? = null,
    @SerialName("temp_max") val tempMax: Double? = null,
    @SerialName("humidity_min") val humidityMin: Double? = null,
    @SerialName("humidity_max") val humidityMax: Double? = null,
    @SerialName("rainfall_min_mm") val rainfallMinMm: Int? = null,
    @SerialName("rainfall_max_mm") val rainfallMaxMm: Int? = null,
    @SerialName("rainfall_period_label_ne") val rainfallPeriodLabelNe: String? = null,
    @SerialName("water_requirement") val waterRequirement: String? = null,
    val sunlight: String? = null,
    @SerialName("planting_season") val plantingSeason: String? = null,
    @SerialName("sowing_period_ne") val sowingPeriodNe: String? = null,
    @SerialName("transplanting_period_ne") val transplantingPeriodNe: String? = null,
    @SerialName("growing_duration_ne") val growingDurationNe: String? = null,
    @SerialName("harvesting_period_ne") val harvestingPeriodNe: String? = null,
    @SerialName("irrigation_management") val irrigationManagement: String? = null,
    @SerialName("special_considerations_ne") val specialConsiderationsNe: String? = null,
    @SerialName("land_preparation") val landPreparation: String? = null,
    @SerialName("seed_selection") val seedSelection: String? = null,
    val spacing: String? = null,
    @SerialName("fertilizer_management") val fertilizerManagement: String? = null,
    @SerialName("crop_care") val cropCare: String? = null,
    @SerialName("pest_disease_control") val pestDiseaseControl: String? = null,
    val harvesting: String? = null,
    val storage: String? = null,
    @SerialName("source_reference") val sourceReference: String = "",
    @SerialName("is_verified_content") val isVerifiedContent: Boolean = true,
    @SerialName("thumbnail_url") val thumbnailUrl: String? = null,
    @SerialName("is_published") val isPublished: Boolean = true,
    @SerialName("last_updated_date") val lastUpdatedDate: String? = null
)

@Serializable
data class SupabaseCropVarietyDto(
    val id: String,
    @SerialName("crop_id") val cropId: String,
    @SerialName("crop_name_ne") val cropNameNe: String,
    @SerialName("crop_name_en") val cropNameEn: String,
    @SerialName("variety_name_ne") val varietyNameNe: String,
    @SerialName("variety_name_en") val varietyNameEn: String? = null,
    @SerialName("notes_ne") val notesNe: String? = null,
    @SerialName("source_reference") val sourceReference: String = "",
    @SerialName("altitude_min_m") val altitudeMinM: Int? = null,
    @SerialName("altitude_opt_min_m") val altitudeOptMinM: Int? = null,
    @SerialName("altitude_opt_max_m") val altitudeOptMaxM: Int? = null,
    @SerialName("altitude_max_m") val altitudeMaxM: Int? = null,
    @SerialName("suitable_altitude") val suitableAltitude: String? = null,
    @SerialName("suitable_zones_csv") val suitableZonesCsv: String? = null,
    @SerialName("suitable_regions_ne") val suitableRegionsNe: String? = null,
    @SerialName("planting_season") val plantingSeason: String? = null,
    @SerialName("transplanting_period_ne") val transplantingPeriodNe: String? = null,
    @SerialName("growing_duration_ne") val growingDurationNe: String? = null,
    @SerialName("last_updated_date") val lastUpdatedDate: String? = null
)

@Serializable
data class SupabaseCropImageDto(
    val id: String,
    @SerialName("crop_id") val cropId: String,
    @SerialName("image_url") val imageUrl: String,
    @SerialName("caption_ne") val captionNe: String,
    @SerialName("stage_category") val stageCategory: String,
    @SerialName("is_sample_image") val isSampleImage: Boolean = false
)

@Serializable
data class SupabaseEducationalVideoDto(
    val id: String,
    @SerialName("crop_id") val cropId: String,
    @SerialName("topic_id") val topicId: Int,
    @SerialName("title_ne") val titleNe: String,
    @SerialName("description_ne") val descriptionNe: String,
    @SerialName("channel_name") val channelName: String,
    @SerialName("thumbnail_url") val thumbnailUrl: String,
    @SerialName("video_url") val videoUrl: String,
    val duration: String? = null,
    val provider: String = "youtube",
    @SerialName("download_permission") val downloadPermission: Boolean = false,
    @SerialName("downloadable_file_url") val downloadableFileUrl: String? = null,
    @SerialName("is_sample_media") val isSampleMedia: Boolean = false
)

@Serializable
data class SupabaseHelplineDto(
    val id: String,
    @SerialName("name_ne") val nameNe: String,
    @SerialName("designation_ne") val designationNe: String,
    @SerialName("ward_ne") val wardNe: String,
    @SerialName("ward_number") val wardNumber: Int = 0,
    @SerialName("mobile_number") val mobileNumber: String,
    val email: String? = null,
    @SerialName("is_verified") val isVerified: Boolean = true
)

@Serializable
data class SupabaseAnimalCategoryDto(
    val id: String,
    @SerialName("name_ne") val nameNe: String,
    @SerialName("name_en") val nameEn: String,
    @SerialName("description_ne") val descriptionNe: String? = null,
    @SerialName("icon_name") val iconName: String? = null,
    @SerialName("thumbnail_url") val thumbnailUrl: String? = null,
    @SerialName("sort_order") val sortOrder: Int = 0
)

@Serializable
data class SupabaseAnimalDto(
    val id: String,
    @SerialName("category_id") val categoryId: String,
    @SerialName("name_ne") val nameNe: String,
    @SerialName("name_en") val nameEn: String,
    @SerialName("scientific_name") val scientificName: String? = null,
    @SerialName("species_code") val speciesCode: String,
    @SerialName("summary_ne") val summaryNe: String,
    @SerialName("purpose_tags_ne") val purposeTagsNe: String? = null,
    @SerialName("bakaiya_relevance_ne") val bakaiyaRelevanceNe: String? = null,
    @SerialName("climate_suitability_ne") val climateSuitabilityNe: String? = null,
    @SerialName("altitude_range_ne") val altitudeRangeNe: String? = null,
    @SerialName("recommended_breeds_summary_ne") val recommendedBreedsSummaryNe: String? = null,
    @SerialName("safety_notice_ne") val safetyNoticeNe: String = "",
    @SerialName("source_reference") val sourceReference: String = "",
    @SerialName("thumbnail_url") val thumbnailUrl: String? = null,
    @SerialName("review_status") val reviewStatus: String = "published",
    @SerialName("is_verified_content") val isVerifiedContent: Boolean = true,
    @SerialName("is_published") val isPublished: Boolean = true,
    @SerialName("sort_order") val sortOrder: Int = 0
)

@Serializable
data class SupabaseAnimalBreedDto(
    val id: String,
    @SerialName("animal_id") val animalId: String,
    @SerialName("breed_name_ne") val breedNameNe: String,
    @SerialName("breed_name_en") val breedNameEn: String? = null,
    @SerialName("origin_type") val originType: String,
    @SerialName("purpose_ne") val purposeNe: String,
    @SerialName("climate_suitability_ne") val climateSuitabilityNe: String? = null,
    @SerialName("characteristics_ne") val characteristicsNe: String,
    @SerialName("notes_ne") val notesNe: String? = null,
    @SerialName("source_reference") val sourceReference: String = "",
    @SerialName("sort_order") val sortOrder: Int = 0
)

@Serializable
data class SupabaseAnimalHousingDto(
    val id: String,
    @SerialName("animal_id") val animalId: String,
    @SerialName("shed_design_ne") val shedDesignNe: String,
    @SerialName("floor_space_standard_ne") val floorSpaceStandardNe: String? = null,
    @SerialName("ventilation_light_ne") val ventilationLightNe: String,
    @SerialName("bedding_drainage_ne") val beddingDrainageNe: String,
    @SerialName("sanitation_routine_ne") val sanitationRoutineNe: String,
    @SerialName("biosecurity_notes_ne") val biosecurityNotesNe: String,
    @SerialName("source_reference") val sourceReference: String = ""
)

@Serializable
data class SupabaseAnimalFeedingDto(
    val id: String,
    @SerialName("animal_id") val animalId: String,
    @SerialName("green_fodder_forage_ne") val greenFodderForageNe: String,
    @SerialName("dry_roughage_ne") val dryRoughageNe: String,
    @SerialName("concentrate_feed_ne") val concentrateFeedNe: String? = null,
    @SerialName("mineral_salt_ne") val mineralSaltNe: String? = null,
    @SerialName("clean_water_ne") val cleanWaterNe: String,
    @SerialName("ration_quantity_note_ne") val rationQuantityNoteNe: String? = null,
    @SerialName("feeding_frequency_ne") val feedingFrequencyNe: String,
    @SerialName("source_reference") val sourceReference: String = ""
)

@Serializable
data class SupabaseAnimalHealthDto(
    val id: String,
    @SerialName("animal_id") val animalId: String,
    @SerialName("daily_hygiene_ne") val dailyHygieneNe: String,
    @SerialName("biosecurity_isolation_ne") val biosecurityIsolationNe: String,
    @SerialName("vaccination_awareness_ne") val vaccinationAwarenessNe: String,
    @SerialName("deworming_parasite_control_ne") val dewormingParasiteControlNe: String,
    @SerialName("warning_signs_ne") val warningSignsNe: String,
    @SerialName("vet_referral_guidance_ne") val vetReferralGuidanceNe: String,
    @SerialName("source_reference") val sourceReference: String = ""
)

@Serializable
data class SupabaseAnimalDiseaseDto(
    val id: String,
    @SerialName("animal_id") val animalId: String,
    @SerialName("name_ne") val nameNe: String,
    @SerialName("name_en") val nameEn: String? = null,
    @SerialName("pathogen_type") val pathogenType: String? = null,
    @SerialName("symptoms_ne") val symptomsNe: String,
    @SerialName("prevention_biosecurity_ne") val preventionBiosecurityNe: String,
    @SerialName("immediate_farmer_action_ne") val immediateFarmerActionNe: String,
    @SerialName("veterinary_caution_ne") val veterinaryCautionNe: String,
    @SerialName("source_reference") val sourceReference: String = "",
    @SerialName("sort_order") val sortOrder: Int = 0
)

@Serializable
data class SupabaseAnimalImageDto(
    val id: String,
    @SerialName("animal_id") val animalId: String,
    @SerialName("image_url") val imageUrl: String,
    @SerialName("medium_url") val mediumUrl: String? = null,
    @SerialName("thumb_url") val thumbUrl: String? = null,
    @SerialName("caption_ne") val captionNe: String,
    @SerialName("stage_category") val stageCategory: String = "general",
    @SerialName("creator_name") val creatorName: String? = null,
    @SerialName("source_url") val sourceUrl: String? = null,
    @SerialName("license_type") val licenseType: String? = null,
    @SerialName("sort_order") val sortOrder: Int = 0
)

data class RemoteSyncBundle(
    val cropCategories: List<SupabaseCropCategoryDto> = emptyList(),
    val crops: List<SupabaseCropDto> = emptyList(),
    val cropVarieties: List<SupabaseCropVarietyDto> = emptyList(),
    val cropImages: List<SupabaseCropImageDto> = emptyList(),
    val videos: List<SupabaseEducationalVideoDto> = emptyList(),
    val helplineContacts: List<SupabaseHelplineDto> = emptyList(),
    val animalCategories: List<SupabaseAnimalCategoryDto> = emptyList(),
    val animals: List<SupabaseAnimalDto> = emptyList(),
    val animalBreeds: List<SupabaseAnimalBreedDto> = emptyList(),
    val animalHousing: List<SupabaseAnimalHousingDto> = emptyList(),
    val animalFeeding: List<SupabaseAnimalFeedingDto> = emptyList(),
    val animalHealth: List<SupabaseAnimalHealthDto> = emptyList(),
    val animalDiseases: List<SupabaseAnimalDiseaseDto> = emptyList(),
    val animalImages: List<SupabaseAnimalImageDto> = emptyList()
)

/**
 * Coordinates all Supabase PostgREST queries & farmer registration sync.
 */
open class RemoteContentSyncClient(
    private val supabaseClient: SupabaseClient?
) {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    open val isSupabaseConfigured: Boolean
        get() = supabaseClient != null

    /**
     * Fetches ALL published agricultural, livestock, media, and helpline datasets directly from Supabase.
     */
    open suspend fun fetchAllRemoteContent(): Result<RemoteSyncBundle> = withContext(Dispatchers.IO) {
        val client = supabaseClient
            ?: return@withContext Result.failure(IllegalStateException("Supabase client unavailable"))
        try {
            val cropCategories = decodeTable<SupabaseCropCategoryDto>(client, "crop_categories")
            val crops = decodeTable<SupabaseCropDto>(client, "crops")
            val cropVarieties = decodeTable<SupabaseCropVarietyDto>(client, "crop_variety_requirements")
            val cropImages = decodeTable<SupabaseCropImageDto>(client, "crop_images")
            val videos = decodeTable<SupabaseEducationalVideoDto>(client, "educational_videos")
            val helpline = decodeTable<SupabaseHelplineDto>(client, "helpline_contacts")
            val animalCategories = decodeTable<SupabaseAnimalCategoryDto>(client, "animal_categories")
            val animals = decodeTable<SupabaseAnimalDto>(client, "animals")
            val animalBreeds = decodeTable<SupabaseAnimalBreedDto>(client, "animal_breeds")
            val animalHousing = decodeTable<SupabaseAnimalHousingDto>(client, "animal_housing_guidance")
            val animalFeeding = decodeTable<SupabaseAnimalFeedingDto>(client, "animal_feeding_guidance")
            val animalHealth = decodeTable<SupabaseAnimalHealthDto>(client, "animal_health_guidance")
            val animalDiseases = decodeTable<SupabaseAnimalDiseaseDto>(client, "animal_diseases")
            val animalImages = decodeTable<SupabaseAnimalImageDto>(client, "animal_images")

            Result.success(
                RemoteSyncBundle(
                    cropCategories = cropCategories,
                    crops = crops,
                    cropVarieties = cropVarieties,
                    cropImages = cropImages,
                    videos = videos,
                    helplineContacts = helpline,
                    animalCategories = animalCategories,
                    animals = animals,
                    animalBreeds = animalBreeds,
                    animalHousing = animalHousing,
                    animalFeeding = animalFeeding,
                    animalHealth = animalHealth,
                    animalDiseases = animalDiseases,
                    animalImages = animalImages
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend inline fun <reified T> decodeTable(
        client: SupabaseClient,
        table: String
    ): List<T> {
        val res = client.from(table).select()
        return json.decodeFromString(res.data)
    }

    open suspend fun uploadFarmerRegistration(registration: FarmerRegistration): RegistrationUploadResult =
        withContext(Dispatchers.IO) {
            when (val edgeOutcome = tryEdgeFunctionRegistration(registration)) {
                is EdgeAttemptResult.Done -> edgeOutcome.result
                EdgeAttemptResult.FunctionNotDeployed -> tryDirectPostgrestUpsert(registration)
            }
        }

    private sealed interface EdgeAttemptResult {
        data class Done(val result: RegistrationUploadResult) : EdgeAttemptResult
        data object FunctionNotDeployed : EdgeAttemptResult
    }

    private fun tryEdgeFunctionRegistration(registration: FarmerRegistration): EdgeAttemptResult {
        val client = supabaseClient ?: return EdgeAttemptResult.Done(
            RegistrationUploadResult.NetworkError(IllegalStateException("Supabase client unavailable"))
        )

        return try {
            val supabaseUrl = client.supabaseUrl.trimEnd('/')
            val url = URL("$supabaseUrl/functions/v1/register-farmer")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 15_000
                readTimeout = 15_000
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Authorization", "Bearer ${client.supabaseKey}")
                setRequestProperty("apikey", client.supabaseKey)
            }

            val payload = JSONObject().apply {
                put("full_name", registration.fullName)
                put("phone", registration.phoneNumber)
                put("municipality", registration.municipality)
                put("ward_number", registration.wardNumber)
                put("tole_village", registration.toleVillage)
                put("consent_status", registration.consentStatus)
                put("registered_at", registration.registeredAt)
            }

            OutputStreamWriter(conn.outputStream, Charsets.UTF_8).use { writer ->
                writer.write(payload.toString())
                writer.flush()
            }

            val responseCode = conn.responseCode
            val responseBody = try {
                val stream = if (responseCode in 200..299) conn.inputStream else conn.errorStream
                BufferedReader(InputStreamReader(stream, Charsets.UTF_8)).use { it.readText() }
            } catch (_: Exception) {
                ""
            } finally {
                conn.disconnect()
            }

            when {
                responseCode in 200..201 -> {
                    val jsonObj = JSONObject(responseBody)
                    if (jsonObj.optBoolean("success", false)) {
                        EdgeAttemptResult.Done(RegistrationUploadResult.Success)
                    } else {
                        EdgeAttemptResult.Done(
                            RegistrationUploadResult.ServerError(
                                jsonObj.optString("error", "Unknown edge function error")
                            )
                        )
                    }
                }
                responseCode == 409 -> EdgeAttemptResult.Done(RegistrationUploadResult.AlreadyRegistered)
                responseCode == 404 -> EdgeAttemptResult.FunctionNotDeployed
                else -> EdgeAttemptResult.Done(
                    RegistrationUploadResult.ServerError("Edge Function HTTP $responseCode: $responseBody")
                )
            }
        } catch (e: Exception) {
            EdgeAttemptResult.Done(RegistrationUploadResult.NetworkError(e))
        }
    }

    private suspend fun tryDirectPostgrestUpsert(registration: FarmerRegistration): RegistrationUploadResult {
        val client = supabaseClient ?: return RegistrationUploadResult.NetworkError(
            IllegalStateException("Supabase client unavailable")
        )
        return try {
            val payload = SupabaseFarmerProfilePayload(
                phone = registration.phoneNumber,
                fullName = registration.fullName,
                municipality = registration.municipality,
                wardNumber = registration.wardNumber,
                toleVillage = registration.toleVillage,
                consentStatus = registration.consentStatus,
                registeredAt = registration.registeredAt
            )
            client.from("farmer_profiles").upsert(payload) {
                onConflict = "phone"
            }
            RegistrationUploadResult.Success
        } catch (e: Exception) {
            val msg = e.message.orEmpty()
            if (msg.contains("409") || msg.contains("duplicate", ignoreCase = true)) {
                RegistrationUploadResult.AlreadyRegistered
            } else if (msg.contains("401") || msg.contains("403") || msg.contains("400") || msg.contains("422")) {
                RegistrationUploadResult.ServerError("PostgREST rejected farmer_profiles upsert: $msg")
            } else {
                RegistrationUploadResult.NetworkError(e)
            }
        }
    }

    open suspend fun fetchFarmerProfileByPhone(phoneNumber: String): RemoteFarmerProfileSnapshot? =
        withContext(Dispatchers.IO) {
            val client = supabaseClient ?: return@withContext null
            try {
                val raw = client.from("farmer_profiles").select {
                    filter {
                        eq("phone", phoneNumber)
                    }
                    limit(1)
                }.data
                val arr = JSONArray(raw)
                if (arr.length() == 0) return@withContext null
                val obj = arr.getJSONObject(0)
                RemoteFarmerProfileSnapshot(
                    fullName = obj.optString("full_name").takeIf { it.isNotBlank() },
                    phoneNumber = obj.optString("phone").takeIf { it.isNotBlank() },
                    municipality = obj.optString("municipality").takeIf { it.isNotBlank() },
                    wardNumber = if (obj.has("ward_number") && !obj.isNull("ward_number")) obj.optInt("ward_number") else null,
                    toleVillage = obj.optString("tole_village").takeIf { it.isNotBlank() }
                )
            } catch (_: Exception) {
                null
            }
        }
}
