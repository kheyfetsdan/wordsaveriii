package com.example.mysimpleapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mysimpleapp.ui.screens.*
import com.example.mysimpleapp.ui.viewmodels.AuthViewModel

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Login.route
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToMain = { navController.navigate(Screen.Main.route) }
            )
        }
        
        composable(Screen.Main.route) {
            MainScreen(
                authViewModel = authViewModel,
                onNavigateToWordDetails = { wordId -> 
                    navController.navigate("${Screen.WordDetails.route}/$wordId")
                },
                onNavigateToQuiz = { navController.navigate(Screen.Quiz.route) },
                onNavigateToRandomWord = { navController.navigate(Screen.RandomWord.route) },
                onNavigateToInput = { navController.navigate(Screen.Input.route) }
            )
        }
        
        composable(
            route = "${Screen.WordDetails.route}/{wordId}",
            arguments = listOf(navArgument("wordId") { type = NavType.IntType })
        ) { backStackEntry ->
            val wordId = backStackEntry.arguments?.getInt("wordId") ?: 0
            WordDetailsScreen(
                wordId = wordId,
                authViewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Другие экраны...
    }
}

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Main : Screen("main")
    object WordDetails : Screen("word_details")
    object Quiz : Screen("quiz")
    object RandomWord : Screen("random_word")
    object Input : Screen("input")
} 