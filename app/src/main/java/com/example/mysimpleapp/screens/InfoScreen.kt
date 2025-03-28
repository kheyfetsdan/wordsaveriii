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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import com.example.mysimpleapp.components.CommonCard
import com.example.mysimpleapp.components.CommonDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoScreen(
    onThemeChange: () -> Unit,
    onBackClick: () -> Unit,
    onLogout: () -> Unit,
    isAuthenticated: Boolean,
    viewModel: InfoViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }
    
    if (showLogoutDialog) {
        CommonDialog(
            title = "Подтверждение",
            text = "Вы действительно хотите выйти?",
            onConfirm = {
                showLogoutDialog = false
                onLogout()
            },
            onDismiss = { showLogoutDialog = false }
        )
    }
    
    Scaffold(
        topBar = {
            if (!isAuthenticated || uiState.showSettings) {
                TopAppBar(
                    title = { Text(if (uiState.showSettings) "Настройки" else "О приложении") },
                    navigationIcon = {
                        IconButton(
                            onClick = if (uiState.showSettings) {
                                { viewModel.hideSettings() }
                            } else onBackClick
                        ) {
                            Icon(Icons.Default.ArrowBack, "Назад")
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        if (uiState.showSettings) {
            SettingsScreen(
                paddingValues = paddingValues,
                onBackClick = { viewModel.hideSettings() },
                onThemeChange = onThemeChange
            )
        } else {
            InfoContent(
                paddingValues = paddingValues,
                showSettings = { viewModel.showSettings() },
                onLogoutClick = { showLogoutDialog = true },
                isAuthenticated = isAuthenticated,
                wordsCount = uiState.wordsCount,
                appVersion = uiState.appVersion,
                appAuthor = uiState.appAuthor
            )
        }
    }
}

@Composable
private fun InfoContent(
    paddingValues: PaddingValues,
    showSettings: () -> Unit,
    onLogoutClick: () -> Unit,
    isAuthenticated: Boolean,
    wordsCount: Int,
    appVersion: String,
    appAuthor: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        
        CommonCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = "Количество слов в базе",
                    style = MaterialTheme.typography.headlineSmall
                )
                
                Text(
                    text = wordsCount.toString(),
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        CommonCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = appVersion,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = appAuthor,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CommonButton(
                text = "Настройки",
                onClick = showSettings,
                type = ButtonType.Secondary,
                modifier = Modifier.fillMaxWidth()
            )

            if (isAuthenticated) {
                CommonButton(
                    text = "Выйти",
                    onClick = onLogoutClick,
                    type = ButtonType.Secondary,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
} 