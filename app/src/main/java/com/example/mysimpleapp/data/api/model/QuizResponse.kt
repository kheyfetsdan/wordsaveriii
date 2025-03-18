package com.example.mysimpleapp.data.api.model

data class QuizResponse(
    val id: Int,
    val word: String,
    val trueTranslation: String,
    val translation1: String,
    val translation2: String,
    val translation3: String
) 