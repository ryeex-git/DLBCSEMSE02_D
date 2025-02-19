package com.example.boardgamerapp.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val BoardGamerColorScheme = lightColorScheme(
    primary = Color(0xFF6200EE),
    secondary = Color(0xFF03DAC5),
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black
)

// Typography-Objekt für Material 3
private val BoardGamerTypography = Typography()

// Shapes manuell definieren, weil Shapes kein Companion Object mehr hat
private val BoardGamerShapes = androidx.compose.material3.Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(16.dp)
)

@Composable
fun BoardGamerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = BoardGamerColorScheme,
        typography = BoardGamerTypography,
        shapes = BoardGamerShapes,
        content = content
    )
}
