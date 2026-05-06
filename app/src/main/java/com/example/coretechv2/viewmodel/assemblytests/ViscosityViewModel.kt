package com.example.coretechv2.viewmodel.assemblytests

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coretechv2.dataclasses.APICallTables
import com.example.coretechv2.dataclasses.APICallTypes
import com.example.coretechv2.dataclasses.MessageItems
import com.example.coretechv2.dataclasses.SnackBarItems
import com.example.coretechv2.dataclasses.VisField
import com.example.coretechv2.dataclasses.VisSettings
import com.example.coretechv2.dataclasses.ViscosityItem
import com.example.coretechv2.dataclasses.visHasValue
import com.example.coretechv2.repository.APICall
import com.example.coretechv2.repository.DataStoreManager
import com.example.coretechv2.viewmodel.SharedViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ViscosityViewModel(private val dataStoreManager: DataStoreManager, var sharedViewModel: SharedViewModel) : ViewModel() {
    private val apiCall = APICall(dataStoreManager)
    var visReading = mutableStateOf(ViscosityItem())
    var visTestNumberCount by mutableStateOf("")
    var showSpindleList by mutableStateOf(false)
        private set
    var showindexList by mutableStateOf(false)
        private set
    var showtestNumber by mutableStateOf(false)
        private set
    var saveType by mutableStateOf(APICallTypes.INSERT)
        private set
    var popupMessage = MessageItems()
    var snackBarDetails = SnackBarItems()
    var closePopupMessage = mutableStateOf(false)
        private set
    var closeTestScreen = mutableStateOf(false)
        private set
    var openPopupMessage = mutableStateOf(false)
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
    fun onTestSave(){
        var ratio = 0.0
        if (visReading.value.indexRange == "6/60"){
            ratio = (visReading.value.vis06.toDoubleOrNull() ?: 0.0) / (visReading.value.vis60.toDoubleOrNull() ?: 0.0)
        }
        if (visReading.value.indexRange == "3/30"){
            ratio = (visReading.value.vis03.toDoubleOrNull() ?: 0.0) / (visReading.value.vis30.toDoubleOrNull() ?: 0.0)
        }
        if (visReading.value.indexRange == "0.6/6"){
            ratio = (visReading.value.vis0_6.toDoubleOrNull() ?: 0.0) / (visReading.value.vis06.toDoubleOrNull() ?: 0.0)
        }
        if (visReading.value.indexRange == "0.3/3"){
            ratio = (visReading.value.vis0_3.toDoubleOrNull() ?: 0.0) / (visReading.value.vis03.toDoubleOrNull() ?: 0.0)
        }
        val indexResult = kotlin.math.round(ratio * 10 * 10) / 10
        viewModelScope.launch {
            var call = ""
            if(saveType == APICallTypes.INSERT){
                call = "INSERT INTO OSTDEF_VISCOSITY_TESTS " +
                        "(ITEMCODE, ORDERNUMBER, ITEMDESCRIPTION, TESTNO, SPINDLE, INDEXREADING, READING60, READING30, READING12, READING6, READING3, READING1_5, READING0_6, READING0_3, SYSUSERCREATED, SYSUSERMODIFIED) " +
                        "VALUES('${sharedViewModel.currentAssemblyHeader?.ITEMCODE}', '${sharedViewModel.currentAssemblyHeader?.ORDERNUMBER}', '${sharedViewModel.currentAssemblyHeader?.ITEMDESCRIPTION}', ${visReading.value.testNumber}, '${visReading.value.spindle}', ${indexResult}, " +
                        "${visReading.value.vis60.toDoubleOrNull() ?: 0.0}, ${visReading.value.vis30.toDoubleOrNull() ?: 0.0}, ${visReading.value.vis12.toDoubleOrNull() ?: 0.0}, ${visReading.value.vis06.toDoubleOrNull() ?: 0.0}, " +
                        "${visReading.value.vis03.toDoubleOrNull() ?: 0.0}, ${visReading.value.vis1_5.toDoubleOrNull() ?: 0.0}, ${visReading.value.vis0_6.toDoubleOrNull() ?: 0.0},${visReading.value.vis0_3.toDoubleOrNull() ?: 0.0}, " +
                        "'${sharedViewModel.currentUser.value}', '${sharedViewModel.currentUser.value}')"
            }
            if(saveType == APICallTypes.UPDATE){
                call = "UPDATE OSTDEF_VISCOSITY_TESTS SET  " +
                        "TESTNO = ${visReading.value.testNumber}," +
                        "SPINDLE = '${visReading.value.spindle}'," +
                        "INDEXREADING = ${indexResult}," +
                        "READING60 = ${visReading.value.vis60.toDoubleOrNull() ?: 0.0}," +
                        "READING30 = ${visReading.value.vis30.toDoubleOrNull() ?: 0.0}," +
                        "READING12 = ${visReading.value.vis12.toDoubleOrNull() ?: 0.0}," +
                        "READING6 = ${visReading.value.vis06.toDoubleOrNull() ?: 0.0}," +
                        "READING3 = ${visReading.value.vis03.toDoubleOrNull() ?: 0.0}," +
                        "READING1_5 = ${visReading.value.vis1_5.toDoubleOrNull() ?: 0.0}," +
                        "READING0_6 = ${visReading.value.vis0_6.toDoubleOrNull() ?: 0.0}," +
                        "READING0_3 = ${visReading.value.vis0_3.toDoubleOrNull() ?: 0.0}," +
                        "SYSUSERMODIFIED = '${sharedViewModel.currentUser.value}'"
            }

            Log.d("Test Save","Call =" + call)
            val response = apiCall.insertUpdateDelete(call)

            Log.d("Test Save","response =" + response)
            if(response == "200 OK"){
                Log.d("Test Save","If = True")
                closeTestScreen.value = true
                sharedViewModel.snackBarMessage("Viscosity Saved successfully")
            } else{
                Log.d("Test Save","Else = True")
                sharedViewModel.snackBarMessage("Error Saving, Please Try Again")
            }
        }
    }
    fun onClear(){
        visReading.value = ViscosityItem().apply {
            visReading.value.spindle = sharedViewModel.currentAssemblyHeader?.ADDITIONALFIELD_3 ?: "N/A"
            visReading.value.indexRange = sharedViewModel.currentAssemblyHeader?.ADDITIONALFIELD_8 ?: "N/A"
        }
        viewModelScope.launch {
            val viscosityLinesCall : List<APICallTables.Count>? = apiCall.query("SELECT COUNT(*) FROM (SELECT DISTINCT TESTNO FROM OSTDEF_VISCOSITY_TESTS WHERE OrderNumber = '${sharedViewModel.currentOrderNumber.value}')")
            visReading.value = visReading.value.copy(testNumber = (viscosityLinesCall?.first()) + 1)
        }
    }

    fun onCancel(){
        if (visHasValue(visReading)){
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