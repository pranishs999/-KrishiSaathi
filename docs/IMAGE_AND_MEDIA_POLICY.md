# Image & Media Asset Policy

## 1. Centralized Media Library (`media_assets`)
All crop and livestock photographs and educational videos are cataloged in a single centralized registry tracking:
- Stable asset ID, asset type (`image` / `video`), MIME type, file size (bytes), and pixel dimensions (`width` x `height`).
- Three derivative URLs for images: `full_url`, `medium_url`, and `thumb_url` (generated on upload via Pillow in `admin_web/`).
- Alt text (English), farmer-facing Nepali caption (`caption_ne`), creator attribution, original source URL, SPDX/CC license tag, and explicit `permission_status` (`approved` / `pending` / `rejected`).

## 2. Verified Media Rules
1. **Licensing Gate:** Images with missing creator, source, or `permission_status != 'approved'` are strictly blocked from publication and never appear in the farmer app.
2. **Authentic Field & Species Photography:**
   - Only genuine photographs representing the exact crop or animal species/breed are permitted.
   - AI-generated images must never be labeled as genuine field photographs.
   - Any development sample test video (`ForBiggerBlazes.mp4`, `ForBiggerEscapes.mp4`) carries `is_sample_media = true` with an explicit Nepali disclosure.
3. **Derivative Sizing for Android Bandwidth & RAM Conservation:**
   - List thumbnails load `thumb` / `medium` URLs via Coil (`ImageLoaderFactory` with 15% memory cache & 25 MB disk cache).
   - Full-resolution viewer opens `medium` / `full` URLs cleanly without loading multi-megabyte originals into list viewports.
4. **YouTube vs Offline Download Separation:**
   - YouTube educational videos (`provider = 'youtube'`, `download_permission = false`) open externally in YouTube / Web Browser via `YouTubeLauncher`; streams are never scraped or downloaded.
   - Only explicitly authorized MP4 media (`download_permission = true`, HTTPS only) can be downloaded to `context.filesDir/authorized_videos` and played offline via Media3 ExoPlayer with canonical path validation.

## 3. Audit Remediation Note (2026-11-26)
Live verification against `commons.wikimedia.org/w/api.php` detected three missing crop image files in prior seed data (`Rice_plants_(IRRI).jpg`, `Zea_mays_crop.jpg`, `Mustard_field_in_Nepal.jpg`) and an outdated hash prefix on `Ginger_plant.jpg`. All crop and livestock images have now been replaced with live-verified, CC-licensed Wikimedia Commons assets with recorded creators and licenses.
