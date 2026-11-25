package com.krishisaathi.app.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Bakaiya Krishi Saathi (बकैया कृषि साथी) Approved Design Palette.
 * Strict Material 3 Light Mode Only.
 */

// 14 Approved Colors
val ForestGreen = Color(0xFF174D36)       // Primary brand, primary buttons, loading, active focus
val NaturalIvory = Color(0xFFF6F7EF)      // Canvas background, primary button text
val LightGreen = Color(0xFFDCE9DF)        // Cards background, search bar, secondary buttons, dialogs
val SoftSage = Color(0xFFE8EDE5)          // Text fields background, chip containers
val DeepCharcoal = Color(0xFF26352D)      // Primary body text, selected navigation state
val MutedGreenGrey = Color(0xFF69776D)    // Subtitle text, unselected navigation state
val LeafGreen = Color(0xFF6E9877)         // Supporting agricultural accent, tags
val DividerSage = Color(0xFFC8D8C9)       // Structural dividers, subtle outlines
val PlaceholderText = Color(0xFF8A9B8D)   // Form placeholders, hint text
val SuccessGreen = Color(0xFF27834A)      // Verified data badge, success status
val WarningOrange = Color(0xFFC56B2A)     // Seasonal advisory notice, warning alert
val ErrorMaroon = Color(0xFF8F3030)       // Error indicators, destructive actions
val DisabledGreen = Color(0xFFA8BDAA)     // Disabled buttons and controls
val PureWhite = Color(0xFFFFFFFF)         // Modal highlights, icon badges

// Backwards compatibility mappings for existing codebase references
val AgroHighlight = ForestGreen
val AgroHighlightDark = ForestGreen
val AgroHighlightLight = LeafGreen
val AgroHighlightContainer = LightGreen
val AgroOnHighlightContainer = DeepCharcoal
val CleanWhite = NaturalIvory
val CleanOffWhite = LightGreen
val CrispBlack = DeepCharcoal
val TextPrimary = DeepCharcoal
val TextSecondary = MutedGreenGrey
val BorderLight = DividerSage
