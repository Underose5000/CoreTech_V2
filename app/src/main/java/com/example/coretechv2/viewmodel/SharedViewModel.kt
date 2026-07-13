package com.example.coretechv2.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coretechv2.dataclasses.APICallTables
import com.example.coretechv2.dataclasses.APICallTypes
import com.example.coretechv2.dataclasses.ItemDescriptorItem
import com.example.coretechv2.dataclasses.LabelElements
import com.example.coretechv2.dataclasses.MessageItems
import com.example.coretechv2.dataclasses.PopupItems
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

    var popupDetails = PopupItems().copy()
    var popupMessageDetails = MessageItems().copy()
    var currentAssemblyHeader: APICallTables.AssemblyHeader? = null
    var currentAssemblyLines: List<APICallTables.AssemblyLines>? = null


    var currentUser = mutableStateOf("")
    var currentOrderNumber = mutableStateOf("")
    var currentItem = mutableStateOf(ItemDescriptorItem())


    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent = _snackbarEvent.asSharedFlow()


    var showPopup = mutableStateOf(false)
    var showMessagePopup = mutableStateOf(false)

    var showLabelPreview = mutableStateOf(false)

    var saveType by mutableStateOf(APICallTypes.INSERT)
        private set
    var testCount = mutableStateOf(0)




    fun openPopup(){
        showPopup.value = true
    }
    fun closePopup(){
        showPopup.value = false
        popupDetails = PopupItems().copy()
    }
    fun openMessagePopup(){
        showMessagePopup.value = true
    }
    fun closeMessagePopup(){
        showMessagePopup.value = false
        popupMessageDetails = MessageItems().copy()
    }
    fun openLabelPreview(){
        showLabelPreview.value = true
    }
    fun closeLabelPreview(){
        showLabelPreview.value = false
    }

    fun snackBarMessage(message: String) {
        viewModelScope.launch {
            _snackbarEvent.emit(message)
        }
    }
    fun updateSaveType(type : APICallTypes){
        saveType = type
    }
}