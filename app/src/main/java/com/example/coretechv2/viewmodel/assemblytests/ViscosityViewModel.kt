package com.example.coretechv2.viewmodel.assemblytests

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coretechv2.dataclasses.APICallTypes
import com.example.coretechv2.dataclasses.PopupMessageItem
import com.example.coretechv2.dataclasses.TestTypes
import com.example.coretechv2.dataclasses.VisField
import com.example.coretechv2.dataclasses.VisSettings
import com.example.coretechv2.dataclasses.ViscosityItem
import com.example.coretechv2.dataclasses.visHasValue
import com.example.coretechv2.repository.APICall
import com.example.coretechv2.repository.DataStoreManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ViscosityViewModel(private val dataStoreManager: DataStoreManager) : ViewModel() {
    private val apiCall = APICall(dataStoreManager)
    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent = _snackbarEvent.asSharedFlow()

    var visReading = mutableStateOf(ViscosityItem())
    var defaultSpindle = mutableStateOf("")
    var defaultIndex = mutableStateOf("")
    var visTestNumberCount by mutableStateOf("")
    var showSpindleList by mutableStateOf(false)
        private set
    var showindexList by mutableStateOf(false)
        private set
    var showtestNumber by mutableStateOf(false)
        private set
    var saveType by mutableStateOf(APICallTypes.INSERT)
        private set
    var popupMessage = PopupMessageItem()
    var showMessageTwo = mutableStateOf(false)
        private set

    private val _closeScreen = MutableSharedFlow<Unit>()

    val closeScreen = _closeScreen


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

    fun retrieveTestCount(orderNumber: String){
        viewModelScope.launch {
            val viscosityLinesCall : List<APICall.viscosityTest>? = apiCall.query("SELECT * FROM OSTDEF_VISCOSITY_TESTS where OrderNumber = '${orderNumber}'")
            visTestNumberCount = (viscosityLinesCall?.count().toString())
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
    fun onTestSave(currentUser: String,itemCode: String,orderNumber: String,itemDescription: String){
        var ratio = 0.0
        if (visReading.value.indexRange == "6/60"){
            ratio = visReading.value.vis06.toDouble() / visReading.value.vis60.toDouble()
        }
        if (visReading.value.indexRange == "3/30"){
            ratio = visReading.value.vis03.toDouble() / visReading.value.vis30.toDouble()
        }
        if (visReading.value.indexRange == "0.6/6"){
            ratio = visReading.value.vis0_6.toDouble() / visReading.value.vis06.toDouble()
        }
        if (visReading.value.indexRange == "0.3/3"){
            ratio = visReading.value.vis0_3.toDouble() / visReading.value.vis03.toDouble()
        }
        val indexResult = kotlin.math.round(ratio * 10 * 10) / 10
        viewModelScope.launch {
            var call = ""
            if(saveType == APICallTypes.INSERT){
                call = "INSERT INTO OSTDEF_VISCOSITY_TESTS " +
                        "(ITEMCODE, ORDERNUMBER, ITEMDESCRIPTION, TESTNO, SPINDLE, INDEXREADING, READING60, READING30, READING12, READING6, READING3, READING1_5, READING0_6, READING0_3, SYSUSERCREATED, SYSUSERMODIFIED) " +
                        "VALUES('${itemCode}', '${orderNumber}', '${itemDescription}', ${visReading.value.testNumber}, '${visReading.value.spindle}', ${indexResult}, " +
                        "${visReading.value.vis60}, ${visReading.value.vis30}, ${visReading.value.vis12}, ${visReading.value.vis06}, ${visReading.value.vis03}, ${visReading.value.vis1_5}, ${visReading.value.vis0_6},${visReading.value.vis0_3}, " +
                        "'${currentUser}', '${currentUser}')"
            }
            if(saveType == APICallTypes.UPDATE){
                call = "UPDATE OSTDEF_VISCOSITY_TESTS SET  " +
                        "TESTNO = ${visReading.value.testNumber}," +
                        "SPINDLE = '${visReading.value.spindle}'," +
                        "INDEXREADING = ${indexResult}," +
                        "READING60 = ${visReading.value.vis60}" +
                        "READING30 = ${visReading.value.vis30}" +
                        "READING12 = ${visReading.value.vis12}" +
                        "READING6 = ${visReading.value.vis06}" +
                        "READING3 = ${visReading.value.vis03}" +
                        "READING1_5 = ${visReading.value.vis1_5}" +
                        "READING0_6 = ${visReading.value.vis0_6}" +
                        "READING0_3 = ${visReading.value.vis0_3}" +
                        "SYSUSERMODIFIED = ${currentUser})"
            }

            Log.d("testsave","response =" + call)
            val response = apiCall.insertUpdateDelete(call)
            if(response == "200 OK"){
                _snackbarEvent.emit("Viscosity Saved successfully")
            } else{
                _snackbarEvent.emit("Error Saving, Please Try Again")
            }
        }
    }
    fun onVisClear(){
        visReading.value = ViscosityItem()
        visReading.value.spindle = defaultSpindle.value
        visReading.value.indexRange = defaultIndex.value
        visReading.value.testNumber = visTestNumberCount
    }

    fun onCancel(){
        if (visHasValue(visReading)){
            popupMessage.message = "Test Results are not saved\nleave without saving?"
            popupMessage.messageButton1Text = "No"
            popupMessage.onClickAction1 = {
                showMessageTwo.value = false
            }
            popupMessage.onClickAction2 = {
                onVisClear()
                showMessageTwo.value = false
                onCloseRequested()
            }
            popupMessage.messageButton2Text = "Yes"
            showMessageTwo.value = true
        } else {
            onVisClear()
            onCloseRequested()
        }
    }

    fun onCloseRequested() {
        viewModelScope.launch {
            _closeScreen.emit(Unit)
        }
    }
}