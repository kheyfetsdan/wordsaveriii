package com.example.mysimpleapp.utils

import com.example.mysimpleapp.data.TextEntity
import com.example.mysimpleapp.data.api.model.WordResponseRemote
import com.example.mysimpleapp.data.remote.ApiResult

/**
 * Утилиты для обеспечения совместимости между старым и новым кодом
 */
object CompatUtils {
    
    /**
     * Преобразует ApiResult в формат, понятный старым ViewModel
     */
    fun <T, R> ApiResult<T>.toUiState(
        transform: (T) -> R,
        loadingState: R,
        errorTransform: (String) -> R
    ): R {
        return when (this) {
            is ApiResult.Success -> transform(data)
            is ApiResult.Loading -> loadingState
            is ApiResult.Error -> errorTransform(message)
        }
    }
    
    /**
     * Преобразует WordResponseRemote в TextEntity
     */
    fun WordResponseRemote.toTextEntity(): TextEntity {
        return TextEntity(
            id = id,
            text = word,
            translation = translation,
            correctAnswers = success * 100,
            wrongAnswers = failed * 100
        )
    }
} 