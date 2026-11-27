-- Additive access control and change notifications.
-- Does not drop tables, recreate working tables, or delete rows.
-- Django connects as the postgres superuser and bypasses RLS.

BEGIN;

-- Farmer records stay private. Registration writes go through the Edge Function service role.
ALTER TABLE public.farmer_profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.farmer_profiles FORCE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "Allow public insert on profiles" ON public.farmer_profiles;
DROP POLICY IF EXISTS "Allow public select on profiles" ON public.farmer_profiles;
DROP POLICY IF EXISTS "Allow public update on profiles" ON public.farmer_profiles;
DROP POLICY IF EXISTS "Allow public delete on profiles" ON public.farmer_profiles;

REVOKE ALL ON TABLE public.farmer_profiles FROM anon, authenticated;

CREATE UNIQUE INDEX IF NOT EXISTS farmer_profiles_phone_uidx
    ON public.farmer_profiles (phone);

-- Published agricultural content only. Child rows follow the parent publication flag.
DROP POLICY IF EXISTS "Public select crop_images" ON public.crop_images;
CREATE POLICY "Public select crop_images" ON public.crop_images
    FOR SELECT TO anon, authenticated
    USING (
        EXISTS (
            SELECT 1 FROM public.crops c
            WHERE c.id = crop_images.crop_id AND c.is_published = TRUE
        )
    );

DROP POLICY IF EXISTS "Public select crop_variety_requirements" ON public.crop_variety_requirements;
CREATE POLICY "Public select crop_variety_requirements" ON public.crop_variety_requirements
    FOR SELECT TO anon, authenticated
    USING (
        EXISTS (
            SELECT 1 FROM public.crops c
            WHERE c.id = crop_variety_requirements.crop_id AND c.is_published = TRUE
        )
    );

DROP POLICY IF EXISTS "Public select educational_videos" ON public.educational_videos;
CREATE POLICY "Public select educational_videos" ON public.educational_videos
    FOR SELECT TO anon, authenticated
    USING (
        EXISTS (
            SELECT 1 FROM public.crops c
            WHERE c.id = educational_videos.crop_id AND c.is_published = TRUE
        )
    );

DROP POLICY IF EXISTS "Public select animal_breeds" ON public.animal_breeds;
CREATE POLICY "Public select animal_breeds" ON public.animal_breeds
    FOR SELECT TO anon, authenticated
    USING (
        EXISTS (
            SELECT 1 FROM public.animals a
            WHERE a.id = animal_breeds.animal_id AND a.is_published = TRUE
        )
    );

DROP POLICY IF EXISTS "Public select animal_housing_guidance" ON public.animal_housing_guidance;
CREATE POLICY "Public select animal_housing_guidance" ON public.animal_housing_guidance
    FOR SELECT TO anon, authenticated
    USING (
        EXISTS (
            SELECT 1 FROM public.animals a
            WHERE a.id = animal_housing_guidance.animal_id AND a.is_published = TRUE
        )
    );

DROP POLICY IF EXISTS "Public select animal_feeding_guidance" ON public.animal_feeding_guidance;
CREATE POLICY "Public select animal_feeding_guidance" ON public.animal_feeding_guidance
    FOR SELECT TO anon, authenticated
    USING (
        EXISTS (
            SELECT 1 FROM public.animals a
            WHERE a.id = animal_feeding_guidance.animal_id AND a.is_published = TRUE
        )
    );

DROP POLICY IF EXISTS "Public select animal_health_guidance" ON public.animal_health_guidance;
CREATE POLICY "Public select animal_health_guidance" ON public.animal_health_guidance
    FOR SELECT TO anon, authenticated
    USING (
        EXISTS (
            SELECT 1 FROM public.animals a
            WHERE a.id = animal_health_guidance.animal_id AND a.is_published = TRUE
        )
    );

DROP POLICY IF EXISTS "Public select animal_diseases" ON public.animal_diseases;
CREATE POLICY "Public select animal_diseases" ON public.animal_diseases
    FOR SELECT TO anon, authenticated
    USING (
        EXISTS (
            SELECT 1 FROM public.animals a
            WHERE a.id = animal_diseases.animal_id AND a.is_published = TRUE
        )
    );

DROP POLICY IF EXISTS "Public select animal_images" ON public.animal_images;
CREATE POLICY "Public select animal_images" ON public.animal_images
    FOR SELECT TO anon, authenticated
    USING (
        EXISTS (
            SELECT 1 FROM public.animals a
            WHERE a.id = animal_images.animal_id AND a.is_published = TRUE
        )
    );

REVOKE INSERT, UPDATE, DELETE, TRUNCATE ON TABLE
    public.crops,
    public.crop_categories,
    public.crop_images,
    public.crop_variety_requirements,
    public.educational_videos,
    public.helpline_contacts,
    public.animals,
    public.animal_categories,
    public.animal_breeds,
    public.animal_housing_guidance,
    public.animal_feeding_guidance,
    public.animal_health_guidance,
    public.animal_diseases,
    public.animal_images,
    public.content_sync_metadata
FROM anon, authenticated;

-- Change notifications. Android refetches published rows; payloads stay small.
CREATE OR REPLACE FUNCTION public.touch_content_sync()
RETURNS TRIGGER
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
BEGIN
    INSERT INTO public.content_sync_metadata (table_name, last_updated_at, version)
    VALUES (TG_TABLE_NAME, NOW(), 1)
    ON CONFLICT (table_name) DO UPDATE
        SET last_updated_at = NOW(),
            version = public.content_sync_metadata.version + 1;
    RETURN NULL;
END;
$$;

DROP TRIGGER IF EXISTS crops_touch_sync ON public.crops;
CREATE TRIGGER crops_touch_sync
    AFTER INSERT OR UPDATE OR DELETE ON public.crops
    FOR EACH STATEMENT EXECUTE FUNCTION public.touch_content_sync();

DROP TRIGGER IF EXISTS animals_touch_sync ON public.animals;
CREATE TRIGGER animals_touch_sync
    AFTER INSERT OR UPDATE OR DELETE ON public.animals
    FOR EACH STATEMENT EXECUTE FUNCTION public.touch_content_sync();

DROP TRIGGER IF EXISTS helpline_touch_sync ON public.helpline_contacts;
CREATE TRIGGER helpline_touch_sync
    AFTER INSERT OR UPDATE OR DELETE ON public.helpline_contacts
    FOR EACH STATEMENT EXECUTE FUNCTION public.touch_content_sync();

COMMIT;

-- Publication membership cannot run inside the same transaction as some hosted Postgres builds.
DO $$
DECLARE
    target TEXT;
BEGIN
    FOREACH target IN ARRAY ARRAY['crops', 'animals', 'helpline_contacts', 'content_sync_metadata']
    LOOP
        IF NOT EXISTS (
            SELECT 1 FROM pg_publication_tables
            WHERE pubname = 'supabase_realtime'
              AND schemaname = 'public'
              AND tablename = target
        ) THEN
            EXECUTE format('ALTER PUBLICATION supabase_realtime ADD TABLE public.%I', target);
        END IF;
    END LOOP;
END $$;

INSERT INTO storage.buckets (id, name, public, file_size_limit)
VALUES ('educational-media', 'educational-media', TRUE, 5242880)
ON CONFLICT (id) DO NOTHING;

DROP POLICY IF EXISTS "Public read educational media" ON storage.objects;
CREATE POLICY "Public read educational media" ON storage.objects
    FOR SELECT TO anon, authenticated
    USING (bucket_id = 'educational-media');
