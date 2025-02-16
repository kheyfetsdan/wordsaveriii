package com.example.mysimpleapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysimpleapp.data.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class InfoUiState(
    val wordsCount: Int = 0,
    val showSettings: Boolean = false,
    val appVersion: String = "1.0.0",
    val appAuthor: String = "Разработчик: Dan Kheyfets"
)

class InfoViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    
    private val _uiState = MutableStateFlow(InfoUiState())
    val uiState: StateFlow<InfoUiState> = _uiState.asStateFlow()

    init {
        loadWordsCount()
        loadAppInfo()
    }

    private fun loadWordsCount() {
        viewModelScope.launch {
            val count = database.textDao().getWordsCount()
            _uiState.value = _uiState.value.copy(wordsCount = count)
        }
    }

    private fun loadAppInfo() {
        val packageInfo = getApplication<Application>().packageManager
            .getPackageInfo(getApplication<Application>().packageName, 0)
        _uiState.value = _uiState.value.copy(
            appVersion = "Версия: ${packageInfo.versionName}"
        )
    }

    fun showSettings() {
        _uiState.value = _uiState.value.copy(showSettings = true)
    }

    fun hideSettings() {
        _uiState.value = _uiState.value.copy(showSettings = false)
    }
} 