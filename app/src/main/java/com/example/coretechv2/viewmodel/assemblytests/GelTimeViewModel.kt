package com.example.coretechv2.viewmodel.assemblytests

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coretechv2.dataclasses.APICallTables
import com.example.coretechv2.dataclasses.APICallTypes
import com.example.coretechv2.dataclasses.assemblytests.GelField
import com.example.coretechv2.dataclasses.assemblytests.GelTimeItem
import com.example.coretechv2.dataclasses.MessageItems
import com.example.coretechv2.dataclasses.assemblytests.gelHasValue
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
class GelTimeViewModel(private val dataStoreManager: DataStoreManager, var sharedViewModel: SharedViewModel) : ViewModel() {
    private val apiCall = APICall(dataStoreManager)
    var popupMessage = MessageItems()
    var closePopupMessage = mutableStateOf(false)
        private set
    var closeTestScreen = mutableStateOf(false)
        private set
    var openPopupMessage = mutableStateOf(false)
        private set
    var gelReading = mutableStateOf(GelTimeItem())
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
            val gelTimeFormatted = toTimeFormatHMMSS(gelReading.value.hour, gelReading.value.minute, gelReading.value.second)
            Log.d("GelTime", "Formated = " + gelTimeFormatted + ", hour = "+ gelReading.value.hour+ ", minute = "+ gelReading.value.minute+ ", second = " + gelReading.value.second)
            var call = ""
            if(sharedViewModel.saveType == APICallTypes.INSERT){
                call = "INSERT INTO OSTDEF_GELTIME_TESTS " +
                        "(ITEMCODE, ORDERNUMBER, ITEMDESCRIPTION, TESTNO, GELTIME, GELCAT, GELCATPERCENT, SYSUSERCREATED, SYSUSERMODIFIED) " +
                        "VALUES('${sharedViewModel.currentAssemblyHeader?.ITEMCODE}', '${sharedViewModel.currentAssemblyHeader?.ORDERNUMBER}', '${sharedViewModel.currentAssemblyHeader?.ITEMDESCRIPTION}', ${gelReading.value.testNumber}, " +
                        "'${gelTimeFormatted}', '${gelReading.value.catalyst}', '${gelReading.value.catPercent}', '${sharedViewModel.currentUser.value}', '${sharedViewModel.currentUser.value}')"
            }
            if(sharedViewModel.saveType == APICallTypes.UPDATE){
                call = "UPDATE OSTDEF_GELTIME_TESTS SET  " +
                        "TESTNO = ${gelReading.value.testNumber}," +
                        "GELTIME = '${gelTimeFormatted}'," +
                        "GELCAT = '${gelReading.value.catalyst}'," +
                        "GELCATPERCENT = '${gelReading.value.catPercent}'," +
                        "SYSUSERMODIFIED = '${sharedViewModel.currentUser.value}'" +
                        "WHERE SYSUNIQUEID = ${gelReading.value.sysID}"
            }

            val response = apiCall.insertUpdateDelete(call)
            Log.d("API Call", call)
            if(response == "200 OK"){
                closeTestScreen.value = true
                sharedViewModel.snackBarMessage("Gel Time Saved successfully")
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
    fun onClear(test: APICallTables.gelTimeTest?) {
        if (test != null) {
            val (h, m, s) = fromTimeFormatHMMSS(test.GELTIME)

            gelReading.value = GelTimeItem().apply {
                sysID = test.SYSUNIQUEID.toInt()
                catalyst = test.GELCAT
                catPercent = test.GELCATPERCENT
                testNumber = test.TESTNO.toString()
                hour = h
                minute = m
                second = s
            }

        } else {
            gelReading.value = GelTimeItem().apply {
                catalyst = sharedViewModel.currentAssemblyHeader?.ADDITIONALFIELD_1?.uppercase() ?: "N/A"
                catPercent = sharedViewModel.currentAssemblyHeader?.ADDITIONALFIELD_9?.replace("%", "") ?: "N/A"
            }
            viewModelScope.launch {
                val linesCall: List<APICallTables.Count>? = apiCall.query("SELECT COUNT(*) FROM OSTDEF_GELTIME_TESTS where OrderNumber = '${sharedViewModel.currentOrderNumber.value}'")
                val testNumberCount = linesCall?.first()?.COUNT ?: 0
                gelReading.value = gelReading.value.copy(testNumber = (testNumberCount + 1).toString())
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