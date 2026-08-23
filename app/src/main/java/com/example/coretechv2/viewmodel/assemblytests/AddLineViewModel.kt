package com.example.coretechv2.viewmodel.assemblytests

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coretechv2.dataclasses.APICallTables
import com.example.coretechv2.dataclasses.ItemDescriptorItem
import com.example.coretechv2.dataclasses.MessageItems
import com.example.coretechv2.dataclasses.PopupItems
import com.example.coretechv2.dataclasses.StepNames
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
class AddLineViewModel(private val dataStoreManager: DataStoreManager, var sharedViewModel: SharedViewModel) : ViewModel() {
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
    var Searchfield by mutableStateOf("")
        private set
    var itemsList = mutableStateListOf<APICallTables.ItemDescriptor>()
        private set
    var itemListSearched = mutableStateListOf<APICallTables.ItemDescriptor>()
        private set
    var stepNameList = mutableStateListOf<Any>()
    var showStepList by mutableStateOf(false)
        private set
    var lineQty = mutableStateOf("")
        private set
    var stepName = mutableStateOf("")
        private set
    fun stepPressed(){
        showStepList = !showStepList
    }
    fun clearSearchField(){
        Searchfield = ""
    }


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

    fun onAddLineQty(newValue: String) {
        lineQty.value = newValue
    }

    fun onSearchFieldChange(newValue: String) {
        Searchfield = newValue
        sharedViewModel.currentItem = mutableStateOf(ItemDescriptorItem())
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

    fun onStepNamesLoad(assemblyStepNames: SnapshotStateList<Any>){
        stepNameList.clear()
        for (i in 0 until assemblyStepNames.size){
            stepNameList.add(assemblyStepNames[i])
        }
        for (i in 0 until StepNames.entries.size){
            if (!stepNameList.contains(StepNames.entries[i].toStringName())){
                stepNameList.add(StepNames.entries[i].toStringName())
            }
        }
        stepName.value = stepNameList[0].toString()
    }

    fun onDropDownChange(newValue: Any){
        stepName.value = newValue.toString()
        showStepList = false
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
    fun onSave() {
        Log.d("Add Line Save", "sharedViewModel.currentAssemblyLines = ${sharedViewModel.currentAssemblyLines}" )
        Log.d("Add Line Save", "sharedViewModel.currentAssemblyLines condtion = ${sharedViewModel.currentAssemblyLines?.isEmpty() == true}" )
        val lineNumber = if (sharedViewModel.currentAssemblyLines.isNullOrEmpty()){ 10 } else { (sharedViewModel.currentAssemblyLines?.size?.plus(1)?.times(10)!!) }
        Log.d("Add Line Save", "lineNumber = $lineNumber" )
        viewModelScope.launch {
            val call = "INSERT INTO AssemblyLines " +
                    "(ORDERNUMBER, LINECODE, LINEDESCRIPTION, ORDERQTY, LINEUNIT, STEPNAME, LINENUMBER, CODETYPE, SYSUSERCREATED, SYSUSERMODIFIED)" +
                    "VALUES('${sharedViewModel.currentOrderNumber.value}','${sharedViewModel.currentItem.value.code}','${sharedViewModel.currentItem.value.description}', " +
                    "${lineQty.value},'${sharedViewModel.currentItem.value.unit}','${stepName.value}',${lineNumber},'${sharedViewModel.currentItem.value.type}','${sharedViewModel.currentUser.value}','${sharedViewModel.currentUser.value}')"

            
            val response = apiCall.insertUpdateDelete(call)
            if (response == "200 OK") {
                sharedViewModel.snackBarMessage("Line Saved successfully")
                closeTestScreen.value = true
                onSearchFieldChange("")
                onAddLineQty("")
            } else {
                sharedViewModel.snackBarMessage("Error Saving Line, Please Try Again")
            }


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
        onSearchFieldChange("")
        onAddLineQty("")
        closeTestScreen.value = true
    }
}