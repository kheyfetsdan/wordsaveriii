package com.example.mysimpleapp.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

@Composable
fun ResultText(
    isCorrect: Boolean,
    text: String
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        color = if (isCorrect) Color(0xFF4CAF50) else Color(0xFFE57373),
        fontWeight = FontWeight.Bold
    )
} 