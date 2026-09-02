package com.example.coretechv2.viewmodel.assemblytests

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
import com.example.coretechv2.viewmodel.SharedViewModel
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing viscosity test data and UI state.
 *
 * This ViewModel handles:
 * - Managing viscosity test readings at different spindle speeds.
 * - Managing the selected spindle and viscosity index range.
 * - Calculating the viscosity index result from the selected reading range.
 * - Loading existing viscosity test results.
 * - Initialising new viscosity tests and determining the next test number.
 * - Saving viscosity test results using INSERT or UPDATE operations.
 * - Managing dropdown and popup dialog state.
 * - Detecting unsaved changes when cancelling a test.
 * - Handling navigation flow for the viscosity test screen lifecycle.
 *
 * It interacts with:
 * - [APICall] for communication with the database through the API.
 * - [SharedViewModel] for shared application state such as the current
 *   assembly order, user, and save type.
 * - Viscosity data classes for representing test readings and field types.
 */
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
     * Toggles the visibility of the spindle selection dropdown.
     */
    fun spindlePressed() {
        showSpindleList = !showSpindleList
    }

    /**
     * Toggles the visibility of the viscosity index range dropdown.
     */
    fun indexPressed() {
        showindexList = !showindexList
    }

    /**
     * Updates a viscosity test dropdown value.
     *
     * The supplied [field] determines whether the spindle, index range,
     * or test number is updated. The corresponding dropdown is then
     * hidden after the selection is made.
     *
     * @param newValue The newly selected value.
     * @param field The viscosity setting being updated.
     */
    fun onDropDownChange(newValue: String, field: VisSettings) {
        visReading.value = when (field) {
            VisSettings.SPINDLE -> visReading.value.copy(spindle = newValue)
            VisSettings.INDEX -> visReading.value.copy(indexRange = newValue)
            VisSettings.TESTNUMBER -> visReading.value.copy(testNumber = newValue)
        }
        if (field == VisSettings.SPINDLE) {
            showSpindleList = false
        }
        if (field == VisSettings.INDEX) {
            showindexList = false
        }
        if (field == VisSettings.TESTNUMBER) {
            showtestNumber = false
        }
    }

    /**
     * Updates a viscosity reading field with a new user-entered value.
     *
     * The supplied [field] determines which viscosity reading is updated.
     * Supported readings correspond to the spindle speed ranges represented
     * by [VisField].
     *
     * @param newValue The new viscosity reading entered by the user.
     * @param field The viscosity reading field being modified.
     */
    fun onVisChange(newValue: String, field: VisField) {
        visReading.value = when (field) {
            VisField.VIS60 -> visReading.value.copy(vis60 = newValue)
            VisField.VIS30 -> visReading.value.copy(vis30 = newValue)
            VisField.VIS12 -> visReading.value.copy(vis12 = newValue)
            VisField.VIS06 -> visReading.value.copy(vis06 = newValue)
            VisField.VIS03 -> visReading.value.copy(vis03 = newValue)
            VisField.VIS1_5 -> visReading.value.copy(vis1_5 = newValue)
            VisField.VIS0_6 -> visReading.value.copy(vis0_6 = newValue)
            VisField.VIS0_3 -> visReading.value.copy(vis0_3 = newValue)
        }
    }

    /**
     * Calculates and saves the current viscosity test.
     *
     * The viscosity index ratio is calculated using the selected index
     * range. Supported ranges are:
     * - `6/60`
     * - `3/30`
     * - `0.6/6`
     * - `0.3/3`
     *
     * The calculated ratio is rounded to one decimal place and stored as
     * the index reading.
     *
     * Depending on [SharedViewModel.saveType], this method either inserts
     * a new record into `OSTDEF_VISCOSITY_TESTS` or updates an existing
     * record.
     *
     * On successful completion, the test screen is closed and a success
     * snackbar message is displayed. If the database operation fails,
     * an error snackbar message is displayed instead.
     */
    fun onTestSave() {
        var ratio = 0.0
        if (visReading.value.indexRange == "6/60") {
            ratio = (visReading.value.vis06.toDoubleOrNull() ?: 0.0) / (visReading.value.vis60.toDoubleOrNull() ?: 0.0)
        }
        if (visReading.value.indexRange == "3/30") {
            ratio = (visReading.value.vis03.toDoubleOrNull() ?: 0.0) / (visReading.value.vis30.toDoubleOrNull() ?: 0.0)
        }
        if (visReading.value.indexRange == "0.6/6") {
            ratio = (visReading.value.vis0_6.toDoubleOrNull() ?: 0.0) / (visReading.value.vis06.toDoubleOrNull() ?: 0.0)
        }
        if (visReading.value.indexRange == "0.3/3") {
            ratio = (visReading.value.vis0_3.toDoubleOrNull() ?: 0.0) / (visReading.value.vis03.toDoubleOrNull() ?: 0.0)
        }
        val indexResult = kotlin.math.round(ratio * 10 * 10) / 10
        viewModelScope.launch {
            var call = ""
            if (sharedViewModel.saveType == APICallTypes.INSERT) {
                call = "INSERT INTO OSTDEF_VISCOSITY_TESTS " +
                        "(ITEMCODE, ORDERNUMBER, ITEMDESCRIPTION, TESTNO, SPINDLE, INDEXREADING, READING60, READING30, READING12, READING6, READING3, READING1_5, READING0_6, READING0_3, SYSUSERCREATED, SYSUSERMODIFIED) " +
                        "VALUES('${sharedViewModel.currentAssemblyHeader?.ITEMCODE}', '${sharedViewModel.currentAssemblyHeader?.ORDERNUMBER}', '${sharedViewModel.currentAssemblyHeader?.ITEMDESCRIPTION}', ${visReading.value.testNumber}, '${visReading.value.spindle}', ${indexResult}, " +
                        "${visReading.value.vis60.toDoubleOrNull() ?: 0.0}, ${visReading.value.vis30.toDoubleOrNull() ?: 0.0}, ${visReading.value.vis12.toDoubleOrNull() ?: 0.0}, ${visReading.value.vis06.toDoubleOrNull() ?: 0.0}, " +
                        "${visReading.value.vis03.toDoubleOrNull() ?: 0.0}, ${visReading.value.vis1_5.toDoubleOrNull() ?: 0.0}, ${visReading.value.vis0_6.toDoubleOrNull() ?: 0.0},${visReading.value.vis0_3.toDoubleOrNull() ?: 0.0}, " +
                        "'${sharedViewModel.currentUser.value}', '${sharedViewModel.currentUser.value}')"
            }
            if (sharedViewModel.saveType == APICallTypes.UPDATE) {
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

            val response = apiCall.insertUpdateDelete(call)

            if (response == "200 OK") {
                closeTestScreen.value = true
                sharedViewModel.snackBarMessage("Viscosity Saved successfully")
            } else {
                sharedViewModel.snackBarMessage("Error Saving, Please Try Again")
            }
        }
    }

    /**
     * Loads an existing viscosity test or initialises a new test.
     *
     * When [test] is provided, the stored viscosity readings are copied
     * into [visReading]. The index range is inferred by comparing the
     * stored index reading against the calculated ratio for each supported
     * reading pair.
     *
     * When [test] is `null`, the spindle and index range are initialised
     * from fields on the current assembly header. The next available test
     * number is then determined from the distinct test numbers already
     * recorded for the current assembly order.
     *
     * @param test The existing viscosity test to load, or `null` when
     * creating a new test.
     */
    fun onClear(test: APICallTables.viscosityTest?) {
        if (test != null) {
            var ratio = "N/A"
            if (kotlin.math.round((test.READING6 / test.READING60) * 10 * 10) / 10 == test.INDEXREADING) {
                ratio = "6/60"
            }
            if (kotlin.math.round((test.READING3 / test.READING30) * 10 * 10) / 10 == test.INDEXREADING) {
                ratio = "3/30"
            }
            if (kotlin.math.round((test.READING0_6 / test.READING6) * 10 * 10) / 10 == test.INDEXREADING) {
                ratio = "0.6/6"
            }
            if (kotlin.math.round((test.READING0_3 / test.READING3) * 10 * 10) / 10 == test.INDEXREADING) {
                ratio = "0.3/3"
            }
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
        } else {
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
     * When editing an existing test, the current values are compared with
     * the original database values. The stored index range is recalculated
     * from the database readings before comparison.
     *
     * If any test values have changed, a confirmation popup is displayed
     * before leaving without saving. If no values have changed, the test
     * screen is closed immediately.
     *
     * When creating a new test, [visHasValue] determines whether any
     * viscosity test values have been entered. If values exist, the user
     * is prompted to confirm leaving without saving. Otherwise, the test
     * screen is closed immediately.
     *
     * @param test The existing viscosity test being edited, or `null`
     * when creating a new test.
     */
    fun onCancel(test: APICallTables.viscosityTest?) {
        if (test != null) {
            var ratio = "N/A"
            if (kotlin.math.round((test.READING6 / test.READING60) * 10 * 10) / 10 == test.INDEXREADING) {
                ratio = "6/60"
            }
            if (kotlin.math.round((test.READING3 / test.READING30) * 10 * 10) / 10 == test.INDEXREADING) {
                ratio = "3/30"
            }
            if (kotlin.math.round((test.READING0_6 / test.READING6) * 10 * 10) / 10 == test.INDEXREADING) {
                ratio = "0.6/6"
            }
            if (kotlin.math.round((test.READING0_3 / test.READING3) * 10 * 10) / 10 == test.INDEXREADING) {
                ratio = "0.3/3"
            }
            if (visReading.value.testNumber != test.TESTNO.toString() ||
                visReading.value.spindle != test.SPINDLE ||
                visReading.value.indexRange != ratio ||
                visReading.value.vis60 != test.READING60.toString() ||
                visReading.value.vis30 != test.READING30.toString() ||
                visReading.value.vis12 != test.READING12.toString() ||
                visReading.value.vis06 != test.READING6.toString() ||
                visReading.value.vis03 != test.READING3.toString() ||
                visReading.value.vis1_5 != test.READING1_5.toString() ||
                visReading.value.vis0_6 != test.READING0_6.toString() ||
                visReading.value.vis0_3 != test.READING0_3.toString()
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
        } else {
            if (visHasValue(visReading)) {
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
}