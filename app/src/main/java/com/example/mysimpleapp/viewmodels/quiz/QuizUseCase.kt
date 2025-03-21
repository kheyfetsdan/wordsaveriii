package com.example.mysimpleapp.viewmodels.quiz

import com.example.mysimpleapp.data.api.model.QuizResponse
import com.example.mysimpleapp.viewmodels.AuthViewModel

class QuizUseCase(
    private val repository: QuizRepository,
    private val authViewModel: AuthViewModel
) {
    suspend fun getQuizWord(token: String, previousWord: String) = 
        repository.getQuizWord(token, previousWord)
    
    suspend fun updateWordStat(token: String, wordId: Int, isCorrect: Boolean) {
        repository.updateWordStat(token, wordId, isCorrect)
    }
    
    suspend fun getToken(): String? {
        return authViewModel.getToken()
    }
    
    fun isAnswerCorrect(selectedTranslation: String, correctTranslation: String): Boolean {
        return selectedTranslation == correctTranslation
    }
    
    fun updateQuizStats(state: QuizState, isCorrect: Boolean): QuizState {
        return state.copy(
            totalAnswered = state.totalAnswered + 1,
            correctAnswers = if (isCorrect) state.correctAnswers + 1 else state.correctAnswers
        )
    }
    
    fun prepareTranslationsFromResponse(response: QuizResponse): List<String> {
        return listOf(
            response.trueTranslation,
            response.translation1,
            response.translation2,
            response.translation3
        ).shuffled()
    }
} 