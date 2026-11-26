package com.krishisaathi.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Agriculture
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.SupportAgent
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Home : BottomNavItem(
        route = "home",
        title = "गृहपृष्ठ",
        icon = Icons.Outlined.Home
    )

    data object Crops : BottomNavItem(
        route = "crops",
        title = "बाली",
        icon = Icons.Outlined.Agriculture
    )

    data object Livestock : BottomNavItem(
        route = "livestock",
        title = "पशुपालन",
        icon = Icons.Outlined.Pets
    )

    data object Helpline : BottomNavItem(
        route = "helpline",
        title = "हेल्पलाइन",
        icon = Icons.Outlined.SupportAgent
    )

    data object Profile : BottomNavItem(
        route = "profile",
        title = "प्रोफाइल",
        icon = Icons.Outlined.Person
    )
}

val bottomNavItems: List<BottomNavItem> = listOf(
    BottomNavItem.Home,
    BottomNavItem.Crops,
    BottomNavItem.Livestock,
    BottomNavItem.Helpline,
    BottomNavItem.Profile
)
