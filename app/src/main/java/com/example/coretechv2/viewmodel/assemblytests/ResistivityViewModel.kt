package com.example.coretechv2.viewmodel.assemblytests

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coretechv2.dataclasses.APICallTables
import com.example.coretechv2.dataclasses.APICallTypes
import com.example.coretechv2.dataclasses.MessageItems
import com.example.coretechv2.repository.APICall
import com.example.coretechv2.repository.DataStoreManager
import com.example.coretechv2.viewmodel.SharedViewModel
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing resistivity test data and UI state.
 *
 * This ViewModel handles:
 * - Managing resistivity test input fields.
 * - Managing the test number and days since the sample was set.
 * - Loading existing resistivity test results.
 * - Initialising new resistivity tests and determining the next test number.
 * - Saving resistivity test results using INSERT or UPDATE operations.
 * - Managing popup dialog state for unsaved changes.
 * - Handling navigation flow for the test screen lifecycle.
 *
 * It interacts with:
 * - [APICall] for communication with the database through the API.
 * - [SharedViewModel] for shared application state such as the current
 *   assembly order, user, and save type.
 */
class ResistivityViewModel(private val dataStoreManager: DataStoreManager, var sharedViewModel: SharedViewModel) : ViewModel() {
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
    var resistivity by mutableStateOf("")
        private set
    var testID by mutableStateOf("")
        private set


    /**
     * Resets the popup message close state.
     *
     * Setting this value to `false` allows the popup close event to be
     * consumed by the UI.
     */
    fun closePopupMessage() {
        closePopupMessage.value = false
    }

    /**
     * Resets the test screen close state.
     *
     * Setting this value to `false` allows the navigation close event to be
     * consumed by the UI.
     */
    fun closeTestScreen() {
        closeTestScreen.value = false
    }

    /**
     * Resets the popup message trigger state.
     *
     * This method currently sets the state to `false`, allowing the popup
     * event to be reset after it has been handled by the UI.
     */
    fun openPopupMessage() {
        openPopupMessage.value = false
    }

    /**
     * Updates the current test number.
     *
     * @param newValue The new test number entered by the user.
     */
    fun onTestNumberChange(newValue: String) {
        testNumber = newValue
    }

    /**
     * Updates the number of days since the sample was set.
     *
     * @param newValue The new days-since-set value entered by the user.
     */
    fun onDaysSinceSet(newValue: String) {
        daysSinceSet = newValue
    }

    /**
     * Updates the current resistivity measurement.
     *
     * @param newValue The new resistivity value entered by the user.
     */
    fun onElongationChange(newValue: String) {
        resistivity = newValue
    }

    /**
     * Saves the current resistivity test results.
     *
     * Depending on [SharedViewModel.saveType], this method either:
     * - Inserts a new record into `OSTDEF_RESISTIVITY_TEST`.
     * - Updates an existing record in `OSTDEF_RESISTIVITY_TEST`.
     *
     * The saved record contains the test number, days since set,
     * resistivity measurement, assembly information, and user information.
     *
     * On successful completion, the test screen is closed and a success
     * snackbar message is displayed. If the database operation fails,
     * an error snackbar message is displayed instead.
     */
    fun onSave() {
        viewModelScope.launch {
            var call = ""
            if (sharedViewModel.saveType == APICallTypes.INSERT) {
                call = "INSERT INTO OSTDEF_RESISTIVITY_TEST " +
                        "(ITEMCODE, ORDERNUMBER, ITEMDESCRIPTION, TESTNO, DAYSSET, RESISTIVITYOHM, SYSUSERCREATED, SYSUSERMODIFIED) " +
                        "VALUES('${sharedViewModel.currentAssemblyHeader?.ITEMCODE}', '${sharedViewModel.currentAssemblyHeader?.ORDERNUMBER}', '${sharedViewModel.currentAssemblyHeader?.ITEMDESCRIPTION}', $testNumber, $daysSinceSet, $resistivity, " +
                        "'${sharedViewModel.currentUser.value}', '${sharedViewModel.currentUser.value}')"
            }
            if (sharedViewModel.saveType == APICallTypes.UPDATE) {
                call = "UPDATE OSTDEF_RESISTIVITY_TEST SET " +
                        "TESTNO = $testNumber," +
                        "DAYSSET = $daysSinceSet," +
                        "RESISTIVITYOHM = $resistivity," +
                        "SYSUSERMODIFIED = '${sharedViewModel.currentUser.value}'" +
                        "WHERE SYSUNIQUEID = $testID"
            }


            val response = apiCall.insertUpdateDelete(call)

            if (response == "200 OK") {
                closeTestScreen.value = true
                sharedViewModel.snackBarMessage("Resistivity Saved successfully")
            } else {
                sharedViewModel.snackBarMessage("Error Saving, Please Try Again")
            }
        }
    }

    /**
     * Loads an existing resistivity test or initialises a new test.
     *
     * When [test] is provided, its database values are copied into the
     * ViewModel state for editing.
     *
     * When [test] is `null`, the input values are cleared and the next
     * available test number is determined by counting the distinct test
     * numbers already recorded for the current assembly order.
     *
     * @param test The existing resistivity test to load, or `null` when
     * creating a new test.
     */
    fun onClear(test: APICallTables.ResistivityTest?) {
        if (test != null) {
            testNumber = test.TESTNO.toString()
            daysSinceSet = test.DAYSSET.toString()
            resistivity = test.RESISTIVITYOHM.toString()
            testID = test.SYSUNIQUEID.toString()
        } else {
            viewModelScope.launch {
                val linesCall: List<APICallTables.Count>? =
                    apiCall.query("SELECT COUNT(*) FROM (SELECT DISTINCT TESTNO FROM OSTDEF_RESISTIVITY_TEST WHERE OrderNumber = '${sharedViewModel.currentOrderNumber.value}')")
                val testNumberCount = linesCall?.first()?.COUNT ?: 0
                testNumber = (testNumberCount + 1).toString()
                daysSinceSet = ""
                resistivity = ""
            }
        }
    }

    /**
     * Handles cancellation of the resistivity test screen.
     *
     * When editing an existing test, the current values are compared with
     * the original database values. If any values have changed, a
     * confirmation popup is displayed before leaving without saving.
     *
     * When creating a new test, the entered days-since-set and resistivity
     * values are checked. If either contains data, the user is prompted to
     * confirm leaving without saving. Otherwise, the test screen is closed
     * immediately.
     *
     * @param test The existing resistivity test being edited, or `null`
     * when creating a new test.
     */
    fun onCancel(test: APICallTables.ResistivityTest?) {
        if (test != null) {
            if (testNumber != test.TESTNO.toString() ||
                daysSinceSet != test.DAYSSET.toString() ||
                resistivity != test.RESISTIVITYOHM.toString()
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
        } else if (daysSinceSet != "" || resistivity != "") {
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