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
    val showErrorMessage: Boolean = false,
    val showSuccessMessage: Boolean = false,
    val error: String? = null,
    val isLoading: Boolean = false
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
        
        val trimmedText = currentState.text.trim()
        val trimmedTranslation = currentState.translation.trim()
        
        if (trimmedText.isBlank() || trimmedTranslation.isBlank()) {
            _uiState.value = currentState.copy(
                showErrorMessage = true,
                error = "Поля не могут быть пустыми"
            )
            return
        }
        
        viewModelScope.launch {
            try {
                _uiState.value = currentState.copy(
                    isLoading = true,
                    showErrorMessage = false,
                    error = null
                )
                
                val token = authViewModel.getToken()
                if (token == null) {
                    _uiState.value = currentState.copy(
                        showErrorMessage = true,
                        error = "Ошибка авторизации",
                        isLoading = false
                    )
                    return@launch
                }

                val response = RetrofitClient.apiService.saveWord(
                    token = "Bearer $token",
                    request = SaveWordRequest(
                        word = trimmedText,
                        translation = trimmedTranslation
                    )
                )

                if (response.isSuccessful) {
                    _uiState.value = InputUiState(
                        showSuccessMessage = true,
                        isLoading = false
                    )
                } else {
                    _uiState.value = currentState.copy(
                        showErrorMessage = true,
                        error = "Ошибка сохранения: ${response.message()}",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = currentState.copy(
                    showErrorMessage = true,
                    error = "Ошибка сохранения: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun clearFields() {
        _uiState.value = InputUiState()
    }

    fun hideMessages() {
        _uiState.value = _uiState.value.copy(
            showErrorMessage = false,
            showSuccessMessage = false,
            error = null
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