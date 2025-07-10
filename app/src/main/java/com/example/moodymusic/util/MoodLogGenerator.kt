package com.example.moodymusic.util

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.moodymusic.model.MoodLog
import com.example.moodymusic.model.MoodLogGroup
import com.example.moodymusic.model.MoodType
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.util.*
import kotlin.random.Random

@RequiresApi(Build.VERSION_CODES.O)

object MoodLogGenerator {
//
//    private val moodOptions = MoodType.values()
//
//    // Generate weekly mood logs (list of weeks)
//
//    fun generateWeeklyData(weeks: Int = 5): List<List<MoodLog>> {
//        val today = LocalDate.now()
//        return (0 until weeks).map { weekOffset ->
//            val start = today.minusWeeks((weeks - 1 - weekOffset).toLong()).with(java.time.DayOfWeek.MONDAY)
//            (0..6).map { dayOffset ->
//                val date = start.plusDays(dayOffset.toLong())
//                MoodLog(
//                    date = date,
//                    mood = moodOptions.random()
//                )
//            }
//        }
//    }
//
//    fun getWeekRangeText(weekIndex: Int): String {
//        val today = LocalDate.now()
//        val start = today.minusWeeks((4 - weekIndex).toLong()).with(java.time.DayOfWeek.MONDAY)
//        val end = start.plusDays(6)
//        return "${start.dayOfMonth} ${start.month.name.lowercase().replaceFirstChar { it.uppercase() }} - ${end.dayOfMonth} ${end.month.name.lowercase().replaceFirstChar { it.uppercase() }}, ${end.year}"
//    }
//
//    // Generate monthly mood logs (each month = list)
//    fun generateMonthlyData(months: Int = 6): List<List<MoodLog>> {
//        val today = LocalDate.now()
//        return (0 until months).map { monthOffset ->
//            val start = today.minusMonths((months - 1 - monthOffset).toLong()).withDayOfMonth(1)
//            val daysInMonth = start.lengthOfMonth()
//            (1..daysInMonth).map { day ->
//                val date = start.withDayOfMonth(day)
//                MoodLog(
//                    date = date,
//                    mood = moodOptions.random()
//                )
//            }
//        }
//    }
//
//    fun summarizeToWeeklyBars(monthLogs: List<MoodLog>): List<MoodLogGroup> {
//        return monthLogs.chunked(7).mapIndexed { index, chunk ->
//            MoodLogGroup(label = "W${index + 1}", logs = chunk)
//        }
//    }
//
//    // Generate yearly mood logs
//    fun generateYearlyData(years: Int = 2): List<List<MoodLog>> {
//        val thisYear = LocalDate.now().year
//        return (0 until years).map { yearOffset ->
//            val start = LocalDate.of(thisYear - (years - 1 - yearOffset), 1, 1)
//            (0..364).mapNotNull { dayOffset ->
//                val date = start.plusDays(dayOffset.toLong())
//                if (date.isAfter(LocalDate.now())) null
//                else MoodLog(
//                    date = date,
//                    mood = moodOptions.random()
//                )
//            }
//        }
//    }
//
//    fun summarizeToMonthlyBars(yearLogs: List<MoodLog>): List<MoodLogGroup> {
//        return (1..12).map { month ->
//            val logs = yearLogs.filter { it.date.monthValue == month }
//            val label = monthLabel(month)
//            MoodLogGroup(label = label, logs = logs)
//        }
//    }
//
//    private fun monthLabel(month: Int): String {
//        return Month.of(month).getDisplayName(TextStyle.SHORT, Locale.getDefault())
//    }
}
