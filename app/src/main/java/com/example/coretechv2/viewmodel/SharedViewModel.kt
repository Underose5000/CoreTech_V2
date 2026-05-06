package com.example.coretechv2.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coretechv2.dataclasses.APICallTables
import com.example.coretechv2.dataclasses.MessageItems
import com.example.coretechv2.dataclasses.PopupItems
import com.example.coretechv2.repository.APICall
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SharedViewModel() : ViewModel(){
    var message = mutableStateOf("")
        private set
    var messageButton1Text = mutableStateOf("")
        private set
    var messageButton2Text = mutableStateOf("")
        private set
    var messageButton1Action = mutableStateOf({})
        private set
    var messageButton2Action = mutableStateOf({})
        private set

    var popupDetails = PopupItems()
    var popupMessageDetails = MessageItems()
    var currentAssemblyHeader: APICallTables.AssemblyHeader? = null
    var currentAssemblyLines: List<APICallTables.AssemblyLines>? = null


    var currentUser = mutableStateOf("")
    var currentOrderNumber = mutableStateOf("")
    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent = _snackbarEvent.asSharedFlow()


    var showPopup = mutableStateOf(false)
    var showMessagePopup = mutableStateOf(false)





    fun openPopup(){
        showPopup.value = true
    }
    fun closePopup(){
        showPopup.value = false
        popupDetails = PopupItems()
    }
    fun openMessagePopup(){
        showMessagePopup.value = true
    }
    fun closeMessagePopup(){
        showMessagePopup.value = false
        popupMessageDetails = MessageItems()
    }
    fun snackBarMessage(message: String) {
        viewModelScope.launch {
            _snackbarEvent.emit(message)
        }
    }
}