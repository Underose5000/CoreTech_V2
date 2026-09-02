package com.example.coretechv2.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coretechv2.dataclasses.APICallTables
import com.example.coretechv2.repository.APICall
import com.example.coretechv2.repository.DataStoreManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * ViewModel responsible for managing API connection settings.
 *
 * This ViewModel loads the currently stored API URL, port, and key from
 * [DataStoreManager], allows these values to be edited, and tests the
 * connection using [APICall].
 *
 * API settings can also be populated by scanning a QR code containing the
 * server address, port, and API key. If a connection attempt fails, the
 * previously stored settings are restored.
 *
 * @param dataStoreManager The manager used to retrieve and save API settings.
 */
class APISettingViewModel(private val dataStoreManager: DataStoreManager) : ViewModel() {
    private val apiCall = APICall(dataStoreManager)

    /**
     * The API URL stored before the current settings are changed.
     */
    val oldurl: String = runBlocking {
        dataStoreManager.apiUrlFlow.firstOrNull() ?: ""
    }

    /**
     * The API port stored before the current settings are changed.
     */
    val oldport: String = runBlocking {
        dataStoreManager.apiPortFlow.firstOrNull() ?: ""
    }

    /**
     * The API key stored before the current settings are changed.
     */
    val oldkey: String = runBlocking {
        dataStoreManager.apiKeyFlow.firstOrNull() ?: ""
    }

    /**
     * Indicates whether the API connection was successfully verified.
     */
    var ConnectedSuccess by mutableStateOf(false)
        private set

    /**
     * Contains the message describing the most recent connection error.
     */
    var errorMessage = mutableStateOf("")
        private set

    /**
     * Determines whether the error message should be displayed.
     */
    var showErrorMessage = mutableStateOf(false)
        private set

    /**
     * The current API URL entered by the user.
     */
    var newurl by mutableStateOf("")
        private set

    /**
     * The current API port entered by the user.
     */
    var newport by mutableStateOf("")
        private set

    /**
     * The current API key entered by the user.
     */
    var newkey by mutableStateOf("")
        private set

    /**
     * Stores the most recently scanned QR code value.
     */
    var scannedCode = mutableStateOf("")

    /**
     * Determines whether the QR code scanner should be displayed.
     */
    var showScanner = mutableStateOf(false)
        private set

    /**
     * Indicates whether camera access has been granted for QR code scanning.
     */
    var cameraAccess = mutableStateOf(false)
        private set

    /**
     * Starts observing the stored API settings and updates the editable
     * settings whenever values are retrieved from [DataStoreManager].
     */
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

    /**
     * Updates the API URL with the value entered by the user.
     *
     * @param newValue The new API URL.
     */
    fun onUrlChange(newValue: String) {
        newurl = newValue
    }
    /**
     * Updates the API port with the value entered by the user.
     *
     * @param newValue The new API port.
     */
    fun onPortChange(newValue: String) {
        newport = newValue
    }

    /**
     * Updates the API key with the value entered by the user.
     *
     * @param newValue The new API key.
     */
    fun onKeyChange(newValue: String) {
        newkey = newValue
    }

    /**
     * Saves the entered API settings and attempts to verify the connection.
     *
     * Any HTTP or HTTPS prefix is removed from the entered URL before the
     * settings are saved. The connection is then tested by querying the
     * `VERIFY_CONNECTION` table.
     *
     * If the connection is successful, [ConnectedSuccess] is set to `true`.
     * If the connection fails, the previously stored API settings are restored
     * and an error message is displayed.
     */
    fun connect() {
        viewModelScope.launch {
            showErrorMessage.value = false

            if (newurl.contains("https://") || newurl.contains("http://")) {
                newurl = newurl.removePrefix("https://")
                newurl = newurl.removePrefix("http://")
            }


            dataStoreManager.saveApiSettings(newurl.trim(), newport.trim(), newkey.trim())

            val connectionConfirmed: List<APICallTables.VerifyConnection>? = apiCall.query("SELECT * FROM VERIFY_CONNECTION")

            if (connectionConfirmed?.firstOrNull()?.IS_CONNECTED == 1) {
                ConnectedSuccess = true
            } else {
                dataStoreManager.saveApiSettings(oldurl, oldport, oldkey)
                errorMessage.value = "Connection Failed"
                showErrorMessage.value = true
            }

        }
    }

    /**
     * Displays the QR code scanner.
     */
    fun scanQR() {
        showScanner.value = true
    }

    /**
     * Processes a scanned QR code and extracts API connection settings.
     *
     * The QR code is interpreted as a URI. If present, its host is used as
     * the API URL, its port is used as the API port, and the `key` query
     * parameter is used as the API key.
     *
     * The scanner is closed after the QR code has been processed.
     *
     * @param scannedCode The URI value obtained from the scanned QR code.
     */
    fun onQRCodeScanned(scannedCode: String) {
        if (scannedCode.isNotEmpty()) {
            val uri = scannedCode.toUri()
            if (!uri.host.isNullOrEmpty()) {
                newurl = uri.host!!
            }
            if (uri.port != 0) {
                newport = uri.port.toString()
            }
            if (!uri.getQueryParameter("key").isNullOrEmpty()) {
                newkey = uri.getQueryParameter("key")!!
            }
            closeScanner()
        }
    }

    /**
     * Hides the QR code scanner.
     */
    fun closeScanner() {
        showScanner.value = false
    }

    /**
     * Updates the camera access state after camera permission is granted.
     */
    fun onCameraAccess() {
        cameraAccess.value = true
    }
}