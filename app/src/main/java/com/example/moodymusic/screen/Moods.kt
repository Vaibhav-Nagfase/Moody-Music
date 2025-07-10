package com.example.moodymusic.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moodymusic.R

@Composable
fun Mood_Screen(padding:PaddingValues, onMoodSelected: (Mood) -> Unit){

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
                text = "Click on the emoji that suits your mood",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(
                    bottom = 64.dp,
                    start = 8.dp,
                    end = 8.dp
                ),
                textAlign = TextAlign.Center
            )
            EmojiWheel(onMoodSelected = onMoodSelected)
        }
    }

}

@Composable
private fun EmojiWheel(onMoodSelected: (Mood) -> Unit) {
    Box(modifier = Modifier.size(400.dp)) {
        EmojiButton(
            mood = Mood.HAPPY,
            modifier = Modifier.align(Alignment.Center),
            onClick = onMoodSelected
        )
        EmojiButton(
            mood = Mood.CALM,
            modifier = Modifier.align(Alignment.TopCenter),
            onClick = onMoodSelected
        )
        EmojiButton(
            mood = Mood.SAD,
            modifier = Modifier.align(Alignment.CenterEnd),
            onClick = onMoodSelected
        )
        EmojiButton(
            mood = Mood.ANGRY,
            modifier = Modifier.align(Alignment.CenterStart),
            onClick = onMoodSelected
        )
        EmojiButton(
            mood = Mood.ROMANTIC,
            modifier = Modifier.align(Alignment.BottomCenter),
            onClick = onMoodSelected
        )
    }
}

@Composable
private fun EmojiButton(mood: Mood, modifier: Modifier = Modifier, onClick: (Mood) -> Unit) {
    Box(
        modifier = modifier
            .size(if (mood == Mood.HAPPY) 160.dp else 112.dp)
            .clickable { onClick(mood) },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = mood.emojiResId),
            contentDescription = mood.name,
            modifier = Modifier.size(if (mood == Mood.HAPPY) 144.dp else 96.dp)
        )
    }
}


enum class Mood(val emojiResId: Int) {
    HAPPY(R.drawable.happy),
    SAD(R.drawable.sad),
    ANGRY(R.drawable.angry),
    CALM(R.drawable.calm),
    ROMANTIC(R.drawable.rommantic)
}