package com.example.moodymusic.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moodymusic.data.PlaylistRepository
import com.example.moodymusic.data.SongModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlaylistViewModel : ViewModel() {

    private val repository = PlaylistRepository()

    private val _allPlaylists = MutableStateFlow<List<String>>(emptyList())
    val allPlaylists: StateFlow<List<String>> get() = _allPlaylists

    var selectedPlaylistName by mutableStateOf("")

    init {
        loadAllPlaylists()
    }

    fun loadAllPlaylists() {
        viewModelScope.launch {
            val playlists = repository.getAllPlaylists()
            _allPlaylists.value = playlists
        }
    }

    fun addSongToPlaylist(playlistName: String, song: SongModel) {
        viewModelScope.launch {
            repository.addSongToPlaylist(playlistName, song)
            loadAllPlaylists()
        }
    }

    fun createEmptyPlaylist(name: String) {
        viewModelScope.launch {
            repository.createEmptyPlaylist(name)
            loadAllPlaylists()
        }
    }

    fun removeSongFromPlaylist(playlistName: String, song: SongModel) {
        viewModelScope.launch {
            repository.removeSongFromPlaylist(playlistName, song)
            loadAllPlaylists()
        }
    }

    fun getSongsFromPlaylist(
        playlistName: String,
        onResult: (List<SongModel>) -> Unit
    ) {
        viewModelScope.launch {
            val songs = repository.getSongsFromPlaylist(playlistName)
            onResult(songs)
        }
    }
}
