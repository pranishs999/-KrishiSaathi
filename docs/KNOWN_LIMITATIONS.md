# Known Limitations & Boundaries

1. **No Live Weather or Market Prices:** Explicitly excluded from product scope.
2. **Helpline Coverage by Ward:** Official `bakaiyakrishi.gov.np` public records provide central Agriculture Development Branch and Chief Administrative Office contacts; individual ward technician phone numbers are unavailable and intentionally omitted rather than invented.
3. **Sample Offline Download Media:** `vid-paddy-sample-offline` and `vid-maize-sample-offline` use Google sample bucket MP4 files flagged `isSampleMedia = true` strictly to demonstrate offline HTTP download/resume and Media3 ExoPlayer playback without violating YouTube terms of service.
4. **Supabase Content Sync:** `BakaiyaCropRepositoryImpl.syncRemoteContent()` currently refreshes local Room seed data (`KrishiSaathiDatabase.seedDatabase`). Live PostgREST delta fetching and two-way sync for `farm_records` require configuring live Supabase credentials in `local.properties` and wiring PostgREST DTO calls.
5. **Room Schema Upgrades:** `KrishiSaathiDatabase` uses `.fallbackToDestructiveMigration()` at version 5 during prototype iteration. Production Play Store releases must add explicit `Migration(5, 6)` paths to retain farmer-entered `farm_records` and downloaded video paths across updates.
6. **External App Dependency for YouTube & Dialing:** Opening YouTube channels/videos or dialing helpline numbers requires a browser/YouTube app or phone dialer on the device; errors are surfaced safely in Nepali when absent.
