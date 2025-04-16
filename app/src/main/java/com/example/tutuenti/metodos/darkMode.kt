package com.example.tutuenti.metodos

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val LightColors = lightColorScheme(
    primary = Color(0xFF005C97),
    onPrimary = Color.White,
    background = Color(0xFFF5F5F5),
    onBackground = Color(0xFF333333),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF333333),
    secondary = Color(0xFF1D9BF0),
    onSecondary = Color.White
)


val DarkColors = darkColorScheme(
    primary = Color(0xFF1D9BF0),
    onPrimary = Color.Black,
    background = Color(0xFF121212),
    onBackground = Color(0xFFE0E0E0),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE0E0E0),
    secondary = Color(0xFF005C97),
    onSecondary = Color.White
)


