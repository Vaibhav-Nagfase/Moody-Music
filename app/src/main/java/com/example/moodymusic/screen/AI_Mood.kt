package com.example.moodymusic.screen

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moodymusic.R
import java.util.*

@Composable
fun AI_Screen(padding: PaddingValues, onSearchClicked: (String) -> Unit) {

    Image(
        painter = painterResource(id = R.drawable.ai_back),
        contentDescription = "Background Image",
        contentScale = ContentScale.Fit,
        modifier = Modifier.fillMaxSize()
    )

    val context = LocalContext.current
    val data = remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    var isListening by remember { mutableStateOf(false) }
    var isSearching by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition()
    val dotCount by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
        isListening = false
        if (!spokenText.isNullOrEmpty()) {
            data.value = spokenText
        } else {
            Toast.makeText(context, "Didn't catch that", Toast.LENGTH_SHORT).show()
        }
    }

    fun startSpeechRecognition() {
        isListening = true
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now")
        }
        speechRecognizerLauncher.launch(intent)
    }

    val titleText = if (isListening) {
        "Listening" + ".".repeat(dotCount.toInt() % 4)
    } else if(isSearching){
        "Searching" + ".".repeat(dotCount.toInt() % 4)
    }else {
        "How do you feel today or What do you want to listen ? You can speak"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = titleText,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp, start = 8.dp, end = 8.dp),
                textAlign = TextAlign.Center
            )

            Box(
                modifier = Modifier
                    .size(350.dp)
                    .clickable { startSpeechRecognition() },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.recorder),
                    contentDescription = "Voice Recorder",
                    modifier = Modifier.size(300.dp)
                )
            }

            Spacer(Modifier.height(20.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                border = BorderStroke(width = 3.dp, color = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    BasicTextField(
                        value = data.value,
                        onValueChange = { data.value = it },
                        textStyle = TextStyle(color = Color.White, fontSize = 24.sp),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 50.dp, max = 112.dp)
                            .verticalScroll(scrollState)
                            .padding(12.dp),
                        maxLines = Int.MAX_VALUE,
                        singleLine = false,
                        decorationBox = { innerTextField -> innerTextField() }
                    )

                    IconButton(onClick = {
                        onSearchClicked(data.value)
                        isSearching = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            modifier = Modifier.size(32.dp),
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}
