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
 * Defines the main navigation graph for the application.
 *
 * This navigation graph contains the top-level destinations used throughout
 * the application and manages navigation between them using Jetpack Compose
 * Navigation.
 *
 * The available destinations are:
 * - `"home"` -> [HomeScreen]
 * - `"assemblyorders"` -> [AssemblyOrdersScreen]
 * - `"assemblyorderdetail"` -> [AssemblyOrderDetails]
 * - `"printboxlabels"` -> [PrintBoxLabelsScreen]
 *
 * A shared instance of [SharedViewModel] is passed to each screen to maintain
 * shared application state across navigation destinations.
 *
 * @param navController The navigation controller used to manage navigation
 * between destinations. Defaults to a remembered navigation controller.
 * @param sharedViewModel The shared ViewModel containing application state
 * that is accessed by the navigation destinations.
 */

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
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
