package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = LuminaPrimaryDark,
    onPrimary = LuminaOnPrimaryDark,
    primaryContainer = LuminaPrimaryContainerDark,
    onPrimaryContainer = LuminaOnPrimaryContainerDark,
    secondary = LuminaSecondaryDark,
    onSecondary = LuminaOnSecondaryDark,
    secondaryContainer = LuminaSecondaryContainerDark,
    onSecondaryContainer = LuminaOnSecondaryContainerDark,
    tertiary = LuminaTertiaryDark,
    onTertiary = LuminaOnTertiaryDark,
    tertiaryContainer = LuminaTertiaryContainerDark,
    onTertiaryContainer = LuminaOnTertiaryContainerDark,
    background = LuminaBackgroundDark,
    onBackground = LuminaOnBackgroundDark,
    surface = LuminaSurfaceDark,
    onSurface = LuminaOnSurfaceDark,
    surfaceVariant = LuminaSurfaceVariantDark,
    onSurfaceVariant = LuminaOnSurfaceVariantDark
)

private val LightColorScheme = lightColorScheme(
    primary = LuminaPrimary,
    onPrimary = LuminaOnPrimary,
    primaryContainer = LuminaPrimaryContainer,
    onPrimaryContainer = LuminaOnPrimaryContainer,
    secondary = LuminaSecondary,
    onSecondary = LuminaOnSecondary,
    secondaryContainer = LuminaSecondaryContainer,
    onSecondaryContainer = LuminaOnSecondaryContainer,
    tertiary = LuminaTertiary,
    onTertiary = LuminaOnTertiary,
    tertiaryContainer = LuminaTertiaryContainer,
    onTertiaryContainer = LuminaOnTertiaryContainer,
    background = LuminaBackground,
    onBackground = LuminaOnBackground,
    surface = LuminaSurface,
    onSurface = LuminaOnSurface,
    surfaceVariant = LuminaSurfaceVariant,
    onSurfaceVariant = LuminaOnSurfaceVariant
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set default to false to force our beautiful custom brand scheme
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
