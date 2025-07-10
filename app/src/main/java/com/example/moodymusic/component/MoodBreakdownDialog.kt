package com.example.moodymusic.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moodymusic.model.MoodLog
import com.example.moodymusic.model.MoodType


@Composable
fun MoodBreakdownDialog(
    logs: List<MoodLog>,
    onDismiss: () -> Unit
) {
    val moodCount = logs.groupingBy { MoodType.valueOf(it.mood) }.eachCount()
    val total = logs.size
    val mostFrequentMood = moodCount.maxByOrNull { it.value }?.key ?: MoodType.NEUTRAL

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Mood Breakdown", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                moodCount.entries.sortedByDescending { it.value }.forEach { (mood, count) ->
                    val percentage = (count * 100f / total).toInt()
                    Text(
                        text = "${mood.emoji} ${mood.name.capitalize()} — $count mood(s) ($percentage%)",
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    "Your dominant mood was:",
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Text(
                    text = "${mostFrequentMood.emoji} ${mostFrequentMood.name.capitalize()}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = mostFrequentMood.color
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}
