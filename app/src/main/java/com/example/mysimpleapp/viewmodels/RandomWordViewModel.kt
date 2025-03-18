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

data class RandomWordUiState(
    val word: String = "",
    val translation: String = "",
    val userTranslation: String = "",
    val showTranslation: Boolean = false,
    val isAnswerChecked: Boolean = false,
    val showConfirmDialog: Boolean = false,
    val isCorrect: Boolean? = null,
    val error: String? = null,
    val wordId: Int = 0
)

class RandomWordViewModel(
    application: Application,
    private val authViewModel: AuthViewModel
) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    
    private val _uiState = MutableStateFlow(RandomWordUiState())
    val uiState: StateFlow<RandomWordUiState> = _uiState.asStateFlow()
    private var isInitialized = false

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
                val token = authViewModel.getToken()
                if (token == null) {
                    _uiState.value = _uiState.value.copy(
                        error = "Ошибка авторизации"
                    )
                    return@launch
                }

                val response = RetrofitClient.apiService.getWord("Bearer $token")

                if (response.isSuccessful) {
                    response.body()?.let { wordResponse ->
                        _uiState.value = RandomWordUiState(
                            word = wordResponse.word,
                            translation = wordResponse.translation,
                            wordId = wordResponse.id
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = "Ошибка загрузки слова"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Ошибка загрузки слова: ${e.message}"
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
            // Игнорируем ошибки обновления статистики
        }
    }

    fun checkAnswer() {
        val currentState = _uiState.value
        val isCorrect = currentState.userTranslation.trim().equals(currentState.translation.trim(), ignoreCase = true)
        
        viewModelScope.launch {
            updateWordStat(isCorrect)
            
            _uiState.value = currentState.copy(
                isAnswerChecked = true,
                isCorrect = isCorrect
            )
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
            
            _uiState.value = _uiState.value.copy(
                showTranslation = true,
                showConfirmDialog = false,
                isAnswerChecked = false
            )
        }
    }

    fun clearState() {
        _uiState.value = RandomWordUiState()
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