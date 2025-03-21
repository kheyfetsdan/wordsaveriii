package com.example.mysimpleapp.viewmodels.dictionary

import android.util.Log
import com.example.mysimpleapp.data.api.RetrofitClient
import com.example.mysimpleapp.data.api.model.GetWordsRequest
import com.example.mysimpleapp.data.api.model.GetWordsResponse
import retrofit2.Response

class DictionaryRepository {
    suspend fun getWordsByUser(
        token: String, 
        sortingParam: String, 
        sortingDirection: String,
        page: Int, 
        pageSize: Int
    ): Response<GetWordsResponse> {
        val formattedToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
        
        return RetrofitClient.apiService.getWordsByUser(
            token = formattedToken,
            request = GetWordsRequest(
                sortingParam = sortingParam,
                sortingDirection = sortingDirection,
                page = page,
                pageSize = pageSize
            )
        )
    }
    
    suspend fun deleteWord(token: String, wordId: Int) {
        try {
            val formattedToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
            
            RetrofitClient.apiService.deleteWord(
                token = formattedToken,
                wordId = wordId
            )
        } catch (e: Exception) {
            Log.e("DictionaryRepository", "Ошибка удаления слова: ${e.message}")
            throw e
        }
    }
} 