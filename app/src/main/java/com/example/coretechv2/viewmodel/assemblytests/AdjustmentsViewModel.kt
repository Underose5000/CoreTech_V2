package com.example.coretechv2.viewmodel.assemblytests

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
 * ViewModel responsible for managing assembly adjustment operations.
 *
 * This ViewModel handles the creation and editing of adjustment records,
 * item selection and searching, adjustment quantities and numbers, popup
 * state, and saving adjustment information to the database.
 *
 * It also updates the associated assembly line quantity when an adjustment
 * is inserted or modified.
 *
 * @property dataStoreManager Provides access to persisted application settings
 * and configuration required by the API layer.
 * @property sharedViewModel Provides shared application state, including the
 * current assembly, selected item, current user, assembly lines, and save mode.
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
     * Resets the message popup close state.
     */
    fun closePopupMessage() {
        closePopupMessage.value = false
    }

    /**
     * Opens the item lookup popup.
     *
     * Configures the popup dimensions and assigns [ItemLookUpScreen] as
     * the popup content before displaying it.
     */
    fun openItemList() {
        popupDetails.width = 700
        popupDetails.height = 500
        popupDetails.content = {
            ItemLookUpScreen(sharedViewModel)
        }
        openItemList.value = true
    }

    /**
     * Resets the state used to close the adjustment screen.
     */
    fun closeTestScreen() {
        closeTestScreen.value = false
    }

    /**
     * Closes the item search popup and updates the search field with the
     * description of the currently selected item.
     */
    fun closeSearchBoxs() {
        Searchfield = sharedViewModel.currentItem.value.description
        showSearchBox = false
        openItemList.value = false
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
     * Retrieves all active items and descriptors from the database.
     *
     * Item and descriptor records are combined using a SQL `UNION ALL` query.
     * Obsolete records are excluded. The retrieved records populate both the
     * complete item list and the initial search result list.
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

    /**
     * Updates the adjustment number in the current adjustment record.
     *
     * @param newValue The new adjustment number entered by the user.
     */
    fun onAdjustmentNumber(newValue: String) {
        adjustmentRecord.value = adjustmentRecord.value.copy(adjustmentNumber = newValue)
    }

    /**
     * Updates the adjustment quantity in the current adjustment record.
     *
     * @param newValue The new adjustment quantity entered by the user.
     */
    fun onAdjustmentqty(newValue: String) {
        adjustmentRecord.value = adjustmentRecord.value.copy(qty = newValue)
    }

    /**
     * Updates and filters the item search field.
     *
     * The search is performed against the item code, description, category,
     * and barcode. Multiple search terms are supported, and an item must
     * contain all supplied terms to appear in the results.
     *
     * @param newValue The new search text entered by the user.
     */
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

    /**
     * Sets the currently selected item.
     *
     * Copies the supplied item descriptor into [selectedItem] and updates
     * [sharedViewModel] with a copy of the selected item for use elsewhere
     * in the application.
     *
     * @param item The item or descriptor selected by the user.
     */
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
     * Retrieves all assembly lines matching the supplied line code.
     *
     * @param code The line code used to filter the current assembly lines.
     * @return A list containing all current assembly lines whose line code
     * matches [code].
     */
    fun currentLineNumbers(code: String): List<APICallTables.AssemblyLines> {
        val lines = mutableStateListOf<APICallTables.AssemblyLines>()

        for (i in 0 until (sharedViewModel.currentAssemblyLines?.size ?: 1)) {
            if (sharedViewModel.currentAssemblyLines?.get(i)?.LINECODE == code) lines.add(sharedViewModel.currentAssemblyLines!![i])
        }
        return lines
    }

    /**
     * Saves the current adjustment to the database.
     *
     * When inserting a new adjustment, an assembly line is created if the
     * selected item does not already have one. The adjustment record is then
     * inserted and the associated assembly line quantity is increased.
     *
     * When updating an existing adjustment, the previous adjustment quantity
     * is removed from the assembly line quantity and the new quantity is added.
     *
     * On successful completion, the adjustment screen is closed and a success
     * snackbar message is displayed. If either database operation fails,
     * an error snackbar message is displayed.
     *
     * @param adjustment The existing adjustment being edited, or `null` when
     * creating a new adjustment.
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


                } else if (currentLines.size > 1) {
                    lineNumber = currentLines.last().LINENUMBER
                } else {
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
            if (response == "200 OK" && assemblyLinesResponse2 == "200 OK") {
                if (currentLines.isEmpty() && assemblyLinesInsert == "200 OK") {
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
     * Loads an existing adjustment for editing or prepares the ViewModel for
     * creating a new adjustment.
     *
     * When an existing adjustment is supplied, its values are copied into
     * [adjustmentRecord], the original quantity is stored in [currentQty],
     * and the associated item is retrieved from the database.
     *
     * When no adjustment is supplied, a new adjustment number is generated
     * based on the number of distinct adjustments already associated with the
     * current assembly order.
     *
     * @param adjustment The existing adjustment to load, or `null` when
     * preparing a new adjustment.
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
                val itemCall: List<APICallTables.ItemDescriptor>? = apiCall.query(
                    "SELECT ITEMCODE AS CODE, ITEMDESCRIPTION AS DESCRIPTION, ITEMUNIT AS UNIT, ITEMSTATUS AS STATUS, ITEMBARCODE AS BARCODE, ITEMCATEGORY AS CATEGORY, " +
                            "ONHANDQTY, SUPPLYQTY, DEMANDQTY, AVAILABLEQTY, FREEQTY, 'Item' AS TYPE, SYSUNIQUEID FROM ITEMMASTER where ITEMCODE = '${adjustment.LINECODE}' " +
                            "UNION ALL " +
                            "SELECT DESCRIPTORCODE AS CODE, DESCRIPTORDESCRIPTION AS DESCRIPTION, DESCRIPTORUNIT AS UNIT, DESCRIPTORSTATUS AS STATUS, DESCRIPTORBARCODE AS BARCODE, DESCRIPTORCATEGORY AS CATEGORY, " +
                            "NULL AS ONHANDQTY, NULL AS SUPPLYQTY, NULL AS DEMANDQTY, NULL AS AVAILABLEQTY, NULL AS FREEQTY, 'Descriptor' AS TYPE, SYSUNIQUEID FROM DESCRIPTORMASTER where DESCRIPTORCODE = '${adjustment.LINECODE}'"
                )

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
                val linesCall: List<APICallTables.Count>? =
                    apiCall.query("SELECT COUNT(*) FROM (SELECT DISTINCT ADJUSTNO FROM OSTDEF_ADJUSTMENTS where OrderNumber = '${sharedViewModel.currentOrderNumber.value}')")
                val testNumberCount = linesCall?.first()?.COUNT ?: 0
                adjustmentRecord.value = adjustmentRecord.value.copy(adjustmentNumber = (testNumberCount + 1).toString())
            }
            showSearchBox = false
            sharedViewModel.currentItem.value = ItemDescriptorItem()
        }
    }

    /**
     * Cancels the current adjustment operation.
     *
     * If an existing adjustment has been modified, or a new adjustment contains
     * values, a confirmation popup is displayed before leaving the screen.
     * Otherwise, the screen is closed immediately.
     *
     * @param adjustment The existing adjustment being edited, or `null` when
     * creating a new adjustment.
     */
    fun onCancel(adjustment: APICallTables.assemblyAdjustment?) {
        if (adjustment != null) {
            if (adjustmentRecord.value.adjustmentNumber != adjustment.ADJUSTNO.toString() ||
                adjustmentRecord.value.qty != adjustment.ADJUSTQTY.toString()
            ) {
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