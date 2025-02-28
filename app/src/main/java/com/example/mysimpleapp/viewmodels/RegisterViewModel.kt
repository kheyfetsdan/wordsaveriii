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

data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val error: String? = null
)

class RegisterViewModel(
    application: Application,
    private val authViewModel: AuthViewModel
) : AndroidViewModel(application) {
    private val userDao = AppDatabase.getDatabase(application).userDao()
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email, error = null)
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password, error = null)
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = confirmPassword, error = null)
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
                // TODO: Добавить проверку формата email
                // TODO: Добавить проверку сложности пароля
            }

            try {
                // Проверяем, не существует ли уже пользователь с таким email
                val existingUser = userDao.getUserByEmail(state.email)
                if (existingUser != null) {
                    _uiState.value = state.copy(error = "Пользователь с таким email уже существует")
                    return@launch
                }

                // Создаем нового пользователя
                val user = UserEntity(
                    email = state.email,
                    password = state.password // В реальном приложении нужно хэшировать!
                )
                userDao.insert(user)

                // Авторизуем пользователя
                authViewModel.login(state.email, state.password)
            } catch (e: Exception) {
                _uiState.value = state.copy(error = "Ошибка при регистрации: ${e.message}")
            }
        }
    }

    companion object {
        fun provideFactory(
            application: Application,
            authViewModel: AuthViewModel
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return RegisterViewModel(application, authViewModel) as T
            }
        }
    }
} 