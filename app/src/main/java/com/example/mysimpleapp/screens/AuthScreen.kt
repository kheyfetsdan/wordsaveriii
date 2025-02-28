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
import com.example.mysimpleapp.components.CommonTextField
import com.example.mysimpleapp.viewmodels.AuthViewModel

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = viewModel(),
    onNavigateToInfo: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

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
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Авторизация",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                CommonTextField(
                    value = uiState.email,
                    onValueChange = { viewModel.updateEmail(it) },
                    label = "Email",
                    modifier = Modifier.fillMaxWidth()
                )

                CommonTextField(
                    value = uiState.password,
                    onValueChange = { viewModel.updatePassword(it) },
                    label = "Пароль",
                    isPassword = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (uiState.error != null) {
                    Text(
                        text = uiState.error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                CommonButton(
                    text = "Войти",
                    onClick = { viewModel.login() },
                    type = ButtonType.Primary,
                    modifier = Modifier.fillMaxWidth()
                )

                CommonButton(
                    text = "О приложении",
                    onClick = onNavigateToInfo,
                    type = ButtonType.Secondary,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
} 