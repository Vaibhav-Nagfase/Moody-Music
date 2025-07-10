package com.example.moodymusic.model

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class MoodLog(
    val date: String = "", // e.g., "2025-07-01"
    val mood: String = ""  // e.g., "HAPPY"
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun dateText(): String {
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
        return LocalDate.parse(date).format(formatter)
    }

    fun toMoodType(): MoodType = MoodType.valueOf(mood)
}

