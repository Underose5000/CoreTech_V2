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
     * Resets the popup message visibility state.
     */
    fun closePopupMessage(){
        closePopupMessage.value = false
    }

    /**
     * Closes the gel test screen and triggers navigation back.
     */
    fun closeTestScreen(){
        closeTestScreen.value = false
    }

    /**
     * Resets the popup trigger state.
     */
    fun openPopupMessage(){
        openPopupMessage.value = false
    }


    /**
     * Retrieves the current list of Items and Descriptors
     *
     * This function:
     * - Calls database on API
     * - Retrieves all items that are not obsolete
     * - Unions all descriptors that are not obsolete
     *
     * On success:
     * - Returns list of items/descriptors to itemList
     *
     * On failure:
     * - Returns empty list to itemList
     */
    fun retrieveItems(){
        viewModelScope.launch {
            val call = "SELECT ITEMCODE AS CODE, ITEMDESCRIPTION AS DESCRIPTION, ITEMUNIT AS UNIT, ITEMSTATUS AS STATUS, ITEMBARCODE AS BARCODE, ITEMCATEGORY AS CATEGORY, " +
                    "ONHANDQTY, SUPPLYQTY, DEMANDQTY, AVAILABLEQTY, FREEQTY, 'Item Code' AS TYPE, SYSUNIQUEID FROM ITEMMASTER where ITEMSTATUS <> 'Obsolete' " +
                    "UNION ALL " +
                    "SELECT DESCRIPTORCODE AS CODE, DESCRIPTORDESCRIPTION AS DESCRIPTION, DESCRIPTORUNIT AS UNIT, DESCRIPTORSTATUS AS STATUS, DESCRIPTORBARCODE AS BARCODE, DESCRIPTORCATEGORY AS CATEGORY, " +
                    "NULL AS ONHANDQTY, NULL AS SUPPLYQTY, NULL AS DEMANDQTY, NULL AS AVAILABLEQTY, NULL AS FREEQTY, 'Descriptor Code' AS TYPE, SYSUNIQUEID FROM DESCRIPTORMASTER where DESCRIPTORSTATUS <> 'Obsolete'"

            val itemscall : List<APICallTables.ItemDescriptor>? = apiCall.query(call)

            itemsList.clear()
            itemListSearched.clear()
            itemscall?.let{
                itemsList.addAll(it)
                itemListSearched.addAll(it)
            }
        }
    }

    fun onSearchFieldChange(newValue: String){
        Searchfield = newValue
        itemListSearched.clear()
        if (newValue.isBlank()){
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



    fun getSelectedItem(item: APICallTables.ItemDescriptor){
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