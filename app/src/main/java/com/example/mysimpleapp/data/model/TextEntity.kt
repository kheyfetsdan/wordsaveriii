package com.example.mysimpleapp.data.model

data class TextEntity(
    val id: Int,
    val text: String,
    val translation: String,
    val correctAnswers: Int = 0,
    val wrongAnswers: Int = 0
) 