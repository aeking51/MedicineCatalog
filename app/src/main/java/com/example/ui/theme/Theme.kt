package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow

enum class GlassMotionProfile(
    val id: String,
    val title: String,
    val emoji: String,
    val description: String,
    val durationMs: Int
) {
    DEFAULT("default", "Default Profile", "🌿", "Natural balanced spring motion for all liquid glass reflections", 300),
    SNAPPY("snappy", "Snappy Motion", "⚡", "Fast 150ms high-responsiveness transitions with crisp rebound", 150),
    FLUID("fluid", "Fluid Motion", "💧", "Velvety smooth 500ms liquid easing with deep dampening", 500)
}

val LocalGlassMotionProfile = compositionLocalOf { GlassMotionProfile.DEFAULT }

enum class AppThemeMode(val displayName: String, val description: String) {
    LIGHT("Classic Light", "Warm herbal tones, sage green & natural parchment"),
    GLASS("Liquid Glass Theme", "Atmospheric dark frosted glass, glowing emerald & specular depth"),
    DARK("Dark Theme", "Botanical charcoal, eye-friendly contrast & night comfort")
}

val LocalAppThemeMode = compositionLocalOf { AppThemeMode.LIGHT }

/**
 * Universal Theme Tokens for the entire AyurGuide application.
 * When a user changes the theme (Light, Glass, Dark), these tokens automatically
 * provide the appropriate backgrounds, card surfaces, borders, text colors,
 * and container shades everywhere across all screens and components.
 */
data class AyurAppThemeColors(
    val background: Color,
    val surface: Color,
    val cardBg: Color,
    val cardBorder: Color,
    val headingText: Color,
    val primaryText: Color,
    val mutedText: Color,
    val innerTileBg: Color,
    val innerTileBorder: Color,
    val primaryBrand: Color,
    val primaryBrandDark: Color,
    val sageContainer: Color,
    val sageBorder: Color,
    val parchmentContainer: Color,
    val parchmentBorder: Color,
    val earthGold: Color,
    val terracotta: Color,
    val pittaGreen: Color,
    val isGlass: Boolean,
    val isDark: Boolean,
    val textShadow: Shadow? = null,
    val headerShadow: Shadow? = null
)

val LightAyurAppThemeColors = AyurAppThemeColors(
    background = Color(0xFFFAF9F6),
    surface = Color(0xFFFFFFFF),
    cardBg = Color(0xFFFFFFFF),
    cardBorder = Color(0xFFE5E2DA),
    headingText = Color(0xFF3F4238),
    primaryText = Color(0xFF2C2C2C),
    mutedText = Color(0xFF6B705C),
    innerTileBg = Color(0xFFF3F6F1),
    innerTileBorder = Color(0xFFE5E2DA),
    primaryBrand = Color(0xFF5A5A40),
    primaryBrandDark = Color(0xFF434839),
    sageContainer = Color(0xFFDCE5D1),
    sageBorder = Color(0xFFC5D1B3),
    parchmentContainer = Color(0xFFF1E7D0),
    parchmentBorder = Color(0xFFE5DCC5),
    earthGold = Color(0xFF8C7851),
    terracotta = Color(0xFF5D4037),
    pittaGreen = Color(0xFFA7C957),
    isGlass = false,
    isDark = false,
    textShadow = null,
    headerShadow = null
)

val DarkAyurAppThemeColors = AyurAppThemeColors(
    background = Color(0xFF131713),       // Deep Botanical Midnight
    surface = Color(0xFF1C221B),          // Dark Herb Slate
    cardBg = Color(0xFF1C221B),
    cardBorder = Color(0xFF2E382C),
    headingText = Color(0xFFF2F7EF),      // Luminous Herb Ivory
    primaryText = Color(0xFFEDEFEA),
    mutedText = Color(0xFFA8B4A4),
    innerTileBg = Color(0xFF252D24),
    innerTileBorder = Color(0xFF333E31),
    primaryBrand = Color(0xFFA7C957),     // Luminous herbal lime
    primaryBrandDark = Color(0xFF8BA646),
    sageContainer = Color(0xFF2A3727),
    sageBorder = Color(0xFF3D4F38),
    parchmentContainer = Color(0xFF2E2922),
    parchmentBorder = Color(0xFF453B2F),
    earthGold = Color(0xFFD4A373),
    terracotta = Color(0xFFE07A5F),
    pittaGreen = Color(0xFFA7C957),
    isGlass = false,
    isDark = true,
    textShadow = Shadow(
        color = Color(0x66000000),
        offset = Offset(0f, 1f),
        blurRadius = 2f
    ),
    headerShadow = null
)

val GlassAyurAppThemeColors = AyurAppThemeColors(
    background = Color(0x00000000),       // Transparent - reveals background imagery
    surface = Color(0xD9FFFFFF),          // 85% High-opacity crystal glass surface for maximal readability
    cardBg = Color(0xE0FFFFFF),           // 88% Frosted luminous white card
    cardBorder = Color(0xF2FFFFFF),       // 95% Specular diamond-cut luminous white rim
    headingText = Color(0xFF03190E),      // Crisp Deep Obsidian Emerald (maximum readability & contrast)
    primaryText = Color(0xFF072416),      // Deep Botanical Charcoal
    mutedText = Color(0xFF1B432E),        // Crisp Dark Forest Pine (never washed out)
    innerTileBg = Color(0xCCF1F8F4),      // 80% Soft Mint-White inner tile
    innerTileBorder = Color(0xE6FFFFFF),  // High-contrast clean border
    primaryBrand = Color(0xFF059669),     // Vibrant Emerald Brand
    primaryBrandDark = Color(0xFF047857), // Deep Emerald
    sageContainer = Color(0x3810B981),    // Jade Capsule with clear tint
    sageBorder = Color(0x9910B981),       // High contrast mint border
    parchmentContainer = Color(0xD9FFFBEB),// Cream Gold translucent card
    parchmentBorder = Color(0x99D97706),  // Amber border
    earthGold = Color(0xFF92400E),        // Rich Amber Ochre
    terracotta = Color(0xFFB91C1C),       // Vibrant Ruby Accent
    pittaGreen = Color(0xFF047857),
    isGlass = true,
    isDark = false,
    textShadow = Shadow(
        color = Color(0xB3FFFFFF),        // High-clarity luminous halo behind all text on glass
        offset = Offset(0f, 1f),
        blurRadius = 3f
    ),
    headerShadow = Shadow(
        color = Color(0xCC000000),        // Deep contrast crisp drop-shadow for headers
        offset = Offset(0f, 2f),
        blurRadius = 5f
    )
)

val LocalAyurAppThemeColors = compositionLocalOf { LightAyurAppThemeColors }

object AyurTheme {
    val colors: AyurAppThemeColors
        @Composable
        get() = LocalAyurAppThemeColors.current

    val mode: AppThemeMode
        @Composable
        get() = LocalAppThemeMode.current

    val motion: GlassMotionProfile
        @Composable
        get() = LocalGlassMotionProfile.current
}

private val NaturalLightColorScheme = lightColorScheme(
    primary = Color(0xFF5A5A40),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFDCE5D1),
    onPrimaryContainer = Color(0xFF3F4238),
    secondary = Color(0xFF6B705C),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFDCE5D1),
    onSecondaryContainer = Color(0xFF434839),
    tertiary = Color(0xFF8C7851),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFF1E7D0),
    onTertiaryContainer = Color(0xFF5D4037),
    background = Color(0xFFFAF9F6),
    onBackground = Color(0xFF2C2C2C),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF2C2C2C),
    surfaceVariant = Color(0xFFF1E7D0),
    onSurfaceVariant = Color(0xFF5D4037),
    outline = Color(0xFFE5E2DA),
    outlineVariant = Color(0xFFE5DCC5)
)

private val NaturalDarkColorScheme = darkColorScheme(
    primary = Color(0xFFA7C957), // Pitta Lime/Herb Green
    onPrimary = Color(0xFF141913),
    primaryContainer = Color(0xFF2A3727),
    onPrimaryContainer = Color(0xFFE3EDE0),
    secondary = Color(0xFFA8B4A4),
    onSecondary = Color(0xFF141913),
    secondaryContainer = Color(0xFF252F23),
    onSecondaryContainer = Color(0xFFDCE5D1),
    tertiary = Color(0xFFD4A373),
    onTertiary = Color(0xFF141913),
    background = Color(0xFF131713), // Deep Botanical Midnight
    onBackground = Color(0xFFEDEFEA),
    surface = Color(0xFF1C221B),   // Dark Herb Slate
    onSurface = Color(0xFFEDEFEA),
    surfaceVariant = Color(0xFF252D24),
    onSurfaceVariant = Color(0xFFB5C2B2),
    outline = Color(0xFF333E31),
    outlineVariant = Color(0xFF222920)
)

private val NaturalGlassColorScheme = darkColorScheme(
    primary = Color(0xFF10B981),         // Primary Vibrant Neon Emerald Green
    onPrimary = Color(0xFF041E14),
    primaryContainer = Color(0x3310B981), // Frosted Neon Emerald Pill
    onPrimaryContainer = Color(0xFFD1FAE5),
    secondary = Color(0xFF34D399),       // Mint Green
    onSecondary = Color(0xFF041E14),
    secondaryContainer = Color(0x2610B981),
    onSecondaryContainer = Color(0xFF6EE7B7),
    tertiary = Color(0xFF6EE7B7),
    onTertiary = Color(0xFF041E14),
    background = Color(0x00000000),       // Transparent - reveals background imagery
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0x9915241C),          // Translucent Liquid Frosted Glass
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0x66182A20),   // Recessed Liquid Glass
    onSurfaceVariant = Color(0xFFD1FAE5),
    outline = Color(0x38FFFFFF),          // Specular Frosted Glass Rim Reflection
    outlineVariant = Color(0x4D10B981)    // Emerald Glow Outline
)

@Composable
fun MyApplicationTheme(
    themeMode: AppThemeMode = AppThemeMode.LIGHT,
    motionProfile: GlassMotionProfile = GlassMotionProfile.DEFAULT,
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val appThemeColors = when (themeMode) {
        AppThemeMode.LIGHT -> LightAyurAppThemeColors
        AppThemeMode.DARK -> DarkAyurAppThemeColors
        AppThemeMode.GLASS -> GlassAyurAppThemeColors
    }

    val colorScheme = when (themeMode) {
        AppThemeMode.LIGHT -> NaturalLightColorScheme
        AppThemeMode.DARK -> NaturalDarkColorScheme
        AppThemeMode.GLASS -> NaturalGlassColorScheme
    }

    CompositionLocalProvider(
        LocalAppThemeMode provides themeMode,
        LocalAyurAppThemeColors provides appThemeColors,
        LocalGlassMotionProfile provides motionProfile
    ) {
        val activeTypography = if (themeMode == AppThemeMode.GLASS) GlassTypography else Typography
        MaterialTheme(
            colorScheme = colorScheme,
            typography = activeTypography,
            content = content
        )
    }
}
