package com.example.coretechv2.viewmodel

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
import com.example.coretechv2.repository.APICall
import com.example.coretechv2.repository.DataStoreManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * Shared ViewModel responsible for maintaining application-wide state.
 *
 * [SharedViewModel] provides a central location for data that needs to be
 * accessed by multiple screens and ViewModels. It manages popup and message
 * state, snackbar events, the current user, selected item, assembly orders,
 * navigation state, label preview state, and database-related information.
 *
 * The ViewModel also provides functions for retrieving assembly order data
 * from the database through [APICall].
 *
 * @param dataStoreManager Provides access to application settings and API
 * configuration required by [APICall].
 */
class SharedViewModel(private val dataStoreManager: DataStoreManager) : ViewModel() {
    private val apiCall = APICall(dataStoreManager)

    var message = mutableStateOf("")
        private set
    var messageButton1Text = mutableStateOf("")
        private set
    var messageButton2Text = mutableStateOf("")
        private set
    var messageButton1Action = mutableStateOf({})
        private set
    var messageButton2Action = mutableStateOf({})
        private set

    var popupDetails = PopupItems().copy()
    var popupMessageDetails = MessageItems().copy()

    var allItemDescriptors: List<APICallTables.ItemDescriptor>? = null
    var allAssemblyOrdersList = mutableStateListOf<APICallTables.AssemblyHeader>()
        private set
    var currentAssemblyHeader: APICallTables.AssemblyHeader? = null
    var currentAssemblyLines: List<APICallTables.AssemblyLines>? = null


    var currentUser = mutableStateOf("")
    var currentOrderNumber = mutableStateOf("")
    var currentItemCode = mutableStateOf("")
    var currentItem = mutableStateOf(ItemDescriptorItem())

    var AssemblyOrdersList = mutableStateListOf<APICallTables.AssemblyHeader>()
        private set

    var AssemblyOrderListSearached = mutableStateListOf<APICallTables.AssemblyHeader>()
        private set

    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent = _snackbarEvent.asSharedFlow()


    var showPopup = mutableStateOf(false)
    var showMessagePopup = mutableStateOf(false)

    var navBack = mutableStateOf(false)

    var showLabelPreview = mutableStateOf(false)

    var saveType by mutableStateOf(APICallTypes.INSERT)
        private set
    var compareList = mutableListOf<String>()

    /**
     * Opens the general popup.
     *
     * Sets [showPopup] to true.
     */
    fun openPopup() {
        showPopup.value = true
    }

    /**
     * Closes the general popup and resets its configuration.
     *
     * Sets [showPopup] to false and replaces [popupDetails] with a new
     * default [PopupItems] instance.
     */
    fun closePopup() {
        showPopup.value = false
        popupDetails = PopupItems().copy()
    }

    /**
     * Opens the message popup.
     *
     * Sets [showMessagePopup] to true.
     */
    fun openMessagePopup() {
        showMessagePopup.value = true
    }

    /**
     * Closes the message popup and resets its configuration.
     *
     * Sets [showMessagePopup] to false and replaces [popupMessageDetails]
     * with a new default [MessageItems] instance.
     */
    fun closeMessagePopup() {
        showMessagePopup.value = false
        popupMessageDetails = MessageItems().copy()
    }

    /**
     * Opens the label preview.
     *
     * Sets [showLabelPreview] to true so that the label preview can be
     * displayed by the UI.
     */
    fun openLabelPreview() {
        showLabelPreview.value = true
    }

    /**
     * Closes the label preview.
     *
     * Sets [showLabelPreview] to false.
     */
    fun closeLabelPreview() {
        showLabelPreview.value = false
    }

    /**
     * Emits a message to the snackbar event stream.
     *
     * The message is emitted asynchronously through [snackbarEvent] so that
     * UI components can display it as a snackbar notification.
     *
     * @param message The message to display in the snackbar.
     */
    fun snackBarMessage(message: String) {
        viewModelScope.launch {
            _snackbarEvent.emit(message)
        }
    }

    /**
     * Updates the type of database operation that should be performed.
     *
     * @param type The new save operation type, such as
     * [APICallTypes.INSERT] or [APICallTypes.UPDATE].
     */
    fun updateSaveType(type: APICallTypes) {
        saveType = type
    }

    /**
     * Retrieves all assembly orders from the database.
     *
     * Unlike [retrieveAssemblyOrders], this function retrieves assembly
     * orders regardless of their current order status.
     *
     * The retrieved records are stored in [allAssemblyOrdersList].
     *
     * The database query is executed asynchronously using [viewModelScope].
     */
    fun retrieveAllAssemblyOrders() {
        viewModelScope.launch {
            val assemblyOrdersListCall: List<APICallTables.AssemblyHeader>? = apiCall.query("SELECT * FROM AssemblyHeader order by OrderNumber DESC")

            allAssemblyOrdersList.clear()
            assemblyOrdersListCall?.let {
                allAssemblyOrdersList.addAll(it)
            }
        }
    }

    /**
     * Retrieves all open assembly orders from the database.
     *
     * Only assembly orders with an `OrderStatus` of `Open` are retrieved.
     * The resulting records are stored in both [AssemblyOrdersList] and
     * [AssemblyOrderListSearached] so that the complete list and displayed
     * search list are initially synchronised.
     *
     * The results are ordered by order number in descending order.
     *
     * The database query is executed asynchronously using [viewModelScope].
     */
    fun retrieveAssemblyOrders() {
        viewModelScope.launch {
            val assemblyOrdersListCall: List<APICallTables.AssemblyHeader>? = apiCall.query("SELECT * FROM AssemblyHeader where OrderStatus = 'Open' order by OrderNumber DESC")

            AssemblyOrdersList.clear()
            AssemblyOrderListSearached.clear()
            assemblyOrdersListCall?.let {
                AssemblyOrdersList.addAll(it)
                AssemblyOrderListSearached.addAll(it)
            }
        }
    }
}