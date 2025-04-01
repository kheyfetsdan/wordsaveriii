package com.example.mysimpleapp.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.example.mysimpleapp.data.api.RetrofitClient
import com.example.mysimpleapp.data.api.model.SaveWordIdRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WordDetailsUiState(
    val id: Int = 0,
    val word: Any? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isEditing: Boolean = false,
    val editedText: String = "",
    val editedTranslation: String = "",
    val isSaving: Boolean = false,
    val showDeleteConfirmation: Boolean = false,
    val isDeleting: Boolean = false
)

class WordDetailsViewModel(
    application: Application,
    authViewModel: AuthViewModel,
    private val wordId: Int
) : BaseViewModel<WordDetailsUiState>(application, authViewModel, WordDetailsUiState()) {

    init {
        loadWord()
    }

    fun loadWord() {
        launchWithErrorHandling(
            onError = { errorMessage ->
                updateState { it.copy(
                    error = "Ошибка загрузки слова: $errorMessage",
                    isLoading = false
                )}
            }
        ) {
            withToken(
                onError = { errorMessage ->
                    updateState { it.copy(
                        error = errorMessage,
                        isLoading = false
                    )}
                }
            ) { token ->
                updateState { it.copy(isLoading = true) }

                apiCall(
                    onError = { errorMessage ->
                        updateState { it.copy(
                            error = "Ошибка загрузки слова: $errorMessage",
                            isLoading = false
                        )}
                    }
                ) {
                    RetrofitClient.apiService.getWordById(
                        token = token,
                        wordId = wordId
                    )
                }?.let { response ->
                    if (response.isSuccessful) {
                        response.body()?.let { wordResponse ->
                            updateState { it.copy(
                                id = wordResponse.id,
                                word = wordResponse,
                                editedText = wordResponse.word,
                                editedTranslation = wordResponse.translation,
                                error = null,
                                isLoading = false
                            )}
                        }
                    } else {
                        updateState { it.copy(
                            error = "Ошибка загрузки слова: ${response.message()}",
                            isLoading = false
                        )}
                    }
                }
            }
        }
    }

    fun startEditing() {
        updateState { it.copy(
            isEditing = true,
            editedText = it.word?.let { it.toString() } ?: "",
            editedTranslation = it.word?.let { it.toString() } ?: ""
        ) }
    }

    fun cancelEditing() {
        updateState { it.copy(
            isEditing = false,
            editedText = "",
            editedTranslation = ""
        ) }
    }

    fun updateEditedText(text: String) {
        updateState { it.copy(editedText = text) }
    }

    fun updateEditedTranslation(translation: String) {
        updateState { it.copy(editedTranslation = translation) }
    }

    fun saveEditedWord() {
        viewModelScope.launch {
            try {
                updateState { it.copy(isSaving = true) }

                val token = authViewModel.getToken()
                if (token == null) {
                    updateState { it.copy(
                        error = "Ошибка авторизации",
                        isSaving = false
                    ) }
                    return@launch
                }

                // Получаем текущие значения из состояния
                val currentState = uiState.value
                
                val response = RetrofitClient.apiService.updateWord(
                    token = "Bearer $token",
                    request = SaveWordIdRequest(
                        id = currentState.id,
                        word = currentState.editedText.trim(),
                        translation = currentState.editedTranslation.trim()
                    )
                )

                if (response.isSuccessful) {
                    loadWord() // Перезагружаем слово после успешного сохранения
                    updateState { it.copy(
                        isEditing = false,
                        isSaving = false
                    ) }
                } else {
                    updateState { it.copy(
                        error = "Ошибка сохранения слова: ${response.message()}",
                        isSaving = false
                    ) }
                }
            } catch (e: Exception) {
                updateState { it.copy(
                    error = "Ошибка сохранения слова: ${e.message}",
                    isSaving = false
                ) }
            }
        }
    }

    fun showDeleteConfirmation() {
        updateState { it.copy(showDeleteConfirmation = true) }
    }

    fun hideDeleteConfirmation() {
        updateState { it.copy(showDeleteConfirmation = false) }
    }

    fun deleteWord(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                updateState { it.copy(isDeleting = true) }

                val token = authViewModel.getToken()
                if (token == null) {
                    updateState { it.copy(
                        error = "Ошибка авторизации",
                        isDeleting = false
                    ) }
                    return@launch
                }

                val response = RetrofitClient.apiService.deleteWord(
                    token = "Bearer $token",
                    wordId = wordId
                )

                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    updateState { it.copy(
                        error = "Ошибка удаления слова: ${response.message()}",
                        isDeleting = false,
                        showDeleteConfirmation = false
                    ) }
                }
            } catch (e: Exception) {
                updateState { it.copy(
                    error = "Ошибка удаления слова: ${e.message}",
                    isDeleting = false,
                    showDeleteConfirmation = false
                ) }
            }
        }
    }

    companion object {
        fun provideFactory(
            application: Application,
            authViewModel: AuthViewModel,
            wordId: Int
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return WordDetailsViewModel(application, authViewModel, wordId) as T
            }
        }
    }
} 