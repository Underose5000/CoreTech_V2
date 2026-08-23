package com.example.coretechv2.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

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
    surfaceBright = testColor,
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
)

@Composable
fun CoreTechV2Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = isSystemInDarkTheme(),
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