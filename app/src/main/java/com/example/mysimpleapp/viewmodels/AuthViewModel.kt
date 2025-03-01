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

data class AuthUiState(
    val isAuthenticated: Boolean = false,
    val email: String = "",
    val password: String = "",
    val error: String? = null
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
        _uiState.value = _uiState.value.copy(
            email = email,
            password = password,
            isAuthenticated = true,
            error = null
        )
        sharedPreferences.edit().putBoolean("is_authenticated", true).apply()
    }

    fun logout() {
        _uiState.value = AuthUiState()
        sharedPreferences.edit().putBoolean("is_authenticated", false).apply()
    }

    fun saveToken(token: String) {
        sharedPreferences.edit().putString("auth_token", token).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString("auth_token", null)
    }
} 