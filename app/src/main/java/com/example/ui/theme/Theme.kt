package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = CyanAccent,
    secondary = EmeraldSuccess,
    tertiary = AmberWarning,
    background = SlateDarker,
    surface = SlateSurface,
    surfaceVariant = SlateSurfaceVariant,
    onPrimary = SlateDarker,
    onSecondary = SlateDarker,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = RedHazard
)

private val LightColorScheme = darkColorScheme(
    primary = CyanAccent,
    secondary = EmeraldSuccess,
    tertiary = AmberWarning,
    background = SlateDarker,
    surface = SlateSurface,
    surfaceVariant = SlateSurfaceVariant,
    onPrimary = SlateDarker,
    onSecondary = SlateDarker,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = RedHazard
)

@Composable
fun VisionSceneTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
