package com.example.moodymusic.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moodymusic.data.SpotifySong
import com.example.moodymusic.data.SpotifyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SpotifyViewModel : ViewModel() {

    private val repository = SpotifyRepository()

    private val _songs = MutableStateFlow<List<SpotifySong>>(emptyList())
    val songs: StateFlow<List<SpotifySong>> = _songs

    fun fetchSongs(accessToken: String, mood: String) {
        viewModelScope.launch {
            try {
                val result = repository.fetchSongsByMood(accessToken, mood)
                _songs.value = result
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
