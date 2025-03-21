package com.example.mysimpleapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysimpleapp.data.AppDatabase
import com.example.mysimpleapp.data.TextEntity
import com.example.mysimpleapp.data.api.RetrofitClient
import com.example.mysimpleapp.data.api.model.SaveWordRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mysimpleapp.viewmodels.input.InputRepository
import com.example.mysimpleapp.viewmodels.input.InputWordState
import com.example.mysimpleapp.viewmodels.input.InputUseCase
import retrofit2.Response

class InputViewModel(
    application: Application,
    authViewModel: AuthViewModel
) : BaseViewModel<InputWordState>(
    application,
    authViewModel,
    InputWordState()
) {
    private val repository = InputRepository()
    private val inputUseCase = InputUseCase(repository, authViewModel)

    fun updateText(text: String) {
        val formattedText = inputUseCase.formatInput(text)
        val isValid = inputUseCase.validateInput(formattedText)
        
        updateState { it.copy(
            text = formattedText,
            isValid = isValid,
            error = if (!isValid && formattedText.isNotEmpty()) "Текст недействителен" else null,
            showErrorMessage = !isValid && formattedText.isNotEmpty()
        )}
    }

    fun updateTranslation(newTranslation: String) {
        updateState { it.copy(translation = newTranslation) }
    }

    fun saveInput() {
        val currentState = _uiState.value
        val currentText = currentState.text
        val currentTranslation = currentState.translation
        
        if (!inputUseCase.validateInput(currentText)) {
            updateState { it.copy(
                error = "Введите слово", 
                showErrorMessage = true
            )}
            return
        }
        
        if (!inputUseCase.validateInput(currentTranslation)) {
            updateState { it.copy(
                error = "Введите перевод",
                showErrorMessage = true
            )}
            return
        }
        
        launchWithErrorHandling(
            onError = { errorMessage ->
                updateState { it.copy(
                    error = "Ошибка сохранения: $errorMessage",
                    isLoading = false,
                    showErrorMessage = true
                )}
            }
        ) {
            saveWordToApi()
        }
    }
    
    private suspend fun saveWordToApi() {
        val saveAction = withLoadingState {
            withToken(
                onError = { errorMessage ->
                    updateState { it.copy(
                        error = errorMessage,
                        isLoading = false
                    )}
                }
            ) { token ->
                apiCall(
                    onError = { errorMessage ->
                        updateState { it.copy(
                            error = "Ошибка сохранения: $errorMessage",
                            isLoading = false
                        )}
                    }
                ) {
                    val currentState = _uiState.value
                    inputUseCase.saveWord(
                        token, 
                        currentState.text, 
                        currentState.translation
                    )
                }?.let { response ->
                    handleSaveResponse(response)
                }
            }
        }
        
        saveAction(
            { isLoading -> _uiState.value.copy(isLoading = isLoading) },
            { error -> _uiState.value.copy(error = error) }
        )
    }
    
    private fun handleSaveResponse(response: Response<*>) {
        if (response.isSuccessful) {
            updateState { it.copy(
                showSuccessMessage = true,
                showErrorMessage = false,
                isLoading = false,
                text = "",
                translation = "",
                isValid = false,
                error = null
            )}
        } else {
            updateState { it.copy(
                error = "Ошибка: ${response.code()} ${response.message()}",
                isLoading = false,
                showErrorMessage = true
            )}
        }
    }

    fun clearFields() {
        updateState { InputWordState() }
    }

    fun hideMessages() {
        updateState { it.copy(
            showErrorMessage = false,
            showSuccessMessage = false,
            error = null
        )}
    }

    fun saveWord() {
        saveInput()
    }

    companion object {
        fun provideFactory(
            application: Application,
            authViewModel: AuthViewModel
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return InputViewModel(application, authViewModel) as T
            }
        }
    }
} 