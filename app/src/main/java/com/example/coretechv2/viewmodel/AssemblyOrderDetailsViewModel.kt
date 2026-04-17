package com.example.coretechv2.viewmodel

import android.R
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coretechv2.dataclasses.MenuItem
import com.example.coretechv2.dataclasses.VisField
import com.example.coretechv2.dataclasses.VisSettings
import com.example.coretechv2.dataclasses.ViscosityItem
import com.example.coretechv2.dataclasses.visHasValue
import com.example.coretechv2.repository.APICall
import com.example.coretechv2.repository.DataStoreManager
import com.example.coretechv2.repository.regex.verifyVisReading
import com.example.coretechv2.ui.screen.assemblytests.ViscosityScreen
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlin.collections.firstOrNull

class AssemblyOrderDetailsViewModel(private val dataStoreManager: DataStoreManager) : ViewModel() {

    private val apiCall = APICall(dataStoreManager)

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
                onVisClear()
                addListItemPressed(showViscosityScreen)
                      },
        ),
        MenuItem(
            title = "Adjustment",
            onClick = {}
        ),
    )

    var message = mutableStateOf("")
        private set

    var messageButtonText1 = mutableStateOf("")
        private set
    var messageButtonText2 = mutableStateOf("")
        private set
    var messageButton1Action = mutableStateOf({})
        private set
    var messageButton2Action = mutableStateOf({})
        private set
    var visReading = mutableStateOf(ViscosityItem())
    var assemblyHeader by mutableStateOf<List<APICall.AssemblyHeader>>(emptyList())
        private set
    var assemblyDetailsLines by mutableStateOf<List<APICall.AssemblyLines>>(emptyList())
        private set


//region Popup windows Showing Variables
    var showActionMenu by mutableStateOf(false)
        private set
    var showAddMenu by mutableStateOf(false)
        private set
    var showViscosityScreen = mutableStateOf(false)
        private set
    var showMessageOne = mutableStateOf(false)
        private set
    var showMessageTwo = mutableStateOf(false)
        private set
    var showSpindleList by mutableStateOf(false)
        private set
    var showindexList by mutableStateOf(false)
        private set
    var showtestNumber by mutableStateOf(false)
        private set
//endregion

    fun retrieveAssemblyDetails(orderNumber: String){
        viewModelScope.launch {
            val assemblyHeaderCall : List<APICall.AssemblyHeader>? = apiCall.query("SELECT * FROM AssemblyHeader where OrderNumber = '${orderNumber}'")
            if(assemblyHeaderCall?.first()?.ADDITIONALFIELD_9 == ""||assemblyHeaderCall?.firstOrNull()?.ADDITIONALFIELD_9 ==null){assemblyHeaderCall?.first()?.ADDITIONALFIELD_9 = "N/A" }
            if(assemblyHeaderCall?.first()?.ADDITIONALFIELD_8 == ""||assemblyHeaderCall?.firstOrNull()?.ADDITIONALFIELD_8 ==null){assemblyHeaderCall?.first()?.ADDITIONALFIELD_8 = "N/A" }
            assemblyHeader = assemblyHeaderCall ?: emptyList()

            val assemblyDetailsLinesCall : List<APICall.AssemblyLines>? = apiCall.query("SELECT * FROM AssemblyLines where OrderNumber = '${orderNumber}'")
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


//region Viscosity Functions
    fun spindlePressed(){
        showSpindleList = !showSpindleList
    }
    fun indexPressed(){
        showindexList = !showindexList
    }
    fun testNumberPressed(){
        showtestNumber = !showtestNumber
    }
    fun onDropDownChange(newValue: String, field : VisSettings){
        visReading.value = when (field) {
            VisSettings.SPINDLE -> visReading.value.copy(spindle = newValue)
            VisSettings.INDEX -> visReading.value.copy(indexRange = newValue)
            VisSettings.TESTNUMBER -> visReading.value.copy(testNumber = newValue)
        }
        if (field == VisSettings.SPINDLE){
            showSpindleList = false
        }
        if (field == VisSettings.INDEX){
            showindexList = false
        }
        if (field == VisSettings.TESTNUMBER){
            showtestNumber = false
        }
    }

    fun onVisChange(newValue: String, field : VisField){
        visReading.value = when (field) {
            VisField.VIS60 -> visReading.value.copy(vis60 = newValue)
            VisField.VIS30 -> visReading.value.copy(vis30 = newValue)
            VisField.VIS12 -> visReading.value.copy(vis12 = newValue)
            VisField.VIS06  -> visReading.value.copy(vis06 = newValue)
            VisField.VIS03  -> visReading.value.copy(vis03 = newValue)
            VisField.VIS1_5 -> visReading.value.copy(vis1_5 = newValue)
            VisField.VIS0_6 -> visReading.value.copy(vis0_6 = newValue)
            VisField.VIS0_3 -> visReading.value.copy(vis0_3 = newValue)
        }
    }

    fun onVisSave(){

    }
    fun onVisClear(){
        visReading = mutableStateOf(
            ViscosityItem(
                spindle = assemblyHeader.first().ADDITIONALFIELD_9,
                indexRange = assemblyHeader.first().ADDITIONALFIELD_8,
                )
        )
    }
    fun onCancel(){
        if (visHasValue(visReading)){
            message.value = "Test Results are not saved\nleave without saving?"
            messageButtonText1.value = "No"
            messageButton1Action.value = {
                showMessageTwo.value = false
            }
            messageButton2Action.value = {
                onVisClear()
                showMessageTwo.value = false
                showViscosityScreen.value = false
            }
            messageButtonText2.value = "Yes"
            message
            showMessageTwo.value = true
        } else {
            onVisClear()
            showViscosityScreen.value = false
        }
    }
//endregion
}