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
 * ViewModel responsible for managing elongational break test operations.
 *
 * This ViewModel manages the test number, number of days since the test
 * specimen was set, elongation percentage, test record identification,
 * popup state, and saving or loading elongational break test results.
 *
 * @property dataStoreManager Provides access to persisted application settings
 * and configuration required by the API layer.
 * @property sharedViewModel Provides shared application state, including the
 * current assembly, current order number, current user, and save mode.
 */
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
     * Resets the message popup close state.
     */
    fun closePopupMessage() {
        closePopupMessage.value = false
    }

    /**
     * Resets the state used to close the elongational break test screen.
     */
    fun closeTestScreen() {
        closeTestScreen.value = false
    }

    /**
     * Resets the message popup open state.
     *
     * This method currently sets the value to `false`, allowing the popup-open
     * event to be consumed or reset by the calling UI.
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
     * Updates the number of days since the test specimen was set.
     *
     * @param newValue The new number of days entered by the user.
     */
    fun onDaysSinceSet(newValue: String) {
        daysSinceSet = newValue
    }

    /**
     * Updates the measured elongation percentage.
     *
     * @param newValue The new elongation value entered by the user.
     */
    fun onElongationChange(newValue: String) {
        elongation = newValue
    }

    /**
     * Saves the current elongational break test to the database.
     *
     * When the shared save type is [APICallTypes.INSERT], a new test record
     * is inserted into the elongational test table using information from
     * the current assembly.
     *
     * When the shared save type is [APICallTypes.UPDATE], the existing record
     * identified by [testID] is updated.
     *
     * On a successful database operation, the test screen is closed and a
     * success snackbar message is displayed. If the operation fails, an
     * error snackbar message is displayed instead.
     */
    fun onSave() {
        viewModelScope.launch {
            var call = ""
            if (sharedViewModel.saveType == APICallTypes.INSERT) {
                call = "INSERT INTO OSTDEF_ELONGATIONAL_TEST " +
                        "(ITEMCODE, ORDERNUMBER, ITEMDESCRIPTION, TESTNO, DAYSSET, ELONGATIONPERCENT, SYSUSERCREATED, SYSUSERMODIFIED) " +
                        "VALUES('${sharedViewModel.currentAssemblyHeader?.ITEMCODE}', '${sharedViewModel.currentAssemblyHeader?.ORDERNUMBER}', '${sharedViewModel.currentAssemblyHeader?.ITEMDESCRIPTION}', $testNumber, $daysSinceSet, $elongation, " +
                        "'${sharedViewModel.currentUser.value}', '${sharedViewModel.currentUser.value}')"
            }
            if (sharedViewModel.saveType == APICallTypes.UPDATE) {
                call = "UPDATE OSTDEF_ELONGATIONAL_TEST SET " +
                        "TESTNO = $testNumber, " +
                        "DAYSSET = $daysSinceSet, " +
                        "ELONGATIONPERCENT = $elongation, " +
                        "SYSUSERMODIFIED = '${sharedViewModel.currentUser.value}' " +
                        "WHERE SYSUNIQUEID = $testID"
            }


            val response = apiCall.insertUpdateDelete(call)

            if (response == "200 OK") {
                closeTestScreen.value = true
                sharedViewModel.snackBarMessage("Elongation Saved successfully")
            } else {
                sharedViewModel.snackBarMessage("Error Saving, Please Try Again")
            }
        }
    }

    /**
     * Loads an existing elongational break test or prepares the ViewModel
     * for creating a new test.
     *
     * When an existing test is supplied, its stored test number, days since
     * set, elongation percentage, and database identifier are loaded into
     * the ViewModel.
     *
     * When no test is supplied, the next available test number is calculated
     * from the number of distinct tests associated with the current order,
     * and the test result fields are cleared.
     *
     * @param test The existing elongational break test to load, or `null`
     * when creating a new test.
     */
    fun onClear(test: APICallTables.ElongationalBreakTest?) {
        if (test != null) {
            testNumber = test.TESTNO.toString()
            daysSinceSet = test.DAYSSET.toString()
            elongation = test.ELONGATIONPERCENT.toString()
            testID = test.SYSUNIQUEID.toString()
        } else {
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
     * Cancels the current elongational break test operation.
     *
     * When editing an existing test, the current values are compared with
     * the original values. If changes have been made, a confirmation popup
     * is displayed before leaving the screen.
     *
     * When creating a new test, a confirmation popup is displayed if either
     * the days-since-set or elongation fields contain data.
     *
     * If no unsaved changes exist, the test screen is closed immediately.
     *
     * @param test The existing elongational break test being edited, or
     * `null` when creating a new test.
     */
    fun onCancel(test: APICallTables.ElongationalBreakTest?) {
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
        } else if (daysSinceSet != "" || elongation != "") {
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