package com.example.mysimpleapp.viewmodels.wordgame

import android.util.Log
import com.example.mysimpleapp.viewmodels.AuthViewModel

/**
 * Класс, содержащий бизнес-логику игры со словами
 */
class WordGameUseCase(
    private val repository: WordGameRepository,
    private val authViewModel: AuthViewModel
) {
    suspend fun getNewWord(token: String): retrofit2.Response<com.example.mysimpleapp.data.api.model.WordResponse> {
        return repository.getWord(token)
    }
    
    suspend fun updateStatistics(token: String, wordId: Int, isCorrect: Boolean) {
        repository.updateWordStat(token, wordId, isCorrect)
    }
    
    suspend fun getToken(): String? {
        return authViewModel.getToken()
    }
    
    fun isAnswerCorrect(userAnswer: String, correctAnswer: String): Boolean {
        return userAnswer.trim().equals(correctAnswer.trim(), ignoreCase = true)
    }
    
    fun updateGameStats(state: WordGameState, isCorrect: Boolean): WordGameState {
        return if (isCorrect) {
            state.copy(
                correctAnswers = state.correctAnswers + 1,
                totalWords = state.totalWords + 1
            )
        } else {
            state.copy(
                wrongAnswers = state.wrongAnswers + 1,
                totalWords = state.totalWords + 1
            )
        }
    }
} 