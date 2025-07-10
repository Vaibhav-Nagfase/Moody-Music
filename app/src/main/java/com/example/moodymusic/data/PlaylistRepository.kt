package com.example.moodymusic.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

// Data class to represent each song
data class SongModel(
    val title: String = "",
    val artist: String = "",
    val imageUrl: String = "",
    val uri: String = "",
    val duration: String = ""
)

data class Playlist(
    val name: String = "",
    val songs: List<SongModel> = emptyList()
)

class PlaylistRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun getUserEmail(): String? = auth.currentUser?.email

    suspend fun getAllPlaylists(): List<String> {
        val email = getUserEmail() ?: return emptyList()
        val snapshot = firestore.collection("users").document(email).collection("playlists").get().await()
        return snapshot.documents.map { it.id }
    }

    suspend fun getSongsFromPlaylist(name: String): List<SongModel> {
        if (name.isBlank()) return emptyList()  // 🛡️ Prevent crash

        val email = getUserEmail() ?: return emptyList()
        val doc = firestore.collection("users").document(email).collection("playlists").document(name).get().await()
        return doc.toObject(Playlist::class.java)?.songs ?: emptyList()
    }

    suspend fun addSongToPlaylist(playlistName: String, song: SongModel) {
        val email = getUserEmail() ?: return
        val playlistRef = firestore.collection("users").document(email).collection("playlists").document(playlistName)

        val snapshot = playlistRef.get().await()
        if (snapshot.exists()) {
            // Playlist exists: add song to array
            playlistRef.update("songs", com.google.firebase.firestore.FieldValue.arrayUnion(song)).await()
        } else {
            // Playlist does not exist: create it with the song
            val newPlaylist = Playlist(name = playlistName, songs = listOf(song))
            playlistRef.set(newPlaylist).await()
        }
    }

    suspend fun createEmptyPlaylist(playlistName: String) {
        val email = getUserEmail() ?: return
        val playlistRef = firestore.collection("users")
            .document(email)
            .collection("playlists")
            .document(playlistName)

        val snapshot = playlistRef.get().await()
        if (!snapshot.exists()) {
            val newPlaylist = Playlist(name = playlistName, songs = emptyList())
            playlistRef.set(newPlaylist).await()
        }
    }

    suspend fun removeSongFromPlaylist(playlistName: String, song: SongModel) {
        val email = getUserEmail() ?: return
        val playlistRef = firestore.collection("users").document(email).collection("playlists").document(playlistName)
        playlistRef.update("songs", com.google.firebase.firestore.FieldValue.arrayRemove(song)).await()
    }
}
