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
import com.example.coretechv2.factory.MainActivityViewModelFactory
import com.example.coretechv2.navigation.AppNavGraph
import com.example.coretechv2.ui.component.ButtonMessage
import com.example.coretechv2.ui.component.PopupWindow
import com.example.coretechv2.ui.screen.APISettingsScreen
import com.example.coretechv2.ui.screen.LoginScreen
import com.example.coretechv2.ui.theme.CoreTechV2Theme
import com.example.coretechv2.viewmodel.MainActivityViewModel
import com.example.coretechv2.viewmodel.SharedViewModel

class MainActivity : ComponentActivity() {

    private var isLoggedIn = mutableStateOf(false)
    private var apiConnected = mutableStateOf(true)
    private var currentUser = mutableStateOf("")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Lifecycle", "onCreate")
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val navController = rememberNavController()
            val viewModel: MainActivityViewModel = viewModel(
                factory = MainActivityViewModelFactory(context)
            )
            val sharedViewModel: SharedViewModel = viewModel()
            viewModel.verifyConnection()

            apiConnected.value = viewModel.ConnectedSuccess
            sharedViewModel.currentUser = currentUser

            CoreTechV2Theme {
                Box{
                    AppNavGraph(navController = navController, sharedViewModel = sharedViewModel)
                    if (!isLoggedIn.value){
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
                    if (sharedViewModel.showPopup.value){
                        Log.d("popup","Popup called")
                        PopupWindow(sharedViewModel.popupDetails)
                    }
                    if (sharedViewModel.showMessagePopup.value){
                        Log.d("popup","Message called")
                        ButtonMessage(sharedViewModel.popupMessageDetails)
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
        Log.d("Lifecycle", "onResume")
    }
    override fun onStart() {
        super.onStart()
        Log.d("Lifecycle", "onStart")
    }

    override fun onPause() {
        super.onPause()
        Log.d("Lifecycle", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("Lifecycle", "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Lifecycle", "onDestroy changingConfig=$isChangingConfigurations")
    }
}