package com.example.scryptem.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.example.scryptem.presentation.settings.SettingsViewModel

private val DarkColorScheme = darkColorScheme(
    primary = OrangePrimary,
    secondary = OrangeSecondary,
    tertiary = OrangeTertiary,

    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.Black
)

private val LightColorScheme = lightColorScheme(
    primary = OrangePrimary,
    secondary = OrangeSecondary,
    tertiary = OrangeTertiary,

    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.Black

)

@Composable
fun ScryptemTheme(
    settingsViewModel: SettingsViewModel,
    content: @Composable () -> Unit
) {
    val theme by settingsViewModel.theme.collectAsState()
    val darkTheme = when (theme) {
        "Dark" -> true
        "Light" -> false
        else -> isSystemInDarkTheme()
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}