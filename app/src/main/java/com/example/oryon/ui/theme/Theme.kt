package com.example.oryon.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColors = lightColorScheme(
    primary = primary_500_light,
    onPrimary = neutral_100_light,
    background = neutral_100_light,
    onBackground = neutral_900_light,
    surface = neutral_200_light,
    onSurface = neutral_800_light,
    surfaceVariant = neutral_300_light,
    onSurfaceVariant = neutral_600_light
)

private val DarkColors = darkColorScheme(
    primary = primary_500_dark,
    onPrimary = neutral_100_dark,
    background = neutral_900_dark,
    onBackground = neutral_200_dark,
    surface = neutral_800_dark,
    onSurface = neutral_300_dark,
    surfaceVariant = neutral_600_dark,
    onSurfaceVariant = neutral_400_dark

)

@Composable
fun OryonTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    content: @Composable () -> Unit
) {
    //val colorScheme = if (darkTheme) DarkColors else LightColors
    val colorScheme = DarkColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}