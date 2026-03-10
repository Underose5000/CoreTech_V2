package com.example.coretechv2.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coretechv2.repository.APICall
import com.example.coretechv2.repository.DataStoreManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class APISettingViewModel(private val dataStoreManager: DataStoreManager) : ViewModel() {

    private val apiCall = APICall(dataStoreManager)

    init {
        viewModelScope.launch {
            dataStoreManager.apiUrlFlow.collect { storedUrl ->
                newurl = storedUrl ?: ""
            }
        }

        viewModelScope.launch {
            dataStoreManager.apiPortFlow.collect { storedPort ->
                newport = storedPort ?: ""
            }
        }

        viewModelScope.launch {
            dataStoreManager.apiKeyFlow.collect { storedKey ->
                newkey = storedKey ?: ""
            }
        }
    }

    val oldurl : String = runBlocking {
        dataStoreManager.apiUrlFlow.firstOrNull() ?: ""
    }

    val oldport : String = runBlocking {
        dataStoreManager.apiPortFlow.firstOrNull() ?: ""
    }

    val oldkey : String = runBlocking {
        dataStoreManager.apiKeyFlow.firstOrNull() ?: ""
    }
    var ConnectedSuccess by mutableStateOf(false)
        private set

    var errorMessage = mutableStateOf("")
        private set

    var showErrorMessage = mutableStateOf(false)
        private set

    var newurl by mutableStateOf("")
        private set

    var newport by mutableStateOf("")
        private set

    var newkey by mutableStateOf("")
        private set

    fun onUrlChange(newValue: String){
        newurl = newValue
    }

    fun onPortChange(newValue: String){
        newport = newValue
    }

    fun onKeyChange(newValue: String){
        newkey = newValue
    }

    fun connect(){
        viewModelScope.launch {
            showErrorMessage.value = false
            Log.d("API Setting", newurl)
            if (newurl.contains("https://") || newurl.contains("http://") ){
                newurl = newurl.removePrefix("https://")
                newurl = newurl.removePrefix("http://")
            }
            Log.d("API Setting", newurl)

            dataStoreManager.saveApiSettings(newurl.trim(), newport.trim(), newkey.trim())

            val connectionConfirmed : List<APICall.VerifyConnection>? = apiCall.query("SELECT * FROM VERIFY_CONNECTION")
            Log.d("API Setting", "connection Confirmed = $connectionConfirmed")
            if (connectionConfirmed?.firstOrNull()?.IS_CONNECTED == 1){
                ConnectedSuccess = true
            } else{
                dataStoreManager.saveApiSettings(oldurl, oldport, oldkey)
                errorMessage.value = "Connection Failed"
                showErrorMessage.value = true
            }

        }
    }
}