package com.krishisaathi.app.data.local

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LivestockSeedDataIntegrityTest {

    @Test
    fun allFourAnimalCategoriesExist() {
        assertEquals(4, BakaiyaLivestockSeedData.animalCategories.size)
        val ids = BakaiyaLivestockSeedData.animalCategories.map { it.id }.toSet()
        assertTrue(ids.containsAll(setOf("acat-cattle-buffalo", "acat-goat", "acat-poultry", "acat-pig-fish")))
        BakaiyaLivestockSeedData.animalCategories.forEach { category ->
            assertFalse("Category ${category.id} missing image", category.thumbnailUrl.isNullOrBlank())
        }
    }

    @Test
    fun publishedAnimalsHaveCompleteVerifiedGuidanceAndUnverifiedStayHidden() {
        val categoryIds = BakaiyaLivestockSeedData.animalCategories.map { it.id }.toSet()
        val published = BakaiyaLivestockSeedData.animals.filter { it.isPublished }
        val unpublished = BakaiyaLivestockSeedData.animals.filterNot { it.isPublished }

        assertEquals(6, published.size)
        assertTrue("Expected sheep and duck to remain unpublished", unpublished.size >= 2)
        assertFalse(unpublished.any { it.reviewStatus == "published" })

        published.forEach { animal ->
            assertTrue("Category missing for ${animal.id}", categoryIds.contains(animal.categoryId))
            assertTrue("Nepali name blank for ${animal.id}", animal.nameNe.isNotBlank())
            assertTrue("Source blank for ${animal.id}", animal.sourceReference.isNotBlank())
            assertTrue("Must be verified for ${animal.id}", animal.isVerifiedContent)
            assertEquals("published", animal.reviewStatus)
            assertTrue("Safety notice missing for ${animal.id}", animal.safetyNoticeNe.isNotBlank())

            assertTrue(
                "Breeds missing for ${animal.id}",
                BakaiyaLivestockSeedData.animalBreeds.any { it.animalId == animal.id }
            )
            assertNotNull(
                "Housing missing for ${animal.id}",
                BakaiyaLivestockSeedData.animalHousing.find { it.animalId == animal.id }
            )
            assertNotNull(
                "Feeding missing for ${animal.id}",
                BakaiyaLivestockSeedData.animalFeeding.find { it.animalId == animal.id }
            )
            assertNotNull(
                "Health missing for ${animal.id}",
                BakaiyaLivestockSeedData.animalHealth.find { it.animalId == animal.id }
            )
            assertTrue(
                "Diseases missing for ${animal.id}",
                BakaiyaLivestockSeedData.animalDiseases.any { it.animalId == animal.id }
            )
            assertTrue(
                "Images missing for ${animal.id}",
                BakaiyaLivestockSeedData.animalImages.any { it.animalId == animal.id }
            )
        }
    }

    @Test
    fun livestockGuidanceNeverContainsFabricatedMgOrMlDosages() {
        val forbiddenDosageRegex = Regex("\\b\\d+\\s*(mg/kg|ml/kg|iu)\\b", RegexOption.IGNORE_CASE)
        BakaiyaLivestockSeedData.animalDiseases.forEach { disease ->
            assertFalse(
                "Dosage leakage in ${disease.id}",
                forbiddenDosageRegex.containsMatchIn(disease.veterinaryCautionNe)
            )
        }
        BakaiyaLivestockSeedData.animalHealth.forEach { health ->
            assertFalse(
                "Dosage leakage in ${health.id}",
                forbiddenDosageRegex.containsMatchIn(health.vetReferralGuidanceNe)
            )
        }
    }
}
