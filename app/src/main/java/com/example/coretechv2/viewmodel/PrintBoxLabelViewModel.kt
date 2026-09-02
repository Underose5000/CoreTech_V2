package com.example.coretechv2.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coretechv2.dataclasses.APICallTables
import com.example.coretechv2.dataclasses.ItemDescriptorItem
import com.example.coretechv2.dataclasses.LabelElements
import com.example.coretechv2.dataclasses.LabelStyles
import com.example.coretechv2.dataclasses.MessageItems
import com.example.coretechv2.dataclasses.PopupItems
import com.example.coretechv2.repository.APICall
import com.example.coretechv2.repository.DataStoreManager
import com.example.coretechv2.ui.screen.ItemLookUpScreen
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing box label printing configuration.
 *
 * [PrintBoxLabelViewModel] manages the item selection, label variant,
 * quantities, batch number, kit/set configuration, and label data required
 * to generate a box label preview.
 *
 * It retrieves item and label information from the database through [APICall]
 * and communicates shared state with [SharedViewModel].
 *
 * @param dataStoreManager Provides access to application settings and API
 * configuration required by [APICall].
 * @param sharedViewModel Shared ViewModel used to access and update state
 * shared between application screens.
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
     * Resets the popup message close state.
     *
     * Sets [closePopupMessage] to false after the UI has processed the
     * popup close event.
     */
    fun closePopupMessage() {
        closePopupMessage.value = false
    }

    /**
     * Opens the item lookup screen.
     *
     * Configures the popup dimensions and content before setting
     * [openItemList] to true.
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
     * Resets the box label screen close state.
     *
     * Sets [closeTestScreen] to false after the UI has processed the
     * screen close event.
     */
    fun closeTestScreen() {
        closeTestScreen.value = false
    }

    /**
     * Closes the item search and lookup interface.
     *
     * Updates the search field with the description of the currently
     * selected item, hides the search box, and closes the item lookup list.
     */
    fun closeSearchBoxs() {
        Searchfield = sharedViewModel.currentItem.value.description
        showSearchBox = false
        openItemList.value = false
    }

    /**
     * Resets the popup message open state.
     *
     * Sets [openPopupMessage] to false after the UI has processed the
     * popup open event.
     */
    fun openPopupMessage() {
        openPopupMessage.value = false
    }

    /**
     * Toggles the visibility of the label variant selection list.
     */
    fun variantPressed() {
        showIndexList = !showIndexList
    }

    /**
     * Selects a label variant from the available item information.
     *
     * If the selected variant contains a middle name, that name is used.
     * Otherwise, the top and bottom names are combined to create the
     * variant name.
     *
     * @param index The index of the label variant to select.
     */
    fun onVariantChange(index: Int) {
        if (itemInfo[index].MIDDLENAME.isNotEmpty()) {
            variantName.value = itemInfo[index].MIDDLENAME
        } else {
            variantName.value = "${itemInfo[index].TOPNAME} ${itemInfo[index].BOTTOMNAME}"
        }
        labelIndex.value = index
        showIndexList = false
    }

    /**
     * Retrieves active items and descriptors from the database.
     *
     * Obsolete items and descriptors are excluded from the results. The
     * retrieved records are stored in both [itemsList] and
     * [itemListSearched] for use by the item lookup interface.
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
     * Retrieves and prepares the label information required for a box label.
     *
     * The method retrieves item label information, label class information,
     * label layout information, and dangerous goods information from the
     * database for the currently selected item.
     *
     * The retrieved information is combined into [LabelElements] objects
     * and stored in [boxLabelDataList].
     *
     * The initial variant name, box quantity, number of boxes, and kit/set
     * state are also configured after the label data is loaded.
     */
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
                kitSet.value = if (sharedViewModel.currentItem.value.type == "Descriptor Code") {
                    true
                } else {
                    false
                }
            }
        }
    }

    /**
     * Updates the batch number used for the box label.
     *
     * The supplied value is also stored in
     * [SharedViewModel.currentOrderNumber] for use by other parts of the
     * application.
     *
     * @param newValue The new batch number.
     */
    fun onBatchNumber(newValue: String) {
        batchNumber.value = newValue
        sharedViewModel.currentOrderNumber.value = newValue
    }

    /**
     * Updates whether the selected label should be treated as a kit or set.
     *
     * The new value is applied to [kitSet] and propagated to every
     * [LabelElements] entry in [boxLabelDataList].
     *
     * @param newValue True when the label represents a kit or set.
     */
    fun onKitSet(newValue: Boolean) {
        kitSet.value = newValue
        boxLabelDataList.forEach {
            it.kitset = newValue
        }
    }

    /**
     * Updates the quantity of items contained in each box.
     *
     * When a non-empty value is supplied, the quantity is converted to an
     * integer and applied to every label element in [boxLabelDataList].
     *
     * @param newValue The new box quantity as text.
     */
    fun onBoxQty(newValue: String) {
        boxQty.value = newValue

        if (newValue != "") {
            boxLabelDataList.forEach {
                it.itemInfo.BOXQTY = boxQty.value.toInt()
            }
        }
    }

    /**
     * Updates the number of boxes to be labelled.
     *
     * @param newValue The new number of boxes as text.
     */
    fun onNumberOfBoxes(newValue: String) {
        numberOfBoxes.value = newValue
    }

    /**
     * Updates the item search field and filters the available items.
     *
     * The search text is split into individual terms. An item is included in
     * [itemListSearched] when every search term is found within its item
     * code, description, category, or barcode.
     *
     * Searching is case-insensitive.
     *
     * If the search field is blank, the search results are cleared and the
     * search box is hidden.
     *
     * @param newValue The new text entered into the search field.
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
     * Confirms the current box label configuration.
     *
     * Displays the label preview and closes the current box label
     * configuration screen.
     */
    fun onOk() {
        showLabel = true
        sharedViewModel.showLabelPreview.value = true
        closeTestScreen.value = true
    }

    /**
     * Cancels the current box label configuration.
     *
     * Closes the box label configuration screen without displaying the
     * label preview.
     */
    fun onCancel() {
        closeTestScreen.value = true
    }
}