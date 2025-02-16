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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.example.mysimpleapp.components.ButtonType
import com.example.mysimpleapp.components.CommonButton
import com.example.mysimpleapp.screens.InputScreen
import com.example.mysimpleapp.screens.RandomWordScreen
import com.example.mysimpleapp.screens.InfoScreen
import com.example.mysimpleapp.screens.DictionaryScreen
import com.example.mysimpleapp.ui.theme.MySimpleAppTheme
import com.example.mysimpleapp.data.TextEntity
import com.example.mysimpleapp.ui.theme.AppTheme

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
                        onThemeChange = { currentTheme = if (currentTheme == AppTheme.MINT) AppTheme.GREY else AppTheme.MINT }
                    )
                }
            }
        }
    }
}

enum class Screen(val icon: ImageVector, val label: String) {
    Input(Icons.Default.Add, "Ввод"),
    Random(Icons.Default.Shuffle, "Случайное"),
    Dictionary(Icons.Default.List, "Словарь"),
    Info(Icons.Default.Info, "Инфо")
}

@Composable
fun MainScreen(onThemeChange: () -> Unit) {
    var currentScreen by remember { mutableStateOf(Screen.Input) }
    
    var dictionaryWords by remember { mutableStateOf<List<TextEntity>>(emptyList()) }
    var isDictionaryTableVisible by remember { mutableStateOf(false) }
    var dictionaryCurrentPage by remember { mutableStateOf(0) }
    var dictionaryTotalPages by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .shadow(8.dp, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                Screen.values().forEach { screen ->
                    NavigationBarItem(
                        icon = { 
                            Icon(
                                screen.icon,
                                contentDescription = screen.label,
                                modifier = Modifier.size(26.dp)
                            ) 
                        },
                        label = { 
                            Text(
                                screen.label,
                                style = MaterialTheme.typography.labelSmall
                            ) 
                        },
                        selected = currentScreen == screen,
                        onClick = { currentScreen = screen },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
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
                Screen.Info -> InfoScreen(onThemeChange = onThemeChange)
                Screen.Dictionary -> DictionaryScreen(
                    words = dictionaryWords,
                    isTableVisible = isDictionaryTableVisible,
                    currentPage = dictionaryCurrentPage,
                    totalPages = dictionaryTotalPages,
                    onWordsChange = { dictionaryWords = it },
                    onTableVisibilityChange = { isDictionaryTableVisible = it },
                    onCurrentPageChange = { dictionaryCurrentPage = it },
                    onTotalPagesChange = { dictionaryTotalPages = it }
                )
            }
        }
    }
}