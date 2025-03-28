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
import android.app.Application
import androidx.compose.animation.core.tween
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.style.TextOverflow
import com.example.mysimpleapp.components.PulseEffect
import com.example.mysimpleapp.viewmodels.AuthViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun QuizScreen(
    authViewModel: AuthViewModel,
    onNavigateToInput: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: QuizViewModel = viewModel(
        factory = QuizViewModel.provideFactory(
            context.applicationContext as Application,
            authViewModel
        )
    )
    val uiState by viewModel.uiState.collectAsState()
    var showToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.startGame()
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
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else if (uiState.error != null) {
                CommonCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = uiState.error.toString(),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                        
                        if (uiState.error.toString().contains("Недостаточно слов")) {
                            Spacer(modifier = Modifier.height(16.dp))
                            CommonButton(
                                text = "Добавить слова",
                                onClick = onNavigateToInput,
                                type = ButtonType.Primary
                            )
                        }
                    }
                }
            } else {
                if (uiState.isCorrectAnswer == true) {
                    Text(
                        text = "Следующее слово через ${uiState.timeLeft} сек.",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // Карточка со словом
                CommonCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AnimatedContent(
                        targetState = uiState.currentWord,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(300)) with 
                            fadeOut(animationSpec = tween(300))
                        }
                    ) { word ->
                        Text(
                            text = word,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Варианты ответов в сетке 2x2
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
                        // Разделяем варианты на две строки
                        val firstRow = uiState.translations.take(2)
                        val secondRow = uiState.translations.drop(2)

                        // Первый ряд
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            firstRow.forEach { translation ->
                                AnimatedContent(
                                    targetState = translation,
                                    transitionSpec = {
                                        fadeIn(animationSpec = tween(300)) with 
                                        fadeOut(animationSpec = tween(300))
                                    },
                                    modifier = Modifier.weight(1f)
                                ) { currentTranslation ->
                                    QuizAnswerCard(
                                        text = currentTranslation,
                                        isSelected = currentTranslation == uiState.selectedTranslation,
                                        isCorrectAnswer = uiState.isCorrectAnswer,
                                        correctTranslation = uiState.correctTranslation,
                                        onClick = { viewModel.checkAnswer(currentTranslation) },
                                        enabled = uiState.selectedTranslation == null || 
                                                 (uiState.selectedTranslation != null && !uiState.isCorrectAnswer!!),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }

                        // Второй ряд
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            secondRow.forEach { translation ->
                                AnimatedContent(
                                    targetState = translation,
                                    transitionSpec = {
                                        fadeIn(animationSpec = tween(300)) with 
                                        fadeOut(animationSpec = tween(300))
                                    },
                                    modifier = Modifier.weight(1f)
                                ) { currentTranslation ->
                                    QuizAnswerCard(
                                        text = currentTranslation,
                                        isSelected = currentTranslation == uiState.selectedTranslation,
                                        isCorrectAnswer = uiState.isCorrectAnswer,
                                        correctTranslation = uiState.correctTranslation,
                                        onClick = { viewModel.checkAnswer(currentTranslation) },
                                        enabled = uiState.selectedTranslation == null || 
                                                 (uiState.selectedTranslation != null && !uiState.isCorrectAnswer!!),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Toast с результатом
        LaunchedEffect(uiState.isCorrectAnswer) {
            if (uiState.isCorrectAnswer == true) {
                toastMessage = "Правильно!"
                showToast = true
            }
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
}

@Composable
private fun QuizAnswerCard(
    text: String,
    isSelected: Boolean,
    isCorrectAnswer: Boolean?,
    correctTranslation: String,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    if (isSelected && text == correctTranslation) {
        PulseEffect(
            modifier = modifier,
            pulseFraction = 1.03f,
            duration = 800
        ) {
            Card(
                modifier = modifier
                    .height(80.dp)
                    .animateContentSize(),
                colors = CardDefaults.cardColors(
                    containerColor = when {
                        isSelected && text == correctTranslation -> Color(0xFF4CAF50)
                        isSelected && isCorrectAnswer == false -> Color(0xFFE57373)
                        else -> MaterialTheme.colorScheme.surface
                    },
                    contentColor = if (isSelected && isCorrectAnswer != null) {
                        Color.White
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                ),
                enabled = enabled,
                onClick = onClick
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    } else {
        Card(
            modifier = modifier
                .height(80.dp)
                .animateContentSize(),
            colors = CardDefaults.cardColors(
                containerColor = when {
                    isSelected && text == correctTranslation -> Color(0xFF4CAF50)
                    isSelected && isCorrectAnswer == false -> Color(0xFFE57373)
                    else -> MaterialTheme.colorScheme.surface
                },
                contentColor = if (isSelected && isCorrectAnswer != null) {
                    Color.White
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            ),
            enabled = enabled,
            onClick = onClick
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
} 