package com.example.coretechv2.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coretechv2.dataclasses.APICallTables
import com.example.coretechv2.repository.APICall
import com.example.coretechv2.repository.DataStoreManager
import io.ktor.client.plugins.ClientRequestException
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing the application's connection and login state.
 *
 * [MainActivityViewModel] verifies the connection to the API, tracks whether
 * the application is connected to the API, and maintains the user's login
 * state for the main activity.
 *
 * @param dataStoreManager Provides access to application settings and API
 * configuration required by [APICall].
 */
class MainActivityViewModel(private val dataStoreManager: DataStoreManager) : ViewModel() {

    private val apiCall = APICall(dataStoreManager)
    var ConnectedSuccess by mutableStateOf(true)
        private set
    var isLoggedIn = mutableStateOf(false)
        private set
    var apiConnected = mutableStateOf(true)
        private set
    var currentUser = mutableStateOf("")
        private set

    /**
     * Updates the API connection state exposed to the UI.
     *
     * The value of [apiConnected] is set to the current value of
     * [ConnectedSuccess].
     */
    fun onApiConnected() {
        apiConnected.value = ConnectedSuccess
    }

    /**
     * Sets the application's login state to logged out.
     *
     * Updates [isLoggedIn] to false.
     */
    fun onIsLoggedInFalse() {
        isLoggedIn.value = false
    }

    /**
     * Sets the application's login state to logged in.
     *
     * Updates [isLoggedIn] to true.
     */
    fun onIsLoggedInTrue() {
        isLoggedIn.value = true
    }

    /**
     * Verifies that the application can successfully communicate with the API.
     *
     * The method executes the `VERIFY_CONNECTION` database query asynchronously
     * using [viewModelScope]. If the API confirms a valid connection,
     * [ConnectedSuccess] is set to true.
     *
     * If the connection cannot be confirmed, or if a
     * [ClientRequestException] or another [Exception] occurs,
     * [ConnectedSuccess] is set to false.
     */
    fun verifyConnection() {
        viewModelScope.launch {
            try {
                val connectionConfirmed: List<APICallTables.VerifyConnection>? = apiCall.query("SELECT * FROM VERIFY_CONNECTION")
                if (connectionConfirmed?.firstOrNull()?.IS_CONNECTED == 1) {
                    ConnectedSuccess = true
                } else {
                    ConnectedSuccess = false
                }
            } catch (e: ClientRequestException) {
                ConnectedSuccess = false
            } catch (e: Exception) {
                ConnectedSuccess = false
            }
        }
    }
}