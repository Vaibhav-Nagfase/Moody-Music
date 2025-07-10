package com.example.moodymusic.component

import android.content.Context
import android.graphics.Color
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import com.example.moodymusic.model.MoodLog
import kotlinx.coroutines.launch
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileOutputStream
import android.content.ContentValues
import android.provider.MediaStore
import com.example.moodymusic.model.MoodType


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DiaryExportDialog(
    showDialog: Boolean,
    context: Context,
    logs: List<MoodLog>,
    onDismiss: () -> Unit,
    scaffoldState: ScaffoldState // Pass this from the screen
) {
    val coroutineScope = rememberCoroutineScope()

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Export Diary") },
            text = { Text("Do you want to download your diary as a PDF?") },
            confirmButton = {
                TextButton(onClick = {
                    exportToPdf(
                        context = context,
                        logs = logs,
                        onComplete = { path ->
                            coroutineScope.launch {
                                scaffoldState.snackbarHostState.showSnackbar("Diary saved to Downloads :)")
                            }
                        },
                        onError = { msg ->
                            coroutineScope.launch {
                                scaffoldState.snackbarHostState.showSnackbar(msg)
                            }
                        }
                    )
                    onDismiss()
                }) {
                    Text("Download")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
}


@RequiresApi(Build.VERSION_CODES.O)
fun exportToPdf(
    context: Context,
    logs: List<MoodLog>,
    onComplete: (String) -> Unit,
    onError: (String) -> Unit
) {
    val document = PdfDocument()
    val paint = Paint().apply {
        textSize = 14f
        color = Color.BLACK
    }
    val titlePaint = Paint().apply {
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textSize = 20f
        color = Color.BLACK
    }

    val pageWidth = 595
    val pageHeight = 842
    var y = 100f

    var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
    var page = document.startPage(pageInfo)
    var canvas = page.canvas

    canvas.drawText("Diary Summary", 210f, 50f, titlePaint)

    logs.forEachIndexed { index, log ->
        if (y > pageHeight - 60f) {
            // Finish current page and start a new one
            document.finishPage(page)
            pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, index + 2).create()
            page = document.startPage(pageInfo)
            canvas = page.canvas
            y = 50f
        }

        val moodType = MoodType.valueOf(log.mood)
        val line = "${log.dateText()} - ${moodType.emoji} ${moodType.name.lowercase().replaceFirstChar { it.uppercase() }} Mood"
        canvas.drawText(line, 40f, y, paint)
        y += 25f
    }

    document.finishPage(page)

    try {
        val fileName = "MoodDiary_${System.currentTimeMillis()}.pdf"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val uri = context.contentResolver.insert(
            MediaStore.Files.getContentUri("external"),
            contentValues
        )

        if (uri != null) {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                document.writeTo(outputStream)
                onComplete("Saved to Downloads as $fileName")
            } ?: onError("Failed to open output stream.")
        } else {
            onError("Unable to create file in Downloads.")
        }
    } catch (e: Exception) {
        e.printStackTrace()
        onError("Failed to export PDF: ${e.message}")
    } finally {
        document.close()
    }
}

