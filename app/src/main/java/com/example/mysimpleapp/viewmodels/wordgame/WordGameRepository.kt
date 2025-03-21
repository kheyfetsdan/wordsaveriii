package com.example.mysimpleapp.viewmodels.wordgame

import android.util.Log
import com.example.mysimpleapp.data.api.RetrofitClient
import com.example.mysimpleapp.data.api.model.WordStatRequest

class WordGameRepository {
    
    suspend fun getWord(token: String) = 
        RetrofitClient.apiService.getWord(token)
    
    suspend fun updateWordStat(token: String, wordId: Int, success: Boolean) {
        try {
            RetrofitClient.apiService.updateWordStat(
                token = token,
                wordId = wordId,
                request = WordStatRequest(success = success)
            )
        } catch (e: Exception) {
            // Логируем ошибку, но не прерываем выполнение
            Log.e("WordGameRepository", "Ошибка обновления статистики: ${e.message}")
        }
    }
} 