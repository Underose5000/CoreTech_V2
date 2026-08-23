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
import com.example.coretechv2.dataclasses.assemblydataclasses.VisField
import com.example.coretechv2.dataclasses.assemblydataclasses.VisSettings
import com.example.coretechv2.dataclasses.assemblydataclasses.ViscosityItem
import com.example.coretechv2.dataclasses.assemblydataclasses.visHasValue
import com.example.coretechv2.repository.APICall
import com.example.coretechv2.repository.DataStoreManager
import com.example.coretechv2.repository.fromTimeFormatHMMSS
import com.example.coretechv2.viewmodel.SharedViewModel
import kotlinx.coroutines.launch
import kotlin.math.round

class ElongationalBreakViewModel(private val dataStoreManager: DataStoreManager, var sharedViewModel: SharedViewModel) : ViewModel() {
    private val apiCall = APICall(dataStoreManager)
    var popupMessage = MessageItems()
    var closePopupMessage = mutableStateOf(false)
        private set
    var closeTestScreen = mutableStateOf(false)
        private set
    var openPopupMessage = mutableStateOf(false)
        private set
    var testNumber by mutableStateOf("")
        private set
    var daysSinceSet by mutableStateOf("")
        private set
    var elongation by mutableStateOf("")
        private set
    var testID by mutableStateOf("")
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
    fun onTestNumberChange(newValue: String){
        testNumber = newValue
    }

    fun onDaysSinceSet(newValue: String){
        daysSinceSet = newValue
    }

    fun onElongationChange(newValue: String){
        elongation = newValue
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
    fun onSave(){
        viewModelScope.launch {
            var call = ""
            if(sharedViewModel.saveType == APICallTypes.INSERT){
                call = "INSERT INTO OSTDEF_ELONGATIONAL_TEST " +
                        "(ITEMCODE, ORDERNUMBER, ITEMDESCRIPTION, TESTNO, DAYSSET, ELONGATIONPERCENT, SYSUSERCREATED, SYSUSERMODIFIED) " +
                        "VALUES('${sharedViewModel.currentAssemblyHeader?.ITEMCODE}', '${sharedViewModel.currentAssemblyHeader?.ORDERNUMBER}', '${sharedViewModel.currentAssemblyHeader?.ITEMDESCRIPTION}', $testNumber, $daysSinceSet, $elongation, " +
                        "'${sharedViewModel.currentUser.value}', '${sharedViewModel.currentUser.value}')"
            }
            if(sharedViewModel.saveType == APICallTypes.UPDATE){
                call = "UPDATE OSTDEF_ELONGATIONAL_TEST SET " +
                        "TESTNO = $testNumber, " +
                        "DAYSSET = $daysSinceSet, " +
                        "ELONGATIONPERCENT = $elongation, " +
                        "SYSUSERMODIFIED = '${sharedViewModel.currentUser.value}' " +
                        "WHERE SYSUNIQUEID = $testID"
            }
            

            val response = apiCall.insertUpdateDelete(call)

            if(response == "200 OK"){
                closeTestScreen.value = true
                sharedViewModel.snackBarMessage("Elongation Saved successfully")
            } else{
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
    fun onClear(test: APICallTables.ElongationalBreakTest?){
        if (test != null) {
            testNumber = test.TESTNO.toString()
            daysSinceSet = test.DAYSSET.toString()
            elongation = test.ELONGATIONPERCENT.toString()
            testID = test.SYSUNIQUEID.toString()
        }else {
            viewModelScope.launch {
                val linesCall: List<APICallTables.Count>? =
                    apiCall.query("SELECT COUNT(*) FROM (SELECT DISTINCT TESTNO FROM OSTDEF_ELONGATIONAL_TEST WHERE OrderNumber = '${sharedViewModel.currentOrderNumber.value}')")
                val testNumberCount = linesCall?.first()?.COUNT ?: 0
                testNumber = (testNumberCount + 1).toString()
                daysSinceSet = ""
                elongation = ""
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
    fun onCancel(test: APICallTables.ElongationalBreakTest?){
        if (test != null) {
            if (testNumber != test.TESTNO.toString() ||
                daysSinceSet != test.DAYSSET.toString() ||
                elongation != test.ELONGATIONPERCENT.toString()
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
        } else if (daysSinceSet != "" || elongation != ""){
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