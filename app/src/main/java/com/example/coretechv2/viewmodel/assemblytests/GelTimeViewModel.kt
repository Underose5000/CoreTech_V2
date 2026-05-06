package com.example.coretechv2.viewmodel.assemblytests

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coretechv2.dataclasses.APICallTables
import com.example.coretechv2.dataclasses.APICallTypes
import com.example.coretechv2.dataclasses.GelField
import com.example.coretechv2.dataclasses.GelTimeItem
import com.example.coretechv2.dataclasses.MessageItems
import com.example.coretechv2.dataclasses.VisField
import com.example.coretechv2.dataclasses.ViscosityItem
import com.example.coretechv2.dataclasses.gelHasValue
import com.example.coretechv2.dataclasses.visHasValue
import com.example.coretechv2.repository.APICall
import com.example.coretechv2.repository.DataStoreManager
import com.example.coretechv2.viewmodel.SharedViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlin.math.round

class GelTimeViewModel(private val dataStoreManager: DataStoreManager, var sharedViewModel: SharedViewModel) : ViewModel() {
    private val apiCall = APICall(dataStoreManager)
    var popupMessage = MessageItems()
    var closePopupMessage = mutableStateOf(false)
        private set
    var closeTestScreen = mutableStateOf(false)
        private set
    var openPopupMessage = mutableStateOf(false)
        private set


    var testNumberCount by mutableStateOf("")
    var saveType by mutableStateOf(APICallTypes.INSERT)
        private set

    var gelReading = mutableStateOf(GelTimeItem())
    var showcatalystList by mutableStateOf(false)
        private set


    fun closePopupMessage(){
        closePopupMessage.value = false
    }
    fun closeTestScreen(){
        closeTestScreen.value = false
    }
    fun openPopupMessage(){
        openPopupMessage.value = false
    }

    fun catalystPressed(){
        showcatalystList = !showcatalystList
    }

    fun retrieveTestCount(orderNumber: String){
        viewModelScope.launch {
            val gelTimeLinesCall : List<APICallTables.gelTimeTest>? = apiCall.query("SELECT * FROM OSTDEF_GELTIME_TESTS where OrderNumber = '${orderNumber}'")
            testNumberCount = (gelTimeLinesCall?.count().toString())
        }
    }
    fun onGelChange(newValue: String, field : GelField){
        gelReading.value = when (field) {
            GelField.CATPERCENT -> gelReading.value.copy(catPercent = newValue)
            GelField.CATALYST -> gelReading.value.copy(catalyst = newValue)
            GelField.TESTNUMBER -> gelReading.value.copy(testNumber = newValue)
            GelField.HOUR -> gelReading.value.copy(hour = newValue)
            GelField.MINUTE -> gelReading.value.copy(minute = newValue)
            GelField.SECOND -> gelReading.value.copy(second = newValue)
        }
        if (field == GelField.CATALYST){
            showcatalystList = false
        }
    }

    fun onSave(){
        val gelTimeFormatted = ""
        viewModelScope.launch {
            var call = ""
            if(saveType == APICallTypes.INSERT){
                call = "INSERT INTO OSTDEF_GELTIME_TESTS " +
                        "(ITEMCODE, ORDERNUMBER, ITEMDESCRIPTION, TESTNO, GELTIME, GELCAT, GELCATPERCENT, SYSUSERCREATED, SYSUSERMODIFIED) " +
                        "VALUES(''${sharedViewModel.currentAssemblyHeader?.ITEMCODE}', '${sharedViewModel.currentAssemblyHeader?.ORDERNUMBER}', '${sharedViewModel.currentAssemblyHeader?.ITEMDESCRIPTION}', ${gelReading.value.testNumber}, " +
                        "'${gelTimeFormatted}', '${gelReading.value.catalyst}', '${gelReading.value.catPercent}', '${sharedViewModel.currentUser}', '${sharedViewModel.currentUser}')"
            }
            if(saveType == APICallTypes.UPDATE){
                call = "UPDATE OSTDEF_GELTIME_TESTS SET  " +
                        "TESTNO = ${gelReading.value.testNumber}," +
                        "GELTIME = '${gelTimeFormatted}'," +
                        "GELCAT = '${gelReading.value.catalyst}'," +
                        "GELCATPERCENT = '${gelReading.value.catPercent}'," +
                        "SYSUSERMODIFIED = '${sharedViewModel.currentUser}'"
            }

            val response = apiCall.insertUpdateDelete(call)
            if(response == "200 OK"){
                closeTestScreen.value = true
                sharedViewModel.snackBarMessage("Gel Time Saved successfully")
            } else{
                sharedViewModel.snackBarMessage("Error Saving, Please Try Again")
            }
        }
    }
    fun onClear(){
        gelReading.value = GelTimeItem().apply {
            gelReading.value.catalyst = sharedViewModel.currentAssemblyHeader?.ADDITIONALFIELD_1?.uppercase() ?: "N/A"
            gelReading.value.catPercent = sharedViewModel.currentAssemblyHeader?.ADDITIONALFIELD_9?.replace("%", "") ?: "N/A"
        }
        viewModelScope.launch {
            val linesCall : List<APICallTables.gelTimeTest>? = apiCall.query("SELECT * FROM OSTDEF_GELTIME_TESTS where OrderNumber = '${sharedViewModel.currentOrderNumber.value}'")
            gelReading.value = gelReading.value.copy(testNumber = linesCall?.count().toString())
        }
    }

    fun onCancel(){
        if (gelHasValue(gelReading)){
            popupMessage.message = "Test Results are not saved\nleave without saving?"
            popupMessage.messageButton1Text = "No"
            popupMessage.onClickAction1 = {
                closePopupMessage.value = true
            }
            popupMessage.messageButton2Text = "Yes"
            popupMessage.onClickAction2 = {
                closePopupMessage.value = true
                closeTestScreen.value = true
            }
            openPopupMessage.value = true

        } else {
            closeTestScreen.value = true
        }
    }
}