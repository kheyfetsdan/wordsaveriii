package com.example.mysimpleapp.viewmodels.input

import android.util.Log
import com.example.mysimpleapp.data.api.RetrofitClient
import com.example.mysimpleapp.data.api.model.SaveWordRequest

class InputRepository {
    suspend fun saveWord(token: String, word: String, translation: String) = 
        RetrofitClient.apiService.saveWord(
            token = token,
            request = SaveWordRequest(
                word = word,
                translation = translation
            )
        )
    
    fun validateInput(text: String): Boolean {
        // Простая валидация - текст не должен быть пустым
        return text.trim().isNotEmpty()
    }
} 