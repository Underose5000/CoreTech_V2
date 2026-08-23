package com.example.coretechv2.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coretechv2.dataclasses.APICallTables
import com.example.coretechv2.dataclasses.APICallTypes
import com.example.coretechv2.dataclasses.ItemDescriptorItem
import com.example.coretechv2.dataclasses.LabelElements
import com.example.coretechv2.dataclasses.LabelStyles
import com.example.coretechv2.dataclasses.MenuItem
import com.example.coretechv2.dataclasses.MessageItems
import com.example.coretechv2.dataclasses.PopupItems
import com.example.coretechv2.repository.APICall
import com.example.coretechv2.repository.DataStoreManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SharedViewModel(private val dataStoreManager: DataStoreManager) : ViewModel(){
    private val apiCall = APICall(dataStoreManager)

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

    var allItemDescriptors: List<APICallTables.ItemDescriptor>? = null
    var allAssemblyOrdersList = mutableStateListOf<APICallTables.AssemblyHeader>()
        private set
    var currentAssemblyHeader: APICallTables.AssemblyHeader? = null
    var currentAssemblyLines: List<APICallTables.AssemblyLines>? = null


    var currentUser = mutableStateOf("")
    var currentOrderNumber = mutableStateOf("")
    var currentItemCode = mutableStateOf("")
    var currentItem = mutableStateOf(ItemDescriptorItem())

    var AssemblyOrdersList = mutableStateListOf<APICallTables.AssemblyHeader>()
        private set

    var AssemblyOrderListSearached = mutableStateListOf<APICallTables.AssemblyHeader>()
        private set

    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent = _snackbarEvent.asSharedFlow()


    var showPopup = mutableStateOf(false)
    var showMessagePopup = mutableStateOf(false)

    var navBack = mutableStateOf(false)

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

    fun retrieveAllAssemblyOrders(){
        viewModelScope.launch {
            val assemblyOrdersListCall : List<APICallTables.AssemblyHeader>? = apiCall.query("SELECT * FROM AssemblyHeader order by OrderNumber DESC")

            allAssemblyOrdersList.clear()
            assemblyOrdersListCall?.let {
                allAssemblyOrdersList.addAll(it)
            }
        }
    }
    fun retrieveAssemblyOrders(){
        viewModelScope.launch {
            val assemblyOrdersListCall : List<APICallTables.AssemblyHeader>? = apiCall.query("SELECT * FROM AssemblyHeader where OrderStatus = 'Open' order by OrderNumber DESC")

            AssemblyOrdersList.clear()
            AssemblyOrderListSearached.clear()
            assemblyOrdersListCall?.let {
                AssemblyOrdersList.addAll(it)
                AssemblyOrderListSearached.addAll(it)
            }
        }
    }
}