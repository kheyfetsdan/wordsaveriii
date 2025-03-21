package com.example.mysimpleapp.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysimpleapp.data.AppDatabase
import com.example.mysimpleapp.data.UserEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.mysimpleapp.data.api.RetrofitClient
import com.example.mysimpleapp.data.api.model.LoginRequest

data class AuthUiState(
    val isAuthenticated: Boolean = false,
    val email: String = "",
    val password: String = "",
    val error: String? = null,
    val token: String? = null
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val sharedPreferences = application.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    private val _uiState = MutableStateFlow(AuthUiState(isAuthenticated = sharedPreferences.getBoolean("is_authenticated", false)))
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        // Добавляем тестовых пользователей только если их еще нет
        viewModelScope.launch {
            val testUsers = listOf(
                UserEntity("test1@test.ru", "111111"),
                UserEntity("test2@test.ru", "222222")
            )
            
            testUsers.forEach { user ->
                if (database.userDao().getUserCount(user.email) == 0) {
                    database.userDao().insertAll(listOf(user))
                }
            }
        }
    }

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            error = null
        )
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            error = null
        )
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.login(
                    LoginRequest(
                        email = email,
                        password = password
                    )
                )

                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        saveToken(loginResponse.token)
                        _uiState.value = _uiState.value.copy(
                            isAuthenticated = true,
                            email = email,
                            password = password,
                            error = null
                        )
                        // Сохраняем состояние аутентификации
                        sharedPreferences.edit()
                            .putBoolean("is_authenticated", true)
                            .apply()
                    }
                } else {
                    when (response.code()) {
                        400 -> {
                            _uiState.value = _uiState.value.copy(
                                error = "Неверный email или пароль"
                            )
                        }
                        else -> {
                            _uiState.value = _uiState.value.copy(
                                error = "Ошибка входа: ${response.message()}"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Ошибка при входе: ${e.message}"
                )
            }
        }
    }

    fun logout() {
        _uiState.value = AuthUiState()
        sharedPreferences.edit()
            .remove("auth_token")
            .putBoolean("is_authenticated", false)
            .apply()
    }

    fun saveToken(token: String) {
        sharedPreferences.edit()
            .putString("auth_token", token)
            .apply()
        
        _uiState.value = _uiState.value.copy(
            token = token,
            isAuthenticated = true
        )
        
        RetrofitClient.init(getApplication())
    }

    fun getToken(): String? {
        val cachedToken = _uiState.value.token
        if (!cachedToken.isNullOrEmpty()) {
            return cachedToken
        }
        
        val token = sharedPreferences.getString("auth_token", null)
        if (!token.isNullOrEmpty()) {
            _uiState.value = _uiState.value.copy(token = token)
        }
        return token
    }

    fun checkAuthentication() {
        val isAuthenticated = sharedPreferences.getBoolean("is_authenticated", false)
        val token = sharedPreferences.getString("auth_token", null)
        
        if (isAuthenticated && !token.isNullOrEmpty()) {
            _uiState.value = _uiState.value.copy(
                isAuthenticated = true,
                token = token
            )
        }
    }

    fun registerUser(token: String, email: String) {
        saveToken(token)
        _uiState.value = _uiState.value.copy(
            isAuthenticated = true,
            email = email,
            error = null
        )
        // Сохраняем состояние аутентификации
        sharedPreferences.edit()
            .putBoolean("is_authenticated", true)
            .apply()
    }
} 