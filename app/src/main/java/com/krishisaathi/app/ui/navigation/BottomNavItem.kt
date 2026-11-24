package com.krishisaathi.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Bottom navigation destinations with certified Devanagari titles.
 */
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem(
        route = "home",
        title = "गृहपृष्ठ",
        icon = Icons.Default.Home
    )

    object Crops : BottomNavItem(
        route = "crops",
        title = "बाली",
        icon = Icons.AutoMirrored.Filled.List
    )

    object Weather : BottomNavItem(
        route = "weather",
        title = "मौसम",
        icon = Icons.Default.DateRange
    )

    object Profile : BottomNavItem(
        route = "profile",
        title = "प्रोफाइल",
        icon = Icons.Default.Person
    )

    companion object {
        val items = listOf(Home, Crops, Weather, Profile)
    }
}

