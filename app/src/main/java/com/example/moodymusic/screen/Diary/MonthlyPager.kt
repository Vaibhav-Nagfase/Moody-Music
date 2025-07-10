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
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MonthlyPager() {
    val moodViewModel: MoodViewModel = viewModel()
    val allLogs by moodViewModel.moods.collectAsState()

    LaunchedEffect(Unit) {
        moodViewModel.fetchMoodLogs()
    }

    var monthYear by remember { mutableStateOf(YearMonth.now()) }
    var breakdownData by remember { mutableStateOf<List<MoodLog>?>(null) }

    // Filter moods for the current month
    val monthLogs = allLogs.filter {
        val logDate = LocalDate.parse(it.date)
        logDate.year == monthYear.year && logDate.month == monthYear.month
    }

    // Group logs by week in the month (start on Monday)
    val weekFields = WeekFields.of(Locale.getDefault())
    val groupedByWeek = monthLogs.groupBy { LocalDate.parse(it.date).get(weekFields.weekOfMonth()) }

    val moodGroups = (1..5).map { weekNumber ->
        MoodLogGroup(
            label = "Week $weekNumber",
            logs = groupedByWeek[weekNumber] ?: emptyList()
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { monthYear = monthYear.minusMonths(1) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Previous Month")
                }
                Text(
                    text = "< ${monthYear.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${monthYear.year} >",
                    fontSize = 14.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                )
                IconButton(onClick = { monthYear = monthYear.plusMonths(1) }) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Next Month")
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
                    logs = moodGroups,
                    onBarClick = { breakdownData = it },
                    yAxisMax = 7f,
                    showMonthNames = false
                )
            }
        }

        item {
            Text(
                text = "Month Wise Mood Entries",
                fontSize = 16.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
            )
        }

        items(monthLogs.sortedByDescending { LocalDate.parse(it.date) }) { log ->
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
