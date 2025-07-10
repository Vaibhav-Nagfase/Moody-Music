package com.example.moodymusic.data

data class SpotifySearchResponse(
    val tracks: Tracks
)

data class Tracks(
    val items: List<Track>
)

data class Track(
    val id: String,
    val name: String,
    val artists: List<Artist>,
    val album: Album,
    val duration_ms: Long,
    val uri: String
)

data class Artist(
    val name: String
)

data class Album(
    val images: List<Image>
)

data class Image(
    val url: String
)

data class SpotifySong(
    val id: String,
    val title: String,
    val artist: String,
    val imageUrl: String,
    val duration: String,
    val uri: String
)
