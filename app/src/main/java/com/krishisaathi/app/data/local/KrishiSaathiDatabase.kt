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
        CropImageEntity::class,
        EducationalVideoEntity::class,
        HelplineContactEntity::class,
        FarmerRegistrationEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class KrishiSaathiDatabase : RoomDatabase() {

    abstract fun bakaiyaCropDao(): BakaiyaCropDao

    companion object {
        @Volatile
        private var INSTANCE: KrishiSaathiDatabase? = null

        /**
         * Preserves unrelated local data (crops, downloaded videos, existing farmer_registration rows)
         * when removing the deprecated farm_records table and extending farmer_registration.
         */
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

        fun getInstance(context: Context): KrishiSaathiDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KrishiSaathiDatabase::class.java,
                    "bakaiya_krishi_saathi_database"
                )
                    .addMigrations(MIGRATION_5_6)
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                val dao = getInstance(context).bakaiyaCropDao()
                                seedDatabase(dao)
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun seedDatabase(dao: BakaiyaCropDao) {
            dao.upsertCategories(BakaiyaSeedData.categories)
            dao.upsertCrops(BakaiyaSeedData.crops)
            dao.upsertImages(BakaiyaSeedData.cropImages)
            // Preserve local download paths if videos already exist locally
            dao.insertVideosIgnoreConflict(BakaiyaSeedData.educationalVideos)
            dao.clearHelplineContacts()
            dao.upsertContacts(BakaiyaSeedData.helplineContacts)
        }
    }
}
