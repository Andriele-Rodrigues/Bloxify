package com.example.brickbreaker.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.brickbreaker.screens.GameScreen
import com.example.brickbreaker.screens.HomeScreen
import com.example.brickbreaker.screens.MembersScreen
import com.example.brickbreaker.screens.SettingsScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home",
    ) {
        composable("home") {
            HomeScreen(
                onStartGame = { navController.navigate("game") },
                onMembers = { navController.navigate("members") }
            ) { navController.navigate("settings") }
        }
        composable("game") {
            GameScreen(onBack = { navController.popBackStack() })
        }
        composable("members") {
            MembersScreen(onBack = { navController.popBackStack() })
        }
        composable("settings") {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}
