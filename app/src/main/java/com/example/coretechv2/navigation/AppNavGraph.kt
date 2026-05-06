package com.example.coretechv2.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.coretechv2.ui.screen.AssemblyOrderDetails
import com.example.coretechv2.ui.screen.AssemblyOrdersScreen
import com.example.coretechv2.ui.screen.HomeScreen
import com.example.coretechv2.viewmodel.SharedViewModel


@Composable
fun AppNavGraph(navController: NavHostController = rememberNavController(),
                sharedViewModel: SharedViewModel
) {
    NavHost(navController = navController, startDestination = "home") {

        composable("home") {
            HomeScreen(
                navController = navController,
                sharedViewModel
            )
        }
        composable("assemblyorders") {
            AssemblyOrdersScreen(
                navController = navController,
                sharedViewModel
            )
        }
        composable("assemblyorderdetail") {
            AssemblyOrderDetails(
                navController = navController,
                sharedViewModel
            )
        }

    }
}
