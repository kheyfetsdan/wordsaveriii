package com.example.mysimpleapp.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mysimpleapp.data.api.model.GetWordsResponse
import com.example.mysimpleapp.viewmodels.dictionary.DictionaryRepository
import com.example.mysimpleapp.viewmodels.dictionary.DictionaryState
import com.example.mysimpleapp.viewmodels.dictionary.DictionaryUseCase
import retrofit2.Response

/**
 * ViewModel для экрана словаря пользователя
 */
class DictionaryViewModel(
    application: Application,
    authViewModel: AuthViewModel
) : BaseViewModel<DictionaryState>(
    application,
    authViewModel,
    DictionaryState()
) {
    private val pageSize = 5
    private val repository = DictionaryRepository()
    private val dictionaryUseCase = DictionaryUseCase(repository, authViewModel)
    
    init {
        loadWords()
    }
    
    /**
     * Загружает список слов пользователя
     */
    fun loadWords() {
        launchWithErrorHandling(
            onError = { errorMessage ->
                updateState { it.copy(
                    error = "Ошибка загрузки словаря: $errorMessage",
                    isLoading = false,
                    showErrorMessage = true,
                    isRefreshing = false
                )}
            }
        ) {
            loadWordsFromApi()
        }
    }
    
    /**
     * Обновляет список слов (pull-to-refresh)
     */
    fun refreshWords() {
        updateState { it.copy(isRefreshing = true) }
        loadWords()
    }
    
    /**
     * Обновляет список слов (pull-to-refresh)
     * Алиас для refreshWords() для совместимости с интерфейсом SwipeRefresh
     */
    fun refresh() {
        refreshWords()
    }
    
    private suspend fun loadWordsFromApi() {
        val loadWordsAction = withLoadingState {
            withToken(
                onError = { errorMessage ->
                    updateState { it.copy(
                        error = errorMessage,
                        isLoading = false,
                        showErrorMessage = true,
                        isRefreshing = false
                    )}
                }
            ) { token ->
                apiCall(
                    onError = { errorMessage ->
                        updateState { it.copy(
                            error = "Ошибка загрузки словаря: $errorMessage",
                            isLoading = false,
                            showErrorMessage = true,
                            isRefreshing = false
                        )}
                    }
                ) {
                    val currentState = uiState.value
                    val response = dictionaryUseCase.getWordsByUser(
                        token,
                        currentState.sortBy,
                        currentState.currentPage,
                        pageSize
                    )
                    response
                }?.let { response ->
                    handleWordsResponse(response)
                }
            }
        }
        
        loadWordsAction(
            { isLoading -> 
                val state = uiState.value
                state.copy(isLoading = isLoading) 
            },
            { error -> 
                val state = uiState.value
                state.copy(error = error, showErrorMessage = true) 
            }
        )
    }
    
    private fun handleWordsResponse(response: retrofit2.Response<com.example.mysimpleapp.data.api.model.GetWordsResponse>) {
        if (response.isSuccessful) {
            response.body()?.let { wordsResponse ->
                // Конвертируем WordResponse в TextEntity
                val words = wordsResponse.wordList.map { remoteWord ->
                    dictionaryUseCase.mapWordResponseToTextEntity(remoteWord)
                }
                
                updateState { it.copy(
                    words = words,
                    totalPages = dictionaryUseCase.calculateTotalPages(wordsResponse.total, pageSize),
                    error = null,
                    isLoading = false,
                    isRefreshing = false,
                    showErrorMessage = false
                )}
            }
        } else {
            updateState { it.copy(
                error = "Ошибка: ${response.code()} ${response.message()}",
                isLoading = false,
                isRefreshing = false,
                showErrorMessage = true
            )}
        }
    }
    
    /**
     * Обновляет текущую страницу и загружает соответствующие слова
     */
    fun updateCurrentPage(page: Int) {
        if (page in 1..uiState.value.totalPages) {
            updateState { it.copy(currentPage = page) }
            loadWords()
        }
    }
    
    /**
     * Обновляет поисковый запрос и загружает соответствующие слова
     */
    fun updateSearchQuery(query: String) {
        updateState { it.copy(
            searchQuery = query,
            currentPage = 1
        )}
        loadWords()
    }
    
    /**
     * Обновляет порядок сортировки и загружает соответствующие слова
     */
    fun updateSortOrder(sortBy: String) {
        updateState { it.copy(
            sortBy = sortBy,
            currentPage = 1
        )}
        loadWords()
    }
    
    /**
     * Скрывает сообщения об ошибках
     */
    fun hideErrorMessage() {
        updateState { it.copy(
            showErrorMessage = false
        )}
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