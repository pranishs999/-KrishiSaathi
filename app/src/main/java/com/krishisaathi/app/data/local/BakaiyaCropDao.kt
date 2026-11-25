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
    @Query("SELECT * FROM crop_categories ORDER BY name_ne ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertCategories(categories: List<CategoryEntity>): List<Long>

    // Crops
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

    // Images
    @Query("SELECT * FROM crop_images WHERE crop_id = :cropId")
    fun getImagesForCrop(cropId: String): Flow<List<CropImageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertImages(images: List<CropImageEntity>): List<Long>

    // Educational Videos
    @Query("SELECT * FROM educational_videos WHERE crop_id = :cropId ORDER BY topic_id ASC")
    fun getVideosForCrop(cropId: String): Flow<List<EducationalVideoEntity>>

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

    @Query("DELETE FROM farmer_registration")
    fun clearFarmerRegistration(): Int
}
