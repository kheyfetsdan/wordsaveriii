package com.example.mysimpleapp.viewmodels.quiz

data class QuizState(
    val currentWord: String = "",
    val currentWordId: Int = 0,
    val translations: List<String> = emptyList(),
    val correctTranslation: String = "",
    val selectedTranslation: String? = null,
    val isCorrectAnswer: Boolean? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showErrorMessage: Boolean = false,
    val timeLeft: Int = 5,
    val totalAnswered: Int = 0,
    val correctAnswers: Int = 0
) 