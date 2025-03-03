package com.example.mysimpleapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysimpleapp.data.AppDatabase
import com.example.mysimpleapp.data.TextEntity
import com.example.mysimpleapp.data.api.RetrofitClient
import com.example.mysimpleapp.data.api.model.SaveWordRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

data class InputUiState(
    val text: String = "",
    val translation: String = "",
    val showSuccessMessage: Boolean = false,
    val showErrorMessage: Boolean = false,
    val error: String? = null
)

class InputViewModel(
    application: Application,
    private val authViewModel: AuthViewModel
) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    
    private val _uiState = MutableStateFlow(InputUiState())
    val uiState: StateFlow<InputUiState> = _uiState.asStateFlow()

    fun updateText(newText: String) {
        _uiState.value = _uiState.value.copy(text = newText)
    }

    fun updateTranslation(newTranslation: String) {
        _uiState.value = _uiState.value.copy(translation = newTranslation)
    }

    fun saveWord() {
        val currentState = _uiState.value
        if (currentState.text.isBlank() || currentState.translation.isBlank()) {
            _uiState.value = currentState.copy(showErrorMessage = true)
            return
        }

        viewModelScope.launch {
            try {
                val token = authViewModel.getToken()
                if (token == null) {
                    _uiState.value = currentState.copy(
                        showErrorMessage = true,
                        error = "Ошибка авторизации"
                    )
                    return@launch
                }

                val response = RetrofitClient.apiService.saveWord(
                    token = "Bearer $token",
                    request = SaveWordRequest(
                        word = currentState.text.trim(),
                        translation = currentState.translation.trim()
                    )
                )

                if (response.isSuccessful) {
                    _uiState.value = InputUiState(showSuccessMessage = true)
                } else {
                    when (response.code()) {
                        409 -> {
                            _uiState.value = _uiState.value.copy(
                                error = "Слово уже существует"
                            )
                        }
                        else -> {
                            _uiState.value = currentState.copy(
                                showErrorMessage = true,
                                error = "Ошибка сохранения: ${response.message()}"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = currentState.copy(
                    showErrorMessage = true,
                    error = "Ошибка при сохранении: ${e.message}"
                )
            }
        }
    }

    fun hideMessages() {
        _uiState.value = _uiState.value.copy(
            showSuccessMessage = false,
            showErrorMessage = false
        )
    }

    companion object {
        fun provideFactory(
            application: Application,
            authViewModel: AuthViewModel
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return InputViewModel(application, authViewModel) as T
            }
        }
    }
} 