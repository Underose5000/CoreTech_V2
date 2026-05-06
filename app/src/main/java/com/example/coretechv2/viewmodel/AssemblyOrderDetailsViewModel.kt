package com.example.coretechv2.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coretechv2.dataclasses.APICallTables
import com.example.coretechv2.dataclasses.GelField
import com.example.coretechv2.dataclasses.GelTimeItem
import com.example.coretechv2.dataclasses.MenuItem
import com.example.coretechv2.dataclasses.MessageItems
import com.example.coretechv2.dataclasses.PopupItems
import com.example.coretechv2.repository.APICall
import com.example.coretechv2.repository.DataStoreManager
import com.example.coretechv2.ui.screen.assemblytests.GelTimeScreen
import com.example.coretechv2.ui.screen.assemblytests.ViscosityScreen
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlin.collections.firstOrNull

class AssemblyOrderDetailsViewModel(private val dataStoreManager: DataStoreManager, var sharedViewModel: SharedViewModel) : ViewModel() {

    private val apiCall = APICall(dataStoreManager)
    var popupDetails = PopupItems()
    var popupMessageDetails = MessageItems()
    var showPopupWindow = mutableStateOf(false)
        private set
    fun togglePopup(window: MutableState<Boolean>){
        window.value = !window.value
    }
    var showPopupMessage = mutableStateOf(false)
        private set
    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent = _snackbarEvent.asSharedFlow()

    val actionMenuList = listOf(
        MenuItem(
            title = "Edit",
            onClick = {},
        ),
        MenuItem(
            title = "Product Labels",
            onClick = {}
        ),
        MenuItem(
            title = "Box Labels",
            onClick = {}
        ),
        MenuItem(
            title = "Complete",
            onClick = {}
        ),
    )

    val addMenuList = listOf(
        MenuItem(
            title = "Viscosity Test",
            onClick = {
                popupDetails.width = 700
                popupDetails.height = 500
                popupDetails.content = {ViscosityScreen(sharedViewModel)}
                showPopupWindow.value = true
                showAddMenu = false
                      },
        ),
        MenuItem(
            title = "Gel Time Test",
            onClick = {
                popupDetails.width = 700
                popupDetails.height = 500
                popupDetails.content = { GelTimeScreen(sharedViewModel) }
                showPopupWindow.value = true
                showAddMenu = false
            },
        ),
        MenuItem(
            title = "Adjustment",
            onClick = {}
        ),
    )

    var message = mutableStateOf("")
        private set

    var gelReading = mutableStateOf(GelTimeItem())
    var assemblyHeader by mutableStateOf<List<APICallTables.AssemblyHeader>>(emptyList())
        private set
    var assemblyDetailsLines by mutableStateOf<List<APICallTables.AssemblyLines>>(emptyList())
        private set


    var showActionMenu by mutableStateOf(false)
        private set
    var showAddMenu by mutableStateOf(false)
        private set
    var showGelScreen = mutableStateOf(false)
        private set
    var showMessageOne = mutableStateOf(false)
        private set

//endregion

    fun retrieveAssemblyDetails(orderNumber: String){
        viewModelScope.launch {
            val assemblyHeaderCall : List<APICallTables.AssemblyHeader>? = apiCall.query("SELECT * FROM AssemblyHeader where OrderNumber = '${orderNumber}'")
            if(assemblyHeaderCall?.first()?.ADDITIONALFIELD_1 == ""||assemblyHeaderCall?.firstOrNull()?.ADDITIONALFIELD_1 ==null){assemblyHeaderCall?.first()?.ADDITIONALFIELD_1 = "N/A" }
            if(assemblyHeaderCall?.first()?.ADDITIONALFIELD_2 == ""||assemblyHeaderCall?.firstOrNull()?.ADDITIONALFIELD_2 ==null){assemblyHeaderCall?.first()?.ADDITIONALFIELD_2 = "N/A" }
            if(assemblyHeaderCall?.first()?.ADDITIONALFIELD_3 == ""||assemblyHeaderCall?.firstOrNull()?.ADDITIONALFIELD_3 ==null){assemblyHeaderCall?.first()?.ADDITIONALFIELD_3 = "N/A" }
            if(assemblyHeaderCall?.first()?.ADDITIONALFIELD_4 == ""||assemblyHeaderCall?.firstOrNull()?.ADDITIONALFIELD_4 ==null){assemblyHeaderCall?.first()?.ADDITIONALFIELD_4 = "N/A" }
            if(assemblyHeaderCall?.first()?.ADDITIONALFIELD_5 == ""||assemblyHeaderCall?.firstOrNull()?.ADDITIONALFIELD_5 ==null){assemblyHeaderCall?.first()?.ADDITIONALFIELD_5 = "N/A" }
            if(assemblyHeaderCall?.first()?.ADDITIONALFIELD_6 == ""||assemblyHeaderCall?.firstOrNull()?.ADDITIONALFIELD_6 ==null){assemblyHeaderCall?.first()?.ADDITIONALFIELD_6 = "N/A" }
            if(assemblyHeaderCall?.first()?.ADDITIONALFIELD_7 == ""||assemblyHeaderCall?.firstOrNull()?.ADDITIONALFIELD_7 ==null){assemblyHeaderCall?.first()?.ADDITIONALFIELD_7 = "N/A" }
            if(assemblyHeaderCall?.first()?.ADDITIONALFIELD_8 == ""||assemblyHeaderCall?.firstOrNull()?.ADDITIONALFIELD_8 ==null){assemblyHeaderCall?.first()?.ADDITIONALFIELD_8 = "N/A" }
            if(assemblyHeaderCall?.first()?.ADDITIONALFIELD_9 == ""||assemblyHeaderCall?.firstOrNull()?.ADDITIONALFIELD_9 ==null){assemblyHeaderCall?.first()?.ADDITIONALFIELD_9 = "N/A" }
            if(assemblyHeaderCall?.first()?.ADDITIONALFIELD_10 == ""||assemblyHeaderCall?.firstOrNull()?.ADDITIONALFIELD_10 ==null){assemblyHeaderCall?.first()?.ADDITIONALFIELD_10 = "N/A" }
            if(assemblyHeaderCall?.first()?.ADDITIONALFIELD_11 == ""||assemblyHeaderCall?.firstOrNull()?.ADDITIONALFIELD_11 ==null){assemblyHeaderCall?.first()?.ADDITIONALFIELD_11 = "N/A" }
            if(assemblyHeaderCall?.first()?.ADDITIONALFIELD_12 == ""||assemblyHeaderCall?.firstOrNull()?.ADDITIONALFIELD_12 ==null){assemblyHeaderCall?.first()?.ADDITIONALFIELD_12 = "N/A" }
            assemblyHeader = assemblyHeaderCall ?: emptyList()


            val assemblyDetailsLinesCall : List<APICallTables.AssemblyLines>? = apiCall.query("SELECT * FROM AssemblyLines where OrderNumber = '${orderNumber}'")
            assemblyDetailsLines = assemblyDetailsLinesCall ?: emptyList()
        }

    }
    fun menuPressed(){
        showAddMenu = false
        showActionMenu = !showActionMenu
    }

    fun addPressed(){
        showActionMenu = false
        showAddMenu = !showAddMenu
    }

    fun addListItemPressed(selection : MutableState<Boolean>){
        selection.value = true
        showAddMenu = false
    }
    fun hideMessage(){
        showMessageOne.value = false
    }

    fun onGelChange(newValue: String, field : GelField){
        gelReading.value = when (field) {
            GelField.HOUR -> gelReading.value.copy(hour = newValue)
            GelField.MINUTE -> gelReading.value.copy(minute = newValue)
            GelField.SECOND -> gelReading.value.copy(second = newValue)
            GelField.CATPERCENT -> gelReading.value.copy(catPercent = newValue)
            GelField.CATALYST -> gelReading.value.copy(catalyst = newValue)
            GelField.TESTNUMBER -> gelReading.value.copy(testNumber = newValue)


        }
    }

    fun onGelClear(){
        gelReading.value = GelTimeItem()
        gelReading.value.catPercent = assemblyHeader.firstOrNull()?.ADDITIONALFIELD_9.toString()
       // gelReading.value.testNumber = gelTestNumberCount
    }


}