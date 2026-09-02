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
 * ViewModel responsible for managing the state and operations required when
 * adding a new assembly line.
 *
 * This ViewModel manages item and descriptor selection, assembly step selection,
 * line quantity entry, search functionality, popup state, and saving the new
 * assembly line to the database.
 *
 * @property dataStoreManager Provides access to persisted application settings
 * and configuration required by the API layer.
 * @property sharedViewModel Provides shared application state, including the
 * currently selected item, assembly order, current user, and assembly lines.
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

    /**
     * Toggles the visibility of the assembly step selection list.
     */
    fun stepPressed() {
        showStepList = !showStepList
    }

    /**
     * Clears the item search field.
     */
    fun clearSearchField() {
        Searchfield = ""
    }

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
     * its content before displaying it.
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
     * Resets the state used to close the add-line screen.
     */
    fun closeTestScreen() {
        closeTestScreen.value = false
    }

    /**
     * Closes the item search popup and updates the search field with the
     * currently selected item's description.
     */
    fun closeSearchBoxs() {
        Searchfield = sharedViewModel.currentItem.value.description
        showSearchBox = false
        openItemList.value = false
    }

    /**
     * Resets the message popup open state.
     *
     * This method currently sets the state to `false`, allowing the popup-open
     * event to be consumed or reset by the calling UI.
     */
    fun openPopupMessage() {
        openPopupMessage.value = false
    }

    /**
     * Retrieves all active items and descriptors from the database.
     *
     * Item and descriptor records are combined using a SQL `UNION ALL` query.
     * Obsolete records are excluded. The retrieved records are used to populate
     * both the complete item list and the initially searched item list.
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
     * Updates the quantity entered for the new assembly line.
     *
     * @param newValue The new quantity value entered by the user.
     */
    fun onAddLineQty(newValue: String) {
        lineQty.value = newValue
    }

    /**
     * Updates and filters the item search field.
     *
     * The search is performed against the item code, description, category,
     * and barcode. Multiple search terms are supported, and an item must
     * contain all supplied terms to be included in the results.
     *
     * Changing the search field also clears the currently selected item in
     * [sharedViewModel].
     *
     * @param newValue The new search text entered by the user.
     */
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

    /**
     * Loads the assembly step names available for the current assembly.
     *
     * Existing step names are copied from [assemblyStepNames]. Any predefined
     * step names from [StepNames] that are not already present are then added.
     * The first resulting step name is selected as the current step.
     *
     * @param assemblyStepNames The list of step names already associated with
     * the current assembly.
     */
    fun onStepNamesLoad(assemblyStepNames: SnapshotStateList<Any>) {
        stepNameList.clear()
        for (i in 0 until assemblyStepNames.size) {
            stepNameList.add(assemblyStepNames[i])
        }
        for (i in 0 until StepNames.entries.size) {
            if (!stepNameList.contains(StepNames.entries[i].toStringName())) {
                stepNameList.add(StepNames.entries[i].toStringName())
            }
        }
        stepName.value = stepNameList[0].toString()
    }

    /**
     * Updates the selected assembly step name and closes the step selection list.
     *
     * @param newValue The newly selected step name.
     */
    fun onDropDownChange(newValue: Any) {
        stepName.value = newValue.toString()
        showStepList = false
    }

    /**
     * Retrieves all existing assembly lines matching the supplied line code.
     *
     * @param code The line code used to filter the current assembly lines.
     * @return A list containing all assembly lines whose line code matches
     * [code].
     */
    fun currentLineNumbers(code: String): List<APICallTables.AssemblyLines> {
        val lines = mutableStateListOf<APICallTables.AssemblyLines>()

        for (i in 0 until (sharedViewModel.currentAssemblyLines?.size ?: 1)) {
            if (sharedViewModel.currentAssemblyLines?.get(i)?.LINECODE == code) lines.add(sharedViewModel.currentAssemblyLines!![i])
        }
        return lines
    }

    /**
     * Saves the currently configured assembly line to the database.
     *
     * The new line number is calculated from the number of existing assembly
     * lines, with the first line using line number 10 and subsequent lines
     * increasing by increments of 10.
     *
     * On successful insertion, the screen state and input fields are reset and
     * a success snackbar message is displayed. If the database operation fails,
     * an error snackbar message is displayed instead.
     */
    fun onSave() {
        Log.d("Add Line Save", "sharedViewModel.currentAssemblyLines = ${sharedViewModel.currentAssemblyLines}")
        Log.d("Add Line Save", "sharedViewModel.currentAssemblyLines condtion = ${sharedViewModel.currentAssemblyLines?.isEmpty() == true}")
        val lineNumber = if (sharedViewModel.currentAssemblyLines.isNullOrEmpty()) {
            10
        } else {
            (sharedViewModel.currentAssemblyLines?.size?.plus(1)?.times(10)!!)
        }
        Log.d("Add Line Save", "lineNumber = $lineNumber")
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
     * Cancels adding the assembly line.
     *
     * Clears the item search field and line quantity before signalling that
     * the add-line screen should be closed.
     */
    fun onCancel() {
        onSearchFieldChange("")
        onAddLineQty("")
        closeTestScreen.value = true
    }
}