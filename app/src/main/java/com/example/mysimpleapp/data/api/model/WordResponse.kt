package com.example.mysimpleapp.data.api.model

data class WordResponse(
    val id: Int,
    val word: String,
    val translation: String,
    val failed: Double,
    val success: Double,
    val addedAt: String
) 