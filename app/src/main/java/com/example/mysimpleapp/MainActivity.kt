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
import com.example.mysimpleapp.screens.WelcomeScreen
import com.example.mysimpleapp.screens.RegisterScreen
import com.example.mysimpleapp.ui.theme.MySimpleAppTheme
import com.example.mysimpleapp.data.TextEntity
import com.example.mysimpleapp.ui.theme.AppTheme
import com.example.mysimpleapp.viewmodels.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.BackHandler

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var currentTheme by remember { mutableStateOf(AppTheme.MINT) }
            
            // Добавляем обработчик системной кнопки "Назад"
            val currentScreen = remember { mutableStateOf(Screen.Welcome) }
            
            BackHandler {
                when (currentScreen.value) {
                    Screen.Auth -> currentScreen.value = Screen.Welcome
                    Screen.Register -> currentScreen.value = Screen.Welcome
                    Screen.Info -> currentScreen.value = Screen.Welcome
                    else -> finish() // Закрываем приложение только если находимся на Welcome screen
                }
            }
            
            MySimpleAppTheme(appTheme = currentTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        onThemeChange = { currentTheme = if (currentTheme == AppTheme.MINT) AppTheme.GREY else AppTheme.MINT },
                        authViewModel = viewModel(),
                        currentScreen = currentScreen,
                        onFinish = { finish() }
                    )
                }
            }
        }
    }
}

enum class Screen(val icon: ImageVector, val label: String) {
    // Экраны неавторизованной зоны (без иконок, так как не используются в навигации)
    Welcome(Icons.Default.Person, ""),
    Auth(Icons.Default.Person, ""),
    Register(Icons.Default.Person, ""),
    // Экраны авторизованной зоны
    Input(Icons.Default.Add, "Ввод"),
    Random(Icons.Default.Shuffle, "Слова"),
    Quiz(Icons.Default.QuestionMark, "Квиз"),
    Dictionary(Icons.Default.List, "Словарь"),
    Info(Icons.Default.Info, "Инфо")
}

@Composable
fun MainScreen(
    onThemeChange: () -> Unit,
    authViewModel: AuthViewModel = viewModel(),
    currentScreen: MutableState<Screen>,
    onFinish: () -> Unit
) {
    val authState by authViewModel.uiState.collectAsState()
    var previousScreen by remember { mutableStateOf<Screen>(Screen.Input) }
    
    // Следим за изменением состояния авторизации
    LaunchedEffect(authState.isAuthenticated) {
        currentScreen.value = if (authState.isAuthenticated) {
            Screen.Input
        } else {
            Screen.Welcome
        }
    }

    // Обработчик системной кнопки "Назад"
    BackHandler {
        if (authState.isAuthenticated) {
            // В авторизованной зоне возвращаемся на предыдущий экран
            currentScreen.value = previousScreen
        } else {
            // В неавторизованной зоне - прежняя логика
            when (currentScreen.value) {
                Screen.Auth -> currentScreen.value = Screen.Welcome
                Screen.Register -> currentScreen.value = Screen.Welcome
                Screen.Info -> currentScreen.value = Screen.Welcome
                else -> onFinish() // Используем переданную функцию
            }
        }
    }

    if (!authState.isAuthenticated) {
        when (currentScreen.value) {
            Screen.Welcome -> WelcomeScreen(
                onNavigateToAuth = { currentScreen.value = Screen.Auth },
                onNavigateToInfo = { currentScreen.value = Screen.Info },
                onNavigateToRegister = { currentScreen.value = Screen.Register }
            )
            Screen.Auth -> AuthScreen(
                onNavigateToInfo = { currentScreen.value = Screen.Info },
                onNavigateBack = { currentScreen.value = Screen.Welcome }
            )
            Screen.Register -> RegisterScreen(
                onNavigateBack = { currentScreen.value = Screen.Welcome },
                authViewModel = authViewModel,
                onRegistrationSuccess = { 
                    currentScreen.value = Screen.Input 
                },
                onNavigateToLogin = {
                    currentScreen.value = Screen.Auth
                }
            )
            Screen.Info -> InfoScreen(
                onThemeChange = onThemeChange,
                onBackClick = { currentScreen.value = Screen.Welcome },
                onLogout = { authViewModel.logout() },
                isAuthenticated = authState.isAuthenticated
            )
            else -> currentScreen.value = Screen.Welcome
        }
    } else {
        // Основной интерфейс приложения
        var selectedItem by remember { mutableStateOf(Screen.Input.ordinal - 3) }
        
        Scaffold(
            bottomBar = {
                NavigationBar {
                    Screen.values().forEach { screen ->
                        if (screen != Screen.Auth && screen != Screen.Welcome && screen != Screen.Register) {
                            NavigationBarItem(
                                icon = { Icon(screen.icon, contentDescription = null) },
                                label = { Text(screen.label) },
                                selected = currentScreen.value == screen,
                                onClick = { 
                                    // Сохраняем предыдущий экран перед переключением
                                    if (currentScreen.value != screen) {
                                        previousScreen = currentScreen.value
                                        currentScreen.value = screen
                                        selectedItem = screen.ordinal - 3
                                    }
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
                when (currentScreen.value) {
                    Screen.Input -> InputScreen(
                        authViewModel = authViewModel
                    )
                    Screen.Random -> RandomWordScreen()
                    Screen.Quiz -> QuizScreen()
                    Screen.Dictionary -> DictionaryScreen()
                    Screen.Info -> InfoScreen(
                        onThemeChange = onThemeChange,
                        onBackClick = { currentScreen.value = Screen.Auth },
                        onLogout = { authViewModel.logout() },
                        isAuthenticated = authState.isAuthenticated
                    )
                    else -> {}
                }
            }
        }
    }
}