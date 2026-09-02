package com.example.coretechv2.viewmodel.assemblytests

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coretechv2.dataclasses.APICallTables
import com.example.coretechv2.dataclasses.APICallTypes
import com.example.coretechv2.dataclasses.MessageItems
import com.example.coretechv2.dataclasses.assemblydataclasses.GelField
import com.example.coretechv2.dataclasses.assemblydataclasses.GelTimeItem
import com.example.coretechv2.dataclasses.assemblydataclasses.gelHasValue
import com.example.coretechv2.repository.APICall
import com.example.coretechv2.repository.DataStoreManager
import com.example.coretechv2.repository.fromTimeFormatHMMSS
import com.example.coretechv2.repository.toTimeFormatHMMSS
import com.example.coretechv2.viewmodel.SharedViewModel
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing gel time test data and UI state.
 *
 * This ViewModel handles:
 * - Managing gel time test input fields.
 * - Managing catalyst and catalyst percentage values.
 * - Managing gel time using separate hour, minute, and second fields.
 * - Loading existing gel time test results.
 * - Initialising new gel time tests and determining the next test number.
 * - Converting gel time between individual time fields and the database
 *   HMMSS format.
 * - Saving test results using INSERT or UPDATE operations.
 * - Managing the catalyst dropdown and popup dialog state.
 * - Handling navigation flow for the test screen lifecycle.
 *
 * It interacts with:
 * - [APICall] for communication with the database through the API.
 * - [SharedViewModel] for shared application state such as the current
 *   assembly order, user, and save type.
 * - Gel time data classes for representing test input and field types.
 * - Time formatting utilities for converting gel time between UI and
 *   database formats.
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
     * Toggles the visibility of the catalyst selection dropdown.
     */
    fun catalystPressed() {
        showcatalystList = !showcatalystList
    }

    /**
     * Updates a gel time test field with a new user-entered value.
     *
     * The supplied [field] determines which property of [gelReading]
     * is updated. When the catalyst field is changed, the catalyst
     * dropdown is also hidden.
     *
     * @param newValue The new value entered or selected by the user.
     * @param field The gel time test field being modified.
     */
    fun onGelChange(newValue: String, field: GelField) {
        gelReading.value = when (field) {
            GelField.CATPERCENT -> gelReading.value.copy(catPercent = newValue)
            GelField.CATALYST -> gelReading.value.copy(catalyst = newValue)
            GelField.TESTNUMBER -> gelReading.value.copy(testNumber = newValue)
            GelField.HOUR -> gelReading.value.copy(hour = newValue)
            GelField.MINUTE -> gelReading.value.copy(minute = newValue)
            GelField.SECOND -> gelReading.value.copy(second = newValue)
        }
        if (field == GelField.CATALYST) {
            showcatalystList = false
        }
    }

    /**
     * Saves the current gel time test results.
     *
     * The gel time entered as separate hour, minute, and second values is
     * first converted into the HMMSS format used by the database.
     *
     * Depending on [SharedViewModel.saveType], this method either:
     * - Inserts a new record into `OSTDEF_GELTIME_TESTS`.
     * - Updates an existing record in `OSTDEF_GELTIME_TESTS`.
     *
     * The saved record includes the test number, gel time, catalyst,
     * catalyst percentage, assembly information, and user information.
     *
     * On successful completion, the test screen is closed and a success
     * snackbar message is displayed. If the database operation fails,
     * an error snackbar message is displayed instead.
     */
    fun onSave() {
        viewModelScope.launch {
            val gelTimeFormatted = toTimeFormatHMMSS(gelReading.value.hour, gelReading.value.minute, gelReading.value.second)
            var call = ""
            if (sharedViewModel.saveType == APICallTypes.INSERT) {
                call = "INSERT INTO OSTDEF_GELTIME_TESTS " +
                        "(ITEMCODE, ORDERNUMBER, ITEMDESCRIPTION, TESTNO, GELTIME, GELCAT, GELCATPERCENT, SYSUSERCREATED, SYSUSERMODIFIED) " +
                        "VALUES('${sharedViewModel.currentAssemblyHeader?.ITEMCODE}', '${sharedViewModel.currentAssemblyHeader?.ORDERNUMBER}', '${sharedViewModel.currentAssemblyHeader?.ITEMDESCRIPTION}', ${gelReading.value.testNumber}, " +
                        "'${gelTimeFormatted}', '${gelReading.value.catalyst}', '${gelReading.value.catPercent}', '${sharedViewModel.currentUser.value}', '${sharedViewModel.currentUser.value}')"
            }
            if (sharedViewModel.saveType == APICallTypes.UPDATE) {
                call = "UPDATE OSTDEF_GELTIME_TESTS SET  " +
                        "TESTNO = ${gelReading.value.testNumber}," +
                        "GELTIME = '${gelTimeFormatted}'," +
                        "GELCAT = '${gelReading.value.catalyst}'," +
                        "GELCATPERCENT = '${gelReading.value.catPercent}'," +
                        "SYSUSERMODIFIED = '${sharedViewModel.currentUser.value}'" +
                        "WHERE SYSUNIQUEID = ${gelReading.value.sysID}"
            }

            val response = apiCall.insertUpdateDelete(call)
            if (response == "200 OK") {
                closeTestScreen.value = true
                sharedViewModel.snackBarMessage("Gel Time Saved successfully")
            } else {
                sharedViewModel.snackBarMessage("Error Saving, Please Try Again")
            }
        }
    }

    /**
     * Loads an existing gel time test or initialises a new test.
     *
     * When [test] is provided, its database values are copied into
     * [gelReading]. The stored gel time is converted from HMMSS format
     * into separate hour, minute, and second values for display in the UI.
     *
     * When [test] is `null`, the catalyst and catalyst percentage are
     * initialised from fields on the current assembly header. The next
     * available test number is then calculated from the distinct test
     * numbers already recorded for the current assembly order.
     *
     * @param test The existing gel time test to load, or `null` when
     * creating a new test.
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
                val linesCall: List<APICallTables.Count>? =
                    apiCall.query("SELECT COUNT(*) FROM (SELECT DISTINCT TESTNO FROM OSTDEF_GELTIME_TESTS where OrderNumber = '${sharedViewModel.currentOrderNumber.value}'")
                val testNumberCount = linesCall?.first()?.COUNT ?: 0
                gelReading.value = gelReading.value.copy(testNumber = (testNumberCount + 1).toString())
            }
        }
    }


    /**
     * Handles cancellation of the gel time test screen.
     *
     * When editing an existing test, the current values are compared with
     * the original database values. If any values have changed, a
     * confirmation popup is displayed before leaving without saving.
     *
     * When creating a new test, [gelHasValue] determines whether any gel
     * time test values have been entered. If values exist, the user is
     * prompted to confirm leaving without saving. Otherwise, the test
     * screen is closed immediately.
     *
     * @param test The existing gel time test being edited, or `null`
     * when creating a new test.
     */
    fun onCancel(test: APICallTables.gelTimeTest?) {
        if (test != null) {
            val (h, m, s) = fromTimeFormatHMMSS(test.GELTIME)
            if (gelReading.value.testNumber != test.TESTNO.toString() ||
                gelReading.value.catalyst != test.GELCAT ||
                gelReading.value.catPercent != test.GELCATPERCENT ||
                gelReading.value.hour != h ||
                gelReading.value.minute != m ||
                gelReading.value.second != s
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
        } else if (gelHasValue(gelReading)) {
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