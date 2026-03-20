package com.example.coretechv2

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.coretechv2.factory.APISettingViewModelFactory
import com.example.coretechv2.factory.MainActivityViewModelFactory
import com.example.coretechv2.navigation.AppNavGraph
import com.example.coretechv2.ui.screen.APISettingsScreen
import com.example.coretechv2.ui.screen.LoginScreen
import com.example.coretechv2.ui.theme.CoreTechV2Theme
import com.example.coretechv2.viewmodel.APISettingViewModel
import com.example.coretechv2.viewmodel.MainActivityViewModel

class MainActivity : ComponentActivity() {

    private var isLoggedIn = mutableStateOf(false)
    private var apiConnected = mutableStateOf(true)

    private var currentUser = mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val navController = rememberNavController()
            val viewModel: MainActivityViewModel = viewModel(
                factory = MainActivityViewModelFactory(context)
            )
            viewModel.verifyConnection()

            apiConnected.value = viewModel.ConnectedSuccess

            CoreTechV2Theme {
                Box{
                    AppNavGraph(navController = navController, currentuser = currentUser.value)//currentUser.value)
                    if (!isLoggedIn.value){
                        isLoggedIn.value = true
                        LoginScreen(
                            currentUser.value,
                            onLoginSuccess = { isLoggedIn.value = true },
                            ondifferentUser = {
                                currentuser -> currentUser.value = currentuser
                                navController.navigate("home"){
                                    popUpTo(navController.graph.startDestinationId) {inclusive = true}
                                    launchSingleTop = true
                                }
                            }
                        )

                    }

                    if (!apiConnected.value) {
                        APISettingsScreen(onConnectedSuccess = {apiConnected.value = true})
                    }
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        isLoggedIn.value = false
    }

}