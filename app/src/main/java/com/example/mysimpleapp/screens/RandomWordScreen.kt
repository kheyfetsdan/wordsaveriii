package com.example.mysimpleapp.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mysimpleapp.components.*
import com.example.mysimpleapp.viewmodels.RandomWordViewModel

@Composable
fun RandomWordScreen(
    viewModel: RandomWordViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val randomText = uiState.randomText
    
    LaunchedEffect(uiState.isAnswerChecked) {
        if (uiState.isAnswerChecked) {
            val isCorrect = uiState.userTranslation.trim().equals(
                randomText?.translation?.trim(),
                ignoreCase = true
            )
            val message = if (isCorrect) "Правильно!" else "Неправильно. Правильный ответ: ${randomText?.translation}"
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    if (uiState.showConfirmDialog) {
        CommonDialog(
            title = "Подтверждение",
            text = "Вы уверены, что хотите показать перевод?",
            onConfirm = { viewModel.showTranslation() },
            onDismiss = { viewModel.hideConfirmDialog() }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CommonCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (randomText != null) {
                    Text(
                        text = randomText.text,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    CommonTextField(
                        value = uiState.userTranslation,
                        onValueChange = { viewModel.updateUserTranslation(it) },
                        label = "Введите перевод",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )

                    if (uiState.isAnswerChecked) {
                        ResultText(
                            isCorrect = uiState.userTranslation.trim().equals(
                                randomText.translation.trim(),
                                ignoreCase = true
                            ),
                            text = if (uiState.userTranslation.trim().equals(
                                randomText.translation.trim(),
                                ignoreCase = true
                            )) "Правильно!" else "Неправильно"
                        )
                    }

                    AnimatedVisibility(
                        visible = uiState.showTranslation,
                        enter = fadeIn(tween(300)) + expandVertically(
                            expandFrom = Alignment.Top,
                            animationSpec = tween(300)
                        ),
                        exit = fadeOut(tween(300)) + shrinkVertically(
                            shrinkTowards = Alignment.Top,
                            animationSpec = tween(300)
                        )
                    ) {
                        Text(
                            text = randomText.translation,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                } else {
                    Text(
                        text = "Нажмите кнопку,\nчтобы получить случайное слово",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Left,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 24.sp
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CommonButton(
                text = "Проверить\nответ",
                onClick = { viewModel.checkAnswer() },
                modifier = Modifier.weight(1f),
                enabled = randomText != null && 
                         uiState.userTranslation.isNotBlank() && 
                         !uiState.isAnswerChecked,
                type = ButtonType.Primary
            )

            CommonButton(
                text = "Случайное\nслово",
                onClick = { viewModel.loadRandomWord() },
                modifier = Modifier.weight(1f),
                type = ButtonType.Secondary
            )
        }

        if (!uiState.showTranslation && randomText != null) {
            CommonButton(
                text = "Показать перевод",
                onClick = { viewModel.showConfirmDialog() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                type = ButtonType.Tertiary
            )
        }
    }
}