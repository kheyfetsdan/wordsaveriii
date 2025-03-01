package com.example.mysimpleapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mysimpleapp.data.AppDatabase
import com.example.mysimpleapp.data.UserEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.mysimpleapp.data.api.RetrofitClient
import com.example.mysimpleapp.data.api.model.RegisterRequest

data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val error: String? = null,
    val showLoginLink: Boolean = false
)

class RegisterViewModel(
    application: Application,
    private val authViewModel: AuthViewModel,
    private val onRegistrationSuccess: () -> Unit,
    private val onNavigateToLogin: () -> Unit
) : AndroidViewModel(application) {
    private val userDao = AppDatabase.getDatabase(application).userDao()
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email, 
            error = null,
            showLoginLink = false
        )
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password, 
            error = null,
            showLoginLink = false
        )
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(
            confirmPassword = confirmPassword, 
            error = null,
            showLoginLink = false
        )
    }

    fun register() {
        viewModelScope.launch {
            val state = _uiState.value
            
            when {
                state.email.isEmpty() -> {
                    _uiState.value = state.copy(error = "Введите email")
                    return@launch
                }
                state.password.isEmpty() -> {
                    _uiState.value = state.copy(error = "Введите пароль")
                    return@launch
                }
                state.confirmPassword.isEmpty() -> {
                    _uiState.value = state.copy(error = "Подтвердите пароль")
                    return@launch
                }
                state.password != state.confirmPassword -> {
                    _uiState.value = state.copy(error = "Пароли не совпадают")
                    return@launch
                }
            }

            try {
                val response = RetrofitClient.apiService.register(
                    RegisterRequest(
                        email = state.email,
                        password = state.password
                    )
                )

                if (response.isSuccessful) {
                    response.body()?.let { registerResponse ->
                        authViewModel.saveToken(registerResponse.token)
                        authViewModel.login(state.email, state.password)
                        onRegistrationSuccess()
                    }
                } else {
                    when (response.code()) {
                        409 -> {
                            _uiState.value = state.copy(
                                error = "Пользователь с таким email уже зарегистрирован",
                                showLoginLink = true
                            )
                        }
                        else -> {
                            _uiState.value = state.copy(
                                error = "Ошибка регистрации: ${response.message()}",
                                showLoginLink = false
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = state.copy(
                    error = "Ошибка при регистрации: ${e.message}",
                    showLoginLink = false
                )
            }
        }
    }

    companion object {
        fun provideFactory(
            application: Application,
            authViewModel: AuthViewModel,
            onRegistrationSuccess: () -> Unit,
            onNavigateToLogin: () -> Unit
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return RegisterViewModel(
                    application, 
                    authViewModel, 
                    onRegistrationSuccess,
                    onNavigateToLogin
                ) as T
            }
        }
    }
} 