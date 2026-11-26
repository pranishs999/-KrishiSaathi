-- ============================================================================
-- BAKAIYA KRISHI SAATHI (बकैया कृषि साथी)
-- Migration: 003_cms_livestock_and_verification.sql
-- Purpose: Complete normalized Crop + Livestock + Multimedia + Verification +
--          RBAC Admin CMS + Strict RLS + Workflow State Enforcement
-- Target: Supabase PostgreSQL 15+
-- ============================================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ----------------------------------------------------------------------------
-- 0. SHARED ENUMS / CHECK CONSTANTS & HELPER FUNCTIONS
-- ----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION public.set_updated_at()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    NEW.updated_at := NOW();
    RETURN NEW;
END;
$$;

-- Enforce workflow transitions & non-self-publication by Content Editors
CREATE OR REPLACE FUNCTION public.enforce_content_workflow()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    v_role TEXT := COALESCE(current_setting('app.admin_role', true), '');
    v_actor TEXT := COALESCE(current_setting('app.admin_actor', true), current_user);
    v_entity_type TEXT := TG_ARGV[0];
BEGIN
    -- Validate workflow state vocabulary
    IF NEW.review_status NOT IN (
        'draft', 'needs_source', 'in_review', 'approved', 'published', 'needs_rereview', 'archived'
    ) THEN
        RAISE EXCEPTION 'Invalid review_status: %', NEW.review_status;
    END IF;

    -- Content Editors must never self-approve or publish directly
    IF v_role = 'content_editor' AND NEW.review_status IN ('approved', 'published') THEN
        IF TG_OP = 'INSERT' OR OLD.review_status IS DISTINCT FROM NEW.review_status THEN
            RAISE EXCEPTION 'Role content_editor cannot approve or publish % directly', v_entity_type;
        END IF;
    END IF;

    -- Publishing requires source provenance on core agricultural content
    IF NEW.review_status IN ('approved', 'published') THEN
        IF v_entity_type IN ('crop', 'animal', 'crop_section', 'animal_section') THEN
            IF COALESCE(NULLIF(BTRIM(NEW.source_reference), ''), '') = '' THEN
                RAISE EXCEPTION 'Cannot mark % as % without source_reference', v_entity_type, NEW.review_status;
            END IF;
        END IF;
        IF v_entity_type = 'media_asset' THEN
            IF COALESCE(NULLIF(BTRIM(NEW.creator_name), ''), '') = ''
               OR COALESCE(NULLIF(BTRIM(NEW.license_type), ''), '') = ''
               OR COALESCE(NULLIF(BTRIM(NEW.usage_permission), ''), '') = '' THEN
                RAISE EXCEPTION 'Media asset cannot be % without creator_name, license_type, and usage_permission', NEW.review_status;
            END IF;
        END IF;
    END IF;

    -- Keep legacy boolean columns synchronized automatically
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = TG_TABLE_NAME AND column_name = 'is_published'
    ) THEN
        NEW.is_published := (NEW.review_status = 'published');
    END IF;

    IF NEW.review_status = 'published' AND (TG_OP = 'INSERT' OR OLD.review_status IS DISTINCT FROM 'published') THEN
        NEW.published_at := NOW();
        NEW.published_by := v_actor;
    END IF;

    RETURN NEW;
END;
$$;

-- Log publication / unpublication events automatically when row changes
CREATE OR REPLACE FUNCTION public.log_publication_event()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    v_entity_type TEXT := TG_ARGV[0];
    v_actor TEXT := COALESCE(current_setting('app.admin_actor', true), current_user);
    v_action TEXT;
BEGIN
    IF TG_OP = 'INSERT' AND NEW.review_status = 'published' THEN
        v_action := 'published';
    ELSIF TG_OP = 'UPDATE' AND OLD.review_status IS DISTINCT FROM NEW.review_status THEN
        IF NEW.review_status = 'published' THEN
            v_action := 'published';
        ELSIF OLD.review_status = 'published' AND NEW.review_status = 'archived' THEN
            v_action := 'archived';
        ELSIF OLD.review_status = 'published' THEN
            v_action := 'unpublished';
        END IF;
    END IF;

    IF v_action IS NOT NULL THEN
        INSERT INTO public.content_publication_history (
            target_type, target_id, action, previous_status, new_status, actor_username, summary_note
        ) VALUES (
            v_entity_type,
            NEW.id,
            v_action,
            CASE WHEN TG_OP = 'UPDATE' THEN OLD.review_status ELSE NULL END,
            NEW.review_status,
            v_actor,
            'Automated workflow state transition'
        );
    END IF;
    RETURN NEW;
END;
$$;

CREATE OR REPLACE FUNCTION public.prevent_audit_log_mutation()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    RAISE EXCEPTION 'audit_logs is append-only; UPDATE and DELETE are forbidden';
END;
$$;

-- ----------------------------------------------------------------------------
-- 1. TAXONOMY & CATEGORIES (Extensible backend taxonomy)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.categories (
    id TEXT PRIMARY KEY,
    domain TEXT NOT NULL CHECK (domain IN ('crop', 'livestock', 'video', 'helpline')),
    parent_id TEXT REFERENCES public.categories(id) ON DELETE SET NULL,
    slug TEXT UNIQUE,
    name_ne TEXT NOT NULL,
    name_en TEXT NOT NULL,
    description_ne TEXT,
    description_en TEXT,
    icon_name TEXT,
    sort_order INT NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_archived BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

ALTER TABLE public.crop_categories
    ADD COLUMN IF NOT EXISTS description_ne TEXT,
    ADD COLUMN IF NOT EXISTS description_en TEXT,
    ADD COLUMN IF NOT EXISTS sort_order INT NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS is_active BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN IF NOT EXISTS is_archived BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();

CREATE TABLE IF NOT EXISTS public.animal_categories (
    id TEXT PRIMARY KEY,
    name_ne TEXT NOT NULL,
    name_en TEXT NOT NULL,
    description_ne TEXT,
    description_en TEXT,
    icon_name TEXT,
    sort_order INT NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_archived BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.video_topics (
    id INT PRIMARY KEY,
    slug TEXT NOT NULL UNIQUE,
    name_ne TEXT NOT NULL,
    name_en TEXT NOT NULL,
    description_en TEXT,
    sort_order INT NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ----------------------------------------------------------------------------
-- 2. SOURCE DOCUMENTS, CITATIONS, REVIEWS, VERSIONS, PUBLICATION HISTORY
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.source_documents (
    id TEXT PRIMARY KEY,
    title TEXT NOT NULL,
    publisher TEXT NOT NULL,
    document_type TEXT NOT NULL CHECK (
        document_type IN (
            'narc_manual', 'moald_diary', 'dls_guideline', 'nvpl_schedule',
            'aic_training', 'municipal_profile', 'fao_reference', 'wikimedia_commons',
            'government_directory', 'extension_bulletin', 'other'
        )
    ),
    publication_year INT,
    source_url TEXT,
    citation_code TEXT,
    verification_status TEXT NOT NULL DEFAULT 'verified' CHECK (
        verification_status IN ('verified', 'historical_context', 'pending_check', 'superseded')
    ),
    is_historical_context BOOLEAN NOT NULL DEFAULT FALSE,
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.content_citations (
    id TEXT PRIMARY KEY,
    target_type TEXT NOT NULL CHECK (
        target_type IN ('crop', 'crop_section', 'crop_variety', 'animal', 'animal_section', 'animal_breed', 'media_asset', 'educational_video', 'helpline_contact')
    ),
    target_id TEXT NOT NULL,
    section_key TEXT NOT NULL DEFAULT 'general',
    source_document_id TEXT NOT NULL REFERENCES public.source_documents(id) ON DELETE RESTRICT,
    page_or_section TEXT,
    excerpt TEXT,
    is_primary BOOLEAN NOT NULL DEFAULT TRUE,
    verified_by TEXT,
    verified_at TIMESTAMPTZ DEFAULT NOW(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_citations_target ON public.content_citations (target_type, target_id, section_key);

CREATE TABLE IF NOT EXISTS public.content_publication_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    target_type TEXT NOT NULL,
    target_id TEXT NOT NULL,
    action TEXT NOT NULL CHECK (action IN ('published', 'unpublished', 'archived', 'restored')),
    previous_status TEXT,
    new_status TEXT NOT NULL,
    actor_username TEXT NOT NULL,
    summary_note TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_pub_history_target ON public.content_publication_history (target_type, target_id, created_at DESC);

-- ----------------------------------------------------------------------------
-- 3. ADMIN RBAC & IMMUTABLE AUDIT LOGS (Server-Side / Service-Role Only)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.admin_roles (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL UNIQUE,
    description TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.admin_permissions (
    id TEXT PRIMARY KEY,
    role_id TEXT NOT NULL REFERENCES public.admin_roles(id) ON DELETE CASCADE,
    permission_code TEXT NOT NULL,
    description TEXT,
    UNIQUE (role_id, permission_code)
);

CREATE TABLE IF NOT EXISTS public.admin_users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username TEXT NOT NULL UNIQUE,
    email TEXT NOT NULL UNIQUE,
    full_name TEXT NOT NULL,
    role_id TEXT NOT NULL REFERENCES public.admin_roles(id) ON DELETE RESTRICT,
    password_hash TEXT NOT NULL,
    totp_secret TEXT,
    totp_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    failed_login_attempts INT NOT NULL DEFAULT 0,
    locked_until TIMESTAMPTZ,
    last_login_at TIMESTAMPTZ,
    password_updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.content_reviews (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    target_type TEXT NOT NULL,
    target_id TEXT NOT NULL,
    section_key TEXT DEFAULT 'all',
    from_status TEXT NOT NULL,
    to_status TEXT NOT NULL,
    decision TEXT NOT NULL CHECK (decision IN ('submit', 'request_source', 'approve', 'reject', 'publish', 'flag_rereview', 'archive')),
    reviewer_username TEXT NOT NULL,
    reviewer_role TEXT NOT NULL,
    comment TEXT,
    checklist_json JSONB NOT NULL DEFAULT '{}'::jsonb,
    reviewed_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_reviews_target ON public.content_reviews (target_type, target_id, reviewed_at DESC);

CREATE TABLE IF NOT EXISTS public.content_versions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    target_type TEXT NOT NULL,
    target_id TEXT NOT NULL,
    version_number INT NOT NULL,
    review_status TEXT NOT NULL,
    snapshot_json JSONB NOT NULL,
    changed_by TEXT NOT NULL,
    change_summary TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (target_type, target_id, version_number)
);

CREATE TABLE IF NOT EXISTS public.audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    actor_username TEXT NOT NULL,
    actor_role TEXT NOT NULL,
    action TEXT NOT NULL,
    target_type TEXT NOT NULL,
    target_id TEXT,
    ip_address TEXT,
    user_agent TEXT,
    details_json JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_audit_created ON public.audit_logs (created_at DESC);

DROP TRIGGER IF EXISTS trg_audit_logs_immutable_update ON public.audit_logs;
CREATE TRIGGER trg_audit_logs_immutable_update
    BEFORE UPDATE ON public.audit_logs
    FOR EACH ROW EXECUTE FUNCTION public.prevent_audit_log_mutation();

DROP TRIGGER IF EXISTS trg_audit_logs_immutable_delete ON public.audit_logs;
CREATE TRIGGER trg_audit_logs_immutable_delete
    BEFORE DELETE ON public.audit_logs
    FOR EACH ROW EXECUTE FUNCTION public.prevent_audit_log_mutation();

-- ----------------------------------------------------------------------------
-- 4. MEDIA ASSETS PIPELINE & DERIVATIVES
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.media_assets (
    id TEXT PRIMARY KEY,
    entity_type TEXT NOT NULL CHECK (entity_type IN ('crop', 'animal', 'category', 'general')),
    entity_id TEXT,
    title_en TEXT NOT NULL,
    title_ne TEXT,
    caption_ne TEXT NOT NULL,
    stage_category TEXT NOT NULL DEFAULT 'general',
    original_filename TEXT NOT NULL,
    mime_type TEXT NOT NULL,
    file_size_bytes BIGINT NOT NULL DEFAULT 0,
    width INT,
    height INT,
    checksum_sha256 TEXT,
    original_url TEXT,
    full_url TEXT NOT NULL,
    medium_url TEXT NOT NULL,
    thumb_url TEXT NOT NULL,
    creator_name TEXT NOT NULL DEFAULT '',
    source_name TEXT NOT NULL DEFAULT '',
    source_url TEXT,
    license_type TEXT NOT NULL DEFAULT '',
    usage_permission TEXT NOT NULL DEFAULT '',
    attribution_text TEXT,
    review_status TEXT NOT NULL DEFAULT 'draft',
    is_published BOOLEAN NOT NULL DEFAULT FALSE,
    is_archived BOOLEAN NOT NULL DEFAULT FALSE,
    published_at TIMESTAMPTZ,
    published_by TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ----------------------------------------------------------------------------
-- 5. EXTEND CROPS SCHEMA WITH NORMALIZED SECTIONS, ENV, PESTS, DISEASES, VARIETIES
-- ----------------------------------------------------------------------------
ALTER TABLE public.crops
    ADD COLUMN IF NOT EXISTS review_status TEXT NOT NULL DEFAULT 'published',
    ADD COLUMN IF NOT EXISTS source_url TEXT,
    ADD COLUMN IF NOT EXISTS verification_notes TEXT,
    ADD COLUMN IF NOT EXISTS is_archived BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS published_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS published_by TEXT,
    ADD COLUMN IF NOT EXISTS bakaiya_priority_note_ne TEXT;

CREATE TABLE IF NOT EXISTS public.crop_varieties (
    id TEXT PRIMARY KEY,
    crop_id TEXT NOT NULL REFERENCES public.crops(id) ON DELETE CASCADE,
    variety_name_ne TEXT NOT NULL,
    variety_name_en TEXT,
    maturity_days TEXT,
    recommended_domain_ne TEXT,
    altitude_min_m INT,
    altitude_opt_min_m INT,
    altitude_opt_max_m INT,
    altitude_max_m INT,
    suitable_altitude TEXT,
    suitable_zones_csv TEXT,
    suitable_regions_ne TEXT,
    planting_season TEXT,
    transplanting_period_ne TEXT,
    yield_potential_note_ne TEXT,
    notes_ne TEXT,
    source_reference TEXT NOT NULL,
    review_status TEXT NOT NULL DEFAULT 'published',
    is_published BOOLEAN NOT NULL DEFAULT TRUE,
    is_archived BOOLEAN NOT NULL DEFAULT FALSE,
    sort_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_crop_varieties_crop ON public.crop_varieties (crop_id, sort_order);

CREATE TABLE IF NOT EXISTS public.crop_content_sections (
    id TEXT PRIMARY KEY,
    crop_id TEXT NOT NULL REFERENCES public.crops(id) ON DELETE CASCADE,
    section_number INT NOT NULL CHECK (section_number BETWEEN 1 AND 22),
    section_key TEXT NOT NULL,
    title_ne TEXT NOT NULL,
    title_en TEXT NOT NULL,
    body_ne TEXT,
    body_en TEXT,
    source_reference TEXT NOT NULL DEFAULT '',
    source_document_id TEXT REFERENCES public.source_documents(id) ON DELETE SET NULL,
    review_status TEXT NOT NULL DEFAULT 'draft',
    is_published BOOLEAN NOT NULL DEFAULT FALSE,
    is_archived BOOLEAN NOT NULL DEFAULT FALSE,
    published_at TIMESTAMPTZ,
    published_by TEXT,
    sort_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (crop_id, section_key)
);

CREATE INDEX IF NOT EXISTS idx_crop_sections_crop ON public.crop_content_sections (crop_id, section_number);

CREATE TABLE IF NOT EXISTS public.crop_environment_requirements (
    id TEXT PRIMARY KEY,
    crop_id TEXT NOT NULL UNIQUE REFERENCES public.crops(id) ON DELETE CASCADE,
    altitude_min_m INT,
    altitude_opt_min_m INT,
    altitude_opt_max_m INT,
    altitude_max_m INT,
    altitude_notes_ne TEXT,
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
    soil_types_ne TEXT,
    soil_ph_min DOUBLE PRECISION,
    soil_ph_max DOUBLE PRECISION,
    soil_drainage_ne TEXT,
    sunlight_ne TEXT,
    water_requirement_ne TEXT,
    source_reference TEXT NOT NULL,
    review_status TEXT NOT NULL DEFAULT 'published',
    is_published BOOLEAN NOT NULL DEFAULT TRUE,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.crop_pests (
    id TEXT PRIMARY KEY,
    crop_id TEXT NOT NULL REFERENCES public.crops(id) ON DELETE CASCADE,
    name_ne TEXT NOT NULL,
    name_en TEXT,
    scientific_name TEXT,
    symptoms_ne TEXT NOT NULL,
    preventive_management_ne TEXT NOT NULL,
    biological_ipm_ne TEXT,
    advisory_caution_ne TEXT NOT NULL,
    source_reference TEXT NOT NULL,
    review_status TEXT NOT NULL DEFAULT 'published',
    is_published BOOLEAN NOT NULL DEFAULT TRUE,
    is_archived BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.crop_diseases (
    id TEXT PRIMARY KEY,
    crop_id TEXT NOT NULL REFERENCES public.crops(id) ON DELETE CASCADE,
    name_ne TEXT NOT NULL,
    name_en TEXT,
    causal_agent TEXT,
    symptoms_ne TEXT NOT NULL,
    cultural_management_ne TEXT NOT NULL,
    advisory_caution_ne TEXT NOT NULL,
    source_reference TEXT NOT NULL,
    review_status TEXT NOT NULL DEFAULT 'published',
    is_published BOOLEAN NOT NULL DEFAULT TRUE,
    is_archived BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.crop_cultivation_steps (
    id TEXT PRIMARY KEY,
    crop_id TEXT NOT NULL REFERENCES public.crops(id) ON DELETE CASCADE,
    step_order INT NOT NULL,
    stage_name_ne TEXT NOT NULL,
    stage_name_en TEXT,
    timing_ne TEXT,
    guidance_ne TEXT NOT NULL,
    source_reference TEXT NOT NULL,
    review_status TEXT NOT NULL DEFAULT 'published',
    is_published BOOLEAN NOT NULL DEFAULT TRUE,
    UNIQUE (crop_id, step_order)
);

ALTER TABLE public.crop_images
    ADD COLUMN IF NOT EXISTS media_asset_id TEXT REFERENCES public.media_assets(id) ON DELETE SET NULL,
    ADD COLUMN IF NOT EXISTS thumb_url TEXT,
    ADD COLUMN IF NOT EXISTS medium_url TEXT,
    ADD COLUMN IF NOT EXISTS creator_name TEXT,
    ADD COLUMN IF NOT EXISTS source_url TEXT,
    ADD COLUMN IF NOT EXISTS license_type TEXT,
    ADD COLUMN IF NOT EXISTS sort_order INT NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS review_status TEXT NOT NULL DEFAULT 'published',
    ADD COLUMN IF NOT EXISTS is_published BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN IF NOT EXISTS is_archived BOOLEAN NOT NULL DEFAULT FALSE;

-- ----------------------------------------------------------------------------
-- 6. LIVESTOCK MODULE TABLES (25-SECTION EXTENSIBLE ARCHITECTURE)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.animals (
    id TEXT PRIMARY KEY,
    category_id TEXT NOT NULL REFERENCES public.animal_categories(id) ON DELETE RESTRICT,
    name_ne TEXT NOT NULL,
    name_en TEXT NOT NULL,
    scientific_name TEXT,
    species_code TEXT NOT NULL,
    summary_ne TEXT NOT NULL,
    purpose_tags_ne TEXT,
    bakaiya_relevance_ne TEXT,
    climate_suitability_ne TEXT,
    altitude_range_ne TEXT,
    recommended_breeds_summary_ne TEXT,
    safety_notice_ne TEXT NOT NULL DEFAULT 'पशु स्वास्थ्य वा औषधि मात्रा सम्बन्धी अन्तिम निर्णय नजिकको पशु सेवा प्राविधिक वा पशु चिकित्सकको प्रत्यक्ष जाँचपछि मात्र लिनुहोस्।',
    source_reference TEXT NOT NULL DEFAULT '',
    source_url TEXT,
    thumbnail_url TEXT,
    review_status TEXT NOT NULL DEFAULT 'draft',
    is_verified_content BOOLEAN NOT NULL DEFAULT FALSE,
    is_published BOOLEAN NOT NULL DEFAULT FALSE,
    is_archived BOOLEAN NOT NULL DEFAULT FALSE,
    published_at TIMESTAMPTZ,
    published_by TEXT,
    sort_order INT NOT NULL DEFAULT 0,
    last_updated_date TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_animals_category ON public.animals (category_id, sort_order);
CREATE INDEX IF NOT EXISTS idx_animals_review ON public.animals (review_status, is_archived);

CREATE TABLE IF NOT EXISTS public.animal_breeds (
    id TEXT PRIMARY KEY,
    animal_id TEXT NOT NULL REFERENCES public.animals(id) ON DELETE CASCADE,
    breed_name_ne TEXT NOT NULL,
    breed_name_en TEXT,
    origin_type TEXT NOT NULL CHECK (origin_type IN ('indigenous', 'improved', 'crossbred', 'exotic')),
    purpose_ne TEXT NOT NULL,
    climate_suitability_ne TEXT,
    characteristics_ne TEXT NOT NULL,
    notes_ne TEXT,
    source_reference TEXT NOT NULL,
    review_status TEXT NOT NULL DEFAULT 'published',
    is_published BOOLEAN NOT NULL DEFAULT TRUE,
    is_archived BOOLEAN NOT NULL DEFAULT FALSE,
    sort_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_animal_breeds_animal ON public.animal_breeds (animal_id, sort_order);

-- 25 independently manageable livestock sections per species
CREATE TABLE IF NOT EXISTS public.animal_content_sections (
    id TEXT PRIMARY KEY,
    animal_id TEXT NOT NULL REFERENCES public.animals(id) ON DELETE CASCADE,
    section_number INT NOT NULL CHECK (section_number BETWEEN 1 AND 25),
    section_key TEXT NOT NULL,
    title_ne TEXT NOT NULL,
    title_en TEXT NOT NULL,
    body_ne TEXT,
    body_en TEXT,
    is_applicable BOOLEAN NOT NULL DEFAULT TRUE,
    source_reference TEXT NOT NULL DEFAULT '',
    source_document_id TEXT REFERENCES public.source_documents(id) ON DELETE SET NULL,
    review_status TEXT NOT NULL DEFAULT 'draft',
    is_published BOOLEAN NOT NULL DEFAULT FALSE,
    is_archived BOOLEAN NOT NULL DEFAULT FALSE,
    published_at TIMESTAMPTZ,
    published_by TEXT,
    sort_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (animal_id, section_key)
);

CREATE INDEX IF NOT EXISTS idx_animal_sections_animal ON public.animal_content_sections (animal_id, section_number);

CREATE TABLE IF NOT EXISTS public.animal_housing_guidance (
    id TEXT PRIMARY KEY,
    animal_id TEXT NOT NULL UNIQUE REFERENCES public.animals(id) ON DELETE CASCADE,
    shed_design_ne TEXT NOT NULL,
    floor_space_standard_ne TEXT,
    ventilation_light_ne TEXT NOT NULL,
    bedding_drainage_ne TEXT NOT NULL,
    sanitation_routine_ne TEXT NOT NULL,
    biosecurity_notes_ne TEXT NOT NULL,
    source_reference TEXT NOT NULL,
    review_status TEXT NOT NULL DEFAULT 'draft',
    is_published BOOLEAN NOT NULL DEFAULT FALSE,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.animal_feeding_guidance (
    id TEXT PRIMARY KEY,
    animal_id TEXT NOT NULL UNIQUE REFERENCES public.animals(id) ON DELETE CASCADE,
    green_fodder_forage_ne TEXT NOT NULL,
    dry_roughage_ne TEXT NOT NULL,
    concentrate_feed_ne TEXT,
    mineral_salt_ne TEXT,
    clean_water_ne TEXT NOT NULL,
    ration_quantity_note_ne TEXT,
    feeding_frequency_ne TEXT NOT NULL,
    source_reference TEXT NOT NULL,
    review_status TEXT NOT NULL DEFAULT 'draft',
    is_published BOOLEAN NOT NULL DEFAULT FALSE,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.animal_breeding_guidance (
    id TEXT PRIMARY KEY,
    animal_id TEXT NOT NULL UNIQUE REFERENCES public.animals(id) ON DELETE CASCADE,
    breeding_age_weight_ne TEXT,
    estrus_heat_signs_ne TEXT,
    mating_ai_guidance_ne TEXT,
    pregnancy_incubation_period_ne TEXT,
    pregnant_care_ne TEXT NOT NULL,
    birthing_hatching_care_ne TEXT NOT NULL,
    newborn_chick_fry_care_ne TEXT NOT NULL,
    weaning_brooding_ne TEXT,
    source_reference TEXT NOT NULL,
    review_status TEXT NOT NULL DEFAULT 'draft',
    is_published BOOLEAN NOT NULL DEFAULT FALSE,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.animal_health_guidance (
    id TEXT PRIMARY KEY,
    animal_id TEXT NOT NULL UNIQUE REFERENCES public.animals(id) ON DELETE CASCADE,
    daily_hygiene_ne TEXT NOT NULL,
    biosecurity_isolation_ne TEXT NOT NULL,
    vaccination_awareness_ne TEXT NOT NULL,
    deworming_parasite_control_ne TEXT NOT NULL,
    warning_signs_ne TEXT NOT NULL,
    vet_referral_guidance_ne TEXT NOT NULL,
    source_reference TEXT NOT NULL,
    review_status TEXT NOT NULL DEFAULT 'draft',
    is_published BOOLEAN NOT NULL DEFAULT FALSE,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.animal_diseases (
    id TEXT PRIMARY KEY,
    animal_id TEXT NOT NULL REFERENCES public.animals(id) ON DELETE CASCADE,
    name_ne TEXT NOT NULL,
    name_en TEXT,
    pathogen_type TEXT CHECK (pathogen_type IN ('viral', 'bacterial', 'parasitic', 'metabolic', 'fungal', 'other')),
    symptoms_ne TEXT NOT NULL,
    prevention_biosecurity_ne TEXT NOT NULL,
    immediate_farmer_action_ne TEXT NOT NULL,
    veterinary_caution_ne TEXT NOT NULL,
    source_reference TEXT NOT NULL,
    review_status TEXT NOT NULL DEFAULT 'draft',
    is_published BOOLEAN NOT NULL DEFAULT FALSE,
    is_archived BOOLEAN NOT NULL DEFAULT FALSE,
    sort_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_animal_diseases_animal ON public.animal_diseases (animal_id, sort_order);

CREATE TABLE IF NOT EXISTS public.animal_production_guidance (
    id TEXT PRIMARY KEY,
    animal_id TEXT NOT NULL UNIQUE REFERENCES public.animals(id) ON DELETE CASCADE,
    primary_products_ne TEXT NOT NULL,
    clean_production_practice_ne TEXT NOT NULL,
    post_harvest_handling_ne TEXT NOT NULL,
    manure_waste_management_ne TEXT NOT NULL,
    marketing_notes_ne TEXT,
    source_reference TEXT NOT NULL,
    review_status TEXT NOT NULL DEFAULT 'draft',
    is_published BOOLEAN NOT NULL DEFAULT FALSE,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.animal_images (
    id TEXT PRIMARY KEY,
    animal_id TEXT NOT NULL REFERENCES public.animals(id) ON DELETE CASCADE,
    media_asset_id TEXT REFERENCES public.media_assets(id) ON DELETE SET NULL,
    image_url TEXT NOT NULL,
    medium_url TEXT,
    thumb_url TEXT,
    caption_ne TEXT NOT NULL,
    stage_category TEXT NOT NULL DEFAULT 'general',
    creator_name TEXT,
    source_url TEXT,
    license_type TEXT,
    sort_order INT NOT NULL DEFAULT 0,
    review_status TEXT NOT NULL DEFAULT 'published',
    is_published BOOLEAN NOT NULL DEFAULT TRUE,
    is_archived BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_animal_images_animal ON public.animal_images (animal_id, sort_order);

-- ----------------------------------------------------------------------------
-- 7. EXTEND EDUCATIONAL VIDEOS + PLAYLISTS (CROPS & LIVESTOCK)
-- ----------------------------------------------------------------------------
ALTER TABLE public.educational_videos
    ALTER COLUMN crop_id DROP NOT NULL;

ALTER TABLE public.educational_videos
    ADD COLUMN IF NOT EXISTS target_type TEXT NOT NULL DEFAULT 'crop' CHECK (target_type IN ('crop', 'animal', 'general')),
    ADD COLUMN IF NOT EXISTS animal_id TEXT REFERENCES public.animals(id) ON DELETE CASCADE,
    ADD COLUMN IF NOT EXISTS youtube_video_id TEXT,
    ADD COLUMN IF NOT EXISTS review_status TEXT NOT NULL DEFAULT 'published',
    ADD COLUMN IF NOT EXISTS is_published BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN IF NOT EXISTS is_archived BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS source_url TEXT,
    ADD COLUMN IF NOT EXISTS published_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS published_by TEXT,
    ADD COLUMN IF NOT EXISTS sort_order INT NOT NULL DEFAULT 0;

CREATE INDEX IF NOT EXISTS idx_videos_animal ON public.educational_videos (animal_id);
CREATE INDEX IF NOT EXISTS idx_videos_target ON public.educational_videos (target_type, review_status);

CREATE TABLE IF NOT EXISTS public.video_playlists (
    id TEXT PRIMARY KEY,
    target_type TEXT NOT NULL CHECK (target_type IN ('crop', 'animal', 'general')),
    crop_id TEXT REFERENCES public.crops(id) ON DELETE CASCADE,
    animal_id TEXT REFERENCES public.animals(id) ON DELETE CASCADE,
    title_ne TEXT NOT NULL,
    title_en TEXT NOT NULL,
    description_ne TEXT,
    review_status TEXT NOT NULL DEFAULT 'published',
    is_published BOOLEAN NOT NULL DEFAULT TRUE,
    is_archived BOOLEAN NOT NULL DEFAULT FALSE,
    sort_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.video_playlist_items (
    id TEXT PRIMARY KEY,
    playlist_id TEXT NOT NULL REFERENCES public.video_playlists(id) ON DELETE CASCADE,
    video_id TEXT NOT NULL REFERENCES public.educational_videos(id) ON DELETE CASCADE,
    position INT NOT NULL DEFAULT 0,
    UNIQUE (playlist_id, video_id)
);

-- ----------------------------------------------------------------------------
-- 8. EXTEND HELPLINE CONTACTS WITH VERIFICATION PROVENANCE
-- ----------------------------------------------------------------------------
ALTER TABLE public.helpline_contacts
    ADD COLUMN IF NOT EXISTS department_ne TEXT NOT NULL DEFAULT 'कृषि तथा पशुपालन सेवा',
    ADD COLUMN IF NOT EXISTS department_en TEXT NOT NULL DEFAULT 'Agriculture & Livestock Advisory',
    ADD COLUMN IF NOT EXISTS contact_category TEXT NOT NULL DEFAULT 'agriculture' CHECK (
        contact_category IN ('agriculture', 'veterinary', 'municipality', 'emergency')
    ),
    ADD COLUMN IF NOT EXISTS coverage_area_ne TEXT NOT NULL DEFAULT 'बकैया गाउँपालिका, मकवानपुर',
    ADD COLUMN IF NOT EXISTS office_hours_ne TEXT,
    ADD COLUMN IF NOT EXISTS source_reference TEXT NOT NULL DEFAULT '',
    ADD COLUMN IF NOT EXISTS source_url TEXT,
    ADD COLUMN IF NOT EXISTS verified_at TEXT,
    ADD COLUMN IF NOT EXISTS is_active BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN IF NOT EXISTS review_status TEXT NOT NULL DEFAULT 'published',
    ADD COLUMN IF NOT EXISTS is_published BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN IF NOT EXISTS is_archived BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS published_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS published_by TEXT,
    ADD COLUMN IF NOT EXISTS sort_order INT NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();

-- ----------------------------------------------------------------------------
-- 9. TRIGGERS FOR WORKFLOW ENFORCEMENT & TIMESTAMPS
-- ----------------------------------------------------------------------------
DROP TRIGGER IF EXISTS trg_crops_workflow ON public.crops;
CREATE TRIGGER trg_crops_workflow BEFORE INSERT OR UPDATE ON public.crops
    FOR EACH ROW EXECUTE FUNCTION public.enforce_content_workflow('crop');

DROP TRIGGER IF EXISTS trg_crops_pub_log ON public.crops;
CREATE TRIGGER trg_crops_pub_log AFTER INSERT OR UPDATE ON public.crops
    FOR EACH ROW EXECUTE FUNCTION public.log_publication_event('crop');

DROP TRIGGER IF EXISTS trg_animals_workflow ON public.animals;
CREATE TRIGGER trg_animals_workflow BEFORE INSERT OR UPDATE ON public.animals
    FOR EACH ROW EXECUTE FUNCTION public.enforce_content_workflow('animal');

DROP TRIGGER IF EXISTS trg_animals_pub_log ON public.animals;
CREATE TRIGGER trg_animals_pub_log AFTER INSERT OR UPDATE ON public.animals
    FOR EACH ROW EXECUTE FUNCTION public.log_publication_event('animal');

DROP TRIGGER IF EXISTS trg_crop_sections_workflow ON public.crop_content_sections;
CREATE TRIGGER trg_crop_sections_workflow BEFORE INSERT OR UPDATE ON public.crop_content_sections
    FOR EACH ROW EXECUTE FUNCTION public.enforce_content_workflow('crop_section');

DROP TRIGGER IF EXISTS trg_animal_sections_workflow ON public.animal_content_sections;
CREATE TRIGGER trg_animal_sections_workflow BEFORE INSERT OR UPDATE ON public.animal_content_sections
    FOR EACH ROW EXECUTE FUNCTION public.enforce_content_workflow('animal_section');

DROP TRIGGER IF EXISTS trg_media_assets_workflow ON public.media_assets;
CREATE TRIGGER trg_media_assets_workflow BEFORE INSERT OR UPDATE ON public.media_assets
    FOR EACH ROW EXECUTE FUNCTION public.enforce_content_workflow('media_asset');

DROP TRIGGER IF EXISTS trg_videos_workflow ON public.educational_videos;
CREATE TRIGGER trg_videos_workflow BEFORE INSERT OR UPDATE ON public.educational_videos
    FOR EACH ROW EXECUTE FUNCTION public.enforce_content_workflow('educational_video');

DROP TRIGGER IF EXISTS trg_helpline_workflow ON public.helpline_contacts;
CREATE TRIGGER trg_helpline_workflow BEFORE INSERT OR UPDATE ON public.helpline_contacts
    FOR EACH ROW EXECUTE FUNCTION public.enforce_content_workflow('helpline_contact');

-- ----------------------------------------------------------------------------
-- 10. STRICT ROW LEVEL SECURITY (RLS) HARDENING
-- ----------------------------------------------------------------------------
-- Drop permissive write-all policies created in 001_initial_schema.sql
DROP POLICY IF EXISTS "Allow all on categories" ON public.crop_categories;
DROP POLICY IF EXISTS "Allow all on crops" ON public.crops;
DROP POLICY IF EXISTS "Allow all on crop_variety_requirements" ON public.crop_variety_requirements;
DROP POLICY IF EXISTS "Allow all on crop_images" ON public.crop_images;
DROP POLICY IF EXISTS "Allow all on educational_videos" ON public.educational_videos;
DROP POLICY IF EXISTS "Allow all on helpline_contacts" ON public.helpline_contacts;
DROP POLICY IF EXISTS "Allow all on farmer_registration" ON public.farmer_registration;
DROP POLICY IF EXISTS "Public can view categories" ON public.crop_categories;
DROP POLICY IF EXISTS "Public can view published crops" ON public.crops;
DROP POLICY IF EXISTS "Public can view crop variety requirements" ON public.crop_variety_requirements;
DROP POLICY IF EXISTS "Public can view crop images" ON public.crop_images;
DROP POLICY IF EXISTS "Public can view educational videos" ON public.educational_videos;
DROP POLICY IF EXISTS "Public can view helpline contacts" ON public.helpline_contacts;

-- Enable RLS across all new tables
ALTER TABLE public.categories ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.animal_categories ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.video_topics ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.crop_varieties ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.crop_content_sections ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.crop_environment_requirements ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.crop_pests ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.crop_diseases ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.crop_cultivation_steps ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.animals ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.animal_breeds ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.animal_content_sections ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.animal_housing_guidance ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.animal_feeding_guidance ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.animal_breeding_guidance ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.animal_health_guidance ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.animal_diseases ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.animal_production_guidance ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.media_assets ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.animal_images ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.video_playlists ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.video_playlist_items ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.source_documents ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.content_citations ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.content_reviews ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.content_versions ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.admin_roles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.admin_permissions ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.admin_users ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.audit_logs ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.content_publication_history ENABLE ROW LEVEL SECURITY;

-- Public (anon / authenticated app clients) can ONLY READ active, published, non-archived rows
CREATE POLICY "Public read published categories" ON public.categories
    FOR SELECT USING (is_active = true AND is_archived = false);
CREATE POLICY "Public read published crop categories" ON public.crop_categories
    FOR SELECT USING (is_active = true AND is_archived = false);
CREATE POLICY "Public read published animal categories" ON public.animal_categories
    FOR SELECT USING (is_active = true AND is_archived = false);
CREATE POLICY "Public read video topics" ON public.video_topics
    FOR SELECT USING (is_active = true);

CREATE POLICY "Public read published crops" ON public.crops
    FOR SELECT USING (review_status = 'published' AND is_published = true AND is_archived = false);
CREATE POLICY "Public read published crop variety requirements" ON public.crop_variety_requirements
    FOR SELECT USING (
        EXISTS (SELECT 1 FROM public.crops c WHERE c.id = crop_id AND c.review_status = 'published' AND c.is_archived = false)
    );
CREATE POLICY "Public read published crop varieties" ON public.crop_varieties
    FOR SELECT USING (review_status = 'published' AND is_published = true AND is_archived = false);
CREATE POLICY "Public read published crop sections" ON public.crop_content_sections
    FOR SELECT USING (review_status = 'published' AND is_published = true AND is_archived = false);
CREATE POLICY "Public read published crop environment" ON public.crop_environment_requirements
    FOR SELECT USING (review_status = 'published' AND is_published = true);
CREATE POLICY "Public read published crop pests" ON public.crop_pests
    FOR SELECT USING (review_status = 'published' AND is_published = true AND is_archived = false);
CREATE POLICY "Public read published crop diseases" ON public.crop_diseases
    FOR SELECT USING (review_status = 'published' AND is_published = true AND is_archived = false);
CREATE POLICY "Public read published crop cultivation steps" ON public.crop_cultivation_steps
    FOR SELECT USING (review_status = 'published' AND is_published = true);

CREATE POLICY "Public read published animals" ON public.animals
    FOR SELECT USING (review_status = 'published' AND is_published = true AND is_archived = false);
CREATE POLICY "Public read published animal breeds" ON public.animal_breeds
    FOR SELECT USING (review_status = 'published' AND is_published = true AND is_archived = false);
CREATE POLICY "Public read published animal sections" ON public.animal_content_sections
    FOR SELECT USING (review_status = 'published' AND is_published = true AND is_archived = false);
CREATE POLICY "Public read published animal housing" ON public.animal_housing_guidance
    FOR SELECT USING (review_status = 'published' AND is_published = true);
CREATE POLICY "Public read published animal feeding" ON public.animal_feeding_guidance
    FOR SELECT USING (review_status = 'published' AND is_published = true);
CREATE POLICY "Public read published animal breeding" ON public.animal_breeding_guidance
    FOR SELECT USING (review_status = 'published' AND is_published = true);
CREATE POLICY "Public read published animal health" ON public.animal_health_guidance
    FOR SELECT USING (review_status = 'published' AND is_published = true);
CREATE POLICY "Public read published animal diseases" ON public.animal_diseases
    FOR SELECT USING (review_status = 'published' AND is_published = true AND is_archived = false);
CREATE POLICY "Public read published animal production" ON public.animal_production_guidance
    FOR SELECT USING (review_status = 'published' AND is_published = true);

CREATE POLICY "Public read published media assets" ON public.media_assets
    FOR SELECT USING (review_status = 'published' AND is_published = true AND is_archived = false);
CREATE POLICY "Public read published crop images" ON public.crop_images
    FOR SELECT USING (review_status = 'published' AND is_published = true AND is_archived = false);
CREATE POLICY "Public read published animal images" ON public.animal_images
    FOR SELECT USING (review_status = 'published' AND is_published = true AND is_archived = false);
CREATE POLICY "Public read published educational videos" ON public.educational_videos
    FOR SELECT USING (review_status = 'published' AND is_published = true AND is_archived = false);
CREATE POLICY "Public read published video playlists" ON public.video_playlists
    FOR SELECT USING (review_status = 'published' AND is_published = true AND is_archived = false);
CREATE POLICY "Public read published video playlist items" ON public.video_playlist_items
    FOR SELECT USING (
        EXISTS (SELECT 1 FROM public.video_playlists vp WHERE vp.id = playlist_id AND vp.review_status = 'published' AND vp.is_archived = false)
    );

CREATE POLICY "Public read active published helpline contacts" ON public.helpline_contacts
    FOR SELECT USING (is_active = true AND is_verified = true AND review_status = 'published' AND is_archived = false);

CREATE POLICY "Public read source documents" ON public.source_documents
    FOR SELECT USING (true);
CREATE POLICY "Public read content citations" ON public.content_citations
    FOR SELECT USING (true);

-- Optional farmer registration: insert-only from public clients (cannot enumerate other farmers' phone numbers)
CREATE POLICY "Public optional registration insert" ON public.farmer_registration
    FOR INSERT WITH CHECK (char_length(BTRIM(full_name)) >= 2 AND ward_number BETWEEN 1 AND 12);

-- Admin & sensitive tables: explicitly locked down from anon/authenticated; Django backend uses server-only service_role
CREATE POLICY "Deny public access admin_roles" ON public.admin_roles FOR ALL USING (false);
CREATE POLICY "Deny public access admin_permissions" ON public.admin_permissions FOR ALL USING (false);
CREATE POLICY "Deny public access admin_users" ON public.admin_users FOR ALL USING (false);
CREATE POLICY "Deny public access content_reviews" ON public.content_reviews FOR ALL USING (false);
CREATE POLICY "Deny public access content_versions" ON public.content_versions FOR ALL USING (false);
CREATE POLICY "Deny public access audit_logs" ON public.audit_logs FOR ALL USING (false);
CREATE POLICY "Deny public access publication_history" ON public.content_publication_history FOR SELECT USING (false);
