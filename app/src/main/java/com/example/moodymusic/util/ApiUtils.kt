package com.example.moodymusic.util

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.Response

interface PingApi {
    @GET("/api/ping")
    suspend fun ping(): Response<String>
}

fun pingBackend() {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://spotify-backend-e92q.onrender.com/") // your backend
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api = retrofit.create(PingApi::class.java)

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = api.ping()
            if (response.isSuccessful) {
                Log.d("BackendPing", "✅ Server is awake")
            } else {
                Log.e("BackendPing", "⚠️ Server ping failed")
            }
        } catch (e: Exception) {
            Log.e("BackendPing", "❌ Ping failed", e)
        }
    }
}
