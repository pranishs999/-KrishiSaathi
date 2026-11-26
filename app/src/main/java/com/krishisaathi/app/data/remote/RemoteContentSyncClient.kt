package com.krishisaathi.app.data.remote

import com.krishisaathi.app.domain.model.FarmerRegistration
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

data class RemoteFarmerProfileSnapshot(
    val fullName: String? = null,
    val phoneNumber: String? = null,
    val municipality: String? = null,
    val wardNumber: Int? = null,
    val toleVillage: String? = null
)

@Serializable
private data class SupabaseFarmerRegistrationPayload(
    val id: String,
    @SerialName("full_name") val fullName: String,
    val municipality: String,
    @SerialName("ward_number") val wardNumber: Int,
    @SerialName("phone_number") val phoneNumber: String,
    @SerialName("registered_at") val registeredAt: Long
)

@Serializable
private data class SupabaseFarmerProfilePayload(
    val phone: String,
    @SerialName("full_name") val fullName: String,
    @SerialName("mpin_hash") val mpinHash: String = "bakaiya_app_no_auth",
    val municipality: String,
    @SerialName("ward_number") val wardNumber: Int
)

/**
 * Coordinates remote published-content and farmer-profile synchronization against the
 * existing Supabase project without embedding private credentials or service keys.
 */
class RemoteContentSyncClient(
    private val supabaseClient: SupabaseClient?,
    private val cmsPublicBaseUrl: String? = null
) {

    val isSupabaseConfigured: Boolean
        get() = supabaseClient != null

    suspend fun uploadFarmerRegistration(registration: FarmerRegistration): Result<Unit> =
        withContext(Dispatchers.IO) {
            val client = supabaseClient
                ?: return@withContext Result.failure(IllegalStateException("Supabase client unavailable"))

            // Primary target on existing Supabase schema: public.farmer_profiles (shared with admin_web)
            val profileUpload = runCatching {
                client.from("farmer_profiles").upsert(
                    SupabaseFarmerProfilePayload(
                        phone = registration.phoneNumber,
                        fullName = registration.fullName,
                        municipality = registration.municipality,
                        wardNumber = registration.wardNumber
                    )
                ) {
                    onConflict = "phone"
                }
            }
            if (profileUpload.isSuccess) {
                return@withContext Result.success(Unit)
            }

            // Secondary target for migration 001/003 deployments: public.farmer_registration
            try {
                val recordId = "reg-${registration.phoneNumber}-${registration.registeredAt}"
                client.from("farmer_registration").insert(
                    SupabaseFarmerRegistrationPayload(
                        id = recordId,
                        fullName = registration.fullName,
                        municipality = registration.municipality,
                        wardNumber = registration.wardNumber,
                        phoneNumber = registration.phoneNumber,
                        registeredAt = registration.registeredAt
                    )
                )
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun fetchFarmerProfileByPhone(phoneNumber: String): RemoteFarmerProfileSnapshot? =
        withContext(Dispatchers.IO) {
            val client = supabaseClient ?: return@withContext null
            val cleanedPhone = phoneNumber.trim()
            if (cleanedPhone.isEmpty()) return@withContext null

            try {
                val res = client.from("farmer_registration").select {
                    filter {
                        eq("phone_number", cleanedPhone)
                    }
                }
                val array = JSONArray(res.data)
                if (array.length() > 0) {
                    val obj = array.getJSONObject(0)
                    return@withContext RemoteFarmerProfileSnapshot(
                        fullName = obj.optString("full_name").takeIf { it.isNotBlank() },
                        phoneNumber = obj.optString("phone_number").takeIf { it.isNotBlank() },
                        municipality = obj.optString("municipality").takeIf { it.isNotBlank() },
                        wardNumber = if (obj.has("ward_number") && !obj.isNull("ward_number")) {
                            obj.optInt("ward_number")
                        } else null,
                        toleVillage = obj.optString("tole_village").takeIf { it.isNotBlank() }
                    )
                }
            } catch (_: Exception) {
                // Fall back cleanly to local stored registration or farmer_profiles check
            }

            try {
                val res = client.from("farmer_profiles").select {
                    filter {
                        eq("phone", cleanedPhone)
                    }
                }
                val array = JSONArray(res.data)
                if (array.length() > 0) {
                    val obj = array.getJSONObject(0)
                    return@withContext RemoteFarmerProfileSnapshot(
                        fullName = obj.optString("full_name").takeIf { it.isNotBlank() },
                        phoneNumber = obj.optString("phone").takeIf { it.isNotBlank() },
                        municipality = obj.optString("municipality").takeIf { it.isNotBlank() },
                        wardNumber = if (obj.has("ward_number") && !obj.isNull("ward_number")) {
                            obj.optInt("ward_number")
                        } else null,
                        toleVillage = obj.optString("tole").takeIf { it.isNotBlank() }
                    )
                }
            } catch (_: Exception) {
                // Offline or table protected by strict RLS: fail safely without crashing UI
            }

            null
        }

    suspend fun fetchPublishedLivestockJson(): Result<String?> = withContext(Dispatchers.IO) {
        try {
            val base = cmsPublicBaseUrl?.trim()?.trimEnd('/')
            if (!base.isNullOrEmpty() && base.startsWith("https://")) {
                val url = URL("$base/api/v1/public/livestock/")
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    connectTimeout = 8_000
                    readTimeout = 8_000
                    setRequestProperty("Accept", "application/json")
                }
                return@withContext conn.useAndRead()
            }

            if (supabaseClient != null) {
                val response = supabaseClient
                    .from("animals")
                    .select {
                        filter {
                            eq("is_published", true)
                        }
                    }
                return@withContext Result.success(response.data)
            }

            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun HttpURLConnection.useAndRead(): Result<String?> {
        return try {
            val code = responseCode
            if (code in 200..299) {
                val payload = inputStream.bufferedReader().use { it.readText() }
                Result.success(payload)
            } else {
                Result.failure(IllegalStateException("HTTP $code from CMS public endpoint"))
            }
        } finally {
            disconnect()
        }
    }
}
