package com.example.mysimpleapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysimpleapp.data.AppDatabase
import com.example.mysimpleapp.data.TextEntity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class QuizUiState(
    val isGameStarted: Boolean = false,
    val currentWords: List<TextEntity> = emptyList(),
    val currentTranslation: String = "",
    val selectedWordId: Int? = null,
    val isCorrectAnswer: Boolean? = null,
    val timeLeft: Int = 5
)

class QuizViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()
    
    private var countdownJob: Job? = null
    
    fun startGame() {
        viewModelScope.launch {
            loadNewQuestion()
            _uiState.value = _uiState.value.copy(isGameStarted = true)
        }
    }
    
    private suspend fun loadNewQuestion() {
        val words = database.textDao().getRandomWords(4)
        if (words.isNotEmpty()) {
            val correctWord = words.random()
            _uiState.value = _uiState.value.copy(
                currentWords = words,
                currentTranslation = correctWord.translation,
                selectedWordId = null,
                isCorrectAnswer = null,
                timeLeft = 5
            )
        }
    }
    
    fun checkAnswer(selectedWord: TextEntity) {
        val isCorrect = selectedWord.translation == _uiState.value.currentTranslation
        _uiState.value = _uiState.value.copy(
            selectedWordId = selectedWord.id,
            isCorrectAnswer = isCorrect
        )
        
        if (isCorrect) {
            startCountdown()
            viewModelScope.launch {
                // Обновляем статистику
                val word = selectedWord.copy(
                    correctAnswers = selectedWord.correctAnswers + 1
                )
                database.textDao().update(word)
            }
        } else {
            viewModelScope.launch {
                // Обновляем статистику
                val word = selectedWord.copy(
                    wrongAnswers = selectedWord.wrongAnswers + 1
                )
                database.textDao().update(word)
            }
        }
    }
    
    private fun startCountdown() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            repeat(5) {
                delay(1000)
                _uiState.value = _uiState.value.copy(
                    timeLeft = 4 - it
                )
            }
            loadNewQuestion()
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
} 