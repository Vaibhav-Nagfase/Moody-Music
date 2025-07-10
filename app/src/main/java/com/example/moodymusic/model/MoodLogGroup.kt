package com.example.moodymusic.model

data class MoodLogGroup(
    val label: String,
    val logs: List<MoodLog>
) {
    val dominantMood: MoodType
        get() = logs
            .map { MoodType.valueOf(it.mood) }
            .groupingBy { it }
            .eachCount()
            .maxByOrNull { it.value }
            ?.key ?: MoodType.NEUTRAL

    val count: Int get() = logs.size

    val labelWithEmoji: String
        get() = "$label\n${dominantMood.emoji}"
}