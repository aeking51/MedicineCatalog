package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

fun createTypography(isGlass: Boolean): Typography {
    val headingShadow = if (isGlass) Shadow(
        color = Color(0x80FFFFFF),
        offset = Offset(0f, 1f),
        blurRadius = 4f
    ) else null

    val bodyShadow = if (isGlass) Shadow(
        color = Color(0x66FFFFFF),
        offset = Offset(0f, 1f),
        blurRadius = 2.5f
    ) else null

    return Typography(
        headlineLarge = TextStyle(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            lineHeight = 34.sp,
            letterSpacing = 0.sp,
            color = Color.Unspecified,
            shadow = headingShadow
        ),
        headlineMedium = TextStyle(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
            lineHeight = 28.sp,
            letterSpacing = 0.sp,
            color = Color.Unspecified,
            shadow = headingShadow
        ),
        headlineSmall = TextStyle(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.sp,
            color = Color.Unspecified,
            shadow = headingShadow
        ),
        titleLarge = TextStyle(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            lineHeight = 26.sp,
            letterSpacing = 0.sp,
            color = Color.Unspecified,
            shadow = headingShadow
        ),
        titleMedium = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.15.sp,
            color = Color.Unspecified,
            shadow = headingShadow
        ),
        titleSmall = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            letterSpacing = 0.1.sp,
            color = Color.Unspecified,
            shadow = headingShadow
        ),
        bodyLarge = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp,
            lineHeight = 22.sp,
            letterSpacing = 0.25.sp,
            color = Color.Unspecified,
            shadow = bodyShadow
        ),
        bodyMedium = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            letterSpacing = 0.25.sp,
            color = Color.Unspecified,
            shadow = bodyShadow
        ),
        bodySmall = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.4.sp,
            color = Color.Unspecified,
            shadow = bodyShadow
        ),
        labelLarge = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp,
            color = Color.Unspecified,
            shadow = bodyShadow
        ),
        labelMedium = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 10.sp,
            lineHeight = 14.sp,
            letterSpacing = 0.5.sp,
            color = Color.Unspecified,
            shadow = bodyShadow
        ),
        labelSmall = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp,
            lineHeight = 12.sp,
            letterSpacing = 0.8.sp,
            color = Color.Unspecified,
            shadow = bodyShadow
        )
    )
}

// Natural Tones Typography with Classical Serif Headers & Modern Clean Body
val Typography = createTypography(isGlass = false)
val GlassTypography = createTypography(isGlass = true)
