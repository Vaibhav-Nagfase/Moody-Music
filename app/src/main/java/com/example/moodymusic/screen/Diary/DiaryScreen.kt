package com.example.moodymusic.screen.Diary

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DiaryScreen() {
    val pagerState = rememberPagerState(initialPage = 0)
    val coroutineScope = rememberCoroutineScope()
    val tabs = listOf("Weekly", "Monthly", "Yearly")

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)
        .padding(8.dp)) {

        Text(
            text = "Mood Chart",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
        )

        // TabRow for swipeable pager
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            backgroundColor = Color.Transparent,
            contentColor = Color(0xFF4CAF50)
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title, fontSize = 14.sp) },
                    selected = pagerState.currentPage == index,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        HorizontalPager(count = tabs.size, state = pagerState) { page ->
            when (page) {
                0 -> WeeklyPager()
                1 -> MonthlyPager()
                2 -> YearlyPager()
            }
        }
    }
}
