package com.krishisaathi.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
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
        FarmerRegistrationEntity::class,
        FarmRecordEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class KrishiSaathiDatabase : RoomDatabase() {

    abstract fun bakaiyaCropDao(): BakaiyaCropDao

    companion object {
        @Volatile
        private var INSTANCE: KrishiSaathiDatabase? = null

        fun getInstance(context: Context): KrishiSaathiDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KrishiSaathiDatabase::class.java,
                    "bakaiya_krishi_saathi_database"
                )
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
