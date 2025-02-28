package com.example.mysimpleapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mysimpleapp.components.CommonButton
import com.example.mysimpleapp.components.ButtonType
import com.example.mysimpleapp.viewmodels.InfoViewModel

@Composable
fun InfoScreen(
    onThemeChange: () -> Unit,
    onBackClick: (() -> Unit)? = null,
    onLogout: (() -> Unit)? = null,
    viewModel: InfoViewModel = viewModel(),
    isAuthenticated: Boolean = false
) {
    val uiState by viewModel.uiState.collectAsState()
    
    if (uiState.showSettings) {
        SettingsScreen(
            onBackClick = { viewModel.hideSettings() },
            onThemeChange = onThemeChange
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Количество слов в базе:",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = uiState.wordsCount.toString(),
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Text(
                text = uiState.appVersion,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = uiState.appAuthor,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Column(
                modifier = Modifier.fillMaxWidth(0.8f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CommonButton(
                    text = "Настройки",
                    onClick = { viewModel.showSettings() },
                    type = ButtonType.Secondary,
                    modifier = Modifier.fillMaxWidth()
                )

                if (!isAuthenticated) {
                    CommonButton(
                        text = "Назад",
                        onClick = { onBackClick?.invoke() },
                        type = ButtonType.Secondary,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (isAuthenticated) {
                    CommonButton(
                        text = "Выйти",
                        onClick = { onLogout?.invoke() },
                        type = ButtonType.Primary,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
} 