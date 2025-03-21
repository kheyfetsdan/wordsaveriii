package com.example.mysimpleapp.viewmodels.dictionary

import com.example.mysimpleapp.data.TextEntity
import com.example.mysimpleapp.data.api.model.GetWordsResponse
import com.example.mysimpleapp.data.api.model.WordResponse
import com.example.mysimpleapp.data.api.model.WordResponseRemote
import com.example.mysimpleapp.viewmodels.AuthViewModel
import retrofit2.Response

class DictionaryUseCase(
    private val repository: DictionaryRepository,
    private val authViewModel: AuthViewModel
) {
    suspend fun getWordsByUser(
        token: String, 
        sortBy: String,
        page: Int, 
        pageSize: Int
    ): Response<GetWordsResponse> {
        val (sortingParam, sortingDirection) = getSortParams(sortBy)
        return repository.getWordsByUser(token, sortingParam, sortingDirection, page, pageSize)
    }
    
    suspend fun deleteWord(token: String, wordId: Int) {
        repository.deleteWord(token, wordId)
    }
    
    suspend fun getToken(): String? {
        return authViewModel.getToken()
    }
    
    fun mapWordResponseToTextEntity(wordResponse: WordResponseRemote): TextEntity {
        return TextEntity(
            id = wordResponse.id,
            text = wordResponse.word,
            translation = wordResponse.translation,
            correctAnswers = wordResponse.success,
            wrongAnswers = wordResponse.failed
        )
    }
    
    fun getSortParams(sortBy: String): Pair<String, String> {
        return when (sortBy) {
            "text_asc" -> "word" to "asc"
            "text_desc" -> "word" to "desc"
            "correct_asc" -> "success" to "asc"
            "correct_desc" -> "success" to "desc"
            "wrong_asc" -> "failed" to "asc"
            "wrong_desc" -> "failed" to "desc"
            else -> "word" to "asc"
        }
    }
    
    fun calculateTotalPages(total: Int, pageSize: Int): Int {
        return (total + pageSize - 1) / pageSize
    }
} 