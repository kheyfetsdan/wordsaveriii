package com.example.mysimpleapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mysimpleapp.components.CommonButton
import com.example.mysimpleapp.components.ButtonType
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mysimpleapp.viewmodels.DictionaryViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.ui.platform.LocalContext
import android.app.Application
import com.example.mysimpleapp.components.WordCard
import com.example.mysimpleapp.viewmodels.AuthViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun DictionaryScreen(
    authViewModel: AuthViewModel,
    onNavigateToWordDetails: (Int) -> Unit
) {
    val context = LocalContext.current
    val viewModel: DictionaryViewModel = viewModel(
        factory = DictionaryViewModel.provideFactory(
            context.applicationContext as Application,
            authViewModel
        )
    )
    val uiState by viewModel.uiState.collectAsState()
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = uiState.isLoading)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Кнопки сортировки
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CommonButton(
                text = if (uiState.sortBy == "text_asc") "А-Я ↑" else "А-Я ↓",
                onClick = { 
                    val newSortOrder = if (uiState.sortBy == "text_asc") "text_desc" else "text_asc"
                    viewModel.updateSortOrder(newSortOrder)
                },
                type = ButtonType.Secondary,
                modifier = Modifier.weight(1f)
            )
            
            CommonButton(
                text = if (uiState.sortBy == "correct_asc") "✓ ↑" else "✓ ↓",
                onClick = { 
                    val newSortOrder = if (uiState.sortBy == "correct_asc") "correct_desc" else "correct_asc"
                    viewModel.updateSortOrder(newSortOrder)
                },
                type = ButtonType.Secondary,
                modifier = Modifier.weight(1f)
            )
            
            CommonButton(
                text = if (uiState.sortBy == "wrong_asc") "✗ ↑" else "✗ ↓",
                onClick = { 
                    val newSortOrder = if (uiState.sortBy == "wrong_asc") "wrong_desc" else "wrong_asc"
                    viewModel.updateSortOrder(newSortOrder)
                },
                type = ButtonType.Secondary,
                modifier = Modifier.weight(1f)
            )
        }

        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier.weight(1f)
        ) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // Список слов
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    items(uiState.words) { word ->
                        WordCard(
                            word = word.text,
                            translation = word.translation,
                            successRate = word.correctAnswers.toInt(),
                            failureRate = word.wrongAnswers.toInt(),
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onNavigateToWordDetails(word.id) }
                        )
                    }
                }
            }
        }

        // Пагинация
        if (uiState.totalPages > 1) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CommonButton(
                    text = "Назад",
                    onClick = { viewModel.updateCurrentPage(uiState.currentPage - 1) },
                    enabled = uiState.currentPage > 1,
                    type = ButtonType.Secondary,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "${uiState.currentPage} из ${uiState.totalPages}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                CommonButton(
                    text = "Вперёд",
                    onClick = { viewModel.updateCurrentPage(uiState.currentPage + 1) },
                    enabled = uiState.currentPage < uiState.totalPages,
                    type = ButtonType.Secondary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Отображение ошибки, если есть
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

@Composable
private fun TableHeader(
    title: String,
    sortKey: String,
    currentSortBy: String,
    onSort: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        IconButton(
            onClick = { 
                val newSortBy = if (currentSortBy == "${sortKey}_asc") 
                    "${sortKey}_desc" else "${sortKey}_asc"
                onSort(newSortBy)
            },
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = if (currentSortBy == "${sortKey}_desc")
                    Icons.Default.ArrowDownward
                else
                    Icons.Default.ArrowUpward,
                contentDescription = "Сортировать по $title",
                tint = if (currentSortBy.startsWith(sortKey))
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
} 