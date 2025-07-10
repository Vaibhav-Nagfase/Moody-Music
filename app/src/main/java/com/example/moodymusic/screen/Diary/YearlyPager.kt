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
import androidx.compose.ui.text.font.FontWeight
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun YearlyPager() {
    val moodViewModel: MoodViewModel = viewModel()
    val allLogs by moodViewModel.moods.collectAsState()

    LaunchedEffect(Unit) {
        moodViewModel.fetchMoodLogs()
    }

    var currentYear by remember { mutableStateOf(LocalDate.now().year) }
    var breakdownData by remember { mutableStateOf<List<MoodLog>?>(null) }

    // Filter moods for the current year
    val yearLogs = allLogs.filter {
        val logDate = LocalDate.parse(it.date)
        logDate.year == currentYear
    }

    // Group logs by month (1 to 12)
    val groupedByMonth = yearLogs.groupBy { LocalDate.parse(it.date).monthValue }

    // Create MoodLogGroup for each month (Jan = 1, Dec = 12)
    val moodGroups = (1..12).map { month ->
        MoodLogGroup(
            label = month.toString(),
            logs = groupedByMonth[month] ?: emptyList()
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
                IconButton(onClick = { currentYear-- }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Previous Year")
                }
                Text(
                    text = "< Year $currentYear >",
                    fontSize = 14.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                )
                IconButton(onClick = { currentYear++ }) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Next Year")
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
                    yAxisMax = 31f,
                    showMonthNames = true
                )
            }
        }

        item {
            Text(
                text = "Year Wise Mood Entries",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
            )
        }

        items(yearLogs.sortedByDescending { LocalDate.parse(it.date) }) { log ->
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
