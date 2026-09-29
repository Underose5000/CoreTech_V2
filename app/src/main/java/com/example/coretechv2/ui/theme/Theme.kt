package com.example.coretechv2.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Defines the Material 3 colour scheme used when the application is displayed
 * using the dark theme.
 *
 * Most colours are explicitly defined to maintain a consistent CoreTech V2
 * appearance. Unused Material 3 colour roles are assigned [testColor] so that
 * they are visually distinct during development.
 */
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF0AA0D9),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF0879A6),
    onPrimaryContainer = Color(0xFFFFFFFF),
    secondary = Color(0xFF0AA0D9),
    background = Color(0xFF2C2C2C),
    outline = Color(0xFF0AA0D9),
    outlineVariant = Color(0xFF547F8F),
    onBackground = Color(0xFFFFFFFF),
    onSurface = Color(0xFFFFFFFF),
    onSurfaceVariant = Color(0xFF0AA0D9),
    inverseSurface = Color(0xFFFFFFFF),
    inverseOnSurface = Color(0xFF000000),
    surfaceBright = Color(0xFF484848),

    onSecondary = testColor,
    onTertiary = testColor,
    surface = testColor,
    primaryFixed = testColor,
    primaryFixedDim = testColor,
    onPrimaryFixed = testColor,
    onPrimaryFixedVariant = testColor,
    secondaryContainer = testColor,
    onSecondaryContainer = testColor,
    tertiaryContainer = testColor,
    onTertiaryContainer = testColor,
    surfaceVariant = testColor,
    tertiary = testColor,
    surfaceTint = testColor,
    inversePrimary = testColor,
    error = testColor,
    onError = testColor,
    errorContainer = testColor,
    onErrorContainer = testColor,
    scrim = testColor,

    surfaceContainer = testColor,
    surfaceContainerHigh = testColor,
    surfaceContainerHighest = testColor,
    surfaceContainerLow = testColor,
    surfaceContainerLowest = testColor,
    surfaceDim = testColor,
    secondaryFixed = testColor,
    secondaryFixedDim = testColor,
    onSecondaryFixed = testColor,
    onSecondaryFixedVariant = testColor,
    tertiaryFixed = testColor,
    tertiaryFixedDim = testColor,
    onTertiaryFixed = testColor,
    onTertiaryFixedVariant = testColor,
)

/**
 * Defines the Material 3 colour scheme used when the application is displayed
 * using the light theme.
 *
 * The colour palette uses the CoreTech V2 blue colour scheme while providing
 * dark text and controls that remain readable against the light background.
 */
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0879A6),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF0AA0D9),
    onPrimaryContainer = Color(0xFFFFFFFF),
    secondary = Color(0xFF0879A6),
    background = Color(0xFFE0E0E0),
    outline = Color(0xFF0879A6),
    outlineVariant = Color(0xFF547F8F),
    onBackground = Color(0xFF000000),
    onSurface = Color(0xFF000000),
    onSurfaceVariant = Color(0xFF0879A6),
    inverseSurface = Color(0xFF727272),
    inverseOnSurface = Color(0xFFFFFFFF),
    surfaceBright = Color(0xFFFFFFFF),
    surfaceDim = Color(0xFFA7B5B7),
)

/**
 * Applies the CoreTech V2 Material 3 theme to the supplied composable
 * content.
 *
 * The theme automatically selects either the light or dark colour scheme
 * based on the device's current system theme unless [darkTheme] is explicitly
 * provided.
 *
 * @param darkTheme determines whether the dark colour scheme should be used.
 * Defaults to the system's current dark-theme setting.
 * @param content the composable content to which the CoreTech V2 theme is
 * applied.
 */
@Composable
fun CoreTechV2Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}