package com.example.mysimpleapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.Job
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import android.util.Log

abstract class BaseViewModel<T>(
    application: Application,
    protected val authViewModel: AuthViewModel,
    initialState: T
) : AndroidViewModel(application) {
    
    protected val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<T> = _uiState.asStateFlow()
    
    protected fun updateState(update: (T) -> T) {
        _uiState.update(update)
    }
    
    protected fun launchWithErrorHandling(
        onError: (String) -> Unit,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        block: suspend () -> Unit
    ): Job {
        return viewModelScope.launch(dispatcher) {
            try {
                block()
            } catch (e: Exception) {
                Log.e(this::class.java.simpleName, "Ошибка: ${e.message}", e)
                onError("Ошибка: ${e.message}")
            }
        }
    }
    
    protected suspend fun getTokenOrNull(): String? {
        return authViewModel.getToken()
    }
    
    protected suspend fun withToken(
        onError: (String) -> Unit,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        block: suspend (String) -> Unit
    ) {
        val token = getTokenOrNull()
        if (token == null) {
            onError("Ошибка авторизации")
            return
        }
        
        try {
            block("Bearer $token")
        } catch (e: Exception) {
            Log.e(this::class.java.simpleName, "Ошибка: ${e.message}", e)
            onError("Ошибка: ${e.message}")
        }
    }
    
    protected fun <R> withLoadingState(
        action: suspend () -> R
    ): suspend (setLoading: (Boolean) -> T, setError: (String?) -> T) -> R {
        return { setLoading, setError ->
            try {
                updateState { setLoading(true) }
                updateState { setError(null) }
                val result = action()
                updateState { setLoading(false) }
                result
            } catch (e: Exception) {
                updateState { setLoading(false) }
                updateState { setError("Ошибка: ${e.message}") }
                throw e
            }
        }
    }
    
    protected fun launchSafe(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        block: suspend () -> Unit
    ): Job {
        return viewModelScope.launch(dispatcher) {
            try {
                block()
            } catch (e: Exception) {
                Log.e(this::class.java.simpleName, "Необработанная ошибка: ${e.message}", e)
            }
        }
    }
    
    protected suspend fun <R> apiCall(
        onError: (String) -> Unit,
        apiRequest: suspend () -> R
    ): R? {
        return try {
            apiRequest()
        } catch (e: Exception) {
            Log.e(this::class.java.simpleName, "Ошибка API: ${e.message}", e)
            onError("Ошибка сервера: ${e.message}")
            null
        }
    }
} 