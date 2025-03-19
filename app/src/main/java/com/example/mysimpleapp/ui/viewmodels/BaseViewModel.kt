package com.example.mysimpleapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysimpleapp.data.remote.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<T> : ViewModel() {
    private val _uiState = MutableStateFlow<ApiResult<T>>(ApiResult.Loading)
    val uiState: StateFlow<ApiResult<T>> = _uiState.asStateFlow()

    protected fun launchDataLoad(block: suspend () -> T) {
        viewModelScope.launch {
            try {
                _uiState.value = ApiResult.Loading
                val result = block()
                _uiState.value = ApiResult.Success(result)
            } catch (e: Exception) {
                _uiState.value = ApiResult.Error(e.message ?: "Неизвестная ошибка")
            }
        }
    }
} 