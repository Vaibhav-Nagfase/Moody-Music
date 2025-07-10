package com.example.moodymusic.api

import com.example.moodymusic.data.SpotifySearchResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SpotifyApiService {
    @GET("v1/search")
    suspend fun searchTracks(
        @Header("Authorization") authHeader: String,
        @Query("q") mood: String,
        @Query("type") type: String = "track",
        @Query("limit") limit: Int = 25,
        @Query("offset") offset: Int = 25
    ): SpotifySearchResponse
}
