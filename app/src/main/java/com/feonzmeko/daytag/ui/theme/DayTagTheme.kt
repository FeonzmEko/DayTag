package com.feonzmeko.daytag.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.feonzmeko.daytag.data.AccentKeys
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object DayTagPalette {
    val Cream = Color(0xFFF0E4CC)
    val CreamLight = Color(0xFFF8EFDF)
    val Cocoa = Color(0xFF2D211A)
    val Muted = Color(0xFF796655)
    val Cinnabar = Color(0xFFA6442B)
    val Sage = Color(0xFF667352)
    val Ochre = Color(0xFF976A43)
    val Slate = Color(0xFF4B586C)
    val Hairline = Color(0x332D211A)
}

fun accentColor(key: String): Color = when (key) {
    AccentKeys.SAGE -> DayTagPalette.Sage
    AccentKeys.OCHRE -> DayTagPalette.Ochre
    AccentKeys.SLATE -> DayTagPalette.Slate
    else -> DayTagPalette.Cinnabar
}

fun accentName(key: String): String = when (key) {
    AccentKeys.SAGE -> "苔绿"
    AccentKeys.OCHRE -> "赭石"
    AccentKeys.SLATE -> "靛青"
    else -> "朱砂"
}

fun formatChineseDate(date: LocalDate): String =
    date.format(DateTimeFormatter.ofPattern("yyyy 年 M 月 d 日 · EEEE", Locale.SIMPLIFIED_CHINESE))

private val LightColors = lightColorScheme(
    primary = DayTagPalette.Cinnabar,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF1D4C8),
    onPrimaryContainer = DayTagPalette.Cocoa,
    background = DayTagPalette.Cream,
    onBackground = DayTagPalette.Cocoa,
    surface = DayTagPalette.CreamLight,
    onSurface = DayTagPalette.Cocoa,
    surfaceVariant = Color(0xFFE8D9C0),
    onSurfaceVariant = DayTagPalette.Muted,
    outline = DayTagPalette.Hairline,
    error = Color(0xFF9D3425),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFE29A82),
    onPrimary = Color(0xFF3D1106),
    background = Color(0xFF211A15),
    onBackground = Color(0xFFF5E9D7),
    surface = Color(0xFF2D241D),
    onSurface = Color(0xFFF5E9D7),
    surfaceVariant = Color(0xFF3A2D24),
    onSurfaceVariant = Color(0xFFD0BEAA),
    outline = Color(0x66F5E9D7),
)

private val DayTagTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Normal,
        fontSize = 92.sp,
        lineHeight = 88.sp,
        letterSpacing = (-4).sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 38.sp,
        lineHeight = 46.sp,
        letterSpacing = (-0.6).sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 29.sp,
        lineHeight = 37.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 23.sp,
        lineHeight = 30.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 16.sp,
        lineHeight = 25.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 14.sp,
        lineHeight = 22.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        letterSpacing = 0.2.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        letterSpacing = 0.6.sp,
    ),
)

@Composable
fun DayTagTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (false) DarkColors else LightColors,
        typography = DayTagTypography,
        content = content,
    )
}
