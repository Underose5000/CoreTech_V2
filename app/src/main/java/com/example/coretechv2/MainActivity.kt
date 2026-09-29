package com.example.coretechv2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
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

/**
 * Main activity for the CoreTech V2 application.
 *
 * This activity is responsible for initialising the application's main
 * Compose UI and coordinating the primary application state.
 *
 * It manages:
 * - The [MainActivityViewModel] used for login and API connection state.
 * - The shared [SharedViewModel] used across application screens.
 * - The application's [AppNavGraph] and navigation controller.
 * - Displaying the login screen when the user is not logged in.
 * - Displaying API settings when the API connection is unavailable.
 * - Displaying application-wide popup and message dialogs.
 * - Managing login state across Android activity lifecycle events.
 *
 * The activity also verifies the API connection when the Compose content
 * is initialised and shares the current user with the [SharedViewModel].
 */
class MainActivity : ComponentActivity() {

    /**
     * ViewModel responsible for managing application-level state such as
     * API connectivity, login status, and the current user.
     *
     * The ViewModel is created using [MainActivityViewModelFactory].
     */
    private val viewModel: MainActivityViewModel by viewModels {
        MainActivityViewModelFactory(this)
    }

    /**
     * Initialises the activity and sets up the application's Compose UI.
     *
     * This method:
     * - Enables edge-to-edge display.
     * - Creates the navigation controller.
     * - Obtains the shared application ViewModel.
     * - Verifies the API connection.
     * - Synchronises the current user with the shared ViewModel.
     * - Displays the application's navigation graph and global UI elements.
     *
     * The login screen is displayed when the user is not logged in.
     * Application-wide popup windows and API settings are also displayed
     * based on their corresponding ViewModel state.
     *
     * @param savedInstanceState Previously saved activity state, or `null`
     * if the activity is being created for the first time.
     */
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
                Box {
                    AppNavGraph(navController = navController, sharedViewModel = sharedViewModel)
                    viewModel.onIsLoggedInTrue()
                    if (!viewModel.isLoggedIn.value) {
                        LoginScreen(
                            viewModel.currentUser.value,
                            onLoginSuccess = { viewModel.onIsLoggedInTrue() },
                            ondifferentUser = { currentuser ->
                                viewModel.currentUser.value = currentuser.uppercase()
                                navController.navigate("home") {
                                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        )

                    }
                    if (sharedViewModel.showPopup.value) {

                        PopupWindow(sharedViewModel.popupDetails)
                    }
                    if (sharedViewModel.showMessagePopup.value) {

                        ButtonMessage(sharedViewModel.popupMessageDetails)
                    }

                    if (!viewModel.apiConnected.value) {
                        APISettingsScreen(onConnectedSuccess = { viewModel.apiConnected.value = true })
                    }
                }
            }
        }
    }

    /**
     * Called when the activity becomes visible and starts interacting
     * with the user.
     *
     * No additional processing is currently performed when the activity
     * resumes.
     */
    override fun onResume() {
        super.onResume()
    }

    /**
     * Called when the activity is becoming visible to the user.
     *
     * No additional processing is currently performed when the activity
     * starts.
     */
    override fun onStart() {
        super.onStart()
    }

    /**
     * Called when the activity is no longer in the foreground.
     *
     * When the activity is not being recreated due to a configuration
     * change, the current login state is reset by calling
     * [MainActivityViewModel.onIsLoggedInFalse].
     */
    override fun onPause() {
        super.onPause()
        if (!isChangingConfigurations) {
            viewModel.onIsLoggedInFalse()
        }

    }

    /**
     * Called when the activity is no longer visible to the user.
     *
     * No additional processing is currently performed when the activity
     * stops.
     */
    override fun onStop() {
        super.onStop()
    }

    /**
     * Called when the activity is being permanently destroyed.
     *
     * When the activity is not being destroyed as part of a configuration
     * change, the current login state is reset by calling
     * [MainActivityViewModel.onIsLoggedInFalse].
     */
    override fun onDestroy() {
        super.onDestroy()
        if (!isChangingConfigurations) {
            viewModel.onIsLoggedInFalse()
        }
    }
}