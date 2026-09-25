package com.example.coretechv2.viewmodel

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coretechv2.dataclasses.APICallTables
import com.example.coretechv2.dataclasses.ItemDescriptorItem
import com.example.coretechv2.dataclasses.MenuItem
import com.example.coretechv2.dataclasses.PopupItems
import com.example.coretechv2.dataclasses.SwitchItem
import com.example.coretechv2.repository.APICall
import com.example.coretechv2.repository.DataStoreManager
import com.example.coretechv2.repository.testAndAdjustmentLookup
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.collections.set

/**
 * ViewModel responsible for managing assembly order records and their related
 * search, filtering, selection, and comparison state.
 *
 * This ViewModel retrieves assembly orders from the Ostendo API and maintains
 * the lists used to display and filter those orders. It also retrieves the
 * tests, adjustments, and notes associated with assembly orders.
 *
 * The ViewModel manages:
 * - Assembly order retrieval and storage.
 * - Searching by item name, item code, order number, and order date.
 * - Filtering orders by the presence of tests, adjustments, and notes.
 * - Selecting orders for comparison.
 * - Search and filter menu state.
 * - Snackbar events.
 * - State used when creating an assembly order.
 *
 * API requests are performed using [APICall], while persistent application
 * and API settings are provided by [DataStoreManager].
 *
 * @property dataStoreManager Provides access to stored application and API settings.
 * @property sharedViewModel Provides shared application state used by the
 * assembly order screens and other ViewModels.
 */
class AssemblyOrdersRecordViewModel(private val dataStoreManager: DataStoreManager, var sharedViewModel: SharedViewModel) : ViewModel() {

    private val apiCall = APICall(dataStoreManager)
    var popupDetails = PopupItems().copy()
    var assemblyQty by mutableStateOf("")
        private set
    var assemblyVersion by mutableStateOf("")
        private set
    var itemListSearched = mutableStateListOf<APICallTables.ItemDescriptor>()
        private set
    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent = _snackbarEvent.asSharedFlow()
    var compareOrders = mutableStateOf(false)
        private set
    var assemblyOrdersList = mutableStateListOf<APICallTables.AssemblyRecordLookup>()
        private set
    var assemblyOrderListSearched = mutableStateListOf<APICallTables.AssemblyRecordLookup>()
        private set
    var selectedOrder by mutableStateOf<APICallTables.AssemblyRecordLookup?>(null)
        private set
    var searchField by mutableStateOf("")
        private set

    var testLoading by mutableStateOf(true)
        private set

    var hasTest by mutableStateOf(false)
        private set
    var hasAdjustments by mutableStateOf(false)
        private set
    var hasNotes by mutableStateOf(false)
        private set
    var searchByItemName by mutableStateOf(true)
        private set
    var searchByItemCode by mutableStateOf(true)
        private set
    var searchByOrderNumber by mutableStateOf(true)
        private set
    var searchByDate by mutableStateOf(false)
        private set
    var searchByQty by mutableStateOf(false)
        private set


    var selectedOrderList = mutableStateListOf<String>()
    val testAndAdjustments = mutableMapOf<String, SnapshotStateList<SnapshotStateList<Any>>>()
    val notes = mutableMapOf<String, String>()
    var showFilterMenu by mutableStateOf(false)
        private set

    var filtersMenuList = listOf(
        MenuItem(
            title = { "Has Tests" },
            clickEnabled = false,
            switch = SwitchItem(
                checked = { hasTest },
                onCheckedChange = { hasTest = !hasTest
                                    onSearchFieldChange(searchField) },
            )
        ),
        MenuItem(
            title = { "Has Adjustment" },
            clickEnabled = false,
            switch = SwitchItem(
                checked = { hasAdjustments },
                onCheckedChange = { hasAdjustments = !hasAdjustments
                                    onSearchFieldChange(searchField) },
            )
        ),
        MenuItem(
            title = { "Has Note" },
            clickEnabled = false,
            switch = SwitchItem(
                checked = { hasNotes },
                onCheckedChange = { hasNotes = !hasNotes
                                    onSearchFieldChange(searchField) },
            )
        ),
        MenuItem(
            title = { "Search by:" },
            clickEnabled = false,
        ),
        MenuItem(
            title = { "Item Name" },
            onClick = { searchByItemName = !searchByItemName
                        onSearchFieldChange(searchField) },
            icon = { if (searchByItemName) Icons.Filled.CheckBox else Icons.Filled.CheckBoxOutlineBlank },
        ),
        MenuItem(
            title = { "Item Code" },
            onClick = { searchByItemCode = !searchByItemCode
                        onSearchFieldChange(searchField) },
            icon = { if (searchByItemCode) Icons.Filled.CheckBox else Icons.Filled.CheckBoxOutlineBlank },
        ),
        MenuItem(
            title = { "Order Number" },
            onClick = { searchByOrderNumber = !searchByOrderNumber
                        onSearchFieldChange(searchField) },
            icon = { if (searchByOrderNumber) Icons.Filled.CheckBox else Icons.Filled.CheckBoxOutlineBlank },
        ),
        MenuItem(
            title = { "Order Date" },
            onClick = { searchByDate = !searchByDate
                        onSearchFieldChange(searchField) },
            icon = { if (searchByDate) Icons.Filled.CheckBox else Icons.Filled.CheckBoxOutlineBlank },
        ),
        MenuItem(
            title = { "Order Qty" },
            onClick = { searchByQty = !searchByQty
                        onSearchFieldChange(searchField) },
            icon = { if (searchByQty) Icons.Filled.CheckBox else Icons.Filled.CheckBoxOutlineBlank },
        ),
    )

    /**
     * Clears the current assembly order search field.
     *
     * This resets the search text without automatically reapplying the filter.
     */
    fun clearSearchField() {
        searchField = ""
    }

    /**
     * Toggles the assembly order comparison mode.
     *
     * When comparison mode is enabled, orders can be selected for comparison.
     */
    fun compareOrdersPressed() {
        compareOrders.value = !compareOrders.value
    }

    /**
     * Adds an assembly order number to the list of orders selected for comparison.
     *
     * @param order The order number to add to the comparison list.
     */
    fun addSelectedOrder(order: String){
        selectedOrderList.add(order)
    }

    /**
     * Removes an assembly order number from the list of orders selected for comparison.
     *
     * @param order The order number to remove from the comparison list.
     */
    fun removeSelectedOrder(order: String){
        selectedOrderList.remove(order)
    }

    /**
     * Toggles the visibility of the assembly order filter menu.
     *
     * The menu contains filters for tests, adjustments, notes, and the fields
     * that are included when performing a search.
     */
    fun menuPressed() {
        showFilterMenu = !showFilterMenu
    }


    /**
     * Updates the assembly order search text and applies the active filters.
     *
     * Search terms are separated using whitespace, allowing multiple terms to be
     * entered. Every search term must match at least one of the currently enabled
     * search fields for an order to be included in the results.
     *
     * The available search fields are:
     * - Item description.
     * - Item code.
     * - Order number.
     * - Order date.
     *
     * In addition to text searching, orders can be filtered by whether they have:
     * - Tests.
     * - Adjustments.
     * - Notes.
     *
     * When multiple existence filters are enabled, an order must satisfy all of
     * the enabled filters. When multiple search terms are entered, every search
     * term must match at least one enabled search field.
     *
     * If no search text or existence filters are active, all assembly orders are
     * returned.
     *
     * @param newValue The new search text entered by the user.
     */
    fun onSearchFieldChange(newValue: String) {
        val searchTerms = newValue
            .trim()
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }

        searchField = newValue
        assemblyOrderListSearched.clear()
        if (newValue.isBlank() && !hasTest && !hasAdjustments && !hasNotes) {
            Log.d("search", "filtered by has")
            assemblyOrderListSearched.addAll(assemblyOrdersList)
        } else if (newValue.isBlank() ) {
            assemblyOrderListSearched.addAll(
                assemblyOrdersList.filter { order ->
                        (!hasTest || order.TESTSEXISTS == 1) && (!hasAdjustments || order.ADJUSTSEXISTS == 1) && (!hasNotes || order.NOTESEXISTS == 1)
                }
            )
            Log.d("search", "No filter")
        } else if (!hasTest && !hasAdjustments && !hasNotes) {
            assemblyOrderListSearched.addAll(
                assemblyOrdersList.filter { order ->
                    searchTerms.all { term ->
                                (searchByItemName && order.ITEMDESCRIPTION.contains(term, ignoreCase = true))  ||
                                        (searchByItemCode && order.ITEMCODE.contains(term, ignoreCase = true)) ||
                                        (searchByOrderNumber && order.ORDERNUMBER.contains(term, ignoreCase = true)) ||
                                        (searchByDate && order.ORDERDATE.contains(term, ignoreCase = true))

                    }
                }
            )
        }else {
            Log.d("search", "filtered by Search")
            assemblyOrderListSearched.addAll(
                assemblyOrdersList.filter { order ->
                    ((!hasTest || order.TESTSEXISTS == 1) && (!hasAdjustments || order.ADJUSTSEXISTS == 1) && (!hasNotes || order.NOTESEXISTS == 1)) &&
                            (searchTerms.all { term ->
                        (searchByItemName && order.ITEMDESCRIPTION.contains(term, ignoreCase = true))  ||
                                (searchByItemCode && order.ITEMCODE.contains(term, ignoreCase = true)) ||
                                (searchByOrderNumber && order.ORDERNUMBER.contains(term, ignoreCase = true)) ||
                                (searchByDate && order.ORDERDATE.contains(term, ignoreCase = true))

                    })
                }
            )
        }
    }

    /**
     * Sets the assembly order currently selected by the user.
     *
     * @param order The assembly order to set as the current selection.
     */
    fun getSelectedOrder(order: APICallTables.AssemblyRecordLookup) {
        selectedOrder = order
    }

    /**
     * Retrieves recent assembly orders from the Ostendo API.
     *
     * Orders from the previous month onward are retrieved and stored in
     * [assemblyOrdersList]. The filtered display list is then updated using the
     * current search text and active filters.
     *
     * The query also determines whether each order has associated tests,
     * adjustments, or notes. These values are stored in the returned
     * [APICallTables.AssemblyRecordLookup] records and are used by the filtering
     * functionality.
     *
     * The loading state is updated when the request has completed.
     */
    fun retrieveAssemblyOrders() {
        val searchDate = LocalDate.now().minusMonths(1)
        viewModelScope.launch {
            val assemblyOrdersListCall: List<APICallTables.AssemblyRecordLookup>? = apiCall.query("SELECT " +
                    "AH.ORDERNUMBER, AH.ORDERSTATUS, AH.ORDERDATE, AH.ITEMCODE, AH.ITEMDESCRIPTION, AH.ITEMUNIT, " +
                    "AH.ORDERQTY, AH.ADDITIONALFIELD_1, AH.ADDITIONALFIELD_2, AH.ADDITIONALFIELD_3, " +
                    "AH.ADDITIONALFIELD_4, AH.ADDITIONALFIELD_5, AH.ADDITIONALFIELD_6, AH.ADDITIONALFIELD_7, " +
                    "AH.ADDITIONALFIELD_8, AH.ADDITIONALFIELD_9, AH.ADDITIONALFIELD_10, AH.ADDITIONALFIELD_11, " +
                    "AH.ADDITIONALFIELD_12, AH.ADDITIONALFIELD_13, AH.SYSUNIQUEID, " +
                    "CASE WHEN " +
                    "   EXISTS (SELECT 1 FROM OSTDEF_VISCOSITY_TESTS T1 WHERE T1.OrderNumber = AH.OrderNumber) " +
                    "   OR EXISTS (SELECT 1 FROM OSTDEF_ELONGATIONAL_TEST T2 WHERE T2.OrderNumber = AH.OrderNumber) " +
                    "   OR EXISTS (SELECT 1 FROM OSTDEF_FLAMMABILITY_TEST T3 WHERE T3.OrderNumber = AH.OrderNumber) " +
                    "   OR EXISTS (SELECT 1 FROM OSTDEF_GELTIME_TESTS T4 WHERE T4.OrderNumber = AH.OrderNumber) " +
                    "   OR EXISTS (SELECT 1 FROM OSTDEF_PEAKEXOTHERM_TEST T5 WHERE T5.OrderNumber = AH.OrderNumber) " +
                    "   OR EXISTS (SELECT 1 FROM OSTDEF_RESISTIVITY_TEST T6 WHERE T6.OrderNumber = AH.OrderNumber) " +
                    "THEN 1 ELSE 0 END AS TESTSEXISTS, " +
                    "CASE WHEN " +
                    "   EXISTS (SELECT 1 FROM OSTDEF_ADJUSTMENTS AD WHERE AD.OrderNumber = AH.OrderNumber) " +
                    "THEN 1 ELSE 0 END AS ADJUSTSEXISTS, " +
                    "CASE WHEN " +
                    "   EXISTS (SELECT 1 FROM OSTDEF_NOTES NOTE WHERE NOTE.IDNUMBER = AH.OrderNumber) " +
                    "THEN 1 ELSE 0 END AS NOTESEXISTS " +
                    "FROM AssemblyHeader AH WHERE AH.ORDERDATE >= '$searchDate' ORDER BY AH.OrderNumber DESC"
            )

            assemblyOrdersList.clear()
            assemblyOrdersListCall?.let {
                assemblyOrdersList.addAll(it)
            }
            assemblyOrderListSearched.clear()
            assemblyOrdersList.let {
                assemblyOrderListSearched.addAll(it)
            }
            onSearchFieldChange(searchField)
            testLoading = false
        }
    }

    /**
     * Retrieves all assembly orders from the Ostendo API.
     *
     * Unlike [retrieveAssemblyOrders], this function does not restrict the query
     * to a particular date range. The retrieved orders replace the current
     * [assemblyOrdersList], after which the active search and filter conditions
     * are reapplied.
     *
     * Once the assembly orders have been retrieved, [retrieveTestDetails] is
     * started to retrieve the tests, adjustments, and notes associated with
     * those orders.
     */
    fun retrieveAllAssemblyOrders() {
        viewModelScope.launch {
            val assemblyOrdersListCall: List<APICallTables.AssemblyRecordLookup>? = apiCall.query("SELECT " +
                    "AH.ORDERNUMBER, AH.ORDERSTATUS, AH.ORDERDATE, AH.ITEMCODE, AH.ITEMDESCRIPTION, AH.ITEMUNIT, " +
                    "AH.ORDERQTY, AH.ADDITIONALFIELD_1, AH.ADDITIONALFIELD_2, AH.ADDITIONALFIELD_3, " +
                    "AH.ADDITIONALFIELD_4, AH.ADDITIONALFIELD_5, AH.ADDITIONALFIELD_6, AH.ADDITIONALFIELD_7, " +
                    "AH.ADDITIONALFIELD_8, AH.ADDITIONALFIELD_9, AH.ADDITIONALFIELD_10, AH.ADDITIONALFIELD_11, " +
                    "AH.ADDITIONALFIELD_12, AH.ADDITIONALFIELD_13, AH.SYSUNIQUEID, " +
                    "CASE WHEN " +
                    "   EXISTS (SELECT 1 FROM OSTDEF_VISCOSITY_TESTS T1 WHERE T1.OrderNumber = AH.OrderNumber) " +
                    "   OR EXISTS (SELECT 1 FROM OSTDEF_ELONGATIONAL_TEST T2 WHERE T2.OrderNumber = AH.OrderNumber) " +
                    "   OR EXISTS (SELECT 1 FROM OSTDEF_FLAMMABILITY_TEST T3 WHERE T3.OrderNumber = AH.OrderNumber) " +
                    "   OR EXISTS (SELECT 1 FROM OSTDEF_GELTIME_TESTS T4 WHERE T4.OrderNumber = AH.OrderNumber) " +
                    "   OR EXISTS (SELECT 1 FROM OSTDEF_PEAKEXOTHERM_TEST T5 WHERE T5.OrderNumber = AH.OrderNumber) " +
                    "   OR EXISTS (SELECT 1 FROM OSTDEF_RESISTIVITY_TEST T6 WHERE T6.OrderNumber = AH.OrderNumber) " +
                    "THEN 1 ELSE 0 END AS TESTSEXISTS, " +
                    "CASE WHEN " +
                    "   EXISTS (SELECT 1 FROM OSTDEF_ADJUSTMENTS AD WHERE AD.OrderNumber = AH.OrderNumber) " +
                    "THEN 1 ELSE 0 END AS ADJUSTSEXISTS, " +
                    "CASE WHEN " +
                    "   EXISTS (SELECT 1 FROM OSTDEF_NOTES NOTE WHERE NOTE.IDNUMBER = AH.OrderNumber) " +
                    "THEN 1 ELSE 0 END AS NOTESEXISTS " +
                    "FROM AssemblyHeader AH ORDER BY AH.OrderNumber DESC"
            )
            assemblyOrdersList.clear()
            assemblyOrdersListCall?.let {
                assemblyOrdersList.addAll(it)
            }
            onSearchFieldChange(searchField)
            retrieveTestDetails()
        }
    }

    /**
     * Retrieves tests, adjustments, and notes associated with the current
     * assembly order list.
     *
     * Each assembly order that contains at least one test, adjustment, or note
     * is queried using [testAndAdjustmentLookup].
     *
     * The retrieved test and adjustment information is stored in
     * [testAndAdjustments], keyed by order number. Notes are stored in [notes],
     * also keyed by order number.
     *
     * Orders without tests, adjustments, or notes are skipped.
     */
    fun retrieveTestDetails() {
        viewModelScope.launch {
            testAndAdjustments.clear()
            for (i in 0 until assemblyOrdersList.size){
                if (assemblyOrdersList[i].TESTSEXISTS == 1 || assemblyOrdersList[i].ADJUSTSEXISTS == 1 || assemblyOrdersList[i].NOTESEXISTS == 1){
                    val (newTestAndAdjustments, newNotes) = testAndAdjustmentLookup(dataStoreManager, assemblyOrdersList[i].ORDERNUMBER)
                    testAndAdjustments[assemblyOrdersList[i].ORDERNUMBER] = newTestAndAdjustments
                    notes[assemblyOrdersList[i].ORDERNUMBER] = newNotes
                }
            }
        }
    }
}

