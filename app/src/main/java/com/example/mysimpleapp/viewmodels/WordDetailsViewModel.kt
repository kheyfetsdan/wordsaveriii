package com.example.mysimpleapp.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.example.mysimpleapp.data.TextEntity
import com.example.mysimpleapp.data.api.RetrofitClient
import com.example.mysimpleapp.data.api.model.SaveWordIdRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WordDetailsUiState(
    val id: Int = 0,
    val word: TextEntity? = null,
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
    private val authViewModel: AuthViewModel,
    private val wordId: Int
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(WordDetailsUiState())
    val uiState: StateFlow<WordDetailsUiState> = _uiState.asStateFlow()

    init {
        loadWord()
    }

    fun loadWord() {
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

                val response = RetrofitClient.apiService.getWordById(
                    token = "Bearer $token",
                    wordId = wordId
                )

                if (response.isSuccessful) {
                    response.body()?.let { wordResponse ->
                        _uiState.value = _uiState.value.copy(
                            id = wordResponse.id,
                            word = TextEntity(
                                id = wordResponse.id,
                                text = wordResponse.word,
                                translation = wordResponse.translation,
                                correctAnswers = (wordResponse.success * 100),
                                wrongAnswers = (wordResponse.failed * 100)
                            ),
                            error = null,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = "Ошибка загрузки слова: ${response.message()}",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Ошибка загрузки слова: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun startEditing() {
        _uiState.value = _uiState.value.copy(
            isEditing = true,
            editedText = _uiState.value.word?.text ?: "",
            editedTranslation = _uiState.value.word?.translation ?: ""
        )
    }

    fun cancelEditing() {
        _uiState.value = _uiState.value.copy(
            isEditing = false,
            editedText = "",
            editedTranslation = ""
        )
    }

    fun updateEditedText(text: String) {
        _uiState.value = _uiState.value.copy(editedText = text)
    }

    fun updateEditedTranslation(translation: String) {
        _uiState.value = _uiState.value.copy(editedTranslation = translation)
    }

    fun saveEditedWord() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isSaving = true)

                val token = authViewModel.getToken()
                if (token == null) {
                    _uiState.value = _uiState.value.copy(
                        error = "Ошибка авторизации",
                        isSaving = false
                    )
                    return@launch
                }

                val response = RetrofitClient.apiService.updateWord(
                    token = "Bearer $token",
                    request = SaveWordIdRequest(
                        id = _uiState.value.id,
                        word = _uiState.value.editedText.trim(),
                        translation = _uiState.value.editedTranslation.trim()
                    )
                )

                if (response.isSuccessful) {
                    loadWord() // Перезагружаем слово после успешного сохранения
                    _uiState.value = _uiState.value.copy(
                        isEditing = false,
                        isSaving = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = "Ошибка сохранения слова: ${response.message()}",
                        isSaving = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Ошибка сохранения слова: ${e.message}",
                    isSaving = false
                )
            }
        }
    }

    fun showDeleteConfirmation() {
        _uiState.value = _uiState.value.copy(showDeleteConfirmation = true)
    }

    fun hideDeleteConfirmation() {
        _uiState.value = _uiState.value.copy(showDeleteConfirmation = false)
    }

    fun deleteWord(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isDeleting = true)

                val token = authViewModel.getToken()
                if (token == null) {
                    _uiState.value = _uiState.value.copy(
                        error = "Ошибка авторизации",
                        isDeleting = false
                    )
                    return@launch
                }

                val response = RetrofitClient.apiService.deleteWord(
                    token = "Bearer $token",
                    wordId = wordId
                )

                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = "Ошибка удаления слова: ${response.message()}",
                        isDeleting = false,
                        showDeleteConfirmation = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Ошибка удаления слова: ${e.message}",
                    isDeleting = false,
                    showDeleteConfirmation = false
                )
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