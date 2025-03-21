package com.example.mysimpleapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysimpleapp.data.AppDatabase
import com.example.mysimpleapp.data.TextEntity
import com.example.mysimpleapp.data.api.model.QuizRequest
import com.example.mysimpleapp.data.api.model.WordStatRequest
import com.example.mysimpleapp.data.api.RetrofitClient
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mysimpleapp.viewmodels.quiz.QuizRepository
import com.example.mysimpleapp.viewmodels.quiz.QuizUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

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

    private val repository = QuizRepository()
    private val quizUseCase = QuizUseCase(repository, authViewModel)
    
    private var countdownJob: Job? = null
    private var previousWord: String = ""
    private var isInitialized = false

    fun loadNewQuestion() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    error = null,
                    selectedTranslation = null,
                    isCorrectAnswer = null
                )

                val token = authViewModel.getToken()
                if (token == null) {
                    _uiState.value = _uiState.value.copy(
                        error = "Ошибка авторизации",
                        isLoading = false
                    )
                    return@launch
                }

                val response = RetrofitClient.apiService.getQuizWord(
                    token = "Bearer $token",
                    request = QuizRequest(previousWord = previousWord)
                )
                if (response.isSuccessful) {
                    response.body()?.let { quizResponse ->
                        previousWord = quizResponse.word
                        val translations = listOf(
                            quizResponse.trueTranslation,
                            quizResponse.translation1,
                            quizResponse.translation2,
                            quizResponse.translation3
                        ).shuffled()

                        _uiState.value = _uiState.value.copy(
                            currentWord = quizResponse.word,
                            currentWordId = quizResponse.id,
                            translations = translations,
                            correctTranslation = quizResponse.trueTranslation,
                            isLoading = false
                        )
                    }

                } else {
                    when (response.code()) {
                        412 -> {
                            _uiState.value = _uiState.value.copy(
                                error = "Недостаточно слов, чтобы начать квиз",
                                isLoading = false
                            )
                        }
                        else -> {
                            println(response.code())
                            _uiState.value = _uiState.value.copy(
                                error = "Ошибка загрузки вопроса",
                                isLoading = false
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Ошибка загрузки вопроса: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun checkAnswer(selectedTranslation: String) {
        val isCorrect = selectedTranslation == _uiState.value.correctTranslation

        viewModelScope.launch {
            try {
                RetrofitClient.apiService.updateWordStat(
                    token = "Bearer ${authViewModel.getToken()}",
                    wordId = _uiState.value.currentWordId,
                    request = WordStatRequest(success = isCorrect)
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