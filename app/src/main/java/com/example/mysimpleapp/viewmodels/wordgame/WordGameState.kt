package com.example.mysimpleapp.viewmodels.wordgame

data class WordGameState(
    val word: String = "",
    val translation: String = "",
    val userTranslation: String = "",
    val showTranslation: Boolean = false,
    val isAnswerChecked: Boolean = false,
    val showConfirmDialog: Boolean = false,
    val isCorrect: Boolean? = null,
    val error: String? = null,
    val wordId: Int = 0,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val isCheckingAnswer: Boolean = false,
    val showSkipConfirmDialog: Boolean = false,
    val countdown: Int = 0,
    val totalWords: Int = 0,
    val correctAnswers: Int = 0,
    val wrongAnswers: Int = 0
) 