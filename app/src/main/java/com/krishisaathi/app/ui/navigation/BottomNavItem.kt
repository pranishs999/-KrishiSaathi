
package com.krishisaathi.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Grass
import androidx.compose.material.icons.outlined.Home
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
        icon = Icons.Outlined.Grass
    )

    data object FarmRecords : BottomNavItem(
        route = "farm_records",
        title = "फार्म हिसाब",
        icon = Icons.Outlined.AccountBalanceWallet
    )

    data object Helpline : BottomNavItem(
        route = "helpline",
        title = "हेल्पलाइन",
        icon = Icons.Outlined.Call
    )
}

// Explicit navigation destination list.
val bottomNavItems: List<BottomNavItem> = listOf(
    BottomNavItem.Home,
    BottomNavItem.Crops,
    BottomNavItem.FarmRecords,
    BottomNavItem.Helpline
)
