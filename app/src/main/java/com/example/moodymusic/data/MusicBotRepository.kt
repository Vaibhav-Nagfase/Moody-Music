package com.example.moodymusic.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import org.json.JSONObject

class MusicBotRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun getUserEmail(): String? = auth.currentUser?.email

    private val _mood = MutableStateFlow("")
    val mood = _mood.asStateFlow()

    private val _recommendation = MutableStateFlow("")
    val recommendation = _recommendation.asStateFlow()

    suspend fun sendPromptToFirebase(prompt: String): String? {
        val email = getUserEmail() ?: return null

        val promptDoc = firestore.collection("Moody_Bot").document(email)
        promptDoc.set(mapOf("prompt" to prompt, "status" to "pending")).await()

        return email
    }

    suspend fun fetchGeminiResponse(email: String) {
        val docRef = firestore.collection("Moody_Bot").document(email)

        while (true) {
            val snapshot = docRef.get().await()
            val responseString = snapshot.getString("response") ?: continue

            try {
                val json = JSONObject(responseString)
                val moodResult = json.optString("mood")
                val recResult = json.optString("recommendation")

                if (!moodResult.isNullOrBlank() && !recResult.isNullOrBlank()) {
                    _mood.value = moodResult
                    _recommendation.value = recResult
                    break
                }
            } catch (e: Exception) {
                // Optional: log or handle the error
            }

            delay(1000) // poll every 1 sec
        }
    }
}
