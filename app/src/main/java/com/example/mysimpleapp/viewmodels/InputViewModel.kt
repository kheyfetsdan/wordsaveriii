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

data class InputUiState(
    val text: String = "",
    val translation: String = "",
    val showSuccessMessage: Boolean = false,
    val showErrorMessage: Boolean = false
)

class InputViewModel(application: Application) : AndroidViewModel(application) {
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
            database.textDao().insert(
                TextEntity(
                    text = currentState.text.trim(),
                    translation = currentState.translation.trim()
                )
            )
            _uiState.value = InputUiState(showSuccessMessage = true)
        }
    }

    fun hideMessages() {
        _uiState.value = _uiState.value.copy(
            showSuccessMessage = false,
            showErrorMessage = false
        )
    }
} 