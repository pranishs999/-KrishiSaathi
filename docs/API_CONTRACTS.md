# API & Layer Contracts

## 1. Domain Repository (`BakaiyaCropRepository`)
Located at `app/src/main/java/com/krishisaathi/app/domain/repository/BakaiyaCropRepository.kt`:

- **Crop Discovery:**
  - `getCategories(): Flow<List<CropCategory>>`
  - `getCrops(): Flow<List<BakaiyaCrop>>`
  - `getCropById(cropId: String): Flow<BakaiyaCrop?>`
  - `getCropsByCategory(categoryId: String): Flow<List<BakaiyaCrop>>`
  - `searchCrops(query: String): Flow<List<BakaiyaCrop>>`
  - `getCropImages(cropId: String): Flow<List<CropImage>>`
- **Educational Video & Offline Media:**
  - `getCropVideos(cropId: String): Flow<List<EducationalVideo>>`
  - `getVideosForTopic(cropId: String, topicId: Int): Flow<List<EducationalVideo>>`
  - `getDownloadedVideos(): Flow<List<EducationalVideo>>` (filters to files physically present on disk)
  - `getVideoById(videoId: String): Flow<EducationalVideo?>`
  - `markVideoDownloaded(videoId: String, localPath: String, sizeBytes: Long): Result<Unit>` (enforces `downloadPermission == true`)
  - `deleteDownloadedVideo(videoId: String): Result<Unit>`
- **Helpline & Registration:**
  - `getHelplineContacts(): Flow<List<HelplineContact>>`
  - `getContactsByWard(wardNumber: Int): Flow<List<HelplineContact>>`
  - `getFarmerRegistration(): Flow<FarmerRegistration?>`
  - `saveFarmerRegistration(name: String, ward: Int, phone: String): Result<Unit>`
  - `clearFarmerRegistration(): Result<Unit>`
- **Farm Records (फार्मको हिसाब):**
  - `getAllFarmRecords(): Flow<List<FarmRecord>>`
  - `getFarmRecordsByType(type: FarmRecordType): Flow<List<FarmRecord>>`
  - `getTotalIncome(): Flow<Double>`
  - `getTotalExpense(): Flow<Double>`
  - `addFarmRecord(record: FarmRecord): Result<Unit>` (rejects blank category or non-positive amount)
  - `deleteFarmRecord(recordId: String): Result<Unit>`
- **Sync:**
  - `syncRemoteContent(): Result<Unit>`

## 2. Media & External Intent Contracts
- **`YouTubeLauncher`**:
  - `openYouTubeVideo(context, videoUrl): YouTubeLaunchResult` — enforces `ValidationUtils.isValidYouTubeUrl` and launches `Intent.ACTION_VIEW` with safe error handling.
  - `openYouTubeChannel(context, channelUrl): YouTubeLaunchResult` — enforces `ValidationUtils.isValidYouTubeChannelUrl` and opens verified `@handle` channel URLs.
- **`VideoDownloadManager`**:
  - Enforces `downloadPermission && ValidationUtils.isAuthorizedDownloadUrl(...)`.
  - Supports HTTP Range resume, `.part` temporary downloads with atomic rename, storage space check (`StatFs`), pause/resume/delete, and `StateFlow<Map<String, VideoDownloadState>>`.

## 3. Supabase PostgreSQL Schema (`supabase/migrations/`)
`001_initial_schema.sql` mirrors Room v5 tables: `crop_categories`, `crops`, `crop_images`, `educational_videos`, `helpline_contacts`, `farmer_registration`, `farm_records`, and `content_sync_metadata` with indexes and RLS policies.
