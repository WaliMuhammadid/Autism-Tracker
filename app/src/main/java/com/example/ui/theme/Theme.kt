package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = Color(0xFF80CBC4), // Calm soft teal primary for dark mode
    onPrimary = Color(0xFF00332C),
    primaryContainer = Color(0xFF004D40),
    onPrimaryContainer = Color(0xFFE0F2F1),
    secondary = Color(0xFFB39DDB), // Lavender accent
    onSecondary = Color(0xFF311B92),
    secondaryContainer = Color(0xFF4527A0),
    onSecondaryContainer = Color(0xFFEDE7F6),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onBackground = Color(0xFFE0E0E0),
    onSurface = Color(0xFFE0E0E0)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = SoftTeal, // Calming core primary SoftTeal (0xFF00796B)
    onPrimary = Color.White,
    primaryContainer = LightTeal, // Soft light-teal base
    onPrimaryContainer = Color(0xFF004D40),
    secondary = LavenderMedium, // Calming secondary Purple for Clinician context
    onSecondary = Color.White,
    secondaryContainer = LightLavender,
    onSecondaryContainer = Color(0xFF311B92),
    background = Color(0xFFFAFAFA),
    surface = Color.White,
    onBackground = Color(0xFF212121),
    onSurface = Color(0xFF212121)
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Keep our custom beautifully designed theme consistent across all Android versions
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
