package com.example.moodymusic.component

import androidx.compose.runtime.Composable
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp


@Composable
fun ConnectSpotifyDialog(showDialog: Boolean,
                         onConnectClick: () -> Unit,
                         onDismiss: () -> Unit){

    // 🌟 Spotify connect dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { /* Do nothing - dialog stays open until user picks */ },
            title = { Text("Connect to Spotify", fontSize = 20.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) },
            text = { Text("To fetch and play songs in your moods, please connect your Spotify account.\n\nNote: Spotify Premium is required for playback.", fontSize = 16.sp) },
            confirmButton = {
                Button(
                    onClick = {
                        onConnectClick()
                        onDismiss()
                    }
                ) {
                    Text("Connect to Spotify")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { onDismiss() }
                ) {
                    Text("Maybe Later")
                }
            },
            backgroundColor = Color(0xFF280050),
            contentColor = Color.White
        )
    }

}


