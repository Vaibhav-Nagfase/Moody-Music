package com.example.moodymusic.screen.Diary

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moodymusic.ViewModel.MoodViewModel
import com.example.moodymusic.component.MoodBarChart
import com.example.moodymusic.component.MoodBreakdownDialog
import com.example.moodymusic.component.MoodCard
import com.example.moodymusic.model.MoodLog
import com.example.moodymusic.model.MoodLogGroup
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeeklyPager() {
    val moodViewModel: MoodViewModel = viewModel()
    val allLogs by moodViewModel.moods.collectAsState()

    LaunchedEffect(Unit) {
        moodViewModel.fetchMoodLogs()
    }

    var currentWeekStart by remember { mutableStateOf(LocalDate.now().with(DayOfWeek.MONDAY)) }
    var breakdownData by remember { mutableStateOf<List<MoodLog>?>(null) }

    // Filter logs in the week range
    val weekLogs = allLogs.filter {
        val logDate = LocalDate.parse(it.date)
        !logDate.isBefore(currentWeekStart) && !logDate.isAfter(currentWeekStart.plusDays(6))
    }

    // Group into a single MoodLogGroup for the bar chart
    val moodGroup = MoodLogGroup(
        label = dateRangeText(currentWeekStart, currentWeekStart.plusDays(6)),
        logs = weekLogs
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { currentWeekStart = currentWeekStart.minusWeeks(1) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Previous Week")
                }

                Text(
                    text = dateRangeText(currentWeekStart, currentWeekStart.plusDays(6)),
                    fontSize = 14.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                )

                IconButton(onClick = { currentWeekStart = currentWeekStart.plusWeeks(1) }) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Next Week")
                }
            }
        }

        item {
            Card(
                elevation = 16.dp,
                backgroundColor = Color(0xFFF6F6F6),
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp)
            ) {
                MoodBarChart(
                    logs = listOf(moodGroup),
                    onBarClick = { breakdownData = it },
                    yAxisMax = 7f,
                    showMonthNames = false
                )
            }
        }

        item {
            Text(
                text = "Week Wise Mood Entries",
                fontSize = 16.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
            )
        }

        items(weekLogs.sortedByDescending { LocalDate.parse(it.date) }) { log ->
            MoodCard(log = log)
        }
    }

    if (breakdownData != null) {
        MoodBreakdownDialog(
            logs = breakdownData!!,
            onDismiss = { breakdownData = null }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun dateRangeText(start: LocalDate, end: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("dd MMM")
    return "${start.format(formatter)} - ${end.format(formatter)}"
}
