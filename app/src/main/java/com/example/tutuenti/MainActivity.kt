package com.example.tutuenti

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.example.tutuenti.metodos.DarkColors
import com.example.tutuenti.metodos.LightColors
import com.example.tutuenti.nav.GrafoNav
import com.example.tutuenti.ui.theme.TuTuentiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isDarkModeEnabled by remember { mutableStateOf(false) }

            TuTuentiTheme(
                darkTheme = isDarkModeEnabled,
                colorScheme = if (isDarkModeEnabled) DarkColors else LightColors
            ) {
                GrafoNav(isDarkModeEnabled) { isDarkModeEnabled = it }
            }
        }
    }
}


@Composable
fun TuTuentiTheme(
    darkTheme: Boolean,
    colorScheme: ColorScheme,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}



