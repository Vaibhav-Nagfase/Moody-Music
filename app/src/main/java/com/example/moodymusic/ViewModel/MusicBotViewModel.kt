package com.example.moodymusic.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moodymusic.data.MusicBotRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MusicBotViewModel(
    private val repository: MusicBotRepository = MusicBotRepository()
) : ViewModel() {

    private val _promptMood = MutableStateFlow("")
    val promptMood = _promptMood.asStateFlow()

    private val _recommendation = MutableStateFlow("")
    val recommendation = _recommendation.asStateFlow()

    fun sendPrompt(prompt: String) {
        viewModelScope.launch {
            val userId = repository.sendPromptToFirebase(prompt)
            if (!userId.isNullOrEmpty()) {
                listenForGeminiResponse(userId)
            }
        }
    }

    private fun listenForGeminiResponse(userId: String) {
        viewModelScope.launch {
            repository.fetchGeminiResponse(userId)
            _promptMood.update { repository.mood.value }
            _recommendation.update { repository.recommendation.value }
        }
    }

    fun clearResponse() {
        _promptMood.value = ""
        _recommendation.value = ""
    }
}
