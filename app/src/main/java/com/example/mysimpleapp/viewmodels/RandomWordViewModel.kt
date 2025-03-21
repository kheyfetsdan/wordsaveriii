package com.example.mysimpleapp.viewmodels

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.mysimpleapp.data.AppDatabase
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mysimpleapp.data.api.RetrofitClient
import com.example.mysimpleapp.data.api.model.WordStatRequest
import android.util.Log
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import com.example.mysimpleapp.viewmodels.wordgame.WordGameState
import com.example.mysimpleapp.viewmodels.wordgame.WordGameRepository
import com.example.mysimpleapp.viewmodels.wordgame.WordGameUseCase

class RandomWordViewModel(
    application: Application,
    authViewModel: AuthViewModel
) : BaseViewModel<WordGameState>(
    application, 
    authViewModel,
    WordGameState()
) {
    private val repository = WordGameRepository()
    private val gameUseCase = WordGameUseCase(repository, authViewModel)
    private var isInitialized = false
    private var countdownJob: Job? = null

    fun startGame() {
        if (!isInitialized) {
            isInitialized = true
            loadNewWord()
        }
    }

    fun updateUserTranslation(translation: String) {
        updateState { it.copy(
            userTranslation = translation,
            isAnswerChecked = false
        )}
    }

    fun loadNewWord() {
        // Сначала очищаем состояние
        updateState { WordGameState(word = "") }

        launchWithErrorHandling(
            onError = { errorMessage ->
                updateState { it.copy(
                    error = "Ошибка загрузки слова: $errorMessage",
                    isLoading = false
                )}
            }
        ) {
            loadWordFromApi()
        }
    }
    
    private suspend fun loadWordFromApi() {
        val loadWordAction = withLoadingState {
            withToken(
                onError = { errorMessage ->
                    updateState { it.copy(
                        error = errorMessage,
                        isLoading = false
                    )}
                }
            ) { token ->
                apiCall(
                    onError = { errorMessage ->
                        updateState { it.copy(
                            error = "Ошибка загрузки слова: $errorMessage",
                            isLoading = false
                        )}
                    }
                ) {
                    gameUseCase.getNewWord(token)
                }?.let { response ->
                    handleWordResponse(response)
                }
            }
        }
        
        loadWordAction(
            { isLoading -> _uiState.value.copy(isLoading = isLoading) },
            { error -> _uiState.value.copy(error = error) }
        )
    }
    
    private fun handleWordResponse(response: retrofit2.Response<com.example.mysimpleapp.data.api.model.WordResponse>) {
        if (response.isSuccessful) {
            response.body()?.let { wordResponse ->
                updateState {
                    WordGameState(
                        word = wordResponse.word,
                        translation = wordResponse.translation,
                        wordId = wordResponse.id,
                        isLoading = false,
                        isSuccess = true
                    )
                }
            }
        } else {
            updateState { it.copy(
                error = "Ошибка: ${response.code()} ${response.message()}",
                isLoading = false
            )}
        }
    }

    private suspend fun updateWordStat(success: Boolean) {
        apiCall(
            onError = { /* Логируем ошибку, но не показываем пользователю */ }
        ) {
            withToken(
                onError = { /* Игнорируем ошибки авторизации при обновлении статистики */ }
            ) { token ->
                gameUseCase.updateStatistics(token, _uiState.value.wordId, success)
            }
        }
    }

    private fun startCountdown() {
        countdownJob?.cancel()
        
        updateState { it.copy(countdown = 3) }
        
        countdownJob = launchSafe {
            for (i in 3 downTo 1) {
                updateState { it.copy(countdown = i) }
                delay(1000)
            }
            
            updateState { it.copy(countdown = 0) }
            loadNewWord()
        }
    }

    private fun updateStats(isCorrect: Boolean) {
        updateState { state -> 
            gameUseCase.updateGameStats(state, isCorrect)
        }
    }

    fun checkAnswer() {
        val currentState = _uiState.value
        
        if (currentState.isCheckingAnswer) return
        
        val trimmedUserTranslation = currentState.userTranslation.trim()
        
        if (trimmedUserTranslation.isBlank()) {
            updateState { it.copy(error = "Введите перевод слова") }
            return
        }
        
        val isCorrect = gameUseCase.isAnswerCorrect(
            trimmedUserTranslation, 
            currentState.translation
        )
        
        launchWithErrorHandling(
            onError = { errorMessage ->
                updateState { it.copy(
                    error = "Ошибка при проверке ответа: $errorMessage",
                    isCheckingAnswer = false
                )}
            }
        ) {
            processAnswer(isCorrect)
        }
    }
    
    private suspend fun processAnswer(isCorrect: Boolean) {
        val checkAnswerAction = withLoadingState {
            updateState { it.copy(
                isCheckingAnswer = true,
                error = null
            )}
            
            updateWordStat(isCorrect)
            updateStats(isCorrect)
            
            updateState { it.copy(
                isAnswerChecked = true,
                isCorrect = isCorrect,
                isCheckingAnswer = false
            )}
            
            startCountdown()
        }
        
        checkAnswerAction(
            { isLoading -> _uiState.value.copy(isCheckingAnswer = isLoading) },
            { error -> _uiState.value.copy(error = error) }
        )
    }

    // Методы для управления диалогами
    fun showConfirmDialog() = updateState { it.copy(showConfirmDialog = true) }
    fun hideConfirmDialog() = updateState { it.copy(showConfirmDialog = false) }
    fun showSkipConfirmDialog() = updateState { it.copy(showSkipConfirmDialog = true) }
    fun hideSkipConfirmDialog() = updateState { it.copy(showSkipConfirmDialog = false) }

    fun showTranslation() {
        launchSafe {
            updateWordStat(false)
            updateStats(false)
            
            updateState { it.copy(
                showTranslation = true,
                showConfirmDialog = false,
                isAnswerChecked = false
            )}
            
            startCountdown()
        }
    }

    fun skipWord() {
        launchSafe {
            try {
                updateWordStat(false)
                updateStats(false)
                updateState { it.copy(showSkipConfirmDialog = false) }
                loadNewWord()
            } catch (e: Exception) {
                updateState { it.copy(
                    error = "Ошибка при пропуске слова: ${e.message}",
                    showSkipConfirmDialog = false
                )}
            }
        }
    }

    fun clearState() {
        countdownJob?.cancel()
        updateState { WordGameState() }
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