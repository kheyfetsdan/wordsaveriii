package com.example.mysimpleapp.screens

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mysimpleapp.components.*
import com.example.mysimpleapp.viewmodels.AuthViewModel
import com.example.mysimpleapp.viewmodels.WordDetailsViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.filled.Delete

@Composable
fun WordDetailsScreen(
    wordId: Int,
    authViewModel: AuthViewModel,
    onNavigateBack: () -> Unit
) {
    BackHandler {
        onNavigateBack()
    }

    val context = LocalContext.current
    val viewModel: WordDetailsViewModel = viewModel(
        key = "word_details_$wordId",
        factory = WordDetailsViewModel.provideFactory(
            context.applicationContext as Application,
            authViewModel,
            wordId
        )
    )
    val uiState by viewModel.uiState.collectAsState()
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = uiState.isLoading)

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = { viewModel.loadWord() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Кнопка назад
            CommonButton(
                text = "Назад",
                onClick = onNavigateBack,
                type = ButtonType.Secondary
            )

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                uiState.word?.let { word ->
                    // Карточка со словом
                    CommonCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (uiState.isEditing) {
                                // Режим редактирования
                                CommonTextField(
                                    value = uiState.editedText,
                                    onValueChange = { viewModel.updateEditedText(it) },
                                    label = "Слово",
                                    modifier = Modifier.fillMaxWidth()
                                )
                                CommonTextField(
                                    value = uiState.editedTranslation,
                                    onValueChange = { viewModel.updateEditedTranslation(it) },
                                    label = "Перевод",
                                    modifier = Modifier.fillMaxWidth()
                                )
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    CommonButton(
                                        text = "Отмена",
                                        onClick = { viewModel.cancelEditing() },
                                        type = ButtonType.Secondary,
                                        modifier = Modifier.weight(1f)
                                    )
                                    CommonButton(
                                        text = "Сохранить",
                                        onClick = { viewModel.saveEditedWord() },
                                        enabled = !uiState.isSaving,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            } else {
                                // Режим просмотра
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row {
                                        Column {
                                            Text(
                                                text = word.text,
                                                style = MaterialTheme.typography.headlineMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = word.translation,
                                                style = MaterialTheme.typography.titleLarge,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    Row {
                                        IconButton(onClick = { viewModel.startEditing() }) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Редактировать",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        IconButton(onClick = { viewModel.showDeleteConfirmation() }) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Удалить",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                // Статистика
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    StatisticItem(
                                        title = "Правильные ответы",
                                        value = "${word.correctAnswers}",
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    StatisticItem(
                                        title = "Неправильные ответы",
                                        value = "${word.wrongAnswers}",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }

                if (uiState.error != null) {
                    Text(
                        text = uiState.error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }

    // В конце композабл функции WordDetailsScreen добавляем диалог подтверждения
    if (uiState.showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { viewModel.hideDeleteConfirmation() },
            title = { Text("Подтверждение") },
            text = { Text("Вы действительно хотите удалить это слово?") },
            confirmButton = {
                TextButton(
                    onClick = { 
                        viewModel.deleteWord(onSuccess = onNavigateBack)
                    },
                    enabled = !uiState.isDeleting
                ) {
                    Text(if (uiState.isDeleting) "Удаление..." else "Удалить")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.hideDeleteConfirmation() },
                    enabled = !uiState.isDeleting
                ) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
private fun StatisticItem(
    title: String,
    value: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = color,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
} 