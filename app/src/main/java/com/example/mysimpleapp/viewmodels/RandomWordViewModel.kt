package com.example.mysimpleapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysimpleapp.data.AppDatabase
import com.example.mysimpleapp.data.TextEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mysimpleapp.data.api.RetrofitClient
import com.example.mysimpleapp.data.api.model.WordStatRequest
import android.util.Log
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

data class RandomWordUiState(
    val word: String = "",
    val translation: String = "",
    val userTranslation: String = "",
    val showTranslation: Boolean = false,
    val isAnswerChecked: Boolean = false,
    val showConfirmDialog: Boolean = false,
    val isCorrect: Boolean? = null,
    val error: String? = null,
    val wordId: Int = 0,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val isCheckingAnswer: Boolean = false,
    val showSkipConfirmDialog: Boolean = false,
    val countdown: Int = 0,
    val totalWords: Int = 0,
    val correctAnswers: Int = 0,
    val wrongAnswers: Int = 0
)

class RandomWordViewModel(
    application: Application,
    private val authViewModel: AuthViewModel
) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    
    private val _uiState = MutableStateFlow(RandomWordUiState())
    val uiState: StateFlow<RandomWordUiState> = _uiState.asStateFlow()
    private var isInitialized = false
    private var countdownJob: Job? = null

    fun startGame() {
        if (!isInitialized) {
            isInitialized = true
            loadNewWord()
        }
    }

    fun updateUserTranslation(translation: String) {
        _uiState.value = _uiState.value.copy(
            userTranslation = translation,
            isAnswerChecked = false
        )
    }

    fun loadNewWord() {
        // Сначала очищаем состояние
        if (_uiState.value.word == null) {
            _uiState.value = RandomWordUiState()
        } else {
            _uiState.value = RandomWordUiState(word = "")
        }

        viewModelScope.launch {
            try {
                // Устанавливаем состояние загрузки
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    error = null
                )
                
                // Проверяем токен
                val token = authViewModel.getToken()
                if (token == null) {
                    _uiState.value = _uiState.value.copy(
                        error = "Ошибка авторизации",
                        isLoading = false
                    )
                    return@launch
                }

                // Используем токен в запросе
                val response = RetrofitClient.apiService.getWord("Bearer $token")

                if (response.isSuccessful) {
                    response.body()?.let { wordResponse ->
                        _uiState.value = RandomWordUiState(
                            word = wordResponse.word,
                            translation = wordResponse.translation,
                            wordId = wordResponse.id,
                            isLoading = false,
                            isSuccess = true
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = "Ошибка загрузки слова: ${response.message()}",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Ошибка загрузки слова: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    private suspend fun updateWordStat(success: Boolean) {
        try {
            val token = authViewModel.getToken() ?: return
            
            RetrofitClient.apiService.updateWordStat(
                token = "Bearer $token",
                wordId = _uiState.value.wordId,
                request = WordStatRequest(success = success)
            )
        } catch (e: Exception) {
            // Логируем ошибку, но не показываем пользователю
            Log.e("RandomWordViewModel", "Ошибка обновления статистики: ${e.message}")
        }
    }

    private fun startCountdown() {
        countdownJob?.cancel()
        
        _uiState.value = _uiState.value.copy(countdown = 3)
        
        countdownJob = viewModelScope.launch {
            for (i in 3 downTo 1) {
                _uiState.value = _uiState.value.copy(countdown = i)
                delay(1000)
            }
            
            _uiState.value = _uiState.value.copy(countdown = 0)
            loadNewWord()
        }
    }

    private fun updateStats(isCorrect: Boolean) {
        val currentState = _uiState.value
        
        if (isCorrect) {
            _uiState.value = currentState.copy(
                correctAnswers = currentState.correctAnswers + 1,
                totalWords = currentState.totalWords + 1
            )
        } else {
            _uiState.value = currentState.copy(
                wrongAnswers = currentState.wrongAnswers + 1,
                totalWords = currentState.totalWords + 1
            )
        }
    }

    fun checkAnswer() {
        val currentState = _uiState.value
        
        if (currentState.isCheckingAnswer) return
        
        val trimmedUserTranslation = currentState.userTranslation.trim()
        
        if (trimmedUserTranslation.isBlank()) {
            _uiState.value = currentState.copy(
                error = "Введите перевод слова"
            )
            return
        }
        
        val isCorrect = trimmedUserTranslation.equals(currentState.translation.trim(), ignoreCase = true)
        
        viewModelScope.launch {
            try {
                _uiState.value = currentState.copy(
                    isCheckingAnswer = true,
                    error = null
                )
                
                updateWordStat(isCorrect)
                updateStats(isCorrect)
                
                _uiState.value = currentState.copy(
                    isAnswerChecked = true,
                    isCorrect = isCorrect,
                    isCheckingAnswer = false
                )
                
                startCountdown()
            } catch (e: Exception) {
                _uiState.value = currentState.copy(
                    error = "Ошибка при проверке ответа: ${e.message}",
                    isCheckingAnswer = false
                )
            }
        }
    }

    fun showConfirmDialog() {
        _uiState.value = _uiState.value.copy(showConfirmDialog = true)
    }

    fun hideConfirmDialog() {
        _uiState.value = _uiState.value.copy(showConfirmDialog = false)
    }

    fun showTranslation() {
        viewModelScope.launch {
            updateWordStat(false)
            updateStats(false)
            
            _uiState.value = _uiState.value.copy(
                showTranslation = true,
                showConfirmDialog = false,
                isAnswerChecked = false
            )
            
            startCountdown()
        }
    }

    fun showSkipConfirmDialog() {
        _uiState.value = _uiState.value.copy(showSkipConfirmDialog = true)
    }

    fun hideSkipConfirmDialog() {
        _uiState.value = _uiState.value.copy(showSkipConfirmDialog = false)
    }

    fun skipWord() {
        viewModelScope.launch {
            try {
                updateWordStat(false)
                updateStats(false)
                
                _uiState.value = _uiState.value.copy(
                    showSkipConfirmDialog = false
                )
                
                loadNewWord()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Ошибка при пропуске слова: ${e.message}",
                    showSkipConfirmDialog = false
                )
            }
        }
    }

    fun clearState() {
        countdownJob?.cancel()
        _uiState.value = RandomWordUiState()
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
                return RandomWordViewModel(application, authViewModel) as T
            }
        }
    }
} 