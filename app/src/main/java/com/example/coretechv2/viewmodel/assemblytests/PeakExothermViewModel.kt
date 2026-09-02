package com.example.coretechv2.viewmodel.assemblytests

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coretechv2.dataclasses.APICallTables
import com.example.coretechv2.dataclasses.APICallTypes
import com.example.coretechv2.dataclasses.MessageItems
import com.example.coretechv2.dataclasses.assemblydataclasses.PeakExothermField
import com.example.coretechv2.dataclasses.assemblydataclasses.PeakExothermItem
import com.example.coretechv2.dataclasses.assemblydataclasses.peakExoHasValue
import com.example.coretechv2.repository.APICall
import com.example.coretechv2.repository.DataStoreManager
import com.example.coretechv2.repository.fromTimeFormatHMMSS
import com.example.coretechv2.repository.toTimeFormatHMMSS
import com.example.coretechv2.viewmodel.SharedViewModel
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing Peak Exotherm Test data and UI state.
 *
 * This ViewModel handles:
 * - Managing peak exotherm test input fields.
 * - Managing catalyst and catalyst percentage values.
 * - Managing peak temperature measurements.
 * - Managing gel time using separate hour, minute, and second fields.
 * - Loading existing peak exotherm test results.
 * - Initialising new tests and determining the next test number.
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
 * - Peak exotherm data classes for representing test input and field types.
 * - Time formatting utilities for converting gel time between UI and
 *   database formats.
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
     * Updates a Peak Exotherm Test field with a new user-entered value.
     *
     * The supplied [field] determines which property of [peakReading]
     * is updated. When the catalyst field is changed, the catalyst
     * dropdown is also hidden.
     *
     * @param newValue The new value entered or selected by the user.
     * @param field The Peak Exotherm Test field being modified.
     */
    fun onGelChange(newValue: String, field: PeakExothermField) {
        peakReading.value = when (field) {
            PeakExothermField.CATPERCENT -> peakReading.value.copy(catPercent = newValue)
            PeakExothermField.CATALYST -> peakReading.value.copy(catalyst = newValue)
            PeakExothermField.TESTNUMBER -> peakReading.value.copy(testNumber = newValue)
            PeakExothermField.HOUR -> peakReading.value.copy(hour = newValue)
            PeakExothermField.MINUTE -> peakReading.value.copy(minute = newValue)
            PeakExothermField.SECOND -> peakReading.value.copy(second = newValue)
            PeakExothermField.TEMPERATURE -> peakReading.value.copy(temperature = newValue)
        }
        if (field == PeakExothermField.CATALYST) {
            showcatalystList = false
        }
    }

    /**
     * Saves the current Peak Exotherm Test results.
     *
     * The gel time entered as separate hour, minute, and second values is
     * first converted into the HMMSS format used by the database.
     *
     * Depending on [SharedViewModel.saveType], this method either:
     * - Inserts a new record into `OSTDEF_PEAKEXOTHERM_TEST`.
     * - Updates an existing record in `OSTDEF_PEAKEXOTHERM_TEST`.
     *
     * The saved record includes the test number, gel time, catalyst,
     * catalyst percentage, peak temperature, assembly information, and
     * user information.
     *
     * On successful completion, the test screen is closed and a success
     * snackbar message is displayed. If the database operation fails,
     * an error snackbar message is displayed instead.
     */
    fun onSave() {
        viewModelScope.launch {
            val peakTimeFormatted = toTimeFormatHMMSS(peakReading.value.hour, peakReading.value.minute, peakReading.value.second)
            var call = ""
            if (sharedViewModel.saveType == APICallTypes.INSERT) {
                call = "INSERT INTO OSTDEF_PEAKEXOTHERM_TEST " +
                        "(ITEMCODE, ORDERNUMBER, ITEMDESCRIPTION, TESTNO, GELTIME, GELCAT, GELCATPERCENT, PEAKTEMPERATURE, SYSUSERCREATED, SYSUSERMODIFIED) " +
                        "VALUES('${sharedViewModel.currentAssemblyHeader?.ITEMCODE}', '${sharedViewModel.currentAssemblyHeader?.ORDERNUMBER}', '${sharedViewModel.currentAssemblyHeader?.ITEMDESCRIPTION}', ${peakReading.value.testNumber}, " +
                        "'${peakTimeFormatted}', '${peakReading.value.catalyst}', '${peakReading.value.catPercent}', ${peakReading.value.temperature}, '${sharedViewModel.currentUser.value}', '${sharedViewModel.currentUser.value}')"
            }
            if (sharedViewModel.saveType == APICallTypes.UPDATE) {
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
            if (response == "200 OK") {
                closeTestScreen.value = true
                sharedViewModel.snackBarMessage("Peak Exotherm Saved successfully")
            } else {
                sharedViewModel.snackBarMessage("Error Saving, Please Try Again")
            }
        }
    }

    /**
     * Loads an existing Peak Exotherm Test or initialises a new test.
     *
     * When [test] is provided, its database values are copied into
     * [peakReading]. The stored gel time is converted from HMMSS format
     * into separate hour, minute, and second values for display in the UI.
     *
     * When [test] is `null`, the catalyst and catalyst percentage are
     * initialised from fields on the current assembly header. The next
     * available test number is then calculated from the distinct test
     * numbers already recorded for the current assembly order.
     *
     * @param test The existing Peak Exotherm Test to load, or `null` when
     * creating a new test.
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
                val linesCall: List<APICallTables.Count>? =
                    apiCall.query("SELECT COUNT(*) FROM (SELECT DISTINCT TESTNO FROM OSTDEF_PEAKEXOTHERM_TEST where OrderNumber = '${sharedViewModel.currentOrderNumber.value}'")
                val testNumberCount = linesCall?.first()?.COUNT ?: 0
                peakReading.value = peakReading.value.copy(testNumber = (testNumberCount + 1).toString())
            }
        }
    }


    /**
     * Handles cancellation of the Peak Exotherm Test screen.
     *
     * When editing an existing test, the current values are compared with
     * the original database values. If any values have changed, a
     * confirmation popup is displayed before leaving without saving.
     *
     * When creating a new test, [peakExoHasValue] determines whether any
     * Peak Exotherm Test values have been entered. If values exist, the
     * user is prompted to confirm leaving without saving. Otherwise, the
     * test screen is closed immediately.
     *
     * @param test The existing Peak Exotherm Test being edited, or `null`
     * when creating a new test.
     */
    fun onCancel(test: APICallTables.peakExothermTest?) {
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
        } else if (peakExoHasValue(peakReading)) {
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