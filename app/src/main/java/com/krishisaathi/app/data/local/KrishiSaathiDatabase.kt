package com.krishisaathi.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Primary offline Room Database for Krishi Saathi.
 * Serves as the Single Source of Truth (SSOT) for all agricultural advisory data.
 */
@Database(
    entities = [CropEntity::class],
    version = 1,
    exportSchema = false
)
abstract class KrishiSaathiDatabase : RoomDatabase() {

    abstract fun cropDao(): CropDao

    companion object {
        @Volatile
        private var INSTANCE: KrishiSaathiDatabase? = null

        fun getInstance(context: Context): KrishiSaathiDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KrishiSaathiDatabase::class.java,
                    "krishi_saathi_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

