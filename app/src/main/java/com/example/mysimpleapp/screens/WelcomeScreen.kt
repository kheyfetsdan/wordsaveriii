package com.example.mysimpleapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mysimpleapp.R
import com.example.mysimpleapp.components.CommonButton
import com.example.mysimpleapp.components.ButtonType
import com.example.mysimpleapp.components.CommonCard

@Composable
fun WelcomeScreen(
    onNavigateToAuth: () -> Unit,
    onNavigateToInfo: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_1024_1024),
            contentDescription = "Логотип",
            modifier = Modifier
                .size(120.dp)
                .padding(bottom = 24.dp)
        )

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
                    text = "Добро пожаловать",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                CommonButton(
                    text = "У меня есть аккаунт",
                    onClick = onNavigateToAuth,
                    type = ButtonType.Primary,
                    modifier = Modifier.fillMaxWidth()
                )

                CommonButton(
                    text = "Я новый пользователь",
                    onClick = onNavigateToRegister,
                    type = ButtonType.Secondary,
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