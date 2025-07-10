package com.example.moodymusic.data

import com.example.moodymusic.api.SpotifyApiService
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class SpotifyRepository {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.spotify.com/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    private val api = retrofit.create(SpotifyApiService::class.java)

    suspend fun fetchSongsByMood(accessToken: String, mood: String): List<SpotifySong> {
        val authHeader = "Bearer $accessToken"
        val response = api.searchTracks(authHeader, mood)

        return response.tracks.items.map { track ->
            SpotifySong(
                id = track.id,
                title = track.name,
                artist = track.artists.joinToString(", ") { it.name },
                imageUrl = track.album.images.firstOrNull()?.url ?: "",
                duration = formatDuration(track.duration_ms),
                uri = track.uri
            )
        }
    }

    private fun formatDuration(durationMs: Long): String {
        val totalSeconds = durationMs / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "%d:%02d".format(minutes, seconds)
    }
}
