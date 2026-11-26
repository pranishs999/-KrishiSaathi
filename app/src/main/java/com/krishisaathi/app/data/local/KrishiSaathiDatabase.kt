package com.krishisaathi.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        CategoryEntity::class,
        BakaiyaCropEntity::class,
        CropVarietyRequirementEntity::class,
        CropImageEntity::class,
        EducationalVideoEntity::class,
        HelplineContactEntity::class,
        FarmerRegistrationEntity::class
    ],
    version = 7,
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

        /**
         * Adds extended crop growing condition columns, indexes, and the crop_variety_requirements
         * table without dropping existing farmer registration or downloaded video paths.
         */
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

        fun getInstance(context: Context): KrishiSaathiDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KrishiSaathiDatabase::class.java,
                    "bakaiya_krishi_saathi_database"
                )
                    .addMigrations(MIGRATION_5_6, MIGRATION_6_7)
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                val database = getInstance(context)
                                seedDatabaseInTransaction(database)
                            }
                        }

                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                val database = getInstance(context)
                                val dao = database.bakaiyaCropDao()
                                if (dao.countPublishedCropsSync() == 0) {
                                    seedDatabaseInTransaction(database)
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun seedDatabaseInTransaction(database: KrishiSaathiDatabase) {
            database.runInTransaction {
                seedDatabase(database.bakaiyaCropDao())
            }
        }

        fun seedDatabase(dao: BakaiyaCropDao) {
            dao.upsertCategories(BakaiyaSeedData.categories)
            dao.upsertCrops(BakaiyaSeedData.crops)
            dao.upsertVarietyRequirements(BakaiyaSeedData.varietyRequirements)
            dao.upsertImages(BakaiyaSeedData.cropImages)
            // Preserve local download paths if videos already exist locally
            dao.insertVideosIgnoreConflict(BakaiyaSeedData.educationalVideos)
            dao.clearHelplineContacts()
            dao.upsertContacts(BakaiyaSeedData.helplineContacts)
        }
    }
}
