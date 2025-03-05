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

data class RandomWordUiState(
    val translation: String? = null,
    val word: String? = null,
    val userTranslation: String = "",
    val showTranslation: Boolean = false,
    val showConfirmDialog: Boolean = false,
    val isAnswerChecked: Boolean = false,
    val error: String? = null
)

class RandomWordViewModel(
    application: Application,
    private val authViewModel: AuthViewModel
) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    
    private val _uiState = MutableStateFlow(RandomWordUiState())
    val uiState: StateFlow<RandomWordUiState> = _uiState.asStateFlow()

    init {
        loadNewWord()
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
                        _uiState.value = RandomWordUiState(  // Создаем новое состояние вместо copy
                            word = wordResponse.word,
                            translation = wordResponse.translation
                        )
                    }
                } else {
                    when (response.code()) {
                        404 -> {
                            _uiState.value = RandomWordUiState(
                                error = "У вас не сохранено ни одного слова"
                            )
                        }
                        else -> {
                            _uiState.value = RandomWordUiState(
                                error = "Ошибка загрузки слова: ${response.message()}"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = RandomWordUiState(
                    error = "Ошибка при загрузке слова: ${e.message}"
                )
            }
        }
    }

    fun checkAnswer() {
        val currentState = _uiState.value
        val translation = currentState.translation ?: return

        _uiState.value = currentState.copy(
            isAnswerChecked = true
        )
    }

    fun showConfirmDialog() {
        _uiState.value = _uiState.value.copy(showConfirmDialog = true)
    }

    fun hideConfirmDialog() {
        _uiState.value = _uiState.value.copy(showConfirmDialog = false)
    }

    fun showTranslation() {
        _uiState.value = _uiState.value.copy(
            showTranslation = true,
            showConfirmDialog = false
        )
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