package com.example.mysimpleapp.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.mysimpleapp.components.CommonButton
import com.example.mysimpleapp.components.ButtonType
import com.example.mysimpleapp.components.CommonCard
import com.example.mysimpleapp.components.CommonTextField
import com.example.mysimpleapp.data.AppDatabase
import com.example.mysimpleapp.data.TextEntity
import kotlinx.coroutines.launch
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward

@Composable
fun DictionaryScreen(
    words: List<TextEntity>,
    isTableVisible: Boolean,
    currentPage: Int,
    totalPages: Int,
    onWordsChange: (List<TextEntity>) -> Unit,
    onTableVisibilityChange: (Boolean) -> Unit,
    onCurrentPageChange: (Int) -> Unit,
    onTotalPagesChange: (Int) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val database = remember { AppDatabase.getDatabase(context) }
    val pageSize = 5
    var searchQuery by remember { mutableStateOf("") }
    var sortBy by remember { mutableStateOf("text_asc") }
    var sortDirection by remember { mutableStateOf(true) } // true = asc, false = desc

    LaunchedEffect(currentPage, searchQuery, sortBy) {
        scope.launch {
            val total = if (searchQuery.isBlank()) {
                database.textDao().getWordsCount()
            } else {
                database.textDao().getSearchWordsCount(searchQuery)
            }
            onTotalPagesChange((total + pageSize - 1) / pageSize)
            
            val pagedWords = if (searchQuery.isBlank()) {
                database.textDao().getPagedWordsSorted(sortBy, pageSize, currentPage * pageSize)
            } else {
                database.textDao().searchWordsSorted(searchQuery, sortBy, pageSize, currentPage * pageSize)
            }
            onWordsChange(pagedWords)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CommonTextField(
            value = searchQuery,
            onValueChange = { 
                searchQuery = it
                onCurrentPageChange(0) // Сбрасываем страницу при поиске
            },
            label = "Поиск",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        CommonButton(
            text = if (isTableVisible) "Скрыть таблицу" else "Показать таблицу",
            onClick = { onTableVisibilityChange(!isTableVisible) },
            type = ButtonType.Primary,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedVisibility(
            visible = isTableVisible,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column {
                CommonCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 320.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "Слово",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    IconButton(
                                        onClick = { 
                                            sortDirection = if (sortBy.startsWith("text")) !sortDirection else true
                                            sortBy = "text" + (if (sortDirection) "_asc" else "_desc")
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (sortBy.startsWith("text") && !sortDirection)
                                                Icons.Default.ArrowDownward
                                            else
                                                Icons.Default.ArrowUpward,
                                            contentDescription = "Сортировать по слову",
                                            tint = if (sortBy.startsWith("text"))
                                                MaterialTheme.colorScheme.primary
                                            else
                                                MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Text(
                                    text = "Перевод",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                                Row(
                                    modifier = Modifier.weight(0.6f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "✓",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF4CAF50),
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "✗",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE57373),
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = { 
                                            sortDirection = if (sortBy.startsWith("wrongAnswers")) !sortDirection else true
                                            sortBy = "wrongAnswers" + (if (sortDirection) "_asc" else "_desc")
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (sortBy.startsWith("wrongAnswers") && !sortDirection)
                                                Icons.Default.ArrowDownward
                                            else
                                                Icons.Default.ArrowUpward,
                                            contentDescription = "Сортировать по ошибкам",
                                            tint = if (sortBy.startsWith("wrongAnswers"))
                                                MaterialTheme.colorScheme.primary
                                            else
                                                MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                            Divider()
                        }

                        items(words.size) { index ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = words[index].text,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = words[index].translation,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = words[index].correctAnswers.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(0.3f),
                                    color = Color(0xFF4CAF50)
                                )
                                Text(
                                    text = words[index].wrongAnswers.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(0.3f),
                                    color = Color(0xFFE57373)
                                )
                            }
                            if (index < words.size - 1) {
                                Divider()
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CommonButton(
                        text = "Предыдущая",
                        onClick = { if (currentPage > 0) onCurrentPageChange(currentPage - 1) },
                        enabled = currentPage > 0,
                        type = ButtonType.Secondary,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = "${currentPage + 1} / $totalPages",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )

                    CommonButton(
                        text = "Следующая",
                        onClick = { if (currentPage < totalPages - 1) onCurrentPageChange(currentPage + 1) },
                        enabled = currentPage < totalPages - 1,
                        type = ButtonType.Secondary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
} 