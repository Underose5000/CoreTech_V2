package com.example.coretechv2.viewmodel

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coretechv2.dataclasses.APICallTables
import com.example.coretechv2.repository.APICall
import com.example.coretechv2.repository.DataStoreManager
import com.example.coretechv2.ui.screen.APISettingsScreen
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivityViewModel(private val dataStoreManager: DataStoreManager) : ViewModel() {

    private val apiCall = APICall(dataStoreManager)

    var ConnectedSuccess by mutableStateOf(true)
        private set


    fun verifyConnection(){
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