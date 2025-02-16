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
import com.example.mysimpleapp.components.CommonDialog
import com.example.mysimpleapp.viewmodels.SettingsViewModel

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onThemeChange: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Настройки",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        CommonButton(
            text = "Сменить тему",
            onClick = onThemeChange,
            type = ButtonType.Secondary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        CommonButton(
            text = "Очистить все слова",
            onClick = { viewModel.showClearDialog() },
            type = ButtonType.Secondary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        CommonButton(
            text = "Нажми меня",
            onClick = { viewModel.showEasterEggDialog() },
            type = ButtonType.Tertiary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        CommonButton(
            text = "Назад",
            onClick = onBackClick,
            type = ButtonType.Primary,
            modifier = Modifier.fillMaxWidth()
        )

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
    }
} 