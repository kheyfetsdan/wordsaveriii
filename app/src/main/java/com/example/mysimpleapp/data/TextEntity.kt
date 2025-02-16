package com.example.mysimpleapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "texts")
data class TextEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val text: String,
    val translation: String,
    val correctAnswers: Int = 0,
    val wrongAnswers: Int = 0
)