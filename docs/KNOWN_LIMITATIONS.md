# Known Limitations & Boundaries

1. **No Weather, Market Prices, or Farm Bookkeeping:** Explicitly excluded from product scope (`फार्म हिसाब` was removed cleanly via `MIGRATION_5_6`).
2. **Under Construction Pages:** Both **Profile (`मेरो विवरण`)** and **Farmer Helpline (`किसान हेल्पलाइन`)** display clean Under Construction placeholders until official municipality contacts directory UI and farmer profile editing are approved. Locally registered farmer details remain stored in Room.
3. **Sample Offline Download Media:** `vid-paddy-sample-offline` and `vid-maize-sample-offline` use Google sample bucket MP4 files flagged `isSampleMedia = true` strictly to demonstrate offline HTTP download/resume and Media3 ExoPlayer playback without violating YouTube terms of service.
4. **Supabase Content Sync:** `BakaiyaCropRepositoryImpl.syncRemoteContent()` currently refreshes local Room seed data (`KrishiSaathiDatabase.seedDatabase`). Live PostgREST delta fetching requires configuring Supabase credentials in `local.properties`.
5. **External App Dependency for YouTube:** Opening YouTube channels/videos requires a browser or YouTube app on the device; errors are surfaced safely in Nepali when absent.
