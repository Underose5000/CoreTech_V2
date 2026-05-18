package com.example.coretechv2.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coretechv2.dataclasses.APICallTables
import com.example.coretechv2.dataclasses.APICallTypes
import com.example.coretechv2.dataclasses.MenuItem
import com.example.coretechv2.dataclasses.MessageItems
import com.example.coretechv2.dataclasses.PopupItems
import com.example.coretechv2.repository.APICall
import com.example.coretechv2.repository.DataStoreManager
import com.example.coretechv2.ui.screen.assemblytestsandadjustments.AdjustmentsScreen
import com.example.coretechv2.ui.screen.assemblytestsandadjustments.GelTimeScreen
import com.example.coretechv2.ui.screen.assemblytestsandadjustments.ViscosityScreen
import kotlinx.coroutines.launch
import kotlin.collections.firstOrNull

class AssemblyOrderDetailsViewModel(private val dataStoreManager: DataStoreManager, var sharedViewModel: SharedViewModel) : ViewModel() {

    private val apiCall = APICall(dataStoreManager)
    var popupDetails = PopupItems().copy()
    var popupMessageDetails = MessageItems()
    var showPopupWindow = mutableStateOf(false)
        private set
    var showPopupMessage = mutableStateOf(false)
        private set

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
                popupDetails.content = {
                    sharedViewModel.updateSaveType(APICallTypes.INSERT)
                    ViscosityScreen(sharedViewModel)
                }
                showPopupWindow.value = true
                showAddMenu = false
                      },
        ),
        MenuItem(
            title = "Gel Time Test",
            onClick = {
                popupDetails.width = 700
                popupDetails.height = 500
                popupDetails.content = {
                    sharedViewModel.updateSaveType(APICallTypes.INSERT)
                    GelTimeScreen(sharedViewModel)
                }
                showPopupWindow.value = true
                showAddMenu = false
            },
        ),
        MenuItem(
            title = "Adjustment",
            onClick = {
                popupDetails.width = 700
                popupDetails.height = 500
                popupDetails.content = {
                    sharedViewModel.updateSaveType(APICallTypes.INSERT)
                    AdjustmentsScreen(sharedViewModel)
                }
                showPopupWindow.value = true
                showAddMenu = false
            }
        ),
    )
    var assemblyHeader by mutableStateOf<List<APICallTables.AssemblyHeader>>(emptyList())
        private set
    var assemblyDetailsLines by mutableStateOf<List<APICallTables.AssemblyLines>>(emptyList())
        private set
    val testAndAdjustments = mutableStateListOf<SnapshotStateList<Any>>()
    var showActionMenu by mutableStateOf(false)
        private set
    var showAddMenu by mutableStateOf(false)
        private set


    fun togglePopup(window: MutableState<Boolean>){
        window.value = !window.value
    }
    fun retrieveAssemblyDetails(){
        viewModelScope.launch {
            val assemblyHeaderCall : List<APICallTables.AssemblyHeader>? = apiCall.query("SELECT * FROM AssemblyHeader where OrderNumber = '${sharedViewModel.currentOrderNumber.value}'")
            assemblyHeader = assemblyHeaderCall ?: emptyList()

            val assemblyDetailsLinesCall : List<APICallTables.AssemblyLines>? = apiCall.query("SELECT * FROM AssemblyLines where OrderNumber = '${sharedViewModel.currentOrderNumber.value}'")
            assemblyDetailsLines = assemblyDetailsLinesCall ?: emptyList()
            sharedViewModel.currentAssemblyHeader = assemblyHeader.firstOrNull()
            sharedViewModel.currentAssemblyLines = assemblyDetailsLines
        }
    }

    fun retrieveTestDetails(){
        testAndAdjustments.clear()
        val testCount = mutableListOf<Int>()
        viewModelScope.launch {
            val viscosityTests : List<APICallTables.viscosityTest>? = apiCall.query("SELECT * FROM OSTDEF_VISCOSITY_TESTS where OrderNumber = '${sharedViewModel.currentOrderNumber.value}'")
            testCount.addAll(viscosityTests?.map { it.TESTNO } ?: emptyList())
            val gelTimeTests : List<APICallTables.gelTimeTest>? = apiCall.query("SELECT * FROM OSTDEF_GELTIME_TESTS where OrderNumber = '${sharedViewModel.currentOrderNumber.value}'")
            testCount.addAll(gelTimeTests?.map { it.TESTNO } ?: emptyList())

            testCount.sortDescending()
            sharedViewModel.testCount.value = testCount.first()

            val adjustmentLines : List<APICallTables.assemblyAdjustment>? = apiCall.query("SELECT * FROM OSTDEF_ADJUSTMENTS where OrderNumber = '${sharedViewModel.currentOrderNumber.value}'")
            testCount.addAll(adjustmentLines?.map { it.ADJUSTNO } ?: emptyList())

            val testAndAdjustmentsCount = testCount.toMutableList()
            testAndAdjustmentsCount.sortDescending()

            for (testValue in 0 until testAndAdjustmentsCount.first()) {
                val testAndAdjustment = mutableStateListOf<Any>()
                val test = mutableStateListOf<Any>()
                val adjust = mutableStateListOf<Any>()
                for (tn in 0 until (viscosityTests?.size ?: 0)) {
                    if (viscosityTests?.get(tn)?.TESTNO == testValue + 1) test.add(viscosityTests[tn])
                }
                for (tn in 0 until (gelTimeTests?.size ?: 0)) {
                    if (gelTimeTests?.get(tn)?.TESTNO == testValue + 1) test.add(gelTimeTests[tn])
                }
                for (tn in 0 until (adjustmentLines?.size ?: 0)) {
                    if (adjustmentLines?.get(tn)?.ADJUSTNO == testValue + 1) adjust.add(adjustmentLines[tn])
                }

                testAndAdjustment.add(test)
                testAndAdjustment.add(adjust)

                testAndAdjustments.add(testAndAdjustment)
            }


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
    fun reload(){
        retrieveAssemblyDetails()
        retrieveTestDetails()
    }
}