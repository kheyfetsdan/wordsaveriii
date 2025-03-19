package com.example.mysimpleapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysimpleapp.data.AppDatabase
import com.example.mysimpleapp.data.TextEntity
import com.example.mysimpleapp.data.api.model.QuizRequest
import com.example.mysimpleapp.data.api.model.QuizResponse
import com.example.mysimpleapp.data.api.model.WordStatRequest
import com.example.mysimpleapp.data.api.RetrofitClient
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mysimpleapp.data.repository.RepositoryAdapter
import com.example.mysimpleapp.data.repository.AuthRepository

data class QuizUiState(
    val currentWord: String = "",
    val currentWordId: Int = 0,
    val translations: List<String> = emptyList(),
    val correctTranslation: String = "",
    val selectedTranslation: String? = null,
    val isCorrectAnswer: Boolean? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val timeLeft: Int = 5
)

class QuizViewModel(
    application: Application,
    private val authViewModel: AuthViewModel
) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private var countdownJob: Job? = null
    private var previousWord: String = ""
    private var isInitialized = false

    private val repositoryAdapter = RepositoryAdapter(AuthRepository(application))

    fun loadNewQuestion() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    error = null,
                    selectedTranslation = null,
                    isCorrectAnswer = null
                )

                val result = repositoryAdapter.getQuizWord(previousWord)
                
                if (result.isSuccess) {
                    val quizWord = result.getOrNull()!!
                    previousWord = quizWord.word
                    
                    val translations = listOf(
                        quizWord.trueTranslation,
                        quizWord.translation1,
                        quizWord.translation2,
                        quizWord.translation3
                    ).shuffled()
                    
                    _uiState.value = _uiState.value.copy(
                        currentWordId = quizWord.id,
                        currentWord = quizWord.word,
                        correctTranslation = quizWord.trueTranslation,
                        translations = translations,
                        isLoading = false,
                        timeLeft = 5
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = result.exceptionOrNull()?.message ?: "Неизвестная ошибка",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Неизвестная ошибка",
                    isLoading = false
                )
            }
        }
    }

    fun checkAnswer(selectedTranslation: String) {
        val isCorrect = selectedTranslation == _uiState.value.correctTranslation

        viewModelScope.launch {
            try {
                repositoryAdapter.updateWordStat(
                    wordId = _uiState.value.currentWordId,
                    success = isCorrect
                )
            } catch (e: Exception) {
                // Игнорируем ошибки обновления статистики
            }
        }

        _uiState.value = _uiState.value.copy(
            selectedTranslation = selectedTranslation,
            isCorrectAnswer = isCorrect
        )

        if (isCorrect) {
            startCountdown()
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

    fun startGame() {
        if (!isInitialized) {
            isInitialized = true
            previousWord = ""
            loadNewQuestion()
        }
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }

    companion object {
        fun provideFactory(
            application: Application,
            authViewModel: AuthViewModel
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return QuizViewModel(application, authViewModel) as T
            }
        }
    }
} 