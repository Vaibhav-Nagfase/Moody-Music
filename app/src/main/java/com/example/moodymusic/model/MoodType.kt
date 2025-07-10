package com.example.moodymusic.model

import androidx.compose.ui.graphics.Color

enum class MoodType(val emoji: String, val color: Color) {
    HAPPY("😄", Color(0xFF4CAF50)),
    CALM("😊", Color(0xFF8BC34A)),
    NEUTRAL("😐", Color(0xFFFFC107)),
    SAD("😢", Color(0xFFFF9800)),
    ANGRY("😠", Color(0xFFF44336)),
    ROMANTIC("🥰", Color(0xFFE91E63))
}
