package com.example.coretechv2.viewmodel.assemblytests

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coretechv2.dataclasses.APICallTables
import com.example.coretechv2.dataclasses.APICallTypes
import com.example.coretechv2.dataclasses.assemblydataclasses.GelField
import com.example.coretechv2.dataclasses.assemblydataclasses.GelTimeItem
import com.example.coretechv2.dataclasses.MessageItems
import com.example.coretechv2.dataclasses.assemblydataclasses.PeakExothermField
import com.example.coretechv2.dataclasses.assemblydataclasses.PeakExothermItem
import com.example.coretechv2.dataclasses.assemblydataclasses.gelHasValue
import com.example.coretechv2.dataclasses.assemblydataclasses.peakExoHasValue
import com.example.coretechv2.repository.APICall
import com.example.coretechv2.repository.DataStoreManager
import com.example.coretechv2.repository.fromTimeFormatHMMSS
import com.example.coretechv2.repository.toTimeFormatHMMSS
import com.example.coretechv2.viewmodel.SharedViewModel
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing Gel Time Test data and UI state.
 *
 * This ViewModel handles:
 * - User input for gel time readings (hours, minutes, seconds, catalyst, etc.)
 * - Loading existing gel test data or initializing new tests
 * - Saving gel time tests via API (INSERT / UPDATE)
 * - Converting time formats for database storage
 * - Managing UI state such as dropdowns and popup dialogs
 * - Handling navigation flow for test screen lifecycle
 *
 * It interacts with:
 * - [APICall] for database communication
 * - [SharedViewModel] for shared app state (order, user, assembly header)
 * - Utility functions for time formatting (HMMSS conversion)
 */
class PeakExothermViewModel(private val dataStoreManager: DataStoreManager, var sharedViewModel: SharedViewModel) : ViewModel() {
    private val apiCall = APICall(dataStoreManager)
    var popupMessage = MessageItems()
    var closePopupMessage = mutableStateOf(false)
        private set
    var closeTestScreen = mutableStateOf(false)
        private set
    var openPopupMessage = mutableStateOf(false)
        private set
    var peakReading = mutableStateOf(PeakExothermItem())
    var showcatalystList by mutableStateOf(false)
        private set

    /**
     * Resets the popup message visibility state.
     */
    fun closePopupMessage(){
        closePopupMessage.value = false
    }

    /**
     * Closes the gel test screen and triggers navigation back.
     */
    fun closeTestScreen(){
        closeTestScreen.value = false
    }

    /**
     * Resets the popup trigger state.
     */
    fun openPopupMessage(){
        openPopupMessage.value = false
    }

    /**
     * Toggles the catalyst dropdown visibility.
     */
    fun catalystPressed(){
        showcatalystList = !showcatalystList
    }

    /**
     * Updates a gel time field based on user input.
     *
     * @param newValue The new value entered by the user.
     * @param field The gel field being updated (CATPERCENT, CATALYST, TESTNUMBER, TIME fields).
     */
    fun onGelChange(newValue: String, field : PeakExothermField){
        peakReading.value = when (field) {
            PeakExothermField.CATPERCENT -> peakReading.value.copy(catPercent = newValue)
            PeakExothermField.CATALYST -> peakReading.value.copy(catalyst = newValue)
            PeakExothermField.TESTNUMBER -> peakReading.value.copy(testNumber = newValue)
            PeakExothermField.HOUR -> peakReading.value.copy(hour = newValue)
            PeakExothermField.MINUTE -> peakReading.value.copy(minute = newValue)
            PeakExothermField.SECOND -> peakReading.value.copy(second = newValue)
            PeakExothermField.TEMPERATURE -> peakReading.value.copy(temperature = newValue)
        }
        if (field == PeakExothermField.CATALYST){
            showcatalystList = false
        }
    }

    /**
     * Saves the current Gel Time test to the database.
     *
     * This function:
     * - Converts hour/minute/second into HMMSS format
     * - Builds SQL INSERT or UPDATE query depending on save type
     * - Sends query to backend API
     * - Handles success or failure response
     *
     * On success:
     * - Closes test screen
     * - Shows success snackbar message
     *
     * On failure:
     * - Shows error snackbar message
     */
    fun onSave(){
        viewModelScope.launch {
            val peakTimeFormatted = toTimeFormatHMMSS(peakReading.value.hour, peakReading.value.minute, peakReading.value.second)
            var call = ""
            if(sharedViewModel.saveType == APICallTypes.INSERT){
                call = "INSERT INTO OSTDEF_PEAKEXOTHERM_TEST " +
                        "(ITEMCODE, ORDERNUMBER, ITEMDESCRIPTION, TESTNO, GELTIME, GELCAT, GELCATPERCENT, PEAKTEMPERATURE, SYSUSERCREATED, SYSUSERMODIFIED) " +
                        "VALUES('${sharedViewModel.currentAssemblyHeader?.ITEMCODE}', '${sharedViewModel.currentAssemblyHeader?.ORDERNUMBER}', '${sharedViewModel.currentAssemblyHeader?.ITEMDESCRIPTION}', ${peakReading.value.testNumber}, " +
                        "'${peakTimeFormatted}', '${peakReading.value.catalyst}', '${peakReading.value.catPercent}', ${peakReading.value.temperature}, '${sharedViewModel.currentUser.value}', '${sharedViewModel.currentUser.value}')"
            }
            if(sharedViewModel.saveType == APICallTypes.UPDATE){
                call = "UPDATE OSTDEF_PEAKEXOTHERM_TEST SET " +
                        "TESTNO = ${peakReading.value.testNumber}, " +
                        "GELTIME = '${peakTimeFormatted}', " +
                        "GELCAT = '${peakReading.value.catalyst}', " +
                        "GELCATPERCENT = '${peakReading.value.catPercent}', " +
                        "PEAKTEMPERATURE = ${peakReading.value.temperature}, " +
                        "SYSUSERMODIFIED = '${sharedViewModel.currentUser.value}' " +
                        "WHERE SYSUNIQUEID = ${peakReading.value.sysID}"
            }

            
            val response = apiCall.insertUpdateDelete(call)
            if(response == "200 OK"){
                closeTestScreen.value = true
                sharedViewModel.snackBarMessage("Peak Exotherm Saved successfully")
            } else{
                sharedViewModel.snackBarMessage("Error Saving, Please Try Again")
            }
        }
    }

    /**
     * Loads an existing gel time test into the UI or initializes a new one.
     *
     * If a test is provided:
     * - Maps database values into UI state
     * - Converts GELTIME into hour/minute/second format
     *
     * If no test is provided:
     * - Initializes default values from assembly header
     * - Retrieves next test number from database
     */
    fun onClear(test: APICallTables.peakExothermTest?) {
        if (test != null) {
            val (h, m, s) = fromTimeFormatHMMSS(test.GELTIME)

            peakReading.value = PeakExothermItem().apply {
                sysID = test.SYSUNIQUEID.toInt()
                catalyst = test.GELCAT
                catPercent = test.GELCATPERCENT
                testNumber = test.TESTNO.toString()
                temperature = test.PEAKTEMPERATURE.toString()
                hour = h
                minute = m
                second = s
            }

        } else {
            peakReading.value = PeakExothermItem().apply {
                catalyst = sharedViewModel.currentAssemblyHeader?.ADDITIONALFIELD_1?.uppercase() ?: "N/A"
                catPercent = sharedViewModel.currentAssemblyHeader?.ADDITIONALFIELD_9?.replace("%", "") ?: "N/A"
            }
            viewModelScope.launch {
                val linesCall: List<APICallTables.Count>? = apiCall.query("SELECT COUNT(*) FROM (SELECT DISTINCT TESTNO FROM OSTDEF_PEAKEXOTHERM_TEST where OrderNumber = '${sharedViewModel.currentOrderNumber.value}'")
                val testNumberCount = linesCall?.first()?.COUNT ?: 0
                peakReading.value = peakReading.value.copy(testNumber = (testNumberCount + 1).toString())
            }
        }
    }


    /**
     * Handles user cancellation of the gel test screen.
     *
     * If the test contains data:
     * - Shows confirmation popup before leaving without saving
     *
     * If no data exists:
     * - Immediately closes the test screen
     */
    fun onCancel(test: APICallTables.peakExothermTest?){
        if (test != null) {
            val (h, m, s) = fromTimeFormatHMMSS(test.GELTIME)
            if (peakReading.value.testNumber != test.TESTNO.toString() ||
                peakReading.value.catalyst != test.GELCAT ||
                peakReading.value.catPercent != test.GELCATPERCENT ||
                peakReading.value.hour != h ||
                peakReading.value.minute != m ||
                peakReading.value.second != s ||
                peakReading.value.temperature != test.PEAKTEMPERATURE.toString()
            ) {
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
        } else if (peakExoHasValue(peakReading)){
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