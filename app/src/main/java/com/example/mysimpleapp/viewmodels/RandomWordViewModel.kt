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

data class RandomWordUiState(
    val randomText: TextEntity? = null,
    val userTranslation: String = "",
    val showTranslation: Boolean = false,
    val showConfirmDialog: Boolean = false,
    val isAnswerChecked: Boolean = false
)

class RandomWordViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    
    private val _uiState = MutableStateFlow(RandomWordUiState())
    val uiState: StateFlow<RandomWordUiState> = _uiState.asStateFlow()

    init {
        loadRandomWord()
    }

    fun updateUserTranslation(translation: String) {
        _uiState.value = _uiState.value.copy(
            userTranslation = translation,
            isAnswerChecked = false
        )
    }

    fun loadRandomWord() {
        viewModelScope.launch {
            val textEntity = database.textDao().getRandomTextByWrongAnswers()
            if (textEntity == null) {
                _uiState.value = _uiState.value.copy(
                    randomText = null,
                    showTranslation = false,
                    userTranslation = "",
                    isAnswerChecked = false
                )
                return@launch
            }
            _uiState.value = _uiState.value.copy(
                randomText = textEntity,
                showTranslation = false,
                userTranslation = "",
                isAnswerChecked = false
            )
        }
    }

    fun checkAnswer() {
        val currentState = _uiState.value
        val randomText = currentState.randomText ?: return

        viewModelScope.launch {
            if (currentState.userTranslation.trim().equals(randomText.translation.trim(), ignoreCase = true)) {
                database.textDao().incrementCorrectAnswers(randomText.id)
            } else {
                database.textDao().incrementWrongAnswers(randomText.id)
            }
            _uiState.value = currentState.copy(isAnswerChecked = true)
        }
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
} 