package com.example.moodymusic.ViewModel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moodymusic.data.MoodRepository
import com.example.moodymusic.model.MoodLog
import com.example.moodymusic.model.MoodType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MoodViewModel : ViewModel() {

    private val repository = MoodRepository()

    private val _moods = MutableStateFlow<List<MoodLog>>(emptyList())
    val moods = _moods.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O)

    fun saveMoodToFirebase(mood: MoodType) {
        viewModelScope.launch {
            repository.saveMood(mood)

            fetchMoodLogs()
        }
    }

    fun fetchMoodLogs() {
        viewModelScope.launch {
            _moods.value = repository.fetchAllMoodLogs()
        }
    }
}

