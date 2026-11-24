package com.krishisaathi.app.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Cached crop entity stored in local Room database.
 * Supports offline read-only consumption for farmers in field conditions.
 */
@Entity(tableName = "crops")
data class CropEntity(
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "name_ne")
    val nameNe: String,

    @ColumnInfo(name = "name_en")
    val nameEn: String,

    @ColumnInfo(name = "image_url")
    val imageUrl: String
)

