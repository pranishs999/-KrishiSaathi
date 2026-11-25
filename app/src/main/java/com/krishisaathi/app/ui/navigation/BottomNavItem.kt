
package com.krishisaathi.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Agriculture
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
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
        title = "बालीहरू",
        icon = Icons.Outlined.Agriculture
    )

    data object Helpline : BottomNavItem(
        route = "helpline",
        title = "किसान हेल्पलाइन",
        icon = Icons.Outlined.SupportAgent
    )

    data object Profile : BottomNavItem(
        route = "profile",
        title = "मेरो विवरण",
        icon = Icons.Outlined.Person
    )
}

// Explicit 4-tab navigation destination list.
val bottomNavItems: List<BottomNavItem> = listOf(
    BottomNavItem.Home,
    BottomNavItem.Crops,
    BottomNavItem.Helpline,
    BottomNavItem.Profile
)
