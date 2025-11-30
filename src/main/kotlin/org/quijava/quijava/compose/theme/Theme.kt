package org.quijava.quijava.compose.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = JavaBlueLight,
    onPrimary = JavaGrayDark,
    secondary = JavaOrangeDark,
    onSecondary = JavaGrayDark,
    tertiary = JavaGray,
    background = JavaGrayDark,
    surface = Color(0xFF1E1E1E),
    onBackground = Color(0xFFE8F4F8),
    onSurface = Color(0xFFE8F4F8)
)

private val LightColorScheme = lightColorScheme(
    primary = JavaBlue,
    onPrimary = Color.White,
    secondary = JavaOrange,
    onSecondary = Color.White,
    tertiary = JavaGray,
    background = JavaGrayLight,
    surface = Color.White,
    onBackground = JavaGrayDark,
    onSurface = JavaGrayDark
)

@Composable
fun QuiJavaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
