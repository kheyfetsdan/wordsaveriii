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

data class DictionaryUiState(
    val words: List<TextEntity> = emptyList(),
    val isTableVisible: Boolean = true,
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val searchQuery: String = "",
    val sortBy: String = "text_asc"
)

class DictionaryViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val pageSize = 5

    private val _uiState = MutableStateFlow(DictionaryUiState())
    val uiState: StateFlow<DictionaryUiState> = _uiState.asStateFlow()

    init {
        loadWords()
    }

    private fun loadWords() {
        viewModelScope.launch {
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
                totalPages = totalPages
            )
        }
    }

    fun toggleTableVisibility() {
        _uiState.value = _uiState.value.copy(
            isTableVisible = !_uiState.value.isTableVisible
        )
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
        _uiState.value = _uiState.value.copy(sortBy = sortBy)
        loadWords()
    }
} 