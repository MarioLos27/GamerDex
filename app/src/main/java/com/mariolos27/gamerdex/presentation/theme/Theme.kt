package com.mariolos27.gamerdex.presentation.theme

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
import androidx.compose.material3.Typography
import com.mariolos27.gamerdex.presentation.theme.Typography

/**
 * Tema oscuro personalizado para GamerDex.
 * Diseño inspirado en gaming con colores vibrantes y contraste alto.
 */
private val GamerDexDarkColorScheme = darkColorScheme(
    primary = GamerPurple,           // Púrpura vibrante
    secondary = GamerCyan,           // Cyan brillante
    tertiary = GamerPink,            // Rosa neón
    background = DarkBackground,     // Fondo casi negro
    surface = DarkSurface,           // Tarjetas oscuras
    onBackground = TextPrimary,      // Texto sobre fondo
    onSurface = TextPrimary,         // Texto sobre superficie
    onPrimary = Color.White,         // Texto sobre primario
    onSecondary = Color.White,       // Texto sobre secundario
    onTertiary = Color.White,        // Texto sobre terciario
    error = AccentRed,               // Rojo para errores
    outline = TextTertiary           // Bordes
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun GamerDexTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,  // Deshabilitado para mantener consistencia gamer
    content: @Composable () -> Unit
) {
    // Forzar tema oscuro para GamerDex (experiencia gamer optimizada)
    val colorScheme = GamerDexDarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}