package com.krishisaathi.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Local Room SQLite cache for Bakaiya Krishi Saathi.
 * Strictly populated and refreshed from Supabase (no inbuilt app seed data).
 */
@Database(
    entities = [
        CategoryEntity::class,
        BakaiyaCropEntity::class,
        CropVarietyRequirementEntity::class,
        CropImageEntity::class,
        EducationalVideoEntity::class,
        HelplineContactEntity::class,
        FarmerRegistrationEntity::class,
        AnimalCategoryEntity::class,
        AnimalEntity::class,
        AnimalBreedEntity::class,
        AnimalHousingEntity::class,
        AnimalFeedingEntity::class,
        AnimalHealthEntity::class,
        AnimalDiseaseEntity::class,
        AnimalImageEntity::class
    ],
    version = 9,
    exportSchema = false
)
abstract class KrishiSaathiDatabase : RoomDatabase() {

    abstract fun bakaiyaCropDao(): BakaiyaCropDao

    companion object {
        @Volatile
        private var INSTANCE: KrishiSaathiDatabase? = null

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE IF EXISTS farm_records")
                db.execSQL(
                    "ALTER TABLE farmer_registration ADD COLUMN municipality TEXT NOT NULL DEFAULT 'बकैया गाउँपालिका, मकवानपुर'"
                )
                db.execSQL(
                    "ALTER TABLE farmer_registration ADD COLUMN registered_at INTEGER NOT NULL DEFAULT 0"
                )
            }
        }

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                val newCropColumns = listOf(
                    "altitude_min_m INTEGER",
                    "altitude_opt_min_m INTEGER",
                    "altitude_opt_max_m INTEGER",
                    "altitude_max_m INTEGER",
                    "altitude_growth_notes_ne TEXT",
                    "suitable_zones_csv TEXT",
                    "suitable_regions_ne TEXT",
                    "soil_texture_ne TEXT",
                    "soil_drainage_ne TEXT",
                    "soil_fertility_ne TEXT",
                    "soil_organic_matter_ne TEXT",
                    "temp_opt_min REAL",
                    "temp_opt_max REAL",
                    "rainfall_min_mm INTEGER",
                    "rainfall_max_mm INTEGER",
                    "rainfall_period_label_ne TEXT",
                    "sowing_period_ne TEXT",
                    "transplanting_period_ne TEXT",
                    "growing_duration_ne TEXT",
                    "harvesting_period_ne TEXT",
                    "special_considerations_ne TEXT",
                    "last_updated_date TEXT"
                )
                newCropColumns.forEach { colDef ->
                    db.execSQL("ALTER TABLE crops ADD COLUMN $colDef")
                }

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS crop_variety_requirements (
                        id TEXT NOT NULL PRIMARY KEY,
                        crop_id TEXT NOT NULL,
                        crop_name_ne TEXT NOT NULL,
                        crop_name_en TEXT NOT NULL,
                        variety_name_ne TEXT NOT NULL,
                        variety_name_en TEXT,
                        notes_ne TEXT,
                        source_reference TEXT NOT NULL,
                        altitude_min_m INTEGER,
                        altitude_opt_min_m INTEGER,
                        altitude_opt_max_m INTEGER,
                        altitude_max_m INTEGER,
                        suitable_altitude TEXT,
                        altitude_growth_notes_ne TEXT,
                        suitable_zones_csv TEXT,
                        suitable_regions_ne TEXT,
                        soil_types TEXT,
                        soil_texture_ne TEXT,
                        soil_ph_min REAL,
                        soil_ph_max REAL,
                        soil_drainage_ne TEXT,
                        soil_fertility_ne TEXT,
                        soil_organic_matter_ne TEXT,
                        temp_min REAL,
                        temp_opt_min REAL,
                        temp_opt_max REAL,
                        temp_max REAL,
                        humidity_min REAL,
                        humidity_max REAL,
                        rainfall_min_mm INTEGER,
                        rainfall_max_mm INTEGER,
                        rainfall_period_label_ne TEXT,
                        water_requirement TEXT,
                        sunlight TEXT,
                        planting_season TEXT,
                        sowing_period_ne TEXT,
                        transplanting_period_ne TEXT,
                        growing_duration_ne TEXT,
                        harvesting_period_ne TEXT,
                        irrigation_management TEXT,
                        special_considerations_ne TEXT,
                        last_updated_date TEXT
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_crops_category_id ON crops (category_id)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_crops_is_published ON crops (is_published)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_crop_variety_requirements_crop_id ON crop_variety_requirements (crop_id)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_crop_images_crop_id ON crop_images (crop_id)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_educational_videos_crop_id ON educational_videos (crop_id)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_educational_videos_topic_id ON educational_videos (topic_id)")
            }
        }

        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE farmer_registration ADD COLUMN tole_village TEXT NOT NULL DEFAULT ''"
                )
                db.execSQL(
                    "ALTER TABLE farmer_registration ADD COLUMN consent_status INTEGER NOT NULL DEFAULT 0"
                )
                db.execSQL(
                    "ALTER TABLE farmer_registration ADD COLUMN sync_status TEXT NOT NULL DEFAULT 'PENDING'"
                )
            }
        }

        /**
         * Adds Livestock local cache tables & category thumbnail/sort columns; clears pre-v9 inbuilt content
         * so Room serves exclusively data synced from Supabase while preserving farmer_registration & downloaded videos.
         */
        val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE crop_categories ADD COLUMN thumbnail_url TEXT")
                db.execSQL("ALTER TABLE crop_categories ADD COLUMN sort_order INTEGER NOT NULL DEFAULT 0")

                // Remove old inbuilt content so only Supabase-synced rows remain
                db.execSQL("DELETE FROM crop_images")
                db.execSQL("DELETE FROM crop_variety_requirements")
                db.execSQL("DELETE FROM crops")
                db.execSQL("DELETE FROM crop_categories")
                db.execSQL("DELETE FROM helpline_contacts")
                db.execSQL("DELETE FROM educational_videos WHERE is_downloaded = 0")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS animal_categories (
                        id TEXT NOT NULL PRIMARY KEY,
                        name_ne TEXT NOT NULL,
                        name_en TEXT NOT NULL,
                        description_ne TEXT,
                        icon_name TEXT,
                        thumbnail_url TEXT,
                        sort_order INTEGER NOT NULL DEFAULT 0
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS animals (
                        id TEXT NOT NULL PRIMARY KEY,
                        category_id TEXT NOT NULL,
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
                        safety_notice_ne TEXT NOT NULL,
                        source_reference TEXT NOT NULL,
                        thumbnail_url TEXT,
                        review_status TEXT NOT NULL,
                        is_verified_content INTEGER NOT NULL,
                        is_published INTEGER NOT NULL,
                        sort_order INTEGER NOT NULL DEFAULT 0
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_animals_category_id ON animals (category_id)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_animals_is_published ON animals (is_published)")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS animal_breeds (
                        id TEXT NOT NULL PRIMARY KEY,
                        animal_id TEXT NOT NULL,
                        breed_name_ne TEXT NOT NULL,
                        breed_name_en TEXT,
                        origin_type TEXT NOT NULL,
                        purpose_ne TEXT NOT NULL,
                        climate_suitability_ne TEXT,
                        characteristics_ne TEXT NOT NULL,
                        notes_ne TEXT,
                        source_reference TEXT NOT NULL,
                        sort_order INTEGER NOT NULL DEFAULT 0
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_animal_breeds_animal_id ON animal_breeds (animal_id)")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS animal_housing_guidance (
                        id TEXT NOT NULL PRIMARY KEY,
                        animal_id TEXT NOT NULL,
                        shed_design_ne TEXT NOT NULL,
                        floor_space_standard_ne TEXT,
                        ventilation_light_ne TEXT NOT NULL,
                        bedding_drainage_ne TEXT NOT NULL,
                        sanitation_routine_ne TEXT NOT NULL,
                        biosecurity_notes_ne TEXT NOT NULL,
                        source_reference TEXT NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_animal_housing_guidance_animal_id ON animal_housing_guidance (animal_id)")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS animal_feeding_guidance (
                        id TEXT NOT NULL PRIMARY KEY,
                        animal_id TEXT NOT NULL,
                        green_fodder_forage_ne TEXT NOT NULL,
                        dry_roughage_ne TEXT NOT NULL,
                        concentrate_feed_ne TEXT,
                        mineral_salt_ne TEXT,
                        clean_water_ne TEXT NOT NULL,
                        ration_quantity_note_ne TEXT,
                        feeding_frequency_ne TEXT NOT NULL,
                        source_reference TEXT NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_animal_feeding_guidance_animal_id ON animal_feeding_guidance (animal_id)")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS animal_health_guidance (
                        id TEXT NOT NULL PRIMARY KEY,
                        animal_id TEXT NOT NULL,
                        daily_hygiene_ne TEXT NOT NULL,
                        biosecurity_isolation_ne TEXT NOT NULL,
                        vaccination_awareness_ne TEXT NOT NULL,
                        deworming_parasite_control_ne TEXT NOT NULL,
                        warning_signs_ne TEXT NOT NULL,
                        vet_referral_guidance_ne TEXT NOT NULL,
                        source_reference TEXT NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_animal_health_guidance_animal_id ON animal_health_guidance (animal_id)")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS animal_diseases (
                        id TEXT NOT NULL PRIMARY KEY,
                        animal_id TEXT NOT NULL,
                        name_ne TEXT NOT NULL,
                        name_en TEXT,
                        pathogen_type TEXT,
                        symptoms_ne TEXT NOT NULL,
                        prevention_biosecurity_ne TEXT NOT NULL,
                        immediate_farmer_action_ne TEXT NOT NULL,
                        veterinary_caution_ne TEXT NOT NULL,
                        source_reference TEXT NOT NULL,
                        sort_order INTEGER NOT NULL DEFAULT 0
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_animal_diseases_animal_id ON animal_diseases (animal_id)")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS animal_images (
                        id TEXT NOT NULL PRIMARY KEY,
                        animal_id TEXT NOT NULL,
                        image_url TEXT NOT NULL,
                        medium_url TEXT,
                        thumb_url TEXT,
                        caption_ne TEXT NOT NULL,
                        stage_category TEXT NOT NULL,
                        creator_name TEXT,
                        source_url TEXT,
                        license_type TEXT,
                        sort_order INTEGER NOT NULL DEFAULT 0
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_animal_images_animal_id ON animal_images (animal_id)")
            }
        }

        fun getInstance(context: Context): KrishiSaathiDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KrishiSaathiDatabase::class.java,
                    "bakaiya_krishi_saathi_database"
                )
                    .addMigrations(MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
