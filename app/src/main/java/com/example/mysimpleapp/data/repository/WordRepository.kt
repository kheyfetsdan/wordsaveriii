package com.example.mysimpleapp.data.repository

import com.example.mysimpleapp.data.api.RetrofitClient
import com.example.mysimpleapp.data.api.model.GetWordsRequest
import com.example.mysimpleapp.data.model.TextEntity
import com.example.mysimpleapp.data.remote.ApiResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WordRepository @Inject constructor(private val authRepository: AuthRepository) {
    
    suspend fun getWords(page: Int, pageSize: Int): ApiResult<List<TextEntity>> = withContext(Dispatchers.IO) {
        try {
            val token = authRepository.getToken() ?: return@withContext ApiResult.Error("Ошибка авторизации")
            
            val response = RetrofitClient.apiService.getWords(
                token = "Bearer $token",
                request = GetWordsRequest(
                    sortingParam = "addedAt",
                    sortingDirection = "desc",
                    page = page,
                    pageSize = pageSize
                )
            )
            
            if (response.isSuccessful) {
                val words = response.body()?.wordList?.map { 
                    TextEntity(
                        id = it.id,
                        text = it.word,
                        translation = it.translation,
                        correctAnswers = it.success * 100,
                        wrongAnswers = it.failed * 100
                    )
                } ?: emptyList()
                
                ApiResult.Success(words)
            } else {
                ApiResult.Error("Ошибка загрузки слов: ${response.message()}")
            }
        } catch (e: Exception) {
            ApiResult.Error("Ошибка загрузки слов: ${e.message}")
        }
    }
} 