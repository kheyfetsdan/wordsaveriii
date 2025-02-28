package com.example.mysimpleapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mysimpleapp.components.CommonButton
import com.example.mysimpleapp.components.ButtonType
import com.example.mysimpleapp.components.CommonCard
import com.example.mysimpleapp.components.CommonDialog
import com.example.mysimpleapp.viewmodels.SettingsViewModel

@Composable
fun SettingsScreen(
    paddingValues: PaddingValues,
    onBackClick: () -> Unit,
    onThemeChange: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.showClearDialog) {
        CommonDialog(
            title = "Подтверждение",
            text = "Вы уверены, что хотите удалить все слова?",
            onConfirm = { viewModel.clearAllWords() },
            onDismiss = { viewModel.hideClearDialog() }
        )
    }

    if (uiState.showEasterEggDialog) {
        CommonDialog(
            title = "Секрет",
            text = "Я пасхалка",
            onConfirm = { viewModel.hideEasterEggDialog() },
            onDismiss = { viewModel.hideEasterEggDialog() },
            confirmText = "Ок",
            dismissText = ""
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        CommonCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Внешний вид",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                CommonButton(
                    text = "Сменить тему",
                    onClick = onThemeChange,
                    type = ButtonType.Secondary,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        CommonCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Данные",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                CommonButton(
                    text = "Очистить все слова",
                    onClick = { viewModel.showClearDialog() },
                    type = ButtonType.Secondary,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        CommonCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Дополнительно",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                CommonButton(
                    text = "Нажми меня",
                    onClick = { viewModel.showEasterEggDialog() },
                    type = ButtonType.Tertiary,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        CommonButton(
            text = "Назад",
            onClick = onBackClick,
            type = ButtonType.Primary,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
    }
} 