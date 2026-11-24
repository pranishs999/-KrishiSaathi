package com.krishisaathi.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = AgroHighlightDark,
    onPrimary = CleanWhite,
    primaryContainer = AgroHighlightContainer,
    onPrimaryContainer = AgroOnHighlightContainer,
    secondary = HarvestGold,
    onSecondary = CleanWhite,
    secondaryContainer = HarvestGoldContainer,
    onSecondaryContainer = HarvestOnGoldContainer,
    tertiary = EarthSlate,
    onTertiary = CleanWhite,
    background = CleanWhite,
    surface = CleanWhite,
    surfaceVariant = CleanOffWhite,
    onBackground = CrispBlack,
    onSurface = CrispBlack,
    onSurfaceVariant = TextSecondary,
    outline = BorderLight,
    outlineVariant = BorderHighlight
)

private val DarkColorScheme = darkColorScheme(
    primary = AgroHighlightLight,
    onPrimary = CrispBlack,
    primaryContainer = AgroHighlightDark,
    onPrimaryContainer = AgroHighlightContainer,
    secondary = HarvestGoldLight,
    onSecondary = CrispBlack,
    secondaryContainer = Color(0xFF451A03),
    onSecondaryContainer = HarvestGoldContainer,
    tertiary = EarthSlateLight,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    surfaceVariant = Color(0xFF2A2A2A),
    onBackground = CleanWhite,
    onSurface = CleanWhite,
    onSurfaceVariant = Color(0xFFD1D5DB),
    outline = Color(0xFF374151),
    outlineVariant = AgroHighlightDark
)

@Composable
fun KrishiSaathiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Keep dynamicColor false to ensure white background, black text, and brand green highlights
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                // In light theme (white bg), ensure status bar & nav bar icons are crisp dark/black
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

