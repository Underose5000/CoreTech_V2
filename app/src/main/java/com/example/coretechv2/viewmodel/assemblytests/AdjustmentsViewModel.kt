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
import com.example.coretechv2.dataclasses.MessageItems
import com.example.coretechv2.dataclasses.PopupItems
import com.example.coretechv2.dataclasses.assemblydataclasses.AdjustmentItem
import com.example.coretechv2.dataclasses.assemblydataclasses.adjustmentHasValue
import com.example.coretechv2.repository.APICall
import com.example.coretechv2.repository.DataStoreManager
import com.example.coretechv2.ui.screen.ItemLookUpScreen
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

    var currentQty by mutableStateOf(0.0)

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

    fun closeTestScreen(){
        closeTestScreen.value = false
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
                    "ONHANDQTY, SUPPLYQTY, DEMANDQTY, AVAILABLEQTY, FREEQTY, 'Item Code' AS TYPE, SYSUNIQUEID FROM ITEMMASTER where ITEMSTATUS <> 'Obsolete' " +
                    "UNION ALL " +
                    "SELECT DESCRIPTORCODE AS CODE, DESCRIPTORDESCRIPTION AS DESCRIPTION, DESCRIPTORUNIT AS UNIT, DESCRIPTORSTATUS AS STATUS, DESCRIPTORBARCODE AS BARCODE, DESCRIPTORCATEGORY AS CATEGORY, " +
                    "NULL AS ONHANDQTY, NULL AS SUPPLYQTY, NULL AS DEMANDQTY, NULL AS AVAILABLEQTY, NULL AS FREEQTY, 'Descriptor Code' AS TYPE, SYSUNIQUEID FROM DESCRIPTORMASTER where DESCRIPTORSTATUS <> 'Obsolete' order by 12 DESC, 1 ASC"

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
        adjustmentRecord.value = adjustmentRecord.value.copy(adjustmentNumber = newValue)
    }

    fun onAdjustmentqty(newValue: String) {
        adjustmentRecord.value = adjustmentRecord.value.copy(qty = newValue)
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

    fun currentLineNumbers(code: String):  List<APICallTables.AssemblyLines> {
        val lines = mutableStateListOf<APICallTables.AssemblyLines>()

        for (i in 0 until (sharedViewModel.currentAssemblyLines?.size ?: 1)){
            if(sharedViewModel.currentAssemblyLines?.get(i)?.LINECODE == code) lines.add(sharedViewModel.currentAssemblyLines!![i])
        }
        return lines
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
    fun onSave(adjustment: APICallTables.assemblyAdjustment?) {
        var lineNumber = 0
        var assemblyLinesInsert: String? = ""
        val currentLines = currentLineNumbers(sharedViewModel.currentItem.value.code)
        viewModelScope.launch {
        if (sharedViewModel.saveType == APICallTypes.INSERT) {
            if (currentLines.isEmpty()) {
                lineNumber = (sharedViewModel.currentAssemblyLines?.size?.plus(1)?.times(10)!!)
                    val assemblyLinesCall = "INSERT INTO ASSEMBLYLINES " +
                            "(ORDERNUMBER, LINECODE, LINEDESCRIPTION, LINENUMBER, LINEUNIT, CODETYPE, STEPNAME, ORDERQTY, SYSUSERCREATED, SYSUSERMODIFIED) " +
                            "VALUES('${sharedViewModel.currentAssemblyHeader?.ORDERNUMBER}', '${sharedViewModel.currentItem.value.code}', '${sharedViewModel.currentItem.value.description}'," +
                            "${lineNumber}, " +
                            "'${sharedViewModel.currentItem.value.unit}', '${sharedViewModel.currentItem.value.type}', '${sharedViewModel.currentAssemblyLines?.first()?.STEPNAME}', '0','${sharedViewModel.currentUser.value}', '${sharedViewModel.currentUser.value}')"

                    assemblyLinesInsert = apiCall.insertUpdateDelete(assemblyLinesCall)
                    

            }
            else if (currentLines.size > 1) {
                lineNumber = currentLines.last().LINENUMBER
            }
            else{
                lineNumber = currentLines.first().LINENUMBER
            }
        }
            var call = ""
            var assemblyLinesCall = ""
            if (sharedViewModel.saveType == APICallTypes.INSERT) {
                call = "INSERT INTO OSTDEF_ADJUSTMENTS " +
                        "(ORDERNUMBER, LINECODE, LINEDESCRIPTION, LINENUMBER, LINEUNIT, CODETYPE, ADJUSTNO, ADJUSTQTY, SYSUSERCREATED, SYSUSERMODIFIED) " +
                        "VALUES('${sharedViewModel.currentAssemblyHeader?.ORDERNUMBER}', '${sharedViewModel.currentItem.value.code}', '${sharedViewModel.currentItem.value.description}', ${lineNumber}, " +
                        "'${sharedViewModel.currentItem.value.unit}', '${sharedViewModel.currentItem.value.type}', ${adjustmentRecord.value.adjustmentNumber}, ${adjustmentRecord.value.qty}, '${sharedViewModel.currentUser.value}', '${sharedViewModel.currentUser.value}')"

                assemblyLinesCall = "UPDATE ASSEMBLYLINES " +
                        "SET ORDERQTY = ORDERQTY + ${adjustmentRecord.value.qty} " +
                        "WHERE ORDERNUMBER = '${sharedViewModel.currentAssemblyHeader?.ORDERNUMBER}' AND LINECODE = '${sharedViewModel.currentItem.value.code}' and LINENUMBER = '$lineNumber'"
            }

            if (sharedViewModel.saveType == APICallTypes.UPDATE) {
                

                call = "UPDATE OSTDEF_ADJUSTMENTS SET  " +
                        "ADJUSTNO = ${adjustmentRecord.value.adjustmentNumber}," +
                        "ADJUSTQTY = '${adjustmentRecord.value.qty}'," +
                        "SYSUSERMODIFIED = '${sharedViewModel.currentUser.value}'" +
                        "WHERE SYSUNIQUEID = ${adjustmentRecord.value.sysID}"

                assemblyLinesCall = "UPDATE ASSEMBLYLINES " +
                        "SET ORDERQTY = ORDERQTY - ${currentQty} + ${adjustmentRecord.value.qty} " +
                        "WHERE ORDERNUMBER = '${sharedViewModel.currentAssemblyHeader?.ORDERNUMBER}' AND LINECODE = '${sharedViewModel.currentItem.value.code}' and LINENUMBER = '${adjustmentRecord.value.adjustmentLineNumber}'"
            }

            val response = apiCall.insertUpdateDelete(call)
            val assemblyLinesResponse2 = apiCall.insertUpdateDelete(assemblyLinesCall)
            if (response == "200 OK" && assemblyLinesResponse2 == "200 OK" ) {
                if(currentLines.isEmpty() && assemblyLinesInsert == "200 OK"){
                closeTestScreen.value = true
                sharedViewModel.snackBarMessage("Adjustment Saved successfully")
                } else {
                    closeTestScreen.value = true
                    sharedViewModel.snackBarMessage("Adjustment Saved successfully")
                }
            } else {
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
    fun onClear(adjustment: APICallTables.assemblyAdjustment?) {
        if (adjustment != null) {
            adjustmentRecord.value = AdjustmentItem().apply {
                qty = adjustment.ADJUSTQTY.toString()
                adjustmentNumber = adjustment.ADJUSTNO.toString()
                adjustmentLineNumber = adjustment.LINENUMBER
                sysID = adjustment.SYSUNIQUEID.toInt()
            }
            currentQty = adjustment.ADJUSTQTY
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
                sharedViewModel.currentItem.value = adjustmentRecord.value.item!!
                Searchfield = adjustmentRecord.value.item?.description ?: ""
            }
        } else {
            adjustmentRecord.value = AdjustmentItem().apply {
                Searchfield = ""
                qty = ""
            }
            viewModelScope.launch {
                val linesCall: List<APICallTables.Count>? = apiCall.query("SELECT COUNT(*) FROM (SELECT DISTINCT ADJUSTNO FROM OSTDEF_ADJUSTMENTS where OrderNumber = '${sharedViewModel.currentOrderNumber.value}')")
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
    fun onCancel(adjustment: APICallTables.assemblyAdjustment?) {
        if (adjustment != null) {
            if (adjustmentRecord.value.adjustmentNumber != adjustment.ADJUSTNO.toString() ||
                adjustmentRecord.value.qty != adjustment.ADJUSTQTY.toString()) {
                popupMessage.message = "Adjustments are not saved\nleave without saving?"
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
            } else{
                closeTestScreen.value = true
            }
        } else if (adjustmentHasValue(adjustmentRecord)) {
                    popupMessage.message = "Adjustments are not saved\nleave without saving?"
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