package com.example.moodymusic.api

import retrofit2.http.Body
import retrofit2.http.POST

data class TokenResponse(
        val access_token: String,
        val token_type: String,
        val expires_in: Int,
        val refresh_token: String?
)

interface SpotifyTokenApi {
        @POST("/api/exchange-token")
        suspend fun getToken(@Body body: Map<String, String>): TokenResponse
}

