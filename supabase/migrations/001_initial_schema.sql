-- BAKAIYA KRISHI SAATHI (बकैया कृषि साथी) - DATABASE SCHEMA
-- Migration: 001_initial_schema.sql
-- PostgreSQL 15+ / Supabase
-- ============================================================================
-- Aligned with Room entity structure used in the Android app.

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ----------------------------------------------------------------------------
-- 1. CROP CATEGORIES
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.crop_categories (
    id TEXT PRIMARY KEY,
    name_ne TEXT NOT NULL,
    name_en TEXT NOT NULL,
    icon_name TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ----------------------------------------------------------------------------
-- 2. CROPS (Bakaiya agricultural knowledge content)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.crops (
    id TEXT PRIMARY KEY,
    category_id TEXT REFERENCES public.crop_categories(id) ON DELETE SET NULL,
    name_ne TEXT NOT NULL,
    name_en TEXT NOT NULL,
    scientific_name TEXT NOT NULL,
    description_ne TEXT NOT NULL,
    varieties TEXT,
    planting_season TEXT,
    climate_overview TEXT,
    suitable_altitude TEXT,

    -- Environmental Requirements & Growing Conditions
    altitude_min_m INT,
    altitude_opt_min_m INT,
    altitude_opt_max_m INT,
    altitude_max_m INT,
    altitude_growth_notes_ne TEXT,
    suitable_zones_csv TEXT,
    suitable_regions_ne TEXT,
    temp_min DOUBLE PRECISION,
    temp_opt_min DOUBLE PRECISION,
    temp_opt_max DOUBLE PRECISION,
    temp_max DOUBLE PRECISION,
    humidity_min DOUBLE PRECISION,
    humidity_max DOUBLE PRECISION,
    rainfall_min_mm INT,
    rainfall_max_mm INT,
    rainfall_period_label_ne TEXT,
    soil_types TEXT,
    soil_texture_ne TEXT,
    soil_ph_min DOUBLE PRECISION,
    soil_ph_max DOUBLE PRECISION,
    soil_drainage_ne TEXT,
    soil_fertility_ne TEXT,
    soil_organic_matter_ne TEXT,
    sunlight TEXT,
    water_requirement TEXT,
    sowing_period_ne TEXT,
    transplanting_period_ne TEXT,
    growing_duration_ne TEXT,
    harvesting_period_ne TEXT,
    special_considerations_ne TEXT,
    last_updated_date TEXT,

    -- Cultivation Sections
    land_preparation TEXT,
    seed_selection TEXT,
    spacing TEXT,
    fertilizer_management TEXT,
    irrigation_management TEXT,
    crop_care TEXT,
    pest_disease_control TEXT,
    harvesting TEXT,
    storage TEXT,

    -- Data Provenance
    source_reference TEXT NOT NULL,
    is_verified_content BOOLEAN NOT NULL DEFAULT TRUE,
    thumbnail_url TEXT,
    is_published BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ----------------------------------------------------------------------------
-- 2B. VARIETY-SPECIFIC GROWING CONDITIONS
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.crop_variety_requirements (
    id TEXT PRIMARY KEY,
    crop_id TEXT NOT NULL REFERENCES public.crops(id) ON DELETE CASCADE,
    crop_name_ne TEXT NOT NULL,
    crop_name_en TEXT NOT NULL,
    variety_name_ne TEXT NOT NULL,
    variety_name_en TEXT,
    notes_ne TEXT,
    source_reference TEXT NOT NULL,
    altitude_min_m INT,
    altitude_opt_min_m INT,
    altitude_opt_max_m INT,
    altitude_max_m INT,
    suitable_altitude TEXT,
    suitable_zones_csv TEXT,
    suitable_regions_ne TEXT,
    planting_season TEXT,
    transplanting_period_ne TEXT,
    growing_duration_ne TEXT,
    last_updated_date TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_crop_variety_crop ON public.crop_variety_requirements (crop_id);
ALTER TABLE public.crop_variety_requirements ENABLE ROW LEVEL SECURITY;
CREATE POLICY "Public can view crop variety requirements" ON public.crop_variety_requirements FOR SELECT USING (true);
CREATE POLICY "Allow all on crop_variety_requirements" ON public.crop_variety_requirements FOR ALL USING (true);

-- ----------------------------------------------------------------------------
-- 3. CROP IMAGES
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.crop_images (
    id TEXT PRIMARY KEY,
    crop_id TEXT NOT NULL REFERENCES public.crops(id) ON DELETE CASCADE,
    image_url TEXT NOT NULL,
    caption_ne TEXT NOT NULL,
    stage_category TEXT NOT NULL,
    is_sample_image BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ----------------------------------------------------------------------------
-- 4. EDUCATIONAL VIDEOS
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.educational_videos (
    id TEXT PRIMARY KEY,
    crop_id TEXT NOT NULL REFERENCES public.crops(id) ON DELETE CASCADE,
    topic_id INT NOT NULL,
    title_ne TEXT NOT NULL,
    description_ne TEXT NOT NULL,
    channel_name TEXT NOT NULL,
    thumbnail_url TEXT NOT NULL,
    video_url TEXT NOT NULL,
    duration TEXT,
    provider TEXT NOT NULL DEFAULT 'youtube',
    download_permission BOOLEAN NOT NULL DEFAULT FALSE,
    downloadable_file_url TEXT,
    local_file_path TEXT,
    file_size_bytes BIGINT,
    is_downloaded BOOLEAN NOT NULL DEFAULT FALSE,
    is_sample_media BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ----------------------------------------------------------------------------
-- 5. HELPLINE CONTACTS
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.helpline_contacts (
    id TEXT PRIMARY KEY,
    name_ne TEXT NOT NULL,
    designation_ne TEXT NOT NULL,
    ward_ne TEXT NOT NULL,
    ward_number INT NOT NULL DEFAULT 0,
    mobile_number TEXT NOT NULL,
    email TEXT,
    is_verified BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ----------------------------------------------------------------------------
-- 6. FARMER REGISTRATION (mandatory first-launch registration, no auth)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.farmer_registration (
    id TEXT PRIMARY KEY DEFAULT 'primary_farmer',
    full_name TEXT NOT NULL,
    municipality TEXT NOT NULL DEFAULT 'बकैया गाउँपालिका, मकवानपुर',
    ward_number INT NOT NULL DEFAULT 1,
    phone_number TEXT NOT NULL,
    registered_at BIGINT NOT NULL DEFAULT (EXTRACT(EPOCH FROM NOW()) * 1000)::BIGINT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ----------------------------------------------------------------------------
-- 7. SYNC METADATA
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.content_sync_metadata (
    table_name TEXT PRIMARY KEY,
    last_updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version INT NOT NULL DEFAULT 1
);

-- ----------------------------------------------------------------------------
-- INDEXES
-- ----------------------------------------------------------------------------
CREATE INDEX IF NOT EXISTS idx_crops_name_ne ON public.crops (name_ne);
CREATE INDEX IF NOT EXISTS idx_crops_name_en ON public.crops (name_en);
CREATE INDEX IF NOT EXISTS idx_crops_category ON public.crops (category_id);
CREATE INDEX IF NOT EXISTS idx_crops_published ON public.crops (is_published);
CREATE INDEX IF NOT EXISTS idx_crop_images_crop ON public.crop_images (crop_id);
CREATE INDEX IF NOT EXISTS idx_videos_crop ON public.educational_videos (crop_id);
CREATE INDEX IF NOT EXISTS idx_videos_topic ON public.educational_videos (topic_id);
CREATE INDEX IF NOT EXISTS idx_helpline_ward ON public.helpline_contacts (ward_number);

-- ----------------------------------------------------------------------------
-- ROW LEVEL SECURITY (RLS)
-- ----------------------------------------------------------------------------
ALTER TABLE public.crop_categories ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.crops ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.crop_images ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.educational_videos ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.helpline_contacts ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.farmer_registration ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.content_sync_metadata ENABLE ROW LEVEL SECURITY;

-- Public read access to published agricultural content
CREATE POLICY "Public can view categories" ON public.crop_categories FOR SELECT USING (true);
CREATE POLICY "Public can view published crops" ON public.crops FOR SELECT USING (is_published = true);
CREATE POLICY "Public can view crop images" ON public.crop_images FOR SELECT USING (true);
CREATE POLICY "Public can view educational videos" ON public.educational_videos FOR SELECT USING (true);
CREATE POLICY "Public can view helpline contacts" ON public.helpline_contacts FOR SELECT USING (true);
CREATE POLICY "Public can view sync metadata" ON public.content_sync_metadata FOR SELECT USING (true);

-- Farmer registration (open for first-launch registration without login)
CREATE POLICY "Allow all on farmer_registration" ON public.farmer_registration FOR ALL USING (true);

-- Admin content management
CREATE POLICY "Allow all on categories" ON public.crop_categories FOR ALL USING (true);
CREATE POLICY "Allow all on crops" ON public.crops FOR ALL USING (true);
CREATE POLICY "Allow all on crop_images" ON public.crop_images FOR ALL USING (true);
CREATE POLICY "Allow all on educational_videos" ON public.educational_videos FOR ALL USING (true);
CREATE POLICY "Allow all on helpline_contacts" ON public.helpline_contacts FOR ALL USING (true);
