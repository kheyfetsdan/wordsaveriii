package com.example.mysimpleapp.screens

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mysimpleapp.components.CommonButton
import com.example.mysimpleapp.components.ButtonType
import com.example.mysimpleapp.components.CommonCard
import com.example.mysimpleapp.components.CommonTextField
import com.example.mysimpleapp.viewmodels.AuthViewModel
import com.example.mysimpleapp.viewmodels.RegisterViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    val viewModel: RegisterViewModel = viewModel(
        factory = RegisterViewModel.provideFactory(
            context.applicationContext as Application,
            authViewModel
        )
    )
    
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Регистрация") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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

                    CommonTextField(
                        value = uiState.confirmPassword,
                        onValueChange = { viewModel.updateConfirmPassword(it) },
                        label = "Подтвердите пароль",
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
                        text = "Зарегистрироваться",
                        onClick = { viewModel.register() },
                        type = ButtonType.Primary,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
} 