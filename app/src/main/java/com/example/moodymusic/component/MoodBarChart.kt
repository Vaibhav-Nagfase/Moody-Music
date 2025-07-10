package com.example.moodymusic.component

import android.os.Build
import android.graphics.Color as AndroidColor
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.moodymusic.model.MoodLog
import com.example.moodymusic.model.MoodLogGroup
import com.example.moodymusic.model.MoodType
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MoodBarChart(
    logs: List<MoodLogGroup>,
    onBarClick: (List<MoodLog>) -> Unit,
    yAxisMax: Float = 7f,
    showMonthNames: Boolean = false
) {
    AndroidView(
        factory = { context ->
            BarChart(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    500
                )
                setTouchEnabled(true)
                setPinchZoom(false)
                description = Description().apply { text = "" }
                axisRight.isEnabled = false
                axisLeft.axisMinimum = 0f
                axisLeft.axisMaximum = yAxisMax
                axisLeft.setDrawGridLines(false)
                xAxis.setDrawGridLines(false)
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.granularity = 1f
                legend.isEnabled = false
            }
        },
        update = { chart ->
            val entries = logs.mapIndexed { index, group ->
                BarEntry(index.toFloat(), group.count.toFloat().coerceAtMost(yAxisMax))
            }

            val colors = logs.map { it.dominantMood.color.toArgb() }

            val dataSet = BarDataSet(entries, "Mood").apply {
                setColors(colors)
                valueTextSize = 12f
                valueTextColor = AndroidColor.DKGRAY
            }

            // ✅ Show month names if enabled
            val xLabels = logs.map {
                if (showMonthNames) {
                    try {
                        val monthName = Month.of(it.label.toInt()).getDisplayName(TextStyle.SHORT, Locale.getDefault())
                        "$monthName ${it.dominantMood.emoji}"
                    } catch (e: Exception) {
                        it.labelWithEmoji
                    }
                } else {
                    it.labelWithEmoji
                }
            }

            chart.xAxis.valueFormatter = IndexAxisValueFormatter(xLabels)
            chart.data = BarData(dataSet)
            chart.invalidate()

            chart.setOnChartValueSelectedListener(object : com.github.mikephil.charting.listener.OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: com.github.mikephil.charting.highlight.Highlight?) {
                    val index = e?.x?.toInt() ?: return
                    onBarClick(logs[index].logs)
                }

                override fun onNothingSelected() {}
            })
        },
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

