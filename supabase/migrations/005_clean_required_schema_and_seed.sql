-- REJECTED: this migration previously dropped public.crops, livestock tables,
-- guides, and related rows. Those tables are in use and must be kept.
-- Do not apply. The additive changes live in 006_publication_access_and_realtime.sql.
DO $$
BEGIN
    RAISE EXCEPTION
        '005_clean_required_schema_and_seed.sql is disabled. It would drop existing content tables. Apply 006 instead.';
END $$;
