package com.example.mysimpleapp.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mysimpleapp.components.CommonButton
import com.example.mysimpleapp.components.ButtonType
import com.example.mysimpleapp.components.CommonCard
import com.example.mysimpleapp.viewmodels.QuizViewModel
import com.example.mysimpleapp.data.TextEntity
import androidx.compose.ui.platform.LocalContext
import com.example.mysimpleapp.components.MaterialToast

@Composable
fun QuizScreen(
    viewModel: QuizViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }

    // Запускаем игру при первом входе
    LaunchedEffect(Unit) {
        if (!uiState.isGameStarted) {
            viewModel.startGame()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            QuizContent(
                words = uiState.currentWords,
                translation = uiState.currentTranslation,
                selectedWordId = uiState.selectedWordId,
                isCorrectAnswer = uiState.isCorrectAnswer,
                timeLeft = uiState.timeLeft,
                onWordSelected = { viewModel.checkAnswer(it) }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            MaterialToast(
                message = toastMessage,
                isVisible = showToast,
                onDismiss = { showToast = false }
            )
        }
    }

    LaunchedEffect(uiState.isCorrectAnswer) {
        if (uiState.isCorrectAnswer != null) {
            toastMessage = if (uiState.isCorrectAnswer == true) {
                "Правильно!"
            } else {
                "Неправильно. Попробуйте еще раз"
            }
            showToast = true
        }
    }
}

@Composable
private fun QuizContent(
    words: List<TextEntity>,
    translation: String,
    selectedWordId: Int?,
    isCorrectAnswer: Boolean?,
    timeLeft: Int,
    onWordSelected: (TextEntity) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (isCorrectAnswer == true) {
            Text(
                text = "Следующий вопрос через: $timeLeft",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        CommonCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = translation,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }

        // Подложка для вариантов ответа
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Разделяем слова на две группы
                val firstRow = words.take(2)
                val secondRow = words.drop(2)

                // Первый ряд
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    firstRow.forEach { word ->
                        AnswerCard(
                            word = word,
                            isSelected = selectedWordId == word.id,
                            isCorrectAnswer = isCorrectAnswer,
                            onWordSelected = onWordSelected,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Второй ряд
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    secondRow.forEach { word ->
                        AnswerCard(
                            word = word,
                            isSelected = selectedWordId == word.id,
                            isCorrectAnswer = isCorrectAnswer,
                            onWordSelected = onWordSelected,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnswerCard(
    word: TextEntity,
    isSelected: Boolean,
    isCorrectAnswer: Boolean?,
    onWordSelected: (TextEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDisabled = when {
        isCorrectAnswer == true -> true
        isCorrectAnswer == false && isSelected -> true
        else -> false
    }

    Card(
        modifier = modifier.animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isSelected && isCorrectAnswer == true -> Color(0xFF4CAF50)
                isSelected && isCorrectAnswer == false -> Color(0xFFE57373)
                else -> MaterialTheme.colorScheme.surface
            },
            contentColor = if (isSelected && (isCorrectAnswer != null)) {
                Color.White
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            disabledContainerColor = when {
                isSelected && isCorrectAnswer == true -> Color(0xFF4CAF50)
                isSelected && isCorrectAnswer == false -> Color(0xFFE57373)
                else -> MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
            },
            disabledContentColor = if (isSelected && (isCorrectAnswer != null)) {
                Color.White
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            }
        ),
        enabled = !isDisabled,
        onClick = { onWordSelected(word) }
    ) {
        Text(
            text = word.text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center
        )
    }
} 