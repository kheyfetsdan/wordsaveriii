package com.example.mysimpleapp.viewmodels.input

import com.example.mysimpleapp.viewmodels.AuthViewModel

class InputUseCase(
    private val repository: InputRepository,
    private val authViewModel: AuthViewModel
) {
    suspend fun saveWord(token: String, word: String, translation: String) =
        repository.saveWord(token, word, translation)
    
    suspend fun getToken(): String? {
        return authViewModel.getToken()
    }
    
    fun validateInput(text: String): Boolean {
        return repository.validateInput(text)
    }
    
    fun formatInput(text: String): String {
        // Здесь может быть логика форматирования ввода
        return text.trim()
    }
} 