package com.example.ui.theme

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

private val LightColorScheme = lightColorScheme(
    primary = SleekNavyPrimary,
    onPrimary = Color.White,
    primaryContainer = SleekBlueContainer,
    onPrimaryContainer = SleekOnBlueContainer,
    secondary = SleekEmerald,
    onSecondary = Color.White,
    secondaryContainer = SleekEmeraldBg,
    onSecondaryContainer = SleekEmerald,
    tertiary = SleekRose,
    onTertiary = Color.White,
    tertiaryContainer = SleekRoseBg,
    onTertiaryContainer = SleekRose,
    background = SleekBackground,
    onBackground = SleekTextPrimary,
    surface = SleekSurface,
    onSurface = SleekTextPrimary,
    surfaceVariant = SleekSurfaceVariant,
    onSurfaceVariant = SleekTextSecondary,
    outline = SleekCardBorder,
    error = SleekRose,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = SleekBlueContainer,
    onPrimary = SleekNavyDark,
    primaryContainer = SleekNavyPrimary,
    onPrimaryContainer = SleekBlueContainer,
    secondary = SleekEmeraldLight,
    onSecondary = Color(0xFF003822),
    secondaryContainer = Color(0xFF064E3B),
    onSecondaryContainer = SleekEmeraldLight,
    tertiary = SleekRoseLight,
    onTertiary = Color(0xFF4C0519),
    tertiaryContainer = Color(0xFF881337),
    onTertiaryContainer = SleekRoseLight,
    background = SleekDarkBackground,
    onBackground = Color(0xFFE2E8F0),
    surface = SleekDarkSurface,
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = SleekDarkSurfaceVariant,
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = SleekDarkCardBorder,
    error = SleekRoseLight,
    onError = Color(0xFF4C0519)
)

@Composable
fun MaqalTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
