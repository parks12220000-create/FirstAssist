package com.firsttech.assistant.ui.theme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
private val DarkColors = darkColorScheme(
    primary = Color(0xFF3B82F6), surface = Color(0xFF0F1117),
    background = Color(0xFF0F1117), onSurface = Color.White, onBackground = Color.White
)
@Composable
fun FirstAssistTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = DarkColors, content = content)
}
