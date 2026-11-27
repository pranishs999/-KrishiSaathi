package com.krishisaathi.app.data.local

import com.krishisaathi.app.data.remote.SupabaseAnimalDiseaseDto
import com.krishisaathi.app.data.remote.SupabaseAnimalDto
import com.krishisaathi.app.data.remote.SupabaseAnimalHealthDto
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LivestockSeedDataIntegrityTest {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Test
    fun supabaseAnimalDtosPreservePublicationAndVerificationFlags() {
        val raw = """
            [
              {
                "id":"animal-goat",
                "category_id":"acat-goat",
                "name_ne":"बाख्रापालन",
                "name_en":"Goat",
                "species_code":"goat",
                "summary_ne":"बाख्रापालन विवरण",
                "safety_notice_ne":"पशु चिकित्सकसँग सल्लाह लिनुहोस्",
                "source_reference":"DLS Nepal",
                "review_status":"published",
                "is_verified_content":true,
                "is_published":true
              },
              {
                "id":"animal-sheep",
                "category_id":"acat-goat",
                "name_ne":"भेडापालन",
                "name_en":"Sheep",
                "species_code":"sheep",
                "summary_ne":"समीक्षाधीन",
                "review_status":"in_review",
                "is_verified_content":false,
                "is_published":false
              }
            ]
        """.trimIndent()

        val decoded = json.decodeFromString<List<SupabaseAnimalDto>>(raw)
        val published = decoded.filter { it.isPublished }
        assertEquals(1, published.size)
        assertEquals("animal-goat", published.first().id)
        assertFalse(decoded.first { it.id == "animal-sheep" }.isPublished)
    }

    @Test
    fun veterinaryGuidanceTextGuardsAgainstUnverifiedPrescriptionDosages() {
        val forbiddenDosageRegex = Regex("\\b\\d+\\s*(mg/kg|ml/kg|iu)\\b", RegexOption.IGNORE_CASE)
        val disease = SupabaseAnimalDiseaseDto(
            id = "dis-1",
            animalId = "animal-goat",
            nameNe = "पी.पी.आर.",
            symptomsNe = "ज्वरो",
            preventionBiosecurityNe = "खोप",
            immediateFarmerActionNe = "अलग राख्ने",
            veterinaryCautionNe = "तुरुन्त पशु चिकित्सकको परामर्श लिनुहोस्।"
        )
        val health = SupabaseAnimalHealthDto(
            id = "health-1",
            animalId = "animal-goat",
            dailyHygieneNe = "सफाइ",
            biosecurityIsolationNe = "अलग्गै राख्ने",
            vaccinationAwarenessNe = "नियमित खोप",
            dewormingParasiteControlNe = "प्राविधिक सल्लाह",
            warningSignsNe = "ज्वरो",
            vetReferralGuidanceNe = "पशु सेवा केन्द्रमा सम्पर्क गर्ने।"
        )

        assertFalse(forbiddenDosageRegex.containsMatchIn(disease.veterinaryCautionNe))
        assertFalse(forbiddenDosageRegex.containsMatchIn(health.vetReferralGuidanceNe))
        assertTrue(health.vetReferralGuidanceNe.isNotBlank())
    }
}
