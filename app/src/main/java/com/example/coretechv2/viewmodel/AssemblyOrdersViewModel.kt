package com.example.coretechv2.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.coretechv2.dataclasses.APICallTables
import com.example.coretechv2.dataclasses.APICallTypes
import com.example.coretechv2.dataclasses.ItemDescriptorItem
import com.example.coretechv2.dataclasses.PopupItems
import com.example.coretechv2.repository.APICall
import com.example.coretechv2.repository.DataStoreManager
import com.example.coretechv2.repository.toDateFormatYYYYMMDD
import com.example.coretechv2.ui.screen.AddAssemblyOrderScreen
import com.example.coretechv2.ui.screen.ItemLookUpScreen
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * ViewModel responsible for managing assembly orders and the creation of
 * new assembly orders.
 *
 * This ViewModel handles retrieving, searching, and displaying assembly
 * orders, as well as retrieving item and BOM information required when
 * creating a new assembly order.
 *
 * It also manages the UI state for searching, item selection, assembly
 * version selection, popups, and the assembly order list. Assembly orders
 * and related data are retrieved and updated through [APICall].
 *
 * @property dataStoreManager Provides access to stored application and API settings.
 * @property sharedViewModel Provides shared application state used by the
 * assembly order screens and other ViewModels.
 */
class AssemblyOrdersViewModel(private val dataStoreManager: DataStoreManager, var sharedViewModel: SharedViewModel) : ViewModel() {

    private val apiCall = APICall(dataStoreManager)
    var popupDetails = PopupItems().copy()
    var openItemList = mutableStateOf(false)
        private set
    var showSearchBox by mutableStateOf(false)
        private set
    var submitButtonEnabled by mutableStateOf(false)
        private set
    var addSearchField by mutableStateOf("")
        private set
    var assemblyQty by mutableStateOf("")
        private set
    var assemblyVersion by mutableStateOf("")
        private set
    var assemblyVersionCode by mutableStateOf("")
        private set

    var assemblyVersionQty by mutableStateOf(0.0)
        private set

    var itemListSearched = mutableStateListOf<APICallTables.ItemDescriptor>()
        private set
    var itemsList = mutableStateListOf<APICallTables.ItemDescriptor>()
        private set
    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent = _snackbarEvent.asSharedFlow()

    var showAllOrders = mutableStateOf(false)
        private set
    var AssemblyOrdersList = mutableStateListOf<APICallTables.AssemblyHeader>()
        private set

    var allAssemblyOrdersList = mutableStateListOf<APICallTables.AssemblyHeader>()
        private set

    var AssemblyOrderListSearached = mutableStateListOf<APICallTables.AssemblyHeader>()
        private set

    var selectedOrder by mutableStateOf<APICallTables.AssemblyHeader?>(null)
        private set

    var assemblyBOMHeaderList = mutableStateListOf<APICallTables.assemblyBOMMaster?>()
        private set
    var Searchfield by mutableStateOf("")
        private set

    var versionPressed by mutableStateOf(false)
        private set

    /**
     * Opens the screen for adding a new assembly order.
     *
     * The add-order popup is configured and opened, and the save operation is
     * set to use an insert operation.
     *
     * @param navController Navigation controller used to navigate to the
     * assembly order details screen after the order is created.
     */
    fun addOrdersPressed(navController: NavController) {
        addSearchField = ""
        popupDetails.width = 700
        popupDetails.height = 500
        popupDetails.content = {
            sharedViewModel.updateSaveType(APICallTypes.INSERT)
            AddAssemblyOrderScreen(navController, sharedViewModel, this)
        }
        sharedViewModel.popupDetails = popupDetails
        sharedViewModel.openPopup()
    }

    /**
     * Opens the assembly version selection interface.
     */
    fun onVersionPressedOpen() {
        versionPressed = true
    }

    /**
     * Closes the assembly version selection interface.
     */
    fun onVersionPressedClose() {
        versionPressed = false
    }

    /**
     * Closes the item search box and item selection list.
     *
     * The search field is updated with the description of the currently selected
     * item before the search interface is closed.
     */
    fun closeSearchBoxs() {
        addSearchField = sharedViewModel.currentItem.value.description
        showSearchBox = false
        openItemList.value = false

    }

    /**
     * Updates the assembly order search field and filters the displayed orders.
     *
     * Searches can be performed against the item description, item code, and
     * order number when the search contains only letters, numbers, and spaces.
     * Otherwise, the search is performed against the item description only.
     *
     * Multiple search terms must all be present for an order to be included
     * in the results. The search is performed against either the current open
     * orders or all assembly orders depending on the [showAllOrders] state.
     *
     * @param newValue The new search text entered by the user.
     */
    fun onSearchFieldChange(newValue: String) {
        val searchTerms = newValue
            .trim()
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }

        val searchAllFields = newValue.matches(Regex("[Aa0-9\\s]+"))
        Searchfield = newValue
        AssemblyOrderListSearached.clear()
        if (newValue.isBlank() && !showAllOrders.value) {
            AssemblyOrderListSearached.addAll(AssemblyOrdersList)
        } else if (newValue.isBlank() && showAllOrders.value) {
            AssemblyOrderListSearached.addAll(allAssemblyOrdersList)
        } else if (showAllOrders.value) {
            AssemblyOrderListSearached.addAll(
                allAssemblyOrdersList.filter { order ->

                    searchTerms.all { term ->
                        if (searchAllFields) {
                            order.ITEMDESCRIPTION.contains(term, ignoreCase = true) ||
                                    order.ITEMCODE.contains(term, ignoreCase = true) ||
                                    order.ORDERNUMBER.contains(term, ignoreCase = true)
                        } else {
                            order.ITEMDESCRIPTION.contains(term, ignoreCase = true)
                        }
                    }
                }
            )
        } else {
            AssemblyOrderListSearached.addAll(
                AssemblyOrdersList.filter { order ->

                    searchTerms.all { term ->
                        if (searchAllFields) {
                            order.ITEMDESCRIPTION.contains(term, ignoreCase = true) ||
                                    order.ITEMCODE.contains(term, ignoreCase = true) ||
                                    order.ORDERNUMBER.contains(term, ignoreCase = true)
                        } else {
                            order.ITEMDESCRIPTION.contains(term, ignoreCase = true)
                        }
                    }
                }
            )
        }
    }

    /**
     * Updates the item search field and filters the available item list.
     *
     * The search checks the item code, description, category, and barcode.
     * Multiple search terms must all be present for an item to be included
     * in the results.
     *
     * @param newValue The new item search text.
     */
    fun onAddSearchFieldChange(newValue: String) {
        addSearchField = newValue
        itemListSearched.clear()
        if (newValue.isBlank()) {
            showSearchBox = false
        } else {
            val searchTerms = addSearchField
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
     * Updates the quantity of the assembly order being created.
     *
     * @param newValue The new assembly quantity.
     */
    fun onAssemblyqty(newValue: String) {
        assemblyQty = newValue
    }

    /**
     * Selects an assembly BOM version for the new assembly order.
     *
     * The selected assembly version, item code, and batch quantity are stored
     * and the submit button is enabled when all required values are present.
     *
     * @param newValue The selected assembly BOM information.
     */
    fun onAssemblyVersion(newValue: APICallTables.assemblyBOMMaster?) {
        assemblyVersion = newValue!!.ASSEMBLYVERSION
        assemblyVersionCode = newValue.ITEMCODE
        assemblyVersionQty = newValue.BATCHQTY
        if (assemblyVersion.isNotEmpty() && assemblyVersionCode.isNotEmpty() && assemblyVersionQty != 0.0) {
            submitButtonEnabled = true
        }
    }

    /**
     * Sets the currently selected assembly order.
     *
     * @param order The assembly order selected by the user.
     */
    fun getSelectedOrder(order: APICallTables.AssemblyHeader) {
        selectedOrder = order
    }

    /**
     * Toggles between displaying open assembly orders and all assembly orders.
     *
     * The current search field is reapplied after changing the displayed order
     * list.
     */
    fun pastOrdersPressed() {
        showAllOrders.value = !showAllOrders.value
        onSearchFieldChange(Searchfield)
    }

    /**
     * Opens the item lookup popup used when creating an assembly order.
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
     * Reloads the assembly order lists and reapplies the current search filter.
     *
     * Both the open assembly orders and all assembly orders are retrieved before
     * the appropriate list is displayed and filtered.
     */
    fun reload() {
        Log.d("Timing", "reloaded")
        retrieveAssemblyOrders()
        retrieveAllAssemblyOrders()
        AssemblyOrderListSearached.clear()
        if (!showAllOrders.value) {
            AssemblyOrdersList.let {
                AssemblyOrderListSearached.addAll(it)
            }
        } else {
            allAssemblyOrdersList.let {
                AssemblyOrderListSearached.addAll(it)
            }
        }
        onSearchFieldChange(Searchfield)
    }

    /**
     * Initialises the assembly order lists using data from the shared ViewModel
     * when available.
     *
     * If the shared ViewModel does not contain the required data, the relevant
     * information is retrieved from the API instead.
     */
    fun setAllAssemblyOrders() {
        Log.d("Orders", "set All Assembly Orders Started")
        if (sharedViewModel.AssemblyOrdersList.isEmpty()) {
            retrieveAssemblyOrders()
            AssemblyOrderListSearached.clear()
            AssemblyOrdersList.let {
                AssemblyOrderListSearached.addAll(it)
            }
            Log.d("Orders", "set All Assembly Orders using retrieveAssemblyOrders. list size = ${AssemblyOrderListSearached.size}. Searchfield = $Searchfield")
        } else {
            AssemblyOrdersList.clear()
            AssemblyOrderListSearached.clear()
            sharedViewModel.AssemblyOrdersList.let {
                AssemblyOrdersList.addAll(it)
                AssemblyOrderListSearached.addAll(it)
            }
            Log.d("Orders", "set All Assembly Orders using SharedViewmodel. list size = ${AssemblyOrderListSearached.size}. Searchfield = $Searchfield")
        }
        if (sharedViewModel.allAssemblyOrdersList.isEmpty()) {
            retrieveAllAssemblyOrders()
        } else {
            allAssemblyOrdersList.clear()
            sharedViewModel.allAssemblyOrdersList.let {
                allAssemblyOrdersList.addAll(it)
            }
        }
        onSearchFieldChange(Searchfield)
    }

    /**
     * Initialises the record order lists using data from the shared ViewModel
     * when available.
     *
     * If the shared ViewModel does not contain the required data, the relevant
     * information is retrieved from the API instead.
     */

    /**
     * Retrieves all open assembly orders from the API.
     *
     * The retrieved orders are stored both locally in this ViewModel and in the
     * shared ViewModel.
     */
    fun retrieveAssemblyOrders() {
        viewModelScope.launch {
            val assemblyOrdersListCall: List<APICallTables.AssemblyHeader>? = apiCall.query("SELECT * FROM AssemblyHeader where OrderStatus = 'Open' order by OrderNumber DESC")

            AssemblyOrdersList.clear()
            sharedViewModel.AssemblyOrdersList.clear()
            assemblyOrdersListCall?.let {
                AssemblyOrdersList.addAll(it)
                sharedViewModel.AssemblyOrdersList.addAll(it)
            }
        }
    }

    /**
     * Retrieves all assembly orders from the API, including completed orders.
     *
     * The retrieved orders are stored both locally in this ViewModel and in the
     * shared ViewModel.
     */
    fun retrieveAllAssemblyOrders() {
        viewModelScope.launch {
            val assemblyOrdersListCall: List<APICallTables.AssemblyHeader>? = apiCall.query("SELECT * FROM AssemblyHeader order by OrderNumber DESC")

            allAssemblyOrdersList.clear()
            sharedViewModel.allAssemblyOrdersList
            assemblyOrdersListCall?.let {
                allAssemblyOrdersList.addAll(it)
                sharedViewModel.allAssemblyOrdersList.addAll(it)
            }
        }
    }

    /**
     * Retrieves all non-obsolete items from the item master.
     *
     * The retrieved items are stored in the complete item list and the searched
     * item list used by the item lookup interface.
     */
    fun retrieveItems() {
        viewModelScope.launch {
            val call = "SELECT ITEMCODE AS CODE, ITEMDESCRIPTION AS DESCRIPTION, ITEMUNIT AS UNIT, ITEMSTATUS AS STATUS, ITEMBARCODE AS BARCODE, ITEMCATEGORY AS CATEGORY, " +
                    "ONHANDQTY, SUPPLYQTY, DEMANDQTY, AVAILABLEQTY, FREEQTY, 'Item Code' AS TYPE, SYSUNIQUEID FROM ITEMMASTER where ITEMSTATUS <> 'Obsolete' order by 12 DESC, 1 ASC"

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
     * Retrieves BOM information for the currently selected item.
     *
     * The retrieved BOM headers are stored in [assemblyBOMHeaderList]. If BOM
     * information is available, the first assembly version is automatically
     * selected.
     */
    fun retrieveBOMInfo() {
        viewModelScope.launch {
            val bomcall: List<APICallTables.assemblyBOMMaster>? = apiCall.query("SELECT * FROM BOMMASTER WHERE ASSEMBLYCODE = '${sharedViewModel.currentItem.value.code}'")

            assemblyBOMHeaderList.clear()
            bomcall?.let {
                assemblyBOMHeaderList.addAll(it)
            }
            if (assemblyBOMHeaderList.isNotEmpty()) {
                onAssemblyVersion(assemblyBOMHeaderList.first())
            }
        }
    }

    /**
     * Ensures that the standard assembly steps exist for all BOMs.
     *
     * The method checks each BOM for the standard Assembly, Down Fill,
     * Packaging, and Recovery steps. Missing steps are inserted into the BOM
     * steps table with their corresponding sequence numbers and descriptions.
     */
    fun addSteps() {
        viewModelScope.launch {
            val bomcall: List<APICallTables.assemblyBOMMaster>? = apiCall.query("SELECT * FROM BOMMASTER")
            assemblyBOMHeaderList.clear()
            bomcall?.let {
                assemblyBOMHeaderList.addAll(it)
            }
            if (assemblyBOMHeaderList.isNotEmpty()) {
                for (x in 0 until assemblyBOMHeaderList.size) {
                    val checkCall: List<APICallTables.assemblyBOMSteps>? = apiCall.query("SELECT ITEMCODE, STEPNAME FROM BOMSTEPS where ITEMCODE = '${assemblyBOMHeaderList[x]?.ITEMCODE}'")

                    if (checkCall != null) {
                        if (!checkCall.contains(APICallTables.assemblyBOMSteps(assemblyBOMHeaderList[x]?.ITEMCODE, STEPNAME = "Assembly"))) {
                            val assemblyCall = "INSERT INTO BOMSTEPS (" +
                                    "ITEMCODE, STEPSEQUENCE, STEPNAME, STEPDESCRIPTION, STEPOVERLAP" +
                                    ") VALUES (" +
                                    "'${assemblyBOMHeaderList[x]?.ITEMCODE}', 10, 'Assembly', 'Standard Assembly Step', 'End of Step')"
                            val response = apiCall.insertUpdateDelete(assemblyCall)
                            if (response != "200 OK") {

                            }
                        }
                        if (!checkCall.contains(APICallTables.assemblyBOMSteps(assemblyBOMHeaderList[x]?.ITEMCODE, STEPNAME = "Down Fill"))) {
                            val downCall = "INSERT INTO BOMSTEPS (" +
                                    "ITEMCODE, STEPSEQUENCE, STEPNAME, STEPDESCRIPTION, STEPOVERLAP" +
                                    ") VALUES (" +
                                    "'${assemblyBOMHeaderList[x]?.ITEMCODE}', 20, 'Down Fill', 'Downfilling Items', 'End of Step')"
                            val response = apiCall.insertUpdateDelete(downCall)
                            if (response != "200 OK") {

                            }
                        }
                        if (!checkCall.contains(APICallTables.assemblyBOMSteps(assemblyBOMHeaderList[x]?.ITEMCODE, STEPNAME = "Packaging"))) {
                            val packagingCall = "INSERT INTO BOMSTEPS (" +
                                    "ITEMCODE, STEPSEQUENCE, STEPNAME, STEPDESCRIPTION, STEPOVERLAP" +
                                    ") VALUES (" +
                                    "'${assemblyBOMHeaderList[x]?.ITEMCODE}', 30, 'Packaging', 'Packaging Items', 'End of Step')"
                            val response = apiCall.insertUpdateDelete(packagingCall)
                            if (response != "200 OK") {

                            }
                        }
                        if (!checkCall.contains(APICallTables.assemblyBOMSteps(assemblyBOMHeaderList[x]?.ITEMCODE, STEPNAME = "Recovery"))) {
                            val recoveryCall = "INSERT INTO BOMSTEPS (" +
                                    "ITEMCODE, STEPSEQUENCE, STEPNAME, STEPDESCRIPTION, STEPOVERLAP" +
                                    ") VALUES (" +
                                    "'${assemblyBOMHeaderList[x]?.ITEMCODE}', 40, 'Recovery', 'Recovery', 'End of Step')"
                            val response = apiCall.insertUpdateDelete(recoveryCall)
                            if (response != "200 OK") {

                            }
                        }

                    }
                }
            }
        }
    }

    /**
     * Clears the fields used when creating an assembly order.
     *
     * The item search field, selected item, assembly quantity, and assembly
     * version are reset to their initial values.
     */
    fun clearFields() {
        onAddSearchFieldChange("")
        sharedViewModel.currentItem.value = ItemDescriptorItem()
        assemblyQty = ""
        assemblyVersion = ""
    }

    /**
     * Cancels the creation of a new assembly order.
     *
     * The selected assembly version and item are cleared before the add-order
     * popup is closed.
     */
    fun onAddCancel() {
        assemblyVersion = ""
        sharedViewModel.currentItem.value = ItemDescriptorItem()
        sharedViewModel.closePopup()
    }

    /**
     * Creates and saves a new assembly order.
     *
     * A new assembly order number is generated and an assembly header is inserted.
     * The BOM lines associated with the selected assembly version are then
     * converted into assembly order lines using the requested order quantity
     * and assembly batch quantity.
     *
     * If any part of the operation fails, the method attempts to remove the
     * partially created order and displays an appropriate error message.
     *
     * When the order is successfully created, the current order information is
     * stored in the shared ViewModel and the user is navigated to the assembly
     * order details screen.
     *
     * @param navController Navigation controller used to navigate to the newly
     * created assembly order.
     */
    fun onAddSave(navController: NavController) {
        submitButtonEnabled = false
        sharedViewModel.snackBarMessage("Assembly Saving")
        viewModelScope.launch {
            val date = toDateFormatYYYYMMDD(LocalDate.now())
            val requiredDate = toDateFormatYYYYMMDD(LocalDate.now().plusDays(3))
            val orderNumber: List<APICallTables.StringData>? = apiCall.query("SELECT 'A' || CAST(CAST(SUBSTRING(MAX(ORDERNUMBER) FROM 2) AS INTEGER) + 1 AS VARCHAR(10)) as STRING FROM ASSEMBLYHEADER")
            val heardercall = "INSERT INTO ASSEMBLYHEADER (" +
                    "ORDERNUMBER, ITEMCODE, ITEMUNIT, ITEMDESCRIPTION, ASSEMBLYVERSION, ORDERQTY, REQUIREDDATE, ORDERDATE, SYSUSERCREATED" +
                    ") VALUES (" +
                    "'${orderNumber?.first()?.STRING}', '${sharedViewModel.currentItem.value.code}', '${sharedViewModel.currentItem.value.unit}', '${sharedViewModel.currentItem.value.description}', " +
                    "'$assemblyVersion', $assemblyQty, '$requiredDate', '$date', '${sharedViewModel.currentUser.value}'" +
                    ")"
            val deleteHeaderCall = "DELETE FROM ASSEMBLYHEADER WHERE ORDERNUMBER = '${orderNumber?.first()?.STRING}'"
            val deleteLineCall = "DELETE FROM ASSEMBLYLINES WHERE ORDERNUMBER = '${orderNumber?.first()?.STRING}'"

            val response = apiCall.insertUpdateDelete(heardercall)
            if (response == "200 OK") {
                val orderHeader: List<APICallTables.AssemblyHeader>? = apiCall.query("SELECT * FROM ASSEMBLYHeader WHERE ORDERNUMBER = '${orderNumber?.first()?.STRING}'")
                val bomLines: List<APICallTables.assemblyBOMLines>? = apiCall.query("SELECT * FROM BOMLINES WHERE ITEMCODE = '$assemblyVersionCode'")

                if (bomLines!!.isEmpty() || orderHeader!!.isEmpty()) {
                    sharedViewModel.snackBarMessage("Error Finding Lines, Please Try Again")
                    val deleteResponse = apiCall.insertUpdateDelete(deleteHeaderCall)
                    if (deleteResponse != "200 OK") {
                        sharedViewModel.snackBarMessage("Error Found, Please See Daniel")
                    }
                } else {
                    var lineCheck = 0
                    for (i in 0 until bomLines.size) {
                        val orderQty = (bomLines[i].PERBATCHQTY * orderHeader[0].ORDERQTY) / assemblyVersionQty
                        val lineCall = "INSERT INTO ASSEMBLYLINES (" +
                                "ORDERNUMBER, HEADERSYSUNIQUEID, CODETYPE, LINECODE, ORDERQTY, LINEDESCRIPTION, LINEUNIT, STEPNAME, LINENUMBER, RUNORSETUP, POSITIONREFERENCE, SYSUSERCREATED" +
                                ") VALUES (" +
                                "'${orderHeader[0].ORDERNUMBER}', ${orderHeader[0].SYSUNIQUEID}, '${bomLines[i].CODETYPE}', '${bomLines[i].LINECODE}', $orderQty, '${bomLines[i].LINEDESCRIPTION}', '${bomLines[i].LINEUNIT}', " +
                                "'${bomLines[i].STEPNAME}', ${bomLines[i].LINENUMBER}, '${bomLines[i].RUNORSETUP}', '${bomLines[i].POSITIONREFERENCE}', '${sharedViewModel.currentUser.value}'" +
                                ")"


                        val lineResponse = apiCall.insertUpdateDelete(lineCall)
                        if (lineResponse == "200 OK") {
                            lineCheck += 1
                        } else {
                            sharedViewModel.snackBarMessage("Error Adding Lines, Please Delete Order and Try Again")
                            val deleteLineResponse = apiCall.insertUpdateDelete(deleteLineCall)
                            val deleteHeaderResponse = apiCall.insertUpdateDelete(deleteHeaderCall)
                            if (deleteHeaderResponse != "200 OK" || deleteLineResponse != "200 OK") {
                                sharedViewModel.snackBarMessage("Error Found, Please See Daniel")
                            }
                            return@launch
                        }
                    }
                    if (lineCheck != bomLines.size) {
                        sharedViewModel.snackBarMessage("Some Assembly Lines Missing, Please Delete Order and Try Again")
                    } else {
                        sharedViewModel.snackBarMessage("Assembly Order Added")
                        sharedViewModel.currentOrderNumber.value = orderHeader[0].ORDERNUMBER
                        sharedViewModel.currentItemCode.value = sharedViewModel.currentItem.value.code
                        sharedViewModel.closePopup()
                        navController.navigate("assemblyorderdetail")
                    }
                }
            } else {
                sharedViewModel.snackBarMessage("Error Adding Header, Please Try Again")
            }
        }
    }
}

