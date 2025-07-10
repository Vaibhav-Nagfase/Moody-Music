package com.example.moodymusic.screen

import android.app.AlertDialog
import android.content.Context
import android.widget.EditText
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moodymusic.ViewModel.PlaylistViewModel
import com.example.moodymusic.data.SpotifySong
import com.example.moodymusic.ViewModel.SpotifyViewModel
import com.example.moodymusic.data.Playlist
import com.example.moodymusic.data.PlaylistRepository
import com.example.moodymusic.data.SongModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun Songs_Screen(mood: String, accessToken: String) {
    val context = LocalContext.current
    val spotifyViewModel  = remember { SpotifyViewModel() }
    val playlistViewModel: PlaylistViewModel = viewModel()

    val songs by spotifyViewModel.songs.collectAsState()
    val playlists by playlistViewModel.allPlaylists.collectAsState()

    val isRedirecting = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Featured song state
    val featuredSong = remember { mutableStateOf<SpotifySong?>(null) }

    LaunchedEffect(mood) {
        Log.d("SpotifyToken", accessToken)
        spotifyViewModel.fetchSongs(accessToken, mood)
        playlistViewModel.loadAllPlaylists()

    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color.Black)
    ) {

        // 🎯 Featured Song Display
        featuredSong.value?.let {
            SpotifyFeaturedSongCard(song = it, isRedirecting = isRedirecting.value, onPlay = {

                // Play in Spotify app
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(it.uri)
                intent.putExtra(Intent.EXTRA_REFERRER, Uri.parse("android-app://${context.packageName}"))
                context.startActivity(intent)

            })
        }

        Text(
            "Recommended Songs for \"$mood\"",
            modifier = Modifier.padding(16.dp),
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        LazyColumn {
            items(songs) { song ->
                SongListItem(song, playlists, playlistViewModel) {

                    // Set as featured
                    featuredSong.value = song

                    isRedirecting.value = true  // show loading or animation

                    coroutineScope.launch {
                        delay(1500)  // Wait
                        isRedirecting.value = false

                        // Play in Spotify app
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(song.uri)
                        intent.putExtra(
                            Intent.EXTRA_REFERRER,
                            Uri.parse("android-app://${context.packageName}")
                        )
                        context.startActivity(intent)

                    }
                }
            }
        }
    }
}



@Composable
fun SpotifyFeaturedSongCard(song: SpotifySong, isRedirecting:Boolean, onPlay: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.background(Color(0xFF36004F))) {

            Image(
                painter = rememberAsyncImagePainter(song.imageUrl),
                contentDescription = song.title,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Row(
                modifier = Modifier.padding(8.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f) // 💡 Text gets proper space, icon won't be pushed out
                ) {
                    Text(
                        song.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White,
                        maxLines = 1
                    )
                    Text(
                        song.artist,
                        color = Color.Gray,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                if (isRedirecting) {
                    androidx.compose.material.CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    IconButton(onClick = onPlay) {
                        Icon(
                            Icons.Default.PlayArrow,
                            modifier = Modifier.size(32.dp),
                            contentDescription = "Play",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}




@Composable
fun SongListItem(
    song: SpotifySong,
    playlists: List<String>,
    playlistViewModel: PlaylistViewModel,
    onPlayClick: () -> Unit
) {
    val context = LocalContext.current
    val showMenu = remember { mutableStateOf(false) }
    val showNestedAddMenu = remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onPlayClick() }
    ) {
        Card(modifier = Modifier.size(64.dp)) {
            Image(
                painter = rememberAsyncImagePainter(model = song.imageUrl),
                contentDescription = song.title,
                modifier = Modifier.fillMaxSize()
            )
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    modifier = Modifier.size(40.dp),
                    contentDescription = "Play",
                    tint = Color.Black.copy(alpha = 0.5f)
                )
            }
        }

        Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
            Text(song.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(song.artist, color = Color.LightGray, fontSize = 15.sp)
            Text("• ${song.duration}", color = Color.LightGray, fontSize = 13.sp)
        }

        Box {
            IconButton(onClick = { showMenu.value = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "More", tint = Color.White)
            }

            DropdownMenu(
                expanded = showMenu.value,
                onDismissRequest = { showMenu.value = false }
            ) {

                // ➕ Add to Playlist main option
                DropdownMenuItem(onClick = {
                    showNestedAddMenu.value = true
                    showMenu.value = false
                }) {
                    Text("➕ Add to Playlist")
                }

                DropdownMenuItem(onClick = {
                    showCreatePlaylistDialog(context, playlistViewModel, song)
                    showMenu.value = false
                }) {
                    Text("📁 Create New Playlist")
                }

                DropdownMenuItem(onClick = {
                    playlists.forEach { playlist ->
                        playlistViewModel.removeSongFromPlaylist(
                            playlist,
                            SongModel(song.title, song.artist, song.imageUrl, song.uri, song.duration)
                        )
                    }
                    showMenu.value = false
                }) {
                    Text("🗑 Remove from All")
                }


            }

            DropdownMenu(
                expanded = showNestedAddMenu.value,
                onDismissRequest = { showNestedAddMenu.value = false }
            ) {
                Box(
                    modifier = Modifier
                        .heightIn(max = 200.dp) // ~5 items * 40dp
                        .verticalScroll(rememberScrollState())
                ) {
                    Column {
                        playlists.forEach { playlist ->
                            DropdownMenuItem(onClick = {
                                playlistViewModel.addSongToPlaylist(
                                    playlist,
                                    SongModel(
                                        title = song.title,
                                        artist = song.artist,
                                        imageUrl = song.imageUrl,
                                        uri = song.uri,
                                        duration = song.duration
                                    )
                                )
                                showNestedAddMenu.value = false
                            }) {
                                Text("➕ $playlist")
                            }
                        }
                    }
                }
            }

        }
    }
}



fun showCreatePlaylistDialog(
    context: Context,
    viewModel: PlaylistViewModel,
    song: SpotifySong
) {
    val input = EditText(context).apply { hint = "Enter playlist name" }

    AlertDialog.Builder(context)
        .setTitle("Create Playlist")
        .setView(input)
        .setPositiveButton("Create") { _, _ ->
            val name = input.text.toString().trim()
            if (name.isEmpty()) {
                Toast.makeText(context, "Please enter a name", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.loadAllPlaylists()
                viewModel.allPlaylists.value.let { existing ->
                    if (existing.contains(name)) {
                        Toast.makeText(context, "Playlist already exists", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.addSongToPlaylist(
                            name,
                            SongModel(song.title, song.artist, song.imageUrl, song.uri, song.duration)
                        )
                        Toast.makeText(context, "Playlist created and song added", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        .setNegativeButton("Cancel", null)
        .show()
}



//@Composable
//fun showCreatePlaylistDialog2(context: Context, repo: PlaylistRepository, song: SpotifySong) {
//
//    val showDialog = remember { mutableStateOf(true) }
//    val newPlaylistName = remember { mutableStateOf("") }
//
//    if (showDialog.value) {
//        AlertDialog(
//            onDismissRequest = { showDialog.value = false },
//            title = {
//                Text("Create New Playlist",
//                    modifier = Modifier.padding(bottom = 5.dp),
//                    fontWeight = FontWeight.Bold)
//            },
//            text = {
//                OutlinedTextField(
//                    value = newPlaylistName.value,
//                    onValueChange = { newPlaylistName.value = it },
//                    label = { Text("Playlist Name") },
//                    singleLine = true
//                )
//            },
//            confirmButton = {
//                TextButton(onClick = {
//                    val name = newPlaylistName.value.trim()
//                    if (name.isNotBlank() && !playlists.contains(name)) {
//                        playlists.add(name)
//                    }
//                    newPlaylistName.value = ""
//                    showDialog.value = false
//                }) {
//                    Text("Create")
//                }
//            },
//            dismissButton = {
//                TextButton(onClick = {
//                    showDialog.value = false
//                    newPlaylistName.value = ""
//                }) {
//                    Text("Cancel")
//                }
//            }
//        )
//    }
//}