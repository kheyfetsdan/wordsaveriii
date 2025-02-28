package com.example.mysimpleapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.example.mysimpleapp.components.ButtonType
import com.example.mysimpleapp.components.CommonButton
import com.example.mysimpleapp.screens.InputScreen
import com.example.mysimpleapp.screens.RandomWordScreen
import com.example.mysimpleapp.screens.InfoScreen
import com.example.mysimpleapp.screens.DictionaryScreen
import com.example.mysimpleapp.screens.QuizScreen
import com.example.mysimpleapp.screens.AuthScreen
import com.example.mysimpleapp.ui.theme.MySimpleAppTheme
import com.example.mysimpleapp.data.TextEntity
import com.example.mysimpleapp.ui.theme.AppTheme
import com.example.mysimpleapp.viewmodels.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var currentTheme by remember { mutableStateOf(AppTheme.MINT) }
            
            MySimpleAppTheme(appTheme = currentTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        onThemeChange = { currentTheme = if (currentTheme == AppTheme.MINT) AppTheme.GREY else AppTheme.MINT },
                        authViewModel = viewModel()
                    )
                }
            }
        }
    }
}

enum class Screen(val icon: ImageVector, val label: String) {
    Auth(Icons.Default.Person, "Авторизация"),
    Input(Icons.Default.Add, "Ввод"),
    Random(Icons.Default.Shuffle, "Слова"),
    Quiz(Icons.Default.QuestionMark, "Квиз"),
    Dictionary(Icons.Default.List, "Словарь"),
    Info(Icons.Default.Info, "Инфо")
}

@Composable
fun MainScreen(
    onThemeChange: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    val authState by authViewModel.uiState.collectAsState()
    var currentScreen by remember { mutableStateOf(Screen.Auth) }

    // Следим за изменением состояния авторизации
    LaunchedEffect(authState.isAuthenticated) {
        currentScreen = if (authState.isAuthenticated) {
            Screen.Input
        } else {
            Screen.Auth
        }
    }

    if (!authState.isAuthenticated) {
        when (currentScreen) {
            Screen.Auth -> AuthScreen(
                onNavigateToInfo = { currentScreen = Screen.Info }
            )
            Screen.Info -> InfoScreen(
                onThemeChange = onThemeChange,
                onBackClick = { currentScreen = Screen.Auth },
                onLogout = { authViewModel.logout() },
                isAuthenticated = authState.isAuthenticated
            )
            else -> currentScreen = Screen.Auth
        }
    } else {
        // Основной интерфейс приложения
        var selectedItem by remember { mutableStateOf(Screen.Input.ordinal - 1) } // -1 because we skip Auth
        
        Scaffold(
            bottomBar = {
                NavigationBar {
                    Screen.values().forEach { screen ->
                        if (screen != Screen.Auth) {
                            NavigationBarItem(
                                icon = { Icon(screen.icon, contentDescription = null) },
                                label = { Text(screen.label) },
                                selected = currentScreen == screen,
                                onClick = { 
                                    currentScreen = screen
                                    selectedItem = screen.ordinal - 1
                                }
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (currentScreen) {
                    Screen.Input -> InputScreen()
                    Screen.Random -> RandomWordScreen()
                    Screen.Quiz -> QuizScreen()
                    Screen.Dictionary -> DictionaryScreen()
                    Screen.Info -> InfoScreen(
                        onThemeChange = onThemeChange,
                        onBackClick = { currentScreen = Screen.Auth },
                        onLogout = { authViewModel.logout() },
                        isAuthenticated = authState.isAuthenticated
                    )
                    else -> {}
                }
            }
        }
    }
}