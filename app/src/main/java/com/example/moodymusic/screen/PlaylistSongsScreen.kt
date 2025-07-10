package com.example.moodymusic.screen

import androidx.compose.runtime.Composable
import com.example.moodymusic.data.SpotifySong
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moodymusic.ViewModel.PlaylistViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun PlaylistSongsScreen(
    playlistViewModel: PlaylistViewModel
) {
    val context = LocalContext.current
    val playlistName = playlistViewModel.selectedPlaylistName

    // Use remember for mutable state list
    val songs = remember { mutableStateListOf<SpotifySong>() }

    if (playlistName.isBlank()) return // avoid crash

    // Fetch songs when playlistName changes
    LaunchedEffect(playlistName) {
        playlistViewModel.getSongsFromPlaylist(playlistName) { songModels ->
            songs.clear()
            songs.addAll(songModels.map {
                SpotifySong(
                    id = "",
                    title = it.title,
                    artist = it.artist,
                    imageUrl = it.imageUrl,
                    uri = it.uri,
                    duration = it.duration
                )
            })
        }

        playlistViewModel.loadAllPlaylists()
    }



    val featuredSong = remember { mutableStateOf<SpotifySong?>(null) }
    val isRedirecting = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val playlists by playlistViewModel.allPlaylists.collectAsState()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Featured Song
        featuredSong.value?.let {
            SpotifyFeaturedSongCard(
                song = it,
                isRedirecting = isRedirecting.value,
                onPlay = {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(it.uri)
                    intent.putExtra(
                        Intent.EXTRA_REFERRER,
                        Uri.parse("android-app://${context.packageName}")
                    )
                    context.startActivity(intent)
                }
            )
        }

        Text(
            "Playlist: \"$playlistName\"",
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



//
//@Composable
//fun PlaylistSongListScreen(playlistName: String, songs: List<SpotifySong>) {
//    val context = LocalContext.current
//    val featuredSong = remember { mutableStateOf<SpotifySong?>(null) }
//    val isRedirecting = remember { mutableStateOf(false) }
//    val coroutineScope = rememberCoroutineScope()
//
//    val playlistViewModel: PlaylistViewModel = viewModel()
//    val playlists by playlistViewModel.allPlaylists.collectAsState()
//
//    LaunchedEffect(playlistName) {
//        playlistViewModel.loadAllPlaylists()
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.Black)
//    ) {
//        // Featured Song
//        featuredSong.value?.let {
//            SpotifyFeaturedSongCard(
//                song = it,
//                isRedirecting = isRedirecting.value,
//                onPlay = {
//                    val intent = Intent(Intent.ACTION_VIEW)
//                    intent.data = Uri.parse(it.uri)
//                    intent.putExtra(
//                        Intent.EXTRA_REFERRER,
//                        Uri.parse("android-app://${context.packageName}")
//                    )
//                    context.startActivity(intent)
//                }
//            )
//        }
//
//        Text(
//            "Playlist: \"$playlistName\"",
//            modifier = Modifier.padding(16.dp),
//            color = Color.White,
//            fontSize = 20.sp,
//            fontWeight = FontWeight.Bold
//        )
//
//        LazyColumn {
//            items(songs) { song ->
//                SongListItem(song, playlists, playlistViewModel) {
//
//                    // Set as featured
//                    featuredSong.value = song
//
//                    isRedirecting.value = true  // show loading or animation
//
//                    coroutineScope.launch {
//                        delay(1500)  // Wait
//                        isRedirecting.value = false
//
//                        // Play in Spotify app
//                        val intent = Intent(Intent.ACTION_VIEW)
//                        intent.data = Uri.parse(song.uri)
//                        intent.putExtra(
//                            Intent.EXTRA_REFERRER,
//                            Uri.parse("android-app://${context.packageName}")
//                        )
//                        context.startActivity(intent)
//
//                    }
//                }
//            }
//        }
//    }
//}
