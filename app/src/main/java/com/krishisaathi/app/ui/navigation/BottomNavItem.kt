
package com.krishisaathi.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Agriculture
import androidx.compose.material.icons.outlined.Home
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
        title = "किसान हेल्पलाइन",
        icon = Icons.Outlined.SupportAgent
    )
}

// Exact 4-tab navigation per Section 3: गृहपृष्ठ, बाली, पशुपालन, किसान हेल्पलाइन
val bottomNavItems: List<BottomNavItem> = listOf(
    BottomNavItem.Home,
    BottomNavItem.Crops,
    BottomNavItem.Livestock,
    BottomNavItem.Helpline
)
