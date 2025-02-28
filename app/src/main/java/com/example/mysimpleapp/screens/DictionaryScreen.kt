package com.example.mysimpleapp.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mysimpleapp.components.CommonButton
import com.example.mysimpleapp.components.ButtonType
import com.example.mysimpleapp.components.CommonCard
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mysimpleapp.viewmodels.DictionaryViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import com.example.mysimpleapp.components.CommonTextField

@Composable
fun DictionaryScreen(
    viewModel: DictionaryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CommonTextField(
            value = uiState.searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            label = "Поиск",
            modifier = Modifier.fillMaxWidth()
        )

        CommonButton(
            text = if (uiState.isTableVisible) "Скрыть таблицу" else "Показать таблицу",
            onClick = { viewModel.toggleTableVisibility() },
            type = ButtonType.Secondary,
            modifier = Modifier.fillMaxWidth()
        )

        AnimatedVisibility(
            visible = uiState.isTableVisible,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            CommonCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    // Заголовок таблицы
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TableHeader(
                            title = "Слово",
                            sortKey = "text",
                            currentSortBy = uiState.sortBy,
                            onSort = { viewModel.updateSortOrder(it) },
                            modifier = Modifier.weight(1f)
                        )
                        TableHeader(
                            title = "Перевод",
                            sortKey = "translation",
                            currentSortBy = uiState.sortBy,
                            onSort = { viewModel.updateSortOrder(it) },
                            modifier = Modifier.weight(1f)
                        )
                        TableHeader(
                            title = "✓",
                            sortKey = "correctAnswers",
                            currentSortBy = uiState.sortBy,
                            onSort = { viewModel.updateSortOrder(it) },
                            modifier = Modifier.weight(0.5f)
                        )
                        TableHeader(
                            title = "✗",
                            sortKey = "wrongAnswers",
                            currentSortBy = uiState.sortBy,
                            onSort = { viewModel.updateSortOrder(it) },
                            modifier = Modifier.weight(0.5f)
                        )
                    }

                    Divider()

                    // Содержимое таблицы
                    LazyColumn {
                        items(uiState.words) { word ->
                            Column {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = word.text,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = word.translation,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "%.1f".format(word.correctAnswers),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.weight(0.5f)
                                    )
                                    Text(
                                        text = "%.1f".format(word.wrongAnswers),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.weight(0.5f)
                                    )
                                }
                                Divider()
                            }
                        }
                    }
                }
            }
        }

        if (uiState.totalPages > 1) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CommonButton(
                    text = "Предыдущая",
                    onClick = { viewModel.updateCurrentPage(uiState.currentPage - 1) },
                    enabled = uiState.currentPage > 0,
                    type = ButtonType.Secondary
                )

                Text(
                    text = "${uiState.currentPage + 1} из ${uiState.totalPages}",
                    style = MaterialTheme.typography.bodyLarge
                )

                CommonButton(
                    text = "Следующая",
                    onClick = { viewModel.updateCurrentPage(uiState.currentPage + 1) },
                    enabled = uiState.currentPage < uiState.totalPages - 1,
                    type = ButtonType.Secondary
                )
            }
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