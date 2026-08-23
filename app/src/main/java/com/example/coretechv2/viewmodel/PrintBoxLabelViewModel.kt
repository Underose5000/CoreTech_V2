package com.example.coretechv2.viewmodel

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
import com.example.coretechv2.dataclasses.LabelElements
import com.example.coretechv2.dataclasses.LabelStyles
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
class PrintBoxLabelViewModel(private val dataStoreManager: DataStoreManager, var sharedViewModel: SharedViewModel) : ViewModel() {
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
    var batchNumber = mutableStateOf("")
        private set

    var boxQty = mutableStateOf("")
        private set

    var numberOfBoxes = mutableStateOf("")
        private set

    var kitSet = mutableStateOf(false)
        private set

    var labelIndex = mutableStateOf(0)
        private set
    var variantName = mutableStateOf("")
        private set
    var showIndexList by mutableStateOf(false)
        private set
    val boxLabelDataList = mutableStateListOf<LabelElements>()

    var itemInfo by mutableStateOf<List<APICallTables.assemblyLabelItemInfo>>(emptyList())
        private set

    var showLabel by mutableStateOf(false)
        private set

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

    fun variantPressed(){
        showIndexList = !showIndexList
    }

    fun onVariantChange(index: Int){
        if (itemInfo[index].MIDDLENAME.isNotEmpty()) {
            variantName.value = itemInfo[index].MIDDLENAME
        } else {
            variantName.value = "${itemInfo[index].TOPNAME} ${itemInfo[index].BOTTOMNAME}"
        }
        labelIndex.value = index
        showIndexList = false
    }

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

    fun onBoxLabelData() {
        viewModelScope.launch {
            boxLabelDataList.clear()
            val itemInfoCall: List<APICallTables.assemblyLabelItemInfo>? =
                apiCall.query("SELECT LII.HEADERSYSUNIQUEID, LII.SYSUNIQUEID, LII.ITEMCODE, LII.TOPNAME, LII.MIDDLENAME, LII.BOTTOMNAME, LII.SIZE, LII.QRCODE, LII.VARIANT, LII.BESTBEFORE, LII.LABELSTYLE, LII.BOXQTY, IM.ITEMBARCODE FROM OSTDEF_LABELITEMINFO AS LII JOIN ITEMMASTER AS IM on LII.ITEMCODE = IM.ITEMCODE where LII.ITEMCODE = '${sharedViewModel.currentItem.value.code}'")
            itemInfo = itemInfoCall ?: emptyList()

            if (itemInfo.isNotEmpty()) {
                for (x in 0 until itemInfo.size) {
                    val classInfoCall: List<APICallTables.assemblyLabelClassInfo>? = apiCall.query("Select * from OSTDEF_LABELCLASSINFO where SYSUNIQUEID = '${itemInfo[x].HEADERSYSUNIQUEID}'")
                    val boxLayoutCall: List<APICallTables.assemblyLabelLayout>? = apiCall.query("Select * from OSTDEF_LABELLAYOUTINFO where LABELID = '${LabelStyles.BOX}'")
                    val dgInfoCall: List<APICallTables.itemDGInfo>? = apiCall.query(
                        "select dgi.UNNUMBER, dgi.PACKINGGROUP, dgi.DGCLASS, dgl.DGQUANTITY from OSTDEF_DGINFO as dgi " +
                                "join OSTDEF_DGLINES as dgl on dgl.HEADERSYSUNIQUEID = dgi.SYSUNIQUEID and dgl.LINECODE = '${itemInfo[x].ITEMCODE}' and dgl.CODETYPE = 'Item Code'"
                    )
                    if (classInfoCall != null && boxLayoutCall != null) {
                        boxLabelDataList.add(LabelElements(itemInfo[x].copy(LABELSTYLE = LabelStyles.BOX), classInfoCall.first(), boxLayoutCall.first(), dgInfoCall?.first(), sharedViewModel))
                    }
                }
                if (itemInfo[0].MIDDLENAME.isNotEmpty()) {
                    variantName.value = itemInfo[0].MIDDLENAME
                } else {
                    variantName.value = "${itemInfo[0].TOPNAME} ${itemInfo[0].BOTTOMNAME}"
                }
                boxQty.value = boxLabelDataList[0].itemInfo.BOXQTY.toString()
                numberOfBoxes.value = "1"
                kitSet.value = if(sharedViewModel.currentItem.value.type == "Descriptor Code"){true} else {false}
            }
        }
    }

    fun onBatchNumber(newValue: String) {
        batchNumber.value = newValue
        sharedViewModel.currentOrderNumber.value = newValue
    }

    fun onKitSet(newValue: Boolean) {
        kitSet.value = newValue
        boxLabelDataList.forEach {
            it.kitset = newValue
        }
    }

    fun onBoxQty(newValue: String) {
        boxQty.value = newValue

        if(newValue != ""){
        boxLabelDataList.forEach {
            it.itemInfo.BOXQTY = boxQty.value.toInt()
        }
        }
    }

    fun onNumberOfBoxes(newValue: String) {
        numberOfBoxes.value = newValue
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

    fun onOk() {
        showLabel = true
        sharedViewModel.showLabelPreview.value = true
        closeTestScreen.value = true
    }

    fun onCancel() {
        closeTestScreen.value = true
    }
}