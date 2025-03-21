package com.example.mysimpleapp.viewmodels.quiz

import android.util.Log
import com.example.mysimpleapp.data.api.RetrofitClient
import com.example.mysimpleapp.data.api.model.QuizRequest
import com.example.mysimpleapp.data.api.model.WordStatRequest

class QuizRepository {
    suspend fun getQuizWord(token: String, previousWord: String) = 
        RetrofitClient.apiService.getQuizWord(
            token = "Bearer $token",
            request = QuizRequest(previousWord = previousWord)
        )
    
    suspend fun updateWordStat(token: String, wordId: Int, success: Boolean) {
        try {
            RetrofitClient.apiService.updateWordStat(
                token = "Bearer $token",
                wordId = wordId,
                request = WordStatRequest(success = success)
            )
        } catch (e: Exception) {
            // Логируем ошибку, но не прерываем выполнение
            Log.e("QuizRepository", "Ошибка обновления статистики: ${e.message}")
        }
    }
} 