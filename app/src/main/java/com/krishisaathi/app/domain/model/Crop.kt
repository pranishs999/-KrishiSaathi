package com.krishisaathi.app.domain.model

/**
 * Clean domain model for Crop, decoupled from database and network layers.
 */
data class Crop(
    val id: String,
    val nameNe: String,
    val nameEn: String,
    val imageUrl: String
)

