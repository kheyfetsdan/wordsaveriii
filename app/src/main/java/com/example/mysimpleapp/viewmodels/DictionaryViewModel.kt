package com.example.mysimpleapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mysimpleapp.data.AppDatabase
import com.example.mysimpleapp.data.TextEntity
import com.example.mysimpleapp.data.api.RetrofitClient
import com.example.mysimpleapp.data.api.model.GetWordsRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DictionaryUiState(
    val words: List<TextEntity> = emptyList(),
    val currentPage: Int = 1,
    val totalPages: Int = 0,
    val searchQuery: String = "",
    val sortBy: String = "text_asc",
    val isLoading: Boolean = false,
    val error: String? = null
)

class DictionaryViewModel(
    application: Application,
    private val authViewModel: AuthViewModel
) : AndroidViewModel(application) {
    private val pageSize = 5

    private val _uiState = MutableStateFlow(DictionaryUiState())
    val uiState: StateFlow<DictionaryUiState> = _uiState.asStateFlow()

    init {
        loadWords()
    }

    private fun loadWords() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                val token = authViewModel.getToken()
                if (token == null) {
                    _uiState.value = _uiState.value.copy(
                        error = "Ошибка авторизации",
                        isLoading = false
                    )
                    return@launch
                }

                // Получаем параметры сортировки для API
                val (sortingParam, sortingDirection) = when (_uiState.value.sortBy) {
                    "text_asc" -> "word" to "asc"
                    "text_desc" -> "word" to "desc"
                    "correct_asc" -> "success" to "asc"
                    "correct_desc" -> "success" to "desc"
                    "wrong_asc" -> "failed" to "asc"
                    "wrong_desc" -> "failed" to "desc"
                    else -> "word" to "asc"
                }

                val response = RetrofitClient.apiService.getWordsByUser(
                    token = "Bearer $token",
                    request = GetWordsRequest(
                        sortingParam = sortingParam,
                        sortingDirection = sortingDirection,
                        page = _uiState.value.currentPage,
                        pageSize = pageSize
                    )
                )

                if (response.isSuccessful) {
                    response.body()?.let { wordsResponse ->
                        // Конвертируем WordResponseRemote в TextEntity
                        val words = wordsResponse.wordList.map { remoteWord ->
                            TextEntity(
                                id = remoteWord.id,
                                text = remoteWord.word,
                                translation = remoteWord.translation,
                                correctAnswers = remoteWord.success,
                                wrongAnswers = remoteWord.failed
                            )
                        }

                        _uiState.value = _uiState.value.copy(
                            words = words,
                            totalPages = (wordsResponse.total + pageSize - 1) / pageSize,
                            error = null,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = "Ошибка загрузки слов: ${response.message()}",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Ошибка загрузки слов: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun updateCurrentPage(page: Int) {
        if (page in 1.._uiState.value.totalPages) {
            _uiState.value = _uiState.value.copy(currentPage = page)
            loadWords()
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            currentPage = 1
        )
        loadWords()
    }

    fun updateSortOrder(sortBy: String) {
        _uiState.value = _uiState.value.copy(
            sortBy = sortBy,
            currentPage = 1
        )
        loadWords()
    }

    fun refresh() {
        _uiState.value = _uiState.value.copy(
            currentPage = 1,
            isLoading = true
        )
        loadWords()
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