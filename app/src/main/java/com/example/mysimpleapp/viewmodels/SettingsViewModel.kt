package com.example.mysimpleapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysimpleapp.data.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val showClearDialog: Boolean = false,
    val showEasterEggDialog: Boolean = false
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun showClearDialog() {
        _uiState.value = _uiState.value.copy(showClearDialog = true)
    }

    fun hideClearDialog() {
        _uiState.value = _uiState.value.copy(showClearDialog = false)
    }

    fun showEasterEggDialog() {
        _uiState.value = _uiState.value.copy(showEasterEggDialog = true)
    }

    fun hideEasterEggDialog() {
        _uiState.value = _uiState.value.copy(showEasterEggDialog = false)
    }

    fun clearAllWords() {
        viewModelScope.launch {
            database.textDao().deleteAll()
            hideClearDialog()
        }
    }
} 