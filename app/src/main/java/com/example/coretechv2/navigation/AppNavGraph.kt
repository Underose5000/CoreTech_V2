package com.example.coretechv2.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.coretechv2.ui.screen.AssemblyOrderDetails
import com.example.coretechv2.ui.screen.AssemblyOrdersScreen
import com.example.coretechv2.ui.screen.HomeScreen
import com.example.coretechv2.ui.screen.PrintBoxLabelsScreen
import com.example.coretechv2.viewmodel.SharedViewModel


/**
 * Main navigation graph for the application.
 *
 * Defines all top-level navigation destinations and routes
 * used throughout the app using Jetpack Compose Navigation.
 *
 * Current destinations:
 * - "home" -> [HomeScreen]
 * - "assemblyorders" -> [AssemblyOrdersScreen]
 * - "assemblyorderdetail" -> [AssemblyOrderDetails]
 *
 * A shared instance of [SharedViewModel] is passed to all screens
 * to maintain shared application state across navigation destinations.
 *
 * @param navController Navigation controller used to manage app navigation.
 * Defaults to a remembered navigation controller instance.
 * @param sharedViewModel Shared ViewModel containing global application state.
 */

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
        composable("printboxlabels") {
            PrintBoxLabelsScreen(
                navController = navController,
                sharedViewModel
            )
        }

    }
}
