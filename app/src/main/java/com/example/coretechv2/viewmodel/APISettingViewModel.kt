package com.example.coretechv2.viewmodel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coretechv2.dataclasses.APICallTables
import com.example.coretechv2.repository.APICall
import com.example.coretechv2.repository.DataStoreManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import androidx.core.net.toUri

class APISettingViewModel(private val dataStoreManager: DataStoreManager) : ViewModel() {

    private val apiCall = APICall(dataStoreManager)

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

    var scannedCode = mutableStateOf("")

    var showScanner = mutableStateOf(false)
        private set

    var cameraAccess = mutableStateOf(false)
        private set

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
            
            if (newurl.contains("https://") || newurl.contains("http://") ){
                newurl = newurl.removePrefix("https://")
                newurl = newurl.removePrefix("http://")
            }
            

            dataStoreManager.saveApiSettings(newurl.trim(), newport.trim(), newkey.trim())

            val connectionConfirmed : List<APICallTables.VerifyConnection>? = apiCall.query("SELECT * FROM VERIFY_CONNECTION")
            
            if (connectionConfirmed?.firstOrNull()?.IS_CONNECTED == 1){
                ConnectedSuccess = true
            } else{
                dataStoreManager.saveApiSettings(oldurl, oldport, oldkey)
                errorMessage.value = "Connection Failed"
                showErrorMessage.value = true
            }

        }
    }

    fun scanQR(){
        showScanner.value = true
    }

    fun onQRCodeScanned(scannedCode: String){
        if(scannedCode.isNotEmpty()) {
            val uri = scannedCode.toUri()
            if (!uri.host.isNullOrEmpty()) { newurl = uri.host!! }
            if (uri.port != 0) { newport = uri.port.toString() }
            if (!uri.getQueryParameter("key").isNullOrEmpty()) { newkey = uri.getQueryParameter("key")!! }
            closeScanner()
        }
    }
    fun closeScanner(){
        showScanner.value = false
    }
    fun onCameraAccess(){
        cameraAccess.value = true
    }
}