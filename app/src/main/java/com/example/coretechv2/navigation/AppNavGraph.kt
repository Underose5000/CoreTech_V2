package com.example.coretechv2.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.coretechv2.ui.screen.HomeScreen


@Composable
fun AppNavGraph(navController: NavHostController = rememberNavController(),
                currentuser: String
) {
    NavHost(navController = navController, startDestination = "home") {

        composable("home") {
            HomeScreen(
                navController = navController,
                currentuser
            )
        }
    }
}
