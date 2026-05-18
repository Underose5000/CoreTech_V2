package com.example.coretechv2.viewmodel.assemblytests

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coretechv2.dataclasses.APICallTables
import com.example.coretechv2.dataclasses.APICallTypes
import com.example.coretechv2.dataclasses.ItemDescriptorItem
import com.example.coretechv2.dataclasses.assemblytests.GelField
import com.example.coretechv2.dataclasses.assemblytests.GelTimeItem
import com.example.coretechv2.dataclasses.MessageItems
import com.example.coretechv2.dataclasses.PopupItems
import com.example.coretechv2.dataclasses.assemblytests.AdjustmentItem
import com.example.coretechv2.dataclasses.assemblytests.adjustmentHasValue
import com.example.coretechv2.dataclasses.assemblytests.gelHasValue
import com.example.coretechv2.repository.APICall
import com.example.coretechv2.repository.DataStoreManager
import com.example.coretechv2.repository.fromTimeFormatHMMSS
import com.example.coretechv2.repository.toTimeFormatHMMSS
import com.example.coretechv2.ui.screen.ItemLookUpScreen
import com.example.coretechv2.ui.screen.assemblytestsandadjustments.ViscosityScreen
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
class AdjustmentsViewModel(private val dataStoreManager: DataStoreManager, var sharedViewModel: SharedViewModel) : ViewModel() {
    private val apiCall = APICall(dataStoreManager)
    var popupDetails = PopupItems().copy()
    var popupMessage = MessageItems()
    var closePopupMessage = mutableStateOf(false)
        private set
    var closeTestScreen = mutableStateOf(false)
        private set
    var openPopupMessage = mutableStateOf(false)
        private set
    var openItemList = mutableStateOf(false)
        private set
    var showSearchBox by mutableStateOf(false)
        private set
    var selectedItem by mutableStateOf(ItemDescriptorItem())
        private set
    var Searchfield by mutableStateOf("")
        private set
    var itemsList = mutableStateListOf<APICallTables.ItemDescriptor>()
        private set
    var itemListSearched = mutableStateListOf<APICallTables.ItemDescriptor>()
        private set
    var adjustmentRecord = mutableStateOf(AdjustmentItem())

    /**
     * Resets the popup message visibility state.
     */
    fun closePopupMessage() {
        closePopupMessage.value = false
    }

    fun openItemList() {
        popupDetails.width = 700
        popupDetails.height = 500
        popupDetails.content = {
            ItemLookUpScreen(sharedViewModel)
        }
        openItemList.value = true
    }

    /**
     * Closes the gel test screen and triggers navigation back.
     */
    fun closeSearchBoxs() {
        Searchfield = sharedViewModel.currentItem.value.description
        showSearchBox = false
        openItemList.value = false
    }

    /**
     * Resets the popup trigger state.
     */
    fun openPopupMessage() {
        openPopupMessage.value = false
    }

    /**
     * Toggles the catalyst dropdown visibility.
     */
    fun retrieveItems() {
        viewModelScope.launch {
            val call = "SELECT ITEMCODE AS CODE, ITEMDESCRIPTION AS DESCRIPTION, ITEMUNIT AS UNIT, ITEMSTATUS AS STATUS, ITEMBARCODE AS BARCODE, ITEMCATEGORY AS CATEGORY, " +
                    "ONHANDQTY, SUPPLYQTY, DEMANDQTY, AVAILABLEQTY, FREEQTY, 'Item' AS TYPE, SYSUNIQUEID FROM ITEMMASTER where ITEMSTATUS <> 'Obsolete' " +
                    "UNION ALL " +
                    "SELECT DESCRIPTORCODE AS CODE, DESCRIPTORDESCRIPTION AS DESCRIPTION, DESCRIPTORUNIT AS UNIT, DESCRIPTORSTATUS AS STATUS, DESCRIPTORBARCODE AS BARCODE, DESCRIPTORCATEGORY AS CATEGORY, " +
                    "NULL AS ONHANDQTY, NULL AS SUPPLYQTY, NULL AS DEMANDQTY, NULL AS AVAILABLEQTY, NULL AS FREEQTY, 'Descriptor' AS TYPE, SYSUNIQUEID FROM DESCRIPTORMASTER where DESCRIPTORSTATUS <> 'Obsolete'"

            val itemscall: List<APICallTables.ItemDescriptor>? = apiCall.query(call)

            itemsList.clear()
            itemListSearched.clear()
            itemscall?.let {
                itemsList.addAll(it)
                itemListSearched.addAll(it)
            }
        }
    }

    fun onAdjustmentNumber(newValue: String) {
        adjustmentRecord.value.adjustmentNumber = newValue
    }

    fun onAdjustmentqty(newValue: String) {
        adjustmentRecord.value.qty = newValue
    }

    fun onSearchFieldChange(newValue: String) {
        Searchfield = newValue
        itemListSearched.clear()
        if (newValue.isBlank()) {
            showSearchBox = false
        } else {
            val searchTerms = Searchfield
                .trim()
                .split("\\s+".toRegex())
                .filter { it.isNotBlank() }

            itemListSearched.addAll(
                itemsList.filter { item ->

                    val searchableText = listOf(
                        item.CODE,
                        item.DESCRIPTION,
                        item.CATEGORY,
                        item.BARCODE
                    ).joinToString(" ")

                    searchTerms.all { term ->
                        searchableText.contains(term, ignoreCase = true)
                    }
                }
            )
            showSearchBox = true
        }
    }

    fun getSelectedItem(item: APICallTables.ItemDescriptor) {
        selectedItem.apply {
            code = item.CODE
            description = item.DESCRIPTION
            unit = item.UNIT
            status = item.STATUS
            barcode = item.BARCODE
            category = item.CATEGORY
            onHandQty = item.ONHANDQTY
            supplyQty = item.SUPPLYQTY
            demandQty = item.DEMANDQTY
            availableQty = item.AVAILABLEQTY
            freeQty = item.FREEQTY
            type = item.TYPE
            sysID = item.SYSUNIQUEID
        }
        sharedViewModel.currentItem.value = selectedItem.copy()

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
    fun onSave() {
        /*viewModelScope.launch {
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
        }*/
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
    fun onClear(adjustment: APICallTables.assemblyAdjustment?) {
        if (adjustment != null) {
            adjustmentRecord.value = AdjustmentItem().apply {
                Searchfield = adjustment.LINEDESCRIPTION
                qty = ""
            }
            viewModelScope.launch {
                val itemCall: List<APICallTables.ItemDescriptor>? = apiCall.query("SELECT ITEMCODE AS CODE, ITEMDESCRIPTION AS DESCRIPTION, ITEMUNIT AS UNIT, ITEMSTATUS AS STATUS, ITEMBARCODE AS BARCODE, ITEMCATEGORY AS CATEGORY, " +
                        "ONHANDQTY, SUPPLYQTY, DEMANDQTY, AVAILABLEQTY, FREEQTY, 'Item' AS TYPE, SYSUNIQUEID FROM ITEMMASTER where ITEMCODE = '${adjustment.LINECODE}' " +
                        "UNION ALL " +
                        "SELECT DESCRIPTORCODE AS CODE, DESCRIPTORDESCRIPTION AS DESCRIPTION, DESCRIPTORUNIT AS UNIT, DESCRIPTORSTATUS AS STATUS, DESCRIPTORBARCODE AS BARCODE, DESCRIPTORCATEGORY AS CATEGORY, " +
                        "NULL AS ONHANDQTY, NULL AS SUPPLYQTY, NULL AS DEMANDQTY, NULL AS AVAILABLEQTY, NULL AS FREEQTY, 'Descriptor' AS TYPE, SYSUNIQUEID FROM DESCRIPTORMASTER where DESCRIPTORCODE = '${adjustment.LINECODE}'")

                adjustmentRecord.value = adjustmentRecord.value.copy(item = ItemDescriptorItem().apply {
                    code = itemCall?.first()?.CODE ?: ""
                    description = itemCall?.first()?.DESCRIPTION ?: ""
                    unit = itemCall?.first()?.UNIT ?: ""
                    status = itemCall?.first()?.STATUS ?: ""
                    barcode = itemCall?.first()?.BARCODE ?: ""
                    category = itemCall?.first()?.CATEGORY ?: ""
                    onHandQty = itemCall?.first()?.ONHANDQTY
                    supplyQty = itemCall?.first()?.SUPPLYQTY
                    demandQty = itemCall?.first()?.DEMANDQTY
                    availableQty = itemCall?.first()?.AVAILABLEQTY
                    freeQty = itemCall?.first()?.FREEQTY
                    type = itemCall?.first()?.TYPE ?: ""
                    sysID = itemCall?.first()?.SYSUNIQUEID
                })
            }
        } else {
            adjustmentRecord.value = AdjustmentItem().apply {
                Searchfield = ""
                qty = ""
            }
            viewModelScope.launch {
                val linesCall: List<APICallTables.Count>? = apiCall.query("SELECT COUNT(*) FROM OSTDEF_ADJUSTMENTS where OrderNumber = '${sharedViewModel.currentOrderNumber.value}'")
                val testNumberCount = linesCall?.first()?.COUNT ?: 0
                adjustmentRecord.value = adjustmentRecord.value.copy(adjustmentNumber = (testNumberCount + 1).toString())
            }
            showSearchBox = false
            sharedViewModel.currentItem.value = ItemDescriptorItem()
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
    fun onCancel() {
        if (adjustmentHasValue(adjustmentRecord)) {
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