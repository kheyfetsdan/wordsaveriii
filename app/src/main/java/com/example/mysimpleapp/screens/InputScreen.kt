package com.example.mysimpleapp.screens

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.key.*
import com.example.mysimpleapp.data.AppDatabase
import com.example.mysimpleapp.data.TextEntity
import kotlinx.coroutines.launch
import com.example.mysimpleapp.components.CommonButton
import com.example.mysimpleapp.components.ButtonType
import com.example.mysimpleapp.components.CommonTextField
import com.example.mysimpleapp.components.CommonCard
import com.example.mysimpleapp.viewmodels.InputViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.style.TextAlign
import com.example.mysimpleapp.viewmodels.AuthViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun InputScreen(
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    val viewModel: InputViewModel = viewModel(
        factory = InputViewModel.provideFactory(
            context.applicationContext as Application,
            authViewModel
        )
    )
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    
    LaunchedEffect(uiState.showSuccessMessage) {
        if (uiState.showSuccessMessage) {
            Toast.makeText(context, "Слово и перевод сохранены", Toast.LENGTH_SHORT).show()
            focusManager.clearFocus()
            viewModel.hideMessages()
            viewModel.clearFields()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CommonCard {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                CommonTextField(
                    value = uiState.text,
                    onValueChange = { viewModel.updateText(it) },
                    label = "Введите слово",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                CommonTextField(
                    value = uiState.translation,
                    onValueChange = { viewModel.updateTranslation(it) },
                    label = "Введите перевод",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .onKeyEvent { event ->
                            if (event.key == Key.Enter && event.type == KeyEventType.KeyUp) {
                                viewModel.saveWord()
                                true
                            } else {
                                false
                            }
                        }
                )

                if (uiState.showErrorMessage) {
                    Text(
                        text = uiState.error ?: "Заполните оба поля",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(24.dp)
                            .padding(top = 8.dp)
                    )
                }
            }
        }
        
        CommonButton(
            text = "Сохранить",
            onClick = { viewModel.saveWord() },
            modifier = Modifier.fillMaxWidth(0.8f),
            type = ButtonType.Primary
        )
    }
} 