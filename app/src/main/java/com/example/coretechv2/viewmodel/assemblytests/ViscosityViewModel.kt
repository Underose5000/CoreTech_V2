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
import com.example.coretechv2.dataclasses.assemblytests.VisField
import com.example.coretechv2.dataclasses.assemblytests.VisSettings
import com.example.coretechv2.dataclasses.assemblytests.ViscosityItem
import com.example.coretechv2.dataclasses.assemblytests.visHasValue
import com.example.coretechv2.repository.APICall
import com.example.coretechv2.repository.DataStoreManager
import com.example.coretechv2.viewmodel.SharedViewModel
import kotlinx.coroutines.launch

class ViscosityViewModel(private val dataStoreManager: DataStoreManager, var sharedViewModel: SharedViewModel) : ViewModel() {
    private val apiCall = APICall(dataStoreManager)
    var visReading = mutableStateOf(ViscosityItem())
    var showSpindleList by mutableStateOf(false)
        private set
    var showindexList by mutableStateOf(false)
        private set
    var showtestNumber by mutableStateOf(false)
        private set
    var popupMessage = MessageItems()
    var closePopupMessage = mutableStateOf(false)
        private set
    var closeTestScreen = mutableStateOf(false)
        private set
    var openPopupMessage = mutableStateOf(false)
        private set


    /**
     * Closes the popup message dialog by resetting its state.
     */
    fun closePopupMessage(){
        closePopupMessage.value = false
    }

    /**
     * Closes the viscosity test screen and triggers navigation back.
     */
    fun closeTestScreen(){
        closeTestScreen.value = false
    }

    /**
     * Resets the popup message trigger flag.
     */
    fun openPopupMessage(){
        openPopupMessage.value = false
    }

    /**
     * Toggles the spindle dropdown visibility.
     */
    fun spindlePressed(){
        showSpindleList = !showSpindleList
    }

    /**
     * Toggles the index range dropdown visibility.
     */
    fun indexPressed(){
        showindexList = !showindexList
    }

    /**
     * Updates the selected value for a dropdown field and hides the dropdown.
     *
     * @param newValue The newly selected value from the dropdown.
     * @param field The dropdown field being updated (SPINDLE, INDEX, TESTNUMBER).
     */
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

    /**
     * Updates a viscosity reading field based on user input.
     *
     * @param newValue The numeric value entered by the user.
     * @param field The viscosity field being updated (e.g. VIS60, VIS30, etc.).
     */
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

    /**
     * Saves the current viscosity test to the database.
     *
     * This function:
     * - Calculates the viscosity index ratio based on selected range
     * - Builds an INSERT or UPDATE SQL query
     * - Sends the query to the backend API
     * - Handles success or failure responses
     *
     * On success:
     * - Closes the test screen
     * - Shows success snackbar message
     *
     * On failure:
     * - Shows error snackbar message
     */
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
            if(sharedViewModel.saveType == APICallTypes.INSERT){
                call = "INSERT INTO OSTDEF_VISCOSITY_TESTS " +
                        "(ITEMCODE, ORDERNUMBER, ITEMDESCRIPTION, TESTNO, SPINDLE, INDEXREADING, READING60, READING30, READING12, READING6, READING3, READING1_5, READING0_6, READING0_3, SYSUSERCREATED, SYSUSERMODIFIED) " +
                        "VALUES('${sharedViewModel.currentAssemblyHeader?.ITEMCODE}', '${sharedViewModel.currentAssemblyHeader?.ORDERNUMBER}', '${sharedViewModel.currentAssemblyHeader?.ITEMDESCRIPTION}', ${visReading.value.testNumber}, '${visReading.value.spindle}', ${indexResult}, " +
                        "${visReading.value.vis60.toDoubleOrNull() ?: 0.0}, ${visReading.value.vis30.toDoubleOrNull() ?: 0.0}, ${visReading.value.vis12.toDoubleOrNull() ?: 0.0}, ${visReading.value.vis06.toDoubleOrNull() ?: 0.0}, " +
                        "${visReading.value.vis03.toDoubleOrNull() ?: 0.0}, ${visReading.value.vis1_5.toDoubleOrNull() ?: 0.0}, ${visReading.value.vis0_6.toDoubleOrNull() ?: 0.0},${visReading.value.vis0_3.toDoubleOrNull() ?: 0.0}, " +
                        "'${sharedViewModel.currentUser.value}', '${sharedViewModel.currentUser.value}')"
            }
            if(sharedViewModel.saveType == APICallTypes.UPDATE){
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
                        "SYSUSERMODIFIED = '${sharedViewModel.currentUser.value}'" +
                        "WHERE SYSUNIQUEID = ${visReading.value.sysID}"
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

    /**
     * Loads an existing viscosity test into the UI or initializes a new test.
     *
     * If a test is provided:
     * - Maps database values into UI state
     * - Calculates correct index range from stored values
     *
     * If no test is provided:
     * - Initializes default values from shared assembly header
     * - Fetches next available test number from the database
     */
    fun onClear(test: APICallTables.viscosityTest?){
        if (test != null) {
            var ratio = "N/A"
            if (kotlin.math.round((test.READING6/test.READING60) * 10 * 10) / 10 == test.INDEXREADING){ ratio = "6/60"}
            if (kotlin.math.round((test.READING3/test.READING30) * 10 * 10) / 10 == test.INDEXREADING){ ratio = "3/30"}
            if (kotlin.math.round((test.READING0_6/test.READING6) * 10 * 10) / 10 == test.INDEXREADING){ ratio = "0.6/6"}
            if (kotlin.math.round((test.READING0_3/test.READING3) * 10 * 10) / 10 == test.INDEXREADING){ ratio = "0.3/3"}
            visReading.value = ViscosityItem().apply {
                sysID = test.SYSUNIQUEID.toInt()
                spindle = test.SPINDLE
                indexRange = ratio
                testNumber = test.TESTNO.toString()
                vis60 = test.READING60.toString()
                vis30 = test.READING30.toString()
                vis12 = test.READING12.toString()
                vis06 = test.READING6.toString()
                vis03 = test.READING3.toString()
                vis1_5 = test.READING1_5.toString()
                vis0_6 = test.READING0_6.toString()
                vis0_3 = test.READING0_3.toString()
            }
        }else {
            visReading.value = ViscosityItem().apply {
                spindle = sharedViewModel.currentAssemblyHeader?.ADDITIONALFIELD_3 ?: "N/A"
                indexRange = sharedViewModel.currentAssemblyHeader?.ADDITIONALFIELD_8 ?: "N/A"
            }
            viewModelScope.launch {
                val linesCall: List<APICallTables.Count>? =
                    apiCall.query("SELECT COUNT(*) FROM (SELECT DISTINCT TESTNO FROM OSTDEF_VISCOSITY_TESTS WHERE OrderNumber = '${sharedViewModel.currentOrderNumber.value}')")
                val testNumberCount = linesCall?.first()?.COUNT ?: 0
                visReading.value = visReading.value.copy(testNumber = (testNumberCount + 1).toString())
            }
        }
    }

    /**
     * Handles cancellation of the viscosity test screen.
     *
     * If the current test contains data:
     * - Shows a confirmation popup before leaving
     *
     * If no data exists:
     * - Immediately closes the test screen
     */
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