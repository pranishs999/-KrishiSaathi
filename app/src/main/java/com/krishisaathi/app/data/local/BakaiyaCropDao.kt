package com.krishisaathi.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BakaiyaCropDao {

    // Categories
    @Query("SELECT * FROM crop_categories ORDER BY sort_order ASC, name_ne ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertCategories(categories: List<CategoryEntity>): List<Long>

    @Query("DELETE FROM crop_categories")
    fun clearCategories(): Int

    // Crops
    @Query("SELECT COUNT(*) FROM crops WHERE is_published = 1")
    fun countPublishedCropsSync(): Int

    @Query("SELECT * FROM crops WHERE is_published = 1 ORDER BY name_ne ASC")
    fun getAllCrops(): Flow<List<BakaiyaCropEntity>>

    @Query("SELECT * FROM crops WHERE id = :cropId LIMIT 1")
    fun getCropById(cropId: String): Flow<BakaiyaCropEntity?>

    @Query("SELECT * FROM crops WHERE category_id = :categoryId AND is_published = 1 ORDER BY name_ne ASC")
    fun getCropsByCategory(categoryId: String): Flow<List<BakaiyaCropEntity>>

    @Query("SELECT * FROM crops WHERE is_published = 1 AND (name_ne LIKE '%' || :query || '%' OR name_en LIKE '%' || :query || '%' OR scientific_name LIKE '%' || :query || '%') ORDER BY name_ne ASC")
    fun searchCrops(query: String): Flow<List<BakaiyaCropEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertCrops(crops: List<BakaiyaCropEntity>): List<Long>

    @Query("DELETE FROM crops")
    fun clearCrops(): Int

    // Variety-Specific Growing Conditions
    @Query("SELECT * FROM crop_variety_requirements WHERE crop_id = :cropId ORDER BY variety_name_ne ASC")
    fun getVarietyRequirementsForCrop(cropId: String): Flow<List<CropVarietyRequirementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertVarietyRequirements(varieties: List<CropVarietyRequirementEntity>): List<Long>

    @Query("DELETE FROM crop_variety_requirements")
    fun clearVarietyRequirements(): Int

    // Crop Images
    @Query("SELECT * FROM crop_images WHERE crop_id = :cropId")
    fun getImagesForCrop(cropId: String): Flow<List<CropImageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertImages(images: List<CropImageEntity>): List<Long>

    @Query("DELETE FROM crop_images")
    fun clearImages(): Int

    // Educational Videos (crop + livestock)
    @Query("SELECT * FROM educational_videos WHERE crop_id = :cropId ORDER BY topic_id ASC")
    fun getVideosForCrop(cropId: String): Flow<List<EducationalVideoEntity>>

    @Query("SELECT * FROM educational_videos WHERE crop_id = :cropId ORDER BY topic_id ASC")
    fun getVideosForTargetSync(cropId: String): List<EducationalVideoEntity>

    @Query("SELECT * FROM educational_videos WHERE crop_id = :cropId AND topic_id = :topicId")
    fun getVideosForCropAndTopic(cropId: String, topicId: Int): Flow<List<EducationalVideoEntity>>

    @Query("SELECT * FROM educational_videos WHERE is_downloaded = 1")
    fun getDownloadedVideos(): Flow<List<EducationalVideoEntity>>

    @Query("SELECT * FROM educational_videos WHERE id = :videoId LIMIT 1")
    fun getVideoById(videoId: String): Flow<EducationalVideoEntity?>

    @Query("SELECT * FROM educational_videos WHERE id = :videoId LIMIT 1")
    fun findVideoByIdSync(videoId: String): EducationalVideoEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertVideosIgnoreConflict(videos: List<EducationalVideoEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertVideos(videos: List<EducationalVideoEntity>): List<Long>

    @Query("DELETE FROM educational_videos WHERE is_downloaded = 0")
    fun clearNonDownloadedVideos(): Int

    @Update
    fun updateVideo(video: EducationalVideoEntity): Int

    @Query("UPDATE educational_videos SET is_downloaded = 0, local_file_path = NULL, file_size_bytes = NULL WHERE id = :videoId")
    fun markVideoDeleted(videoId: String): Int

    // Helpline Contacts
    @Query("SELECT * FROM helpline_contacts ORDER BY ward_number ASC, name_ne ASC")
    fun getAllHelplineContacts(): Flow<List<HelplineContactEntity>>

    @Query("SELECT * FROM helpline_contacts WHERE ward_number = :wardNumber ORDER BY name_ne ASC")
    fun getContactsByWard(wardNumber: Int): Flow<List<HelplineContactEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertContacts(contacts: List<HelplineContactEntity>): List<Long>

    @Query("DELETE FROM helpline_contacts")
    fun clearHelplineContacts(): Int

    // Farmer Registration
    @Query("SELECT * FROM farmer_registration LIMIT 1")
    fun getFarmerRegistration(): Flow<FarmerRegistrationEntity?>

    @Query("SELECT * FROM farmer_registration LIMIT 1")
    fun findFarmerRegistrationSync(): FarmerRegistrationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveFarmerRegistration(registration: FarmerRegistrationEntity): Long

    @Query("UPDATE farmer_registration SET sync_status = :status WHERE id = 'primary_farmer'")
    fun updateRegistrationSyncStatus(status: String): Int

    @Query("DELETE FROM farmer_registration")
    fun clearFarmerRegistration(): Int

    // ---- Livestock Room Queries (strictly populated from Supabase sync) ----
    @Query("SELECT * FROM animal_categories ORDER BY sort_order ASC, name_ne ASC")
    fun getAllAnimalCategoriesSync(): List<AnimalCategoryEntity>

    @Query("DELETE FROM animal_categories")
    fun clearAnimalCategories(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertAnimalCategories(categories: List<AnimalCategoryEntity>): List<Long>

    @Query("SELECT * FROM animals WHERE is_published = 1 ORDER BY sort_order ASC, name_ne ASC")
    fun getPublishedAnimalsSync(): List<AnimalEntity>

    @Query("SELECT * FROM animals WHERE category_id = :categoryId AND is_published = 1 ORDER BY sort_order ASC, name_ne ASC")
    fun getPublishedAnimalsByCategorySync(categoryId: String): List<AnimalEntity>

    @Query("SELECT * FROM animals WHERE id = :animalId AND is_published = 1 LIMIT 1")
    fun getPublishedAnimalByIdSync(animalId: String): AnimalEntity?

    @Query("DELETE FROM animals")
    fun clearAnimals(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertAnimals(animals: List<AnimalEntity>): List<Long>

    @Query("SELECT * FROM animal_breeds WHERE animal_id = :animalId ORDER BY sort_order ASC")
    fun getBreedsForAnimalSync(animalId: String): List<AnimalBreedEntity>

    @Query("DELETE FROM animal_breeds")
    fun clearAnimalBreeds(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertAnimalBreeds(breeds: List<AnimalBreedEntity>): List<Long>

    @Query("SELECT * FROM animal_housing_guidance WHERE animal_id = :animalId LIMIT 1")
    fun getHousingForAnimalSync(animalId: String): AnimalHousingEntity?

    @Query("DELETE FROM animal_housing_guidance")
    fun clearAnimalHousing(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertAnimalHousing(list: List<AnimalHousingEntity>): List<Long>

    @Query("SELECT * FROM animal_feeding_guidance WHERE animal_id = :animalId LIMIT 1")
    fun getFeedingForAnimalSync(animalId: String): AnimalFeedingEntity?

    @Query("DELETE FROM animal_feeding_guidance")
    fun clearAnimalFeeding(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertAnimalFeeding(list: List<AnimalFeedingEntity>): List<Long>

    @Query("SELECT * FROM animal_health_guidance WHERE animal_id = :animalId LIMIT 1")
    fun getHealthForAnimalSync(animalId: String): AnimalHealthEntity?

    @Query("DELETE FROM animal_health_guidance")
    fun clearAnimalHealth(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertAnimalHealth(list: List<AnimalHealthEntity>): List<Long>

    @Query("SELECT * FROM animal_diseases WHERE animal_id = :animalId ORDER BY sort_order ASC")
    fun getDiseasesForAnimalSync(animalId: String): List<AnimalDiseaseEntity>

    @Query("DELETE FROM animal_diseases")
    fun clearAnimalDiseases(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertAnimalDiseases(list: List<AnimalDiseaseEntity>): List<Long>

    @Query("SELECT * FROM animal_images WHERE animal_id = :animalId ORDER BY sort_order ASC")
    fun getImagesForAnimalSync(animalId: String): List<AnimalImageEntity>

    @Query("DELETE FROM animal_images")
    fun clearAnimalImages(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertAnimalImages(list: List<AnimalImageEntity>): List<Long>
}
