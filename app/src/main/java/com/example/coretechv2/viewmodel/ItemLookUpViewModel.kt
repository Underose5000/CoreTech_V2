package com.example.coretechv2.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coretechv2.dataclasses.APICallTables
import com.example.coretechv2.dataclasses.ItemDescriptorItem
import com.example.coretechv2.dataclasses.MessageItems
import com.example.coretechv2.repository.APICall
import com.example.coretechv2.repository.DataStoreManager
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing item and descriptor lookup data.
 *
 * [ItemLookUpViewModel] retrieves item and descriptor information from the
 * database through [APICall], maintains the list of available items, provides
 * search functionality, and stores the currently selected item.
 *
 * The ViewModel also communicates the selected item to [SharedViewModel] so
 * that it can be accessed by other screens within the application.
 *
 * @param dataStoreManager Provides access to application settings and API
 * configuration required by [APICall].
 * @param sharedViewModel Shared ViewModel used to pass the selected item to
 * other parts of the application.
 */
class ItemLookUpViewModel(private val dataStoreManager: DataStoreManager, var sharedViewModel: SharedViewModel) : ViewModel() {
    private val apiCall = APICall(dataStoreManager)
    var popupMessage = MessageItems()
    var closePopupMessage = mutableStateOf(false)
        private set
    var closeTestScreen = mutableStateOf(false)
        private set
    var openPopupMessage = mutableStateOf(false)
        private set

    var selectedItem by mutableStateOf(ItemDescriptorItem())
        private set
    var Searchfield by mutableStateOf("")
        private set
    var itemsList = mutableStateListOf<APICallTables.ItemDescriptor>()
        private set
    var itemListSearched = mutableStateListOf<APICallTables.ItemDescriptor>()
        private set

    /**
     * Closes the popup message.
     *
     * Sets [closePopupMessage] to false so that the popup can remain closed
     * after the associated UI action has been processed.
     */
    fun closePopupMessage() {
        closePopupMessage.value = false
    }

    /**
     * Closes the item lookup/test screen.
     *
     * Sets [closeTestScreen] to false so that the close event can be reset
     * after it has been handled by the UI.
     */
    fun closeTestScreen() {
        closeTestScreen.value = false
    }

    /**
     * Resets the popup message open state.
     *
     * Sets [openPopupMessage] to false after the popup open event has been
     * processed.
     */
    fun openPopupMessage() {
        openPopupMessage.value = false
    }

    /**
     * Retrieves all non-obsolete items and descriptors from the database.
     *
     * Item and descriptor records are combined into a single list using a
     * database UNION query. The resulting data is stored in [itemsList] and
     * [itemListSearched] so that both the complete and currently displayed
     * lists are synchronised.
     *
     * The database operation is performed within [viewModelScope] to ensure
     * that the coroutine is cancelled automatically when the ViewModel is
     * cleared.
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
     * Updates the search field and filters the available item list.
     *
     * When the search field is blank, all items from [itemsList] are displayed.
     * Otherwise, the search text is split into individual terms and an item
     * must contain every search term within its code, description, category,
     * or barcode to be included in [itemListSearched].
     *
     * Searching is case-insensitive and allows multiple search terms to be
     * entered in any order.
     *
     * @param newValue The new text entered into the search field.
     */
    fun onSearchFieldChange(newValue: String) {
        Searchfield = newValue
        itemListSearched.clear()
        if (newValue.isBlank()) {
            itemListSearched.addAll(itemsList)
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
        }
    }

    /**
     * Sets the supplied item as the currently selected item.
     *
     * The selected item's properties are copied into [selectedItem], and a
     * copy of the resulting item is stored in [SharedViewModel.currentItem]
     * so that other screens can access the selected item.
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

}