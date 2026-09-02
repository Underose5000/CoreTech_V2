package com.example.coretechv2.viewmodel.assemblytests

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coretechv2.dataclasses.APICallTables
import com.example.coretechv2.dataclasses.APICallTypes
import com.example.coretechv2.dataclasses.MessageItems
import com.example.coretechv2.dataclasses.assemblydataclasses.FlammabilityField
import com.example.coretechv2.dataclasses.assemblydataclasses.FlammabilityItem
import com.example.coretechv2.dataclasses.assemblydataclasses.flameHasValue
import com.example.coretechv2.repository.APICall
import com.example.coretechv2.repository.DataStoreManager
import com.example.coretechv2.repository.fromTimeFormatHMMSS
import com.example.coretechv2.repository.toTimeFormatHMMSS
import com.example.coretechv2.viewmodel.SharedViewModel
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing flammability test data and UI state.
 *
 * This ViewModel handles:
 * - Managing flammability test input fields.
 * - Tracking test number, days since set, burn length, and flame time.
 * - Loading existing flammability test results.
 * - Initialising new flammability tests and determining the next test number.
 * - Converting flame time between individual hour, minute, and second fields
 *   and the database HMMSS format.
 * - Saving test results using INSERT or UPDATE operations.
 * - Managing popup dialog and test screen navigation state.
 *
 * It interacts with:
 * - [APICall] for communication with the database through the API.
 * - [SharedViewModel] for shared application state such as the current
 *   assembly order, user, and save type.
 * - Flammability data classes for representing test input and field types.
 * - Time formatting utilities for converting flame time between UI and
 *   database formats.
 */
class FlammabilityViewModel(private val dataStoreManager: DataStoreManager, var sharedViewModel: SharedViewModel) : ViewModel() {
    private val apiCall = APICall(dataStoreManager)
    var popupMessage = MessageItems()
    var closePopupMessage = mutableStateOf(false)
        private set
    var closeTestScreen = mutableStateOf(false)
        private set
    var openPopupMessage = mutableStateOf(false)
        private set
    var flameReading = mutableStateOf(FlammabilityItem())
    var showcatalystList by mutableStateOf(false)
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
     * Setting this value to `false` allows the navigation close event to
     * be consumed by the UI.
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
     * Updates a flammability test field with a new user-entered value.
     *
     * The supplied [field] determines which property of [flameReading]
     * is updated.
     *
     * @param newValue The new value entered by the user.
     * @param field The flammability test field being modified.
     */
    fun onFlameChange(newValue: String, field: FlammabilityField) {
        flameReading.value = when (field) {
            FlammabilityField.DAYSSET -> flameReading.value.copy(daysSet = newValue)
            FlammabilityField.BURNLENGTH -> flameReading.value.copy(burnLength = newValue)
            FlammabilityField.TESTNUMBER -> flameReading.value.copy(testNumber = newValue)
            FlammabilityField.HOUR -> flameReading.value.copy(hour = newValue)
            FlammabilityField.MINUTE -> flameReading.value.copy(minute = newValue)
            FlammabilityField.SECOND -> flameReading.value.copy(second = newValue)
        }
    }

    /**
     * Saves the current flammability test results.
     *
     * The flame time entered as separate hour, minute, and second values is
     * first converted into the HMMSS format used by the database.
     *
     * Depending on [SharedViewModel.saveType], this method either:
     * - Inserts a new record into `OSTDEF_FLAMMABILITY_TEST`.
     * - Updates an existing record in `OSTDEF_FLAMMABILITY_TEST`.
     *
     * On successful completion, the test screen is closed and a success
     * snackbar message is displayed. If the database operation fails,
     * an error snackbar message is displayed instead.
     */
    fun onSave() {
        viewModelScope.launch {
            val flameTimeFormatted = toTimeFormatHMMSS(flameReading.value.hour, flameReading.value.minute, flameReading.value.second)
            var call = ""
            if (sharedViewModel.saveType == APICallTypes.INSERT) {
                call = "INSERT INTO OSTDEF_FLAMMABILITY_TEST " +
                        "(ITEMCODE, ORDERNUMBER, ITEMDESCRIPTION, TESTNO, FLAMETIME, DAYSSET, BURNLENGTH, SYSUSERCREATED, SYSUSERMODIFIED) " +
                        "VALUES('${sharedViewModel.currentAssemblyHeader?.ITEMCODE}', '${sharedViewModel.currentAssemblyHeader?.ORDERNUMBER}', '${sharedViewModel.currentAssemblyHeader?.ITEMDESCRIPTION}', ${flameReading.value.testNumber}, " +
                        "'${flameTimeFormatted}', ${flameReading.value.daysSet}, ${flameReading.value.burnLength}, '${sharedViewModel.currentUser.value}', '${sharedViewModel.currentUser.value}')"
            }
            if (sharedViewModel.saveType == APICallTypes.UPDATE) {
                call = "UPDATE OSTDEF_FLAMMABILITY_TEST SET  " +
                        "TESTNO = ${flameReading.value.testNumber}," +
                        "FLAMETIME = '$flameTimeFormatted'," +
                        "DAYSSET = ${flameReading.value.daysSet}," +
                        "BURNLENGTH = ${flameReading.value.burnLength}," +
                        "SYSUSERMODIFIED = '${sharedViewModel.currentUser.value}'" +
                        "WHERE SYSUNIQUEID = ${flameReading.value.sysID}"
            }

            val response = apiCall.insertUpdateDelete(call)
            if (response == "200 OK") {
                closeTestScreen.value = true
                sharedViewModel.snackBarMessage("Flammability Saved successfully")
            } else {
                sharedViewModel.snackBarMessage("Error Saving, Please Try Again")
            }
        }
    }

    /**
     * Loads an existing flammability test or initialises a new test.
     *
     * When [test] is provided, its database values are copied into
     * [flameReading]. The stored flame time is converted from HMMSS format
     * into separate hour, minute, and second values for display in the UI.
     *
     * When [test] is `null`, the input fields are cleared and the next
     * available test number is calculated from the distinct test numbers
     * already recorded for the current assembly order.
     *
     * @param test The existing flammability test to load, or `null` when
     * creating a new test.
     */
    fun onClear(test: APICallTables.FlammabilityTest?) {
        if (test != null) {
            val (h, m, s) = fromTimeFormatHMMSS(test.FLAMETIME)

            flameReading.value = FlammabilityItem().apply {
                sysID = test.SYSUNIQUEID.toInt()
                daysSet = test.DAYSSET.toString()
                burnLength = test.BURNLENGTH.toString()
                testNumber = test.TESTNO.toString()
                hour = h
                minute = m
                second = s
            }

        } else {
            flameReading.value = FlammabilityItem().apply {
                daysSet = ""
                burnLength = ""
                hour = ""
                minute = ""
                second = ""
            }
            viewModelScope.launch {
                val linesCall: List<APICallTables.Count>? =
                    apiCall.query("SELECT COUNT(*) FROM (SELECT DISTINCT TESTNO FROM OSTDEF_FLAMMABILITY_TEST where OrderNumber = '${sharedViewModel.currentOrderNumber.value}')")
                val testNumberCount = linesCall?.first()?.COUNT ?: 0
                flameReading.value = flameReading.value.copy(testNumber = (testNumberCount + 1).toString())
            }
        }
    }


    /**
     * Handles cancellation of the flammability test screen.
     *
     * When editing an existing test, the current values are compared with
     * the original database values. If changes have been made, a
     * confirmation popup is displayed before leaving.
     *
     * When creating a new test, [flameHasValue] determines whether any
     * flammability test values have been entered. If values exist, the user
     * is prompted to confirm leaving without saving. Otherwise, the test
     * screen is closed immediately.
     *
     * @param test The existing flammability test being edited, or `null`
     * when creating a new test.
     */
    fun onCancel(test: APICallTables.FlammabilityTest?) {
        if (test != null) {
            val (h, m, s) = fromTimeFormatHMMSS(test.FLAMETIME)
            if (flameReading.value.testNumber != test.TESTNO.toString() ||
                flameReading.value.daysSet != test.DAYSSET.toString() ||
                flameReading.value.burnLength != test.BURNLENGTH.toString() ||
                flameReading.value.hour != h ||
                flameReading.value.minute != m ||
                flameReading.value.second != s
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
        } else if (flameHasValue(flameReading)) {
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