package com.example.moodymusic.screen

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.moodymusic.R
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextButton
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButtonDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.*
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.moodymusic.Screen
import com.example.moodymusic.ViewModel.PlaylistViewModel
import com.example.moodymusic.data.SongModel
import com.example.moodymusic.data.SpotifySong


@Composable
fun Playlist_Screen(
    navController: NavController,
    playlistViewModel: PlaylistViewModel
) {
    val playlists by playlistViewModel.allPlaylists.collectAsState()
    val showDialog = remember { mutableStateOf(false) }
    val newPlaylistName = remember { mutableStateOf("") }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        playlistViewModel.loadAllPlaylists()
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // Background Image
        Image(
            painter = painterResource(id = R.drawable.playlist_back3),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()

        )


        // Playlist Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 12.dp)
        ) {
            items(playlists) { playlist ->
                BrowserItem(cat = playlist, drawable = R.drawable.baseline_category_24) {
                    playlistViewModel.selectedPlaylistName = playlist
                    navController.navigate(Screen.PlaylistSongScreen.route)
                }
            }
        }

        // FAB to create new playlist
        FloatingActionButton(
            onClick = { showDialog.value = true },
            backgroundColor = Color(0xFF00FDD4),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            elevation = FloatingActionButtonDefaults.elevation(8.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.Black)
        }

        // Create Playlist Dialog
        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = {
                    Text("Create New Playlist", fontWeight = FontWeight.Bold)
                },
                text = {
                    OutlinedTextField(
                        value = newPlaylistName.value,
                        onValueChange = { newPlaylistName.value = it },
                        label = { Text("Playlist Name") },
                        singleLine = true
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        val name = newPlaylistName.value.trim()
                        if (name.isNotBlank() && !playlists.contains(name)) {
                            playlistViewModel.createEmptyPlaylist(name) // Adds empty list
                        }else{
                            Toast.makeText(context, "Error : Playlist name required or already exist", Toast.LENGTH_SHORT).show()
                        }
                        newPlaylistName.value = ""
                        showDialog.value = false
                    }) {
                        Text("Create")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDialog.value = false
                        newPlaylistName.value = ""
                    }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun BrowserItem(cat: String, drawable: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(12.dp)
            .size(180.dp)
            .clickable { onClick() },
        border = BorderStroke(1.dp, Color(0xFFCCCCCC)),
        backgroundColor = Color.White,
        elevation = 8.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().padding(8.dp)
        ) {
            Icon(
                painter = painterResource(id = drawable),
                contentDescription = cat,
                tint = Color(0xFF00FDD4),
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(cat, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}
