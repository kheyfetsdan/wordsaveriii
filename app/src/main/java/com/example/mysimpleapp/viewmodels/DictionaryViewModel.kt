package com.example.mysimpleapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mysimpleapp.data.AppDatabase
import com.example.mysimpleapp.data.TextEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DictionaryUiState(
    val words: List<TextEntity> = emptyList(),
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val searchQuery: String = "",
    val sortBy: String = "text_asc",
    val error: String? = null
)

class DictionaryViewModel(
    application: Application,
    private val authViewModel: AuthViewModel
) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val pageSize = 10 // Увеличим количество слов на странице

    private val _uiState = MutableStateFlow(DictionaryUiState())
    val uiState: StateFlow<DictionaryUiState> = _uiState.asStateFlow()

    init {
        loadWords()
    }

    private fun loadWords() {
        viewModelScope.launch {
            try {
                val token = authViewModel.getToken()
                if (token == null) {
                    _uiState.value = _uiState.value.copy(
                        error = "Ошибка авторизации"
                    )
                    return@launch
                }

                val total = database.textDao().getFilteredWordsCount(_uiState.value.searchQuery)
                val totalPages = (total + pageSize - 1) / pageSize

                val words = database.textDao().getPagedWordsSorted(
                    query = _uiState.value.searchQuery,
                    sortBy = _uiState.value.sortBy,
                    limit = pageSize,
                    offset = _uiState.value.currentPage * pageSize
                )

                _uiState.value = _uiState.value.copy(
                    words = words,
                    totalPages = totalPages,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Ошибка загрузки слов: ${e.message}"
                )
            }
        }
    }

    fun updateCurrentPage(page: Int) {
        if (page in 0 until _uiState.value.totalPages) {
            _uiState.value = _uiState.value.copy(currentPage = page)
            loadWords()
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            currentPage = 0
        )
        loadWords()
    }

    fun updateSortOrder(sortBy: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                sortBy = sortBy,
                currentPage = 0 // Сбрасываем на первую страницу при изменении сортировки
            )
            loadWords() // Явно вызываем загрузку слов после обновления состояния
        }
    }

    companion object {
        fun provideFactory(
            application: Application,
            authViewModel: AuthViewModel
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DictionaryViewModel(application, authViewModel) as T
            }
        }
    }
} 