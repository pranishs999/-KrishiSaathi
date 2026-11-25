package com.krishisaathi.app.domain.model

/**
 * Domain model for farm income/expense record — "फार्मको हिसाब कसरी राख्ने?"
 */
data class FarmRecord(
    val id: String,
    val type: FarmRecordType,
    val categoryNe: String,
    val amount: Double,
    val descriptionNe: String,
    val cropNameNe: String?,
    val dateMillis: Long,
    val createdAt: Long
)

enum class FarmRecordType(val value: String, val labelNe: String) {
    INCOME("income", "आम्दानी"),
    EXPENSE("expense", "खर्च");

    companion object {
        fun fromValue(value: String) = entries.find { it.value == value } ?: EXPENSE
    }
}

/**
 * Predefined Nepali expense categories for quick entry.
 */
object FarmRecordCategories {
    val expenseCategories = listOf(
        "बीउ बिजन",
        "मलखाद",
        "विषादी",
        "मजदुरी / ज्याला",
        "सिँचाइ",
        "यन्त्र / उपकरण",
        "ढुवानी",
        "भण्डारण",
        "अन्य खर्च"
    )

    val incomeCategories = listOf(
        "बाली बिक्री",
        "तरकारी बिक्री",
        "फलफूल बिक्री",
        "मसला बिक्री",
        "अनुदान / सहायता",
        "अन्य आम्दानी"
    )
}
