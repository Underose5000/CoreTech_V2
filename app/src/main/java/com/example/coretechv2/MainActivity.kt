package com.example.coretechv2

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.coretechv2.factory.AssemblyOrderDetailsViewModelFactory
import com.example.coretechv2.factory.MainActivityViewModelFactory
import com.example.coretechv2.factory.SharedViewModelFactory
import com.example.coretechv2.navigation.AppNavGraph
import com.example.coretechv2.ui.component.ButtonMessage
import com.example.coretechv2.ui.component.PopupWindow
import com.example.coretechv2.ui.screen.APISettingsScreen
import com.example.coretechv2.ui.screen.LoginScreen
import com.example.coretechv2.ui.theme.CoreTechV2Theme
import com.example.coretechv2.viewmodel.MainActivityViewModel
import com.example.coretechv2.viewmodel.SharedViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels {
        MainActivityViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val navController = rememberNavController()
            val sharedViewModel: SharedViewModel = viewModel(factory = SharedViewModelFactory(context))
            viewModel.verifyConnection()
            viewModel.onApiConnected()
            sharedViewModel.currentUser = viewModel.currentUser

            CoreTechV2Theme {
                Box{
                    AppNavGraph(navController = navController, sharedViewModel = sharedViewModel)
                    //viewModel.onIsLoggedInTrue()
                    if (!viewModel.isLoggedIn.value){
                        LoginScreen(
                            viewModel.currentUser.value,
                            onLoginSuccess = { viewModel.onIsLoggedInTrue() },
                            ondifferentUser = {
                                currentuser -> viewModel.currentUser.value = currentuser.uppercase()
                                navController.navigate("home"){
                                    popUpTo(navController.graph.startDestinationId) {inclusive = true}
                                    launchSingleTop = true
                                }
                            }
                        )

                    }
                    if (sharedViewModel.showPopup.value){
                        
                        PopupWindow(sharedViewModel.popupDetails)
                    }
                    if (sharedViewModel.showMessagePopup.value){
                        
                        ButtonMessage(sharedViewModel.popupMessageDetails)
                    }

                    if (!viewModel.apiConnected.value) {
                        APISettingsScreen(onConnectedSuccess = {viewModel.apiConnected.value = true})
                    }
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        
    }
    override fun onStart() {
        super.onStart()
        
    }

    override fun onPause() {
        super.onPause()
        if(!isChangingConfigurations){
            viewModel.onIsLoggedInFalse()
        }
        
    }

    override fun onStop() {
        super.onStop()

        
    }

    override fun onDestroy() {
        super.onDestroy()
        if(!isChangingConfigurations){
            viewModel.onIsLoggedInFalse()
        }
        
    }
}