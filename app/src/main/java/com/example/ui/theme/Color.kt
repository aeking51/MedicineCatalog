package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

// Default Fallback Color Constants
val DefaultNaturalBackground = Color(0xFFFAF9F6)     // Soft Alabaster / Off-white
val DefaultNaturalTextPrimary = Color(0xFF2C2C2C)    // Deep Charcoal Body Text
val DefaultNaturalTextHeading = Color(0xFF3F4238)    // Deep Olive Charcoal
val DefaultNaturalOliveMuted = Color(0xFF6B705C)     // Muted Sage Olive
val DefaultNaturalEarthGold = Color(0xFF8C7851)      // Warm Earth Ochre / Gold
val DefaultNaturalTerracotta = Color(0xFF5D4037)     // Warm Earth Terracotta Brown

val DefaultNaturalMossPrimary = Color(0xFF5A5A40)    // Rich Moss / Deep Olive Green
val DefaultNaturalMossDark = Color(0xFF434839)

val DefaultNaturalSageContainer = Color(0xFFDCE5D1)  // Soft Sage Pill / Card
val DefaultNaturalSageBorder = Color(0xFFC5D1B3)
val DefaultNaturalParchmentContainer = Color(0xFFF1E7D0) // Warm Parchment Cream Card
val DefaultNaturalParchmentBorder = Color(0xFFE5DCC5)
val DefaultNaturalCardBorder = Color(0xFFE5E2DA)     // Standard Card Border
val DefaultNaturalCardSurface = Color(0xFFFFFFFF)

// Dynamic Composable Color Accessors that react across the ENTIRE application
// whenever the user switches between Light, Glass, and Dark themes.
val NaturalBackground: Color
    @Composable
    get() = AyurTheme.colors.background

val NaturalTextPrimary: Color
    @Composable
    get() = AyurTheme.colors.primaryText

val NaturalTextHeading: Color
    @Composable
    get() = AyurTheme.colors.headingText

val NaturalOliveMuted: Color
    @Composable
    get() = AyurTheme.colors.mutedText

val NaturalEarthGold: Color
    @Composable
    get() = AyurTheme.colors.earthGold

val NaturalTerracotta: Color
    @Composable
    get() = AyurTheme.colors.terracotta

val NaturalMossPrimary: Color
    @Composable
    get() = AyurTheme.colors.primaryBrand

val NaturalMossDark: Color
    @Composable
    get() = AyurTheme.colors.primaryBrandDark

val NaturalSageContainer: Color
    @Composable
    get() = AyurTheme.colors.sageContainer

val NaturalSageBorder: Color
    @Composable
    get() = AyurTheme.colors.sageBorder

val NaturalParchmentContainer: Color
    @Composable
    get() = AyurTheme.colors.parchmentContainer

val NaturalParchmentBorder: Color
    @Composable
    get() = AyurTheme.colors.parchmentBorder

val NaturalCardBorder: Color
    @Composable
    get() = AyurTheme.colors.cardBorder

val NaturalCardSurface: Color
    @Composable
    get() = AyurTheme.colors.cardBg

val NaturalPittaGreen: Color
    @Composable
    get() = AyurTheme.colors.pittaGreen

// Additional Accents
val NaturalHydrationBlue = Color(0xFF669BBC)  // Soft Herbal Hydration Blue
val NaturalKaphaGold = Color(0xFFD4A373)      // Warm Kapha Sand
val NaturalVataViolet = Color(0xFF9E829C)     // Grounding Vata Violet
val NaturalProgressTrack = Color(0xFFF5F5F5)

