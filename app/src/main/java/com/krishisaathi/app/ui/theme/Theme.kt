package com.krishisaathi.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Strict Light Mode Color Scheme for Bakaiya Krishi Saathi.
 * Dark mode is explicitly disabled per the master design specification.
 */
private val BakaiyaLightColorScheme = lightColorScheme(
    primary = ForestGreen,
    onPrimary = NaturalIvory,
    primaryContainer = LightGreen,
    onPrimaryContainer = DeepCharcoal,
    secondary = LeafGreen,
    onSecondary = NaturalIvory,
    secondaryContainer = SoftSage,
    onSecondaryContainer = DeepCharcoal,
    tertiary = MutedGreenGrey,
    onTertiary = PureWhite,
    background = NaturalIvory,
    onBackground = DeepCharcoal,
    surface = LightGreen,
    onSurface = DeepCharcoal,
    surfaceVariant = SoftSage,
    onSurfaceVariant = MutedGreenGrey,
    outline = DividerSage,
    outlineVariant = DividerSage,
    error = ErrorMaroon,
    onError = PureWhite,
    errorContainer = SoftSage,
    onErrorContainer = ErrorMaroon
)

@Composable
fun KrishiSaathiTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = BakaiyaLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = true
                insetsController.isAppearanceLightNavigationBars = true
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
