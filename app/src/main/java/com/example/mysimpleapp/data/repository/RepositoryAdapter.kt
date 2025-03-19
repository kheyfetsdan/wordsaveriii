package com.example.mysimpleapp.data.repository

import com.example.mysimpleapp.data.TextEntity
import com.example.mysimpleapp.data.api.RetrofitClient
import com.example.mysimpleapp.data.api.model.GetWordsRequest
import com.example.mysimpleapp.data.api.model.QuizRequest
import com.example.mysimpleapp.data.api.model.WordStatRequest
import com.example.mysimpleapp.utils.CompatUtils.toTextEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Адаптер для обеспечения совместимости между старым кодом и новыми репозиториями
 */
class RepositoryAdapter(private val authRepository: AuthRepository) {
    
    suspend fun getToken(): String? {
        return authRepository.getToken()
    }
    
    suspend fun getWords(page: Int, pageSize: Int): Result<List<TextEntity>> = withContext(Dispatchers.IO) {
        try {
            val token = authRepository.getToken() ?: return@withContext Result.failure(Exception("Ошибка авторизации"))
            
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
                val words = response.body()?.wordList?.map { it.toTextEntity() } ?: emptyList()
                Result.success(words)
            } else {
                Result.failure(Exception("Ошибка загрузки слов: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getQuizWord(previousWord: String) = withContext(Dispatchers.IO) {
        try {
            val token = authRepository.getToken() ?: return@withContext Result.failure(Exception("Ошибка авторизации"))
            
            val response = RetrofitClient.apiService.getQuizWord(
                token = "Bearer $token",
                request = QuizRequest(previousWord = previousWord)
            )
            
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Ошибка загрузки слова для квиза: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateWordStat(wordId: Int, success: Boolean) = withContext(Dispatchers.IO) {
        try {
            val token = authRepository.getToken() ?: return@withContext Result.failure(Exception("Ошибка авторизации"))
            
            val response = RetrofitClient.apiService.updateWordStat(
                token = "Bearer $token",
                wordId = wordId,
                request = WordStatRequest(success = success)
            )
            
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Ошибка обновления статистики: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 