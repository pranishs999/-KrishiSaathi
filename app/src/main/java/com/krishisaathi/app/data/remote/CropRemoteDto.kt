package com.krishisaathi.app.data.remote

/**
 * Data Transfer Object representing crop records from remote Supabase backend.
 */
data class CropRemoteDto(
    val id: String,
    val nameNe: String,
    val nameEn: String,
    val imageUrl: String
)

