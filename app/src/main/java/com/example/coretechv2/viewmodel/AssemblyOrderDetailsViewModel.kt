package com.example.coretechv2.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coretechv2.repository.APICall
import com.example.coretechv2.repository.DataStoreManager
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch

class AssemblyOrderDetailsViewModel(private val dataStoreManager: DataStoreManager) : ViewModel() {

    private val apiCall = APICall(dataStoreManager)

    var assemblyHeader by mutableStateOf<List<APICall.AssemblyHeader>>(emptyList())
        private set
    var assemblyDetailsLines by mutableStateOf<List<APICall.AssemblyLines>>(emptyList())
        private set


    fun retrieveAssemblyDetails(orderNumber: String){
        viewModelScope.launch {
            Log.d("Assembly Details View", "Call Start, Order Number = ${orderNumber}")
            val assemblyHeaderCall : List<APICall.AssemblyHeader>? = apiCall.query("SELECT * FROM AssemblyHeader where OrderNumber = '${orderNumber}'")
            assemblyHeader = assemblyHeaderCall ?: emptyList()
            Log.d("Assembly Details View", "Header Call finished")

            val assemblyDetailsLinesCall : List<APICall.AssemblyLines>? = apiCall.query("SELECT * FROM AssemblyLines where OrderNumber = '${orderNumber}'")
            assemblyDetailsLines = assemblyDetailsLinesCall ?: emptyList()
            Log.d("Assembly Details View", "${assemblyDetailsLinesCall}")


        }
    }
}