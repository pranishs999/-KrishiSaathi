# Offline-First Storage & Synchronization

## 1. Local Single Source of Truth (Room Database v5)
`KrishiSaathiDatabase` (`bakaiya_krishi_saathi_database`) stores all data locally in SQLite so every core workflow functions without internet access:
- Pre-populated on database creation via `RoomDatabase.Callback.onCreate` -> `seedDatabase(dao)`.
- Uses `insertVideosIgnoreConflict` during re-seeding so locally downloaded video file paths and sizes are preserved across syncs.
- Exposes reactive `Flow` streams across all screens via `BakaiyaCropRepositoryImpl`.

## 2. Offline Video Download & Verification
- YouTube videos are NEVER downloaded (`downloadPermission = false`). They open exclusively in the genuine YouTube app or browser via `YouTubeLauncher`.
- Explicitly permitted HTTPS `.mp4` assets (`downloadPermission = true`) download to `context.filesDir/educational_videos/` via `VideoDownloadManager`.
- Reads through `getDownloadedVideos()` and `getCropVideos()` verify `File(localFilePath).exists()`; missing files on disk automatically revert `isDownloaded` to `false` in domain mapping.
- Playback runs completely offline using AndroidX Media3 ExoPlayer (`VideoPlayerScreen`).

## 3. Local Farm Accounting (`farm_records`)
- Income and expense entries created in `FarmRecordsScreen` are persisted directly to the `farm_records` Room table (`FarmRecordEntity`) and reactively aggregate totals (`SUM(amount)`).

## 4. Background Sync Worker (`SyncWorker`)
- `SyncWorker` invokes `appContainer.bakaiyaCropRepository.syncRemoteContent()` via AndroidX WorkManager, updating `DataStoreManager.LAST_SYNC_TIMESTAMP`. Supabase REST integration hooks live in `SupabaseClientProvider` (`app/build.gradle.kts` `BuildConfig.SUPABASE_URL` / `SUPABASE_ANON_KEY`).
