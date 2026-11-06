package com.example.coretechv2.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coretechv2.dataclasses.APICallTables
import com.example.coretechv2.dataclasses.APICallTypes
import com.example.coretechv2.dataclasses.ItemDescriptorItem
import com.example.coretechv2.dataclasses.MessageItems
import com.example.coretechv2.dataclasses.PopupItems
import com.example.coretechv2.repository.APICall
import com.example.coretechv2.repository.DataStoreManager
import kotlinx.coroutines.Job
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
    var popupDetails = PopupItems().copy()
    var popupMessageDetails = MessageItems().copy()
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

    var isFlipPhone = false
    var isTablet = false


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

    /** True while the open assembly orders are being fetched. */
    var openOrdersLoading by mutableStateOf(false)
        private set

    /** True while all assembly orders (including completed) are being fetched. */
    var allOrdersLoading by mutableStateOf(false)
        private set

    /** True once the open orders have been fetched successfully at least once. */
    var openOrdersLoaded by mutableStateOf(false)
        private set

    /** True once all orders have been fetched successfully at least once. */
    var allOrdersLoaded by mutableStateOf(false)
        private set

    /** Set when the most recent order fetch failed, cleared on the next successful fetch. */
    var ordersError by mutableStateOf<String?>(null)
        private set

    private var openOrdersJob: Job? = null
    private var allOrdersJob: Job? = null

    /**
     * Loads both the open and all assembly order lists.
     *
     * This is the single entry point for loading assembly orders, used by both
     * the home screen preload and the assembly orders screen. It is safe to call
     * repeatedly and from several screens at once:
     * - If a load is already running it is reused rather than started again, so
     *   a preload that is mid-flight when the user opens the orders screen simply
     *   finishes and fills that screen.
     * - If a list has already loaded and [force] is false, nothing is fetched.
     * - With [force] true, any running load is cancelled and a fresh one started,
     *   so changes made on another screen are always picked up.
     *
     * Both queries run in parallel. Lists are only replaced once new data has
     * arrived, so existing orders stay on screen during a refresh, and a failed
     * request leaves the previous data in place instead of blanking the list.
     *
     * @param force Fetch again even if the data is already loaded or loading.
     */
    fun loadAssemblyOrders(force: Boolean = false) {
        if (force || (!openOrdersLoaded && openOrdersJob?.isActive != true)) {
            openOrdersJob?.cancel()
            openOrdersJob = viewModelScope.launch {
                openOrdersLoading = true
                try {
                    val result: List<APICallTables.AssemblyHeader>? =
                        apiCall.query("SELECT * FROM AssemblyHeader where OrderStatus = 'Open' order by OrderNumber DESC")
                    if (result != null) {
                        replaceList(AssemblyOrdersList, result)
                        replaceList(AssemblyOrderListSearached, result)
                        openOrdersLoaded = true
                        ordersError = null
                    } else {
                        ordersError = "Couldn't load assembly orders"
                    }
                } finally {
                    // A cancelled job has been replaced by a newer one, which owns the flag now
                    if (coroutineContext[Job]?.isCancelled != true) openOrdersLoading = false
                }
            }
        }
        if (force || (!allOrdersLoaded && allOrdersJob?.isActive != true)) {
            allOrdersJob?.cancel()
            allOrdersJob = viewModelScope.launch {
                allOrdersLoading = true
                try {
                    val result: List<APICallTables.AssemblyHeader>? =
                        apiCall.query("SELECT * FROM AssemblyHeader order by OrderNumber DESC")
                    if (result != null) {
                        replaceList(allAssemblyOrdersList, result)
                        allOrdersLoaded = true
                    } else {
                        ordersError = "Couldn't load assembly orders"
                    }
                } finally {
                    if (coroutineContext[Job]?.isCancelled != true) allOrdersLoading = false
                }
            }
        }
    }

    /**
     * Replaces the contents of [target] with [items] in a single snapshot so the
     * UI never sees the list in a cleared-but-not-yet-filled state.
     */
    private fun <T> replaceList(target: SnapshotStateList<T>, items: List<T>) {
        Snapshot.withMutableSnapshot {
            target.clear()
            target.addAll(items)
        }
    }

    /**
     * Retrieves all assembly orders from the database, including completed ones.
     *
     * Kept for existing callers; delegates to [loadAssemblyOrders].
     */
    fun retrieveAllAssemblyOrders() = loadAssemblyOrders()

    /**
     * Retrieves all open assembly orders from the database.
     *
     * Kept for existing callers; delegates to [loadAssemblyOrders].
     */
    fun retrieveAssemblyOrders() = loadAssemblyOrders()
}