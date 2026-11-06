package com.example.coretechv2.viewmodel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coretechv2.dataclasses.APICallTables
import com.example.coretechv2.dataclasses.APICallTypes
import com.example.coretechv2.dataclasses.AssemblyHeaderFields
import com.example.coretechv2.dataclasses.LabelElements
import com.example.coretechv2.dataclasses.LabelStyles
import com.example.coretechv2.dataclasses.MenuItem
import com.example.coretechv2.dataclasses.MessageItems
import com.example.coretechv2.dataclasses.NoteTypes
import com.example.coretechv2.dataclasses.PopupItems
import com.example.coretechv2.dataclasses.assemblydataclasses.AssemblyLinesItem
import com.example.coretechv2.repository.APICall
import com.example.coretechv2.repository.BarcodeParser
import com.example.coretechv2.repository.ScannedBarcode
import com.example.coretechv2.repository.issueToAssemblyLine
import com.example.coretechv2.repository.loadAssemblyLine
import com.example.coretechv2.repository.orderAcceptsIssues
import com.example.coretechv2.repository.sqlText
import com.example.coretechv2.repository.DataStoreManager
import com.example.coretechv2.repository.testAndAdjustmentLookup
import com.example.coretechv2.repository.toDateFormatYYYYMMDD
import com.example.coretechv2.ui.screen.LineEntryScreen
import com.example.coretechv2.ui.screen.NotesScreen
import com.example.coretechv2.ui.screen.assemblytestsandadjustments.AddLineScreen
import com.example.coretechv2.ui.screen.assemblytestsandadjustments.AdjustmentsScreen
import com.example.coretechv2.ui.screen.assemblytestsandadjustments.ElongationalBreakScreen
import com.example.coretechv2.ui.screen.assemblytestsandadjustments.FlammabilityScreen
import com.example.coretechv2.ui.screen.assemblytestsandadjustments.GelTimeScreen
import com.example.coretechv2.ui.screen.assemblytestsandadjustments.PeakExothermScreen
import com.example.coretechv2.ui.screen.assemblytestsandadjustments.ResistivityScreen
import com.example.coretechv2.ui.screen.assemblytestsandadjustments.ViscosityScreen
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.math.ceil

/**
 * ViewModel responsible for managing the details and operations of an assembly order.
 *
 * This ViewModel retrieves and manages assembly order information, assembly lines,
 * tests, adjustments, notes, and label information. It also handles editing,
 * deleting, completing, and refreshing assembly orders.
 *
 * The ViewModel communicates with the Ostendo API through [APICall] and shares
 * application-level state with [SharedViewModel]. It also maintains UI state for
 * menus, popups, detail views, editing modes, and test and adjustment screens.
 *
 * @property dataStoreManager Provides access to stored application and API settings.
 * @property sharedViewModel Provides shared application state and communication
 * with other ViewModels and screens.
 */
class AssemblyOrderDetailsViewModel(private val dataStoreManager: DataStoreManager, var sharedViewModel: SharedViewModel) : ViewModel() {

    private val apiCall = APICall(dataStoreManager)
    var popupDetails = PopupItems().copy()
    var popupMessageDetails = MessageItems()
    var showPopupWindow = mutableStateOf(false)
        private set
    var showPopupMessage = mutableStateOf(false)
        private set
    var savedActionMenuList: MutableList<MenuItem> = mutableListOf()
        private set
    var savedAddMenuList: MutableList<MenuItem> = mutableListOf()
        private set

    var assemblyHeader by mutableStateOf<List<APICallTables.AssemblyHeader>>(emptyList())
        private set
    var assemblyDetailsLines = mutableStateListOf<AssemblyLinesItem>()
        private set
    var itemInfo by mutableStateOf<List<APICallTables.assemblyLabelItemInfo>>(emptyList())
        private set
    var testAndAdjustments = mutableStateListOf<SnapshotStateList<Any>>()

    val productLabelDataList = mutableStateListOf<LabelElements>()

    val boxLabelDataList = mutableStateListOf<LabelElements>()
    var showActionMenu by mutableStateOf(false)
        private set
    var showAddMenu by mutableStateOf(false)
        private set
    var showTestAndAdjustments = mutableStateOf(false)
        private set

    var showAssemblyDetails = mutableStateOf(false)
        private set
    var closeDetailScreen = mutableStateOf(false)
        private set
    var hideAllDetails = mutableStateOf(false)
        private set

    var notes by mutableStateOf("")
        private set
    var labelIndex: Int = 0

    var labelStyle: LabelStyles = LabelStyles.ERROR

    var editMode by mutableStateOf(false)
        private set

    var orderQty = mutableStateOf("")
        private set
    var numberOfBatches = mutableStateOf("1")
        private set
    var oldNumberOfBatches = mutableStateOf("")
        private set
    var maxBatchSize = mutableStateOf("")
        private set

    /**
     * True once the order is Finished or Closed, when every line shows both
     * its planned and actual quantities. While the order is open, only lines
     * that have had material issued show their actual quantity.
     */
    var showActualQty by mutableStateOf(false)
        private set

    /** Each line code's barcode from ITEMMASTER / DESCRIPTORMASTER, used to match scans. */
    private var lineBarcodes: Map<String, String> = emptyMap()

    /** The line being entered in the batch and qty window, or null when it is closed. */
    var entryLine by mutableStateOf<AssemblyLinesItem?>(null)
        private set

    /** Qty typed into the batch and qty window. */
    var entryQty by mutableStateOf("")
        private set

    /** Batch number typed or scanned into the batch and qty window. */
    var entryBatch by mutableStateOf("")
        private set

    /** Qty printed on a scanned label (GS1 AI 37), shown as a hint. */
    var entryLabelQty by mutableStateOf<String?>(null)
        private set

    /** True while an issue is being saved, to stop the same qty being issued twice. */
    var issuing by mutableStateOf(false)
        private set

    /**
     * Whether this order can be split into batches: false when the header's
     * "Disable Multi Batch" field ([AssemblyHeaderFields.DISABLE_MULTI_BATCH])
     * is 'true'.
     */
    var multiBatchAllowed by mutableStateOf(true)
        private set

    /**
     * True when lines are always added at their planned quantity for the
     * batches done, recording only the batch number (multi-batch orders).
     * False when the quantity added is entered for each line.
     */
    val fullQtyMode: Boolean
        get() = multiBatchAllowed

    /**
     * How many batches are done or in process, from 1 to [totalBatches].
     *
     * Nothing stores this; it is worked out from the issued quantities each
     * time the order loads (see [lineBatchesDone]). Before any line has been
     * added it starts at batch 1, and the +/- buttons change it.
     */
    var batchesDone by mutableIntStateOf(1)
        private set

    /** The number of batches the order is split into. */
    val totalBatches: Int
        get() = numberOfBatches.value.toIntOrNull()?.coerceAtLeast(1) ?: 1

    /** True if material can still be issued to this order (it isn't Finished or Closed). */
    val canIssue: Boolean
        get() = orderAcceptsIssues(assemblyHeader.firstOrNull()?.ORDERSTATUS)

    val stepNames = mutableStateListOf<Any>()

    var batchSize = mutableDoubleStateOf(0.0)
        private set
    var batchSizeUnit = mutableStateOf("Kg")
        private set

    var isFlipPhone by mutableStateOf(false)
        private set

    var isTablet by mutableStateOf(false)

    var isLandscape by mutableStateOf(false)

    var actionMenuList: MutableList<MenuItem> = mutableListOf(
        MenuItem(
            title = { "Refresh page" },
            onClick = {
                reload()
                showActionMenu = false
            }
        ),
        MenuItem(
            title = {
                if (hideAllDetails.value) {
                    "Show Details"
                } else {
                    "Hide Details"
                }
            },
            onClick = {
                if (!hideAllDetails.value && showAssemblyDetails.value) {
                    showAssemblyDetails.value = false
                    hideAllDetails.value = !hideAllDetails.value
                    showActionMenu = false
                } else {
                    hideAllDetails.value = !hideAllDetails.value
                    showActionMenu = false
                }
            }
        ),
        MenuItem(
            title = { "Edit Order" },
            onClick = {
                savedActionMenuList = actionMenuList.toMutableList()
                savedAddMenuList = addMenuList.toMutableList()
                editMode = true
                showActionMenu = false
                actionMenuList = mutableListOf(
                    MenuItem(
                        title = { "Save Edits" },
                        onClick = {
                            actionMenuList = savedActionMenuList.toMutableList()
                            addMenuList = savedAddMenuList.toMutableList()
                            editMode = false
                            saveEditsChange()
                            showActionMenu = false
                        }
                    ),
                    MenuItem(
                        title = { "Delete Order" },
                        onClick = {
                            actionMenuList = savedActionMenuList.toMutableList()
                            addMenuList = savedAddMenuList.toMutableList()
                            editMode = false
                            deleteAssemblyOrder()
                            showActionMenu = false
                        }
                    ),
                    MenuItem(
                        title = { "Cancel" },
                        onClick = {
                            actionMenuList = savedActionMenuList.toMutableList()
                            addMenuList = savedAddMenuList.toMutableList()
                            editMode = false
                            reload()
                            showActionMenu = false
                        }
                    )
                )
                addMenuList = mutableListOf(
                    MenuItem(
                        title = { "Add Line" },
                        onClick = {
                            popupDetails.width = 700
                            popupDetails.height = 500
                            popupDetails.content = {
                                AddLineScreen(sharedViewModel, stepNames)
                            }
                            showPopupWindow.value = true
                            showAddMenu = false
                        }
                    ),
                )
            },
        ),
        MenuItem(
            title = { "Complete" },
            onClick = {
                showActionMenu = false
                completeOrder()
            }
        ),

        )

    var addMenuList = listOf(
        MenuItem(
            title = { "Viscosity Test" },
            onClick = {
                popupDetails.width = 700
                popupDetails.height = 500
                popupDetails.content = {
                    sharedViewModel.updateSaveType(APICallTypes.INSERT)
                    ViscosityScreen(sharedViewModel)
                }
                showPopupWindow.value = true
                showAddMenu = false
            },
        ),
        MenuItem(
            title = { "Gel Time Test" },
            onClick = {
                popupDetails.width = 700
                popupDetails.height = 500
                popupDetails.content = {
                    sharedViewModel.updateSaveType(APICallTypes.INSERT)
                    GelTimeScreen(sharedViewModel)
                }
                showPopupWindow.value = true
                showAddMenu = false
            },
        ),
        MenuItem(
            title = { "Elongation Test" },
            onClick = {
                popupDetails.width = 600
                popupDetails.height = 350
                popupDetails.content = {
                    sharedViewModel.updateSaveType(APICallTypes.INSERT)
                    ElongationalBreakScreen(sharedViewModel)
                }
                showPopupWindow.value = true
                showAddMenu = false
            },
        ),
        MenuItem(
            title = { "Flammability Test" },
            onClick = {
                popupDetails.width = 700
                popupDetails.height = 500
                popupDetails.content = {
                    sharedViewModel.updateSaveType(APICallTypes.INSERT)
                    FlammabilityScreen(sharedViewModel)
                }
                showPopupWindow.value = true
                showAddMenu = false
            },
        ),
        MenuItem(
            title = { "Resistivity Test" },
            onClick = {
                popupDetails.width = 600
                popupDetails.height = 350
                popupDetails.content = {
                    sharedViewModel.updateSaveType(APICallTypes.INSERT)
                    ResistivityScreen(sharedViewModel)
                }
                showPopupWindow.value = true
                showAddMenu = false
            },
        ),
        MenuItem(
            title = { "Peak Exo Test" },
            onClick = {
                popupDetails.width = 700
                popupDetails.height = 500
                popupDetails.content = {
                    sharedViewModel.updateSaveType(APICallTypes.INSERT)
                    PeakExothermScreen(sharedViewModel)
                }
                showPopupWindow.value = true
                showAddMenu = false
            },
        ),
        MenuItem(
            title = { "Adjustment" },
            onClick = {
                popupDetails.width = 700
                popupDetails.height = 500
                popupDetails.content = {
                    sharedViewModel.updateSaveType(APICallTypes.INSERT)
                    AdjustmentsScreen(sharedViewModel)
                }
                showPopupWindow.value = true
                showAddMenu = false
            }
        ),
    )

    /**
     * Toggles the visibility state of the supplied popup window.
     * @param window The mutable state controlling the popup's visibility.
     */
    fun togglePopup(window: MutableState<Boolean>) {
        window.value = !window.value
    }

    /**
     * Records that the device is being used in flip-phone mode.
     */
    fun onFlipPhone() {
        isFlipPhone = true
    }

    /**
     * Displays the assembly details section.
     */
    fun showAssemblyDetails() {
        showAssemblyDetails.value = true
    }

    /**
     * Hides the assembly details section.
     */
    fun hideAssemblyDetails() {
        showAssemblyDetails.value = false
    }

    /**
     * Enables the display of all assembly details.
     */
    fun showAllDetails() {
        hideAllDetails.value = true
    }

    /**
     * Disables the display of all assembly details.
     */
    fun hideAllDetails() {
        hideAllDetails.value = false
    }

    /**
     * Displays the tests and adjustments section.
     */
    fun showTestAndAdjustments() {
        showTestAndAdjustments.value = true
    }

    /**
     * Hides the tests and adjustments section.
     */
    fun hideTestAndAdjustments() {
        showTestAndAdjustments.value = false
    }

    /**
     * Totals the adjustment quantities for each assembly line.
     *
     * @param adjustments The adjustment records for an assembly order.
     * @return A map of line code and line number to the total adjustment quantity.
     */
    private fun adjustmentTotals(adjustments: List<APICallTables.assemblyAdjustment>): Map<Pair<String, Int>, Double> {
        return adjustments
            .groupBy { it.LINECODE to it.LINENUMBER }
            .mapValues { (_, lineAdjustments) -> lineAdjustments.sumOf { it.ADJUSTQTY } }
    }

    /**
     * Retrieves the assembly order header and assembly line details from the API.
     *
     * The retrieved information is used to populate the assembly order state,
     * calculate the number of batches and batch size, identify assembly steps,
     * and update the corresponding values in the shared ViewModel.
     *
     * Each line's actual quantity is its issued quantity (TOTALISSUEDQTY), as
     * material is issued as soon as it is added. The barcodes of the line
     * items are also loaded so scans can be matched to lines.
     *
     * The method also updates the assembly header with the calculated batch
     * information.
     */
    fun retrieveAssemblyDetails() {
        viewModelScope.launch {
            val assemblyHeaderCall: List<APICallTables.AssemblyHeader>? = apiCall.query("SELECT * FROM AssemblyHeader where OrderNumber = '${sharedViewModel.currentOrderNumber.value}'")
            assemblyHeader = assemblyHeaderCall ?: emptyList()
            orderQty.value = assemblyHeader.firstOrNull()?.ORDERQTY.toString()
            // ADDITIONALFIELD_14: max batch size, used to work out the number of batches
            val headerMaxBatchSize = assemblyHeader.firstOrNull()?.ADDITIONALFIELD_14
            if (orderQty.value.isNotEmpty() && !headerMaxBatchSize.isNullOrBlank()) {
                maxBatchSize.value = headerMaxBatchSize
                numberOfBatches.value = ceil(orderQty.value.toDouble() / maxBatchSize.value.toDouble()).toInt().toString()
                oldNumberOfBatches.value = ceil(orderQty.value.toDouble() / maxBatchSize.value.toDouble()).toInt().toString()
            }

            // ADDITIONALFIELD_13: Disable Multi Batch ('true' for intense mixes)
            multiBatchAllowed = !(assemblyHeader.firstOrNull()?.ADDITIONALFIELD_13 ?: "").trim().equals("true", ignoreCase = true)
            if (!multiBatchAllowed) {
                // Intense mixes are always made as a single batch
                numberOfBatches.value = "1"
                oldNumberOfBatches.value = "1"
            }

            val assemblyDetailsLinesCall: List<APICallTables.AssemblyLines>? =
                apiCall.query("SELECT * FROM AssemblyLines where OrderNumber = '${sharedViewModel.currentOrderNumber.value}' order by LINENUMBER")
            val adjustmentsCall: List<APICallTables.assemblyAdjustment>? =
                apiCall.query("SELECT * FROM OSTDEF_ADJUSTMENTS where OrderNumber = '${sharedViewModel.currentOrderNumber.value}'")
            val adjustmentTotals = adjustmentTotals(adjustmentsCall ?: emptyList())
            showActualQty = !orderAcceptsIssues(assemblyHeader.firstOrNull()?.ORDERSTATUS)
            loadLineBarcodes(assemblyDetailsLinesCall.orEmpty())
            assemblyDetailsLines.clear()
            batchSize.doubleValue = 0.0
            for (i in 0 until (assemblyDetailsLinesCall?.size ?: 0)) {
                val actualQty = assemblyDetailsLinesCall!![i].TOTALISSUEDQTY
                val assemblyDetailsLine = AssemblyLinesItem(
                    ORDERNUMBER = assemblyDetailsLinesCall!![i].ORDERNUMBER,
                    STEPNAME = assemblyDetailsLinesCall[i].STEPNAME,
                    STEPSEQUENCE = assemblyDetailsLinesCall[i].STEPSEQUENCE,
                    LINESTATUS = assemblyDetailsLinesCall[i].LINESTATUS,
                    LINENUMBER = assemblyDetailsLinesCall[i].LINENUMBER,
                    CODETYPE = assemblyDetailsLinesCall[i].CODETYPE,
                    LINECODE = assemblyDetailsLinesCall[i].LINECODE,
                    LINEDESCRIPTION = assemblyDetailsLinesCall[i].LINEDESCRIPTION,
                    LINEUNIT = assemblyDetailsLinesCall[i].LINEUNIT,
                    ORDERQTY = assemblyDetailsLinesCall[i].ORDERQTY.toString(),
                    TOTALISSUEDQTY = assemblyDetailsLinesCall[i].TOTALISSUEDQTY.toString(),
                    REMAININGQTY = assemblyDetailsLinesCall[i].REMAININGQTY.toString(),
                    POSITIONREFERENCE = assemblyDetailsLinesCall[i].POSITIONREFERENCE,
                    LINENOTES = assemblyDetailsLinesCall[i].LINENOTES,
                    HEADERSYSUNIQUEID = assemblyDetailsLinesCall[i].HEADERSYSUNIQUEID.toString(),
                    ADDITIONALFIELD_1 = assemblyDetailsLinesCall[i].ADDITIONALFIELD_1.toBoolean(),
                    ADDITIONALFIELD_2 = assemblyDetailsLinesCall[i].ADDITIONALFIELD_2,
                    ADDITIONALFIELD_3 = assemblyDetailsLinesCall[i].ADDITIONALFIELD_3,
                    ADDITIONALFIELD_4 = assemblyDetailsLinesCall[i].ADDITIONALFIELD_4,
                    ADDITIONALFIELD_6 = assemblyDetailsLinesCall[i].ADDITIONALFIELD_6,
                    ACTUALQTY = actualQty.toString(),
                    ADJUSTQTY = (adjustmentTotals[assemblyDetailsLinesCall[i].LINECODE to assemblyDetailsLinesCall[i].LINENUMBER] ?: 0.0).toString(),
                )
                assemblyDetailsLines.add(assemblyDetailsLine)
                if (assemblyDetailsLinesCall[i].STEPNAME == "Assembly") {
                    batchSize.doubleValue += assemblyDetailsLinesCall[i].ORDERQTY
                }
                if (!stepNames.contains(assemblyDetailsLinesCall[i].STEPNAME)) {
                    stepNames.add(assemblyDetailsLinesCall[i].STEPNAME)
                }
            }
            if (batchSize.doubleValue <= 0) {
                batchSize.doubleValue = orderQty.value.toDoubleOrNull() ?: 0.0
                batchSizeUnit.value = assemblyHeader.first().ITEMUNIT
            }
            updateBatchesDoneFromIssues()
            sharedViewModel.currentAssemblyHeader = assemblyHeader.firstOrNull()
            sharedViewModel.currentAssemblyLines = assemblyDetailsLinesCall

            val call = "UPDATE AssemblyHeader SET " +
                    "ADDITIONALFIELD_12 = ${numberOfBatches.value.toInt()}, " +
                    "ADDITIONALFIELD_6 = ${batchSize.doubleValue}, " +
                    "SYSUSERMODIFIED = '${sharedViewModel.currentUser.value}' " +
                    "WHERE ORDERNUMBER = '${assemblyHeader.first().ORDERNUMBER}'"


            apiCall.insertUpdateDelete(call)


        }
    }

    /**
     * Retrieves all tests, adjustments, and notes associated with the current
     * assembly order.
     *
     * Test records are retrieved from the relevant Ostendo test tables and grouped
     * by test number. Adjustment records are also included so that tests and
     * adjustments can be displayed together. The highest test or adjustment number
     * is used to determine the number of test and adjustment groups.
     *
     * Notes associated with the current assembly order are also retrieved and
     * combined into the ViewModel's notes state.
     */
    fun retrieveTestDetails() {
        viewModelScope.launch {
            val (newTestAndAdjustments, newNotes) = testAndAdjustmentLookup(dataStoreManager, sharedViewModel.currentOrderNumber.value)
            testAndAdjustments = newTestAndAdjustments
            notes = newNotes
        }
    }

    /**
     * Retrieves label configuration and dangerous-goods information for the
     * current assembly item.
     *
     * Product and box label layouts are constructed from the item, class,
     * layout, and dangerous-goods information retrieved from the API. Appropriate
     * label actions are also added to the action menu.
     */
    fun retrieveLabelData() {
        viewModelScope.launch {
            val itemInfoCall: List<APICallTables.assemblyLabelItemInfo>? =
                apiCall.query("SELECT LII.HEADERSYSUNIQUEID, LII.SYSUNIQUEID, LII.ITEMCODE, LII.TOPNAME, LII.MIDDLENAME, LII.BOTTOMNAME, LII.SIZE, LII.QRCODE, LII.VARIANT, LII.BESTBEFORE, LII.LABELSTYLE, LII.BOXQTY, IM.ITEMBARCODE FROM OSTDEF_LABELITEMINFO AS LII JOIN ITEMMASTER AS IM on LII.ITEMCODE = IM.ITEMCODE where LII.ITEMCODE = '${sharedViewModel.currentItemCode.value}'")
            itemInfo = itemInfoCall ?: emptyList()

            if (itemInfo.isNotEmpty()) {
                for (x in 0 until itemInfo.size) {
                    val classInfoCall: List<APICallTables.assemblyLabelClassInfo>? = apiCall.query("Select * from OSTDEF_LABELCLASSINFO where SYSUNIQUEID = '${itemInfo[x].HEADERSYSUNIQUEID}'")
                    val labelLayoutCall: List<APICallTables.assemblyLabelLayout>? = apiCall.query("Select * from OSTDEF_LABELLAYOUTINFO where LABELID = '${itemInfo[x].LABELSTYLE}'")
                    val boxLayoutCall: List<APICallTables.assemblyLabelLayout>? = apiCall.query("Select * from OSTDEF_LABELLAYOUTINFO where LABELID = '${LabelStyles.BOX}'")
                    val dgInfoCall: List<APICallTables.itemDGInfo>? = apiCall.query(
                        "select dgi.UNNUMBER, dgi.PACKINGGROUP, dgi.DGCLASS, dgl.DGQUANTITY from OSTDEF_DGINFO as dgi " +
                                "join OSTDEF_DGLINES as dgl on dgl.HEADERSYSUNIQUEID = dgi.SYSUNIQUEID and dgl.LINECODE = '${itemInfo[x].ITEMCODE}' and dgl.CODETYPE = 'Item Code'"
                    )
                    if (classInfoCall != null && labelLayoutCall != null) {
                        productLabelDataList.add(LabelElements(itemInfo[x], classInfoCall.first(), labelLayoutCall.first(), dgInfoCall?.first(), sharedViewModel))
                        if (boxLayoutCall != null) {
                            boxLabelDataList.add(LabelElements(itemInfo[x].copy(LABELSTYLE = LabelStyles.BOX), classInfoCall.first(), boxLayoutCall.first(), dgInfoCall?.first(), sharedViewModel))
                        }
                        var itemName = ""
                        if (itemInfo.size > 1) {
                            if (itemInfo[x].MIDDLENAME.isNotEmpty()) {
                                itemName = "${itemInfo[x].MIDDLENAME}\n"
                            } else {
                                itemName = "${itemInfo[x].TOPNAME} ${itemInfo[x].BOTTOMNAME}\n"
                            }
                        }
                        actionMenuList.addAll(
                            listOf(
                                MenuItem(
                                    title = { "${itemName}Product Labels" },
                                    onClick = {
                                        labelStyle = itemInfo[x].LABELSTYLE
                                        labelIndex = x
                                        sharedViewModel.openLabelPreview()
                                        showActionMenu = false
                                    }
                                ),
                                MenuItem(
                                    title = { "${itemName}Box Labels" },
                                    onClick = {
                                        labelStyle = LabelStyles.BOX
                                        labelIndex = x
                                        sharedViewModel.openLabelPreview()
                                        showActionMenu = false
                                    }
                                ),
                            )
                        )
                    }
                }
            }
        }
    }

    /**
     * Toggles the main action menu and closes the add menu if it is currently open.
     */
    fun menuPressed() {
        showAddMenu = false
        showActionMenu = !showActionMenu
    }

    /**
     * Toggles the add menu and closes the main action menu if it is currently open.
     */
    fun addPressed() {
        showActionMenu = false
        showAddMenu = !showAddMenu
    }

    /**
     * Closes both the action menu and the add menu.
     */
    fun closeMenus() {
        showAddMenu = false
        showActionMenu = false
    }

    /**
     * Opens the assembly order detail screen.
     *
     * Updates the detail-screen state so that the screen is not marked for closing.
     */
    fun openDetailScreen() {
        closeDetailScreen.value = false
    }

    /**
     * Opens the notes entry popup for the current assembly order.
     *
     * The popup is configured to display the [NotesScreen] and uses the assembly
     * note type when saving the note.
     */
    fun notesPressed() {
        popupDetails.width = 700
        popupDetails.height = 500
        popupDetails.content = {
            sharedViewModel.updateSaveType(APICallTypes.INSERT)
            NotesScreen(sharedViewModel, NoteTypes.ASSEMBLY)
        }
        showPopupWindow.value = true
        showAddMenu = false
    }

    /**
     * Updates the maximum batch size with the supplied value.
     *
     * @param newValue The new maximum batch size.
     */
    fun onMaxBatchSizeChange(newValue: String) {
        maxBatchSize.value = newValue
    }

    /**
     * Updates the number of batches with the supplied value.
     *
     * @param newValue The new number of batches.
     */
    fun onNumberOfBatchesChange(newValue: String) {
        numberOfBatches.value = newValue
    }

    /**
     * Updates the assembly order quantity with the supplied value.
     *
     * @param newValue The new order quantity.
     */
    fun orderQty(newValue: String) {
        orderQty.value = newValue
    }

    /**
     * Updates the order quantity of an assembly line.
     *
     * The specified line is replaced with a copy containing the new order
     * quantity.
     *
     * @param newValue The new order quantity for the assembly line.
     * @param line The index of the assembly line within [assemblyDetailsLines].
     */
    fun onEditLineChange(newValue: String, line: Int) {

        val updatedList = assemblyDetailsLines

        updatedList[line] = updatedList[line].copy(
            ORDERQTY = newValue
        )

        assemblyDetailsLines = updatedList

    }

    /**
     * Saves changes made while editing the assembly order.
     *
     * If the assembly details section is being edited, the order quantity,
     * number of batches, and maximum batch size are updated in the assembly
     * header. Otherwise, the quantities of individual assembly lines are saved.
     *
     * A success or error message is displayed based on the API response, and
     * the assembly order is reloaded after the operation.
     */
    fun saveEditsChange() {
        if (showAssemblyDetails.value) {
            if (!multiBatchAllowed) {
                // Multi batch disabled (intense mix): always a single batch of the full order qty
                numberOfBatches.value = "1"
                maxBatchSize.value = orderQty.value
            } else if (oldNumberOfBatches.value == numberOfBatches.value) {
                numberOfBatches.value = ceil(orderQty.value.toDouble() / maxBatchSize.value.toDouble()).toInt().toString()
            } else {
                maxBatchSize.value = (orderQty.value.toDouble() / numberOfBatches.value.toDouble()).toString()
            }
            viewModelScope.launch {
                val call = "UPDATE AssemblyHeader SET " +
                        "ORDERQTY = ${orderQty.value.toDouble()}, " +
                        "${AssemblyHeaderFields.NUMBER_OF_BATCHES} = ${numberOfBatches.value.toInt()}, " +
                        "${AssemblyHeaderFields.MAX_BATCH_SIZE} = ${maxBatchSize.value.toDouble()}, " +
                        "SYSUSERMODIFIED = '${sharedViewModel.currentUser.value}' " +
                        "WHERE ORDERNUMBER = '${assemblyHeader.first().ORDERNUMBER}'"


                val response = apiCall.insertUpdateDelete(call)
                if (response == "200 OK") {
                    sharedViewModel.snackBarMessage("Qty Saved successfully")
                    resyncAfterEdit()
                } else {
                    sharedViewModel.snackBarMessage("Error Saving Details, Please Try Again")
                }
                reload()
            }
        } else {
            viewModelScope.launch {
                var callCheck = 0
                for (i in 0 until assemblyDetailsLines.size) {
                    val call = "UPDATE AssemblyLines SET  " +
                            "ORDERQTY = ${assemblyDetailsLines[i].ORDERQTY.toDouble()}," +
                            "SYSUSERMODIFIED = '${sharedViewModel.currentUser.value}'" +
                            "WHERE LINENUMBER = ${assemblyDetailsLines[i].LINENUMBER} and " +
                            "LINECODE = '${assemblyDetailsLines[i].LINECODE}' and " +
                            "ORDERNUMBER = '${assemblyDetailsLines[i].ORDERNUMBER}'"

                    val response = apiCall.insertUpdateDelete(call)
                    if (response == "200 OK") {
                        callCheck += 1
                    } else {
                        sharedViewModel.snackBarMessage("Error Saving ${assemblyDetailsLines[i].LINEDESCRIPTION}, Please Try Again")
                    }
                }
                if (callCheck == assemblyDetailsLines.size) {
                    sharedViewModel.snackBarMessage("Qty Saved successfully")
                }
                resyncAfterEdit()
                reload()
            }
        }
    }

    /**
     * After the number of batches or a line's planned qty is edited on a
     * multi-batch order, keeps the batches done within the new number of
     * batches and brings the added lines' issues in line with the new
     * planned quantities.
     */
    private suspend fun resyncAfterEdit() {
        if (!fullQtyMode || !canIssue) return
        batchesDone = batchesDone.coerceIn(1, totalBatches)
        syncFullQtyIssues()
    }

    /**
     * Reloads the assembly order information when the detail screen is still open.
     *
     * Assembly header, assembly line, test, adjustment, and note information
     * are refreshed from the API.
     */
    fun reload() {
        if (!closeDetailScreen.value) {
            retrieveAssemblyDetails()
            retrieveTestDetails()
        }
    }

    /**
     * How many batches a line has had issued, worked out from its issues:
     * (issued - adjustments) / (planned / number of batches). For example a
     * line planned at 10 kg over 4 batches with 5 kg issued has 2 batches done.
     *
     * Adjustments are taken off first as they are issued in full when saved.
     *
     * @return The batches done (may be fractional), or null if the line has
     * no planned quantity to divide by.
     */
    fun lineBatchesDone(order: AssemblyLinesItem): Double? {
        val planned = order.ORDERQTY.toDoubleOrNull() ?: return null
        if (planned <= 0.0) return null
        val adjust = order.ADJUSTQTY.toDoubleOrNull() ?: 0.0
        val issued = order.TOTALISSUEDQTY.toDoubleOrNull() ?: 0.0
        return (issued - adjust) / (planned / totalBatches)
    }

    /**
     * Sets [batchesDone] from the issued quantities of the added lines, using
     * the highest batch count among them. If no line has been added yet, the
     * current value is kept (batch 1 when the order is first opened).
     */
    private fun updateBatchesDoneFromIssues() {
        val fromIssues = assemblyDetailsLines
            .filter { it.ADDITIONALFIELD_1 }
            .mapNotNull { lineBatchesDone(it) }
            .maxOrNull()
            ?.let { kotlin.math.round(it).toInt() }
        if (fromIssues != null && fromIssues >= 1) batchesDone = fromIssues
        batchesDone = batchesDone.coerceIn(1, totalBatches)
    }

    /** Rounds a quantity to 6 decimal places so split batch quantities issue cleanly. */
    private fun roundQty(qty: Double): Double =
        qty.toBigDecimal().setScale(6, java.math.RoundingMode.HALF_UP).toDouble()

    /**
     * The quantity an added line should have issued on a multi-batch order:
     * its planned quantity for the batches done, plus its adjustments (which
     * are issued in full when they are saved).
     */
    private fun fullQtyTarget(line: APICallTables.AssemblyLines, adjustQty: Double): Double =
        roundQty(line.ORDERQTY * batchesDone / totalBatches + adjustQty)

    /** The total adjustments for a line, from the lines last loaded. */
    private fun adjustQtyFor(lineCode: String, lineNumber: Int): Double =
        assemblyDetailsLines
            .firstOrNull { it.LINECODE == lineCode && it.LINENUMBER == lineNumber }
            ?.ADJUSTQTY?.toDoubleOrNull() ?: 0.0

    /**
     * How much more a line needs issuing to reach its planned quantity for
     * the batches done. Shown in the batch window on multi-batch orders.
     */
    fun fullQtyRemaining(order: AssemblyLinesItem): Double {
        val planned = order.ORDERQTY.toDoubleOrNull() ?: 0.0
        val adjust = order.ADJUSTQTY.toDoubleOrNull() ?: 0.0
        val issued = order.TOTALISSUEDQTY.toDoubleOrNull() ?: 0.0
        return roundQty(planned * batchesDone / totalBatches + adjust - issued)
    }

    /**
     * Issues whatever a line needs to reach its target for the batches done
     * (see [fullQtyTarget]). The amount is negative if the line has more
     * issued than it should, e.g. after the batches done is lowered.
     *
     * @return True if the line now matches its target.
     */
    private suspend fun issueToFullQty(line: APICallTables.AssemblyLines, batchNo: String?): Boolean {
        val delta = roundQty(fullQtyTarget(line, adjustQtyFor(line.LINECODE, line.LINENUMBER)) - line.TOTALISSUEDQTY)
        if (kotlin.math.abs(delta) < 0.0000001) return true
        return issueToAssemblyLine(apiCall, line, delta, batchNo, sharedViewModel.currentUser.value)
    }

    /**
     * Brings every added line on a multi-batch order up (or down) to its
     * planned quantity for the batches done. Lines not yet added are left
     * alone.
     *
     * @return True if every added line was updated.
     */
    private suspend fun syncFullQtyIssues(): Boolean {
        val orderNumber = assemblyHeader.firstOrNull()?.ORDERNUMBER ?: return false
        val lines: List<APICallTables.AssemblyLines> = apiCall.query(
            "SELECT * FROM AssemblyLines where OrderNumber = '${sqlText(orderNumber)}' order by LINENUMBER"
        ) ?: return false
        var allOk = true
        for (line in lines.filter { it.ADDITIONALFIELD_1.toBoolean() }) {
            if (!issueToFullQty(line, null)) {
                sharedViewModel.snackBarMessage("Error Updating ${line.LINEDESCRIPTION}, Please Try Again")
                allOk = false
            }
        }
        return allOk
    }

    /**
     * Changes how many batches are done or in process on a multi-batch order,
     * e.g. from 2 of 4 to 3 of 4.
     *
     * Every added line is issued (or reversed) to match its planned quantity
     * for that many batches, e.g. 2 of 4 issues planned / 4 * 2. The count is
     * read back from the issues the next time the order loads.
     *
     * @param change +1 to move to the next batch, -1 to go back one.
     */
    fun changeBatchesDone(change: Int) {
        if (!fullQtyMode || !canIssue || issuing) return
        val newDone = (batchesDone + change).coerceIn(1, totalBatches)
        if (newDone == batchesDone) return

        issuing = true
        viewModelScope.launch {
            try {
                batchesDone = newDone
                if (syncFullQtyIssues()) {
                    sharedViewModel.snackBarMessage("Batch $newDone of $totalBatches, added lines updated")
                }
                reload()
            } finally {
                issuing = false
            }
        }
    }

    /**
     * Saves the batch window on a multi-batch order.
     *
     * The line is issued its planned quantity for the batches done, marked as
     * added, and the batch number (if entered) is added to its list.
     */
    private fun saveFullQtyEntry(order: AssemblyLinesItem) {
        val batch = entryBatch.trim()
        issuing = true
        viewModelScope.launch {
            try {
                val line = loadAssemblyLine(apiCall, order.ORDERNUMBER, order.LINECODE, order.LINENUMBER)
                if (line == null) {
                    sharedViewModel.snackBarMessage("Error Loading ${order.LINEDESCRIPTION}, Please Try Again")
                    return@launch
                }
                if (!issueToFullQty(line, batch)) {
                    sharedViewModel.snackBarMessage("Error Adding ${line.LINEDESCRIPTION}, Please Try Again")
                    return@launch
                }
                if (!updateLineAddedAndBatch(line, true, mergeBatches(line.ADDITIONALFIELD_2, batch))) {
                    sharedViewModel.snackBarMessage("Added, but the line didn't update. Please Try Again")
                } else {
                    sharedViewModel.snackBarMessage("Added ${line.LINEDESCRIPTION} for $batchesDone of $totalBatches batches")
                }
                entryLine = null
                sharedViewModel.closePopup()
            } finally {
                issuing = false
            }
        }
    }

    /**
     * Removes a line from a multi-batch order when it was added by mistake.
     *
     * Its planned quantity is reversed out of the issues (adjustments stay
     * issued) and the line is marked as not added. Its batch numbers are kept.
     */
    fun removeFullQtyEntry() {
        val order = entryLine ?: return
        if (issuing) return
        issuing = true
        viewModelScope.launch {
            try {
                val line = loadAssemblyLine(apiCall, order.ORDERNUMBER, order.LINECODE, order.LINENUMBER)
                if (line == null) {
                    sharedViewModel.snackBarMessage("Error Loading ${order.LINEDESCRIPTION}, Please Try Again")
                    return@launch
                }
                val plannedIssued = roundQty(line.TOTALISSUEDQTY - adjustQtyFor(line.LINECODE, line.LINENUMBER))
                if (plannedIssued > 0.0000001 &&
                    !issueToAssemblyLine(apiCall, line, -plannedIssued, null, sharedViewModel.currentUser.value)
                ) {
                    sharedViewModel.snackBarMessage("Error Removing ${line.LINEDESCRIPTION}, Please Try Again")
                    return@launch
                }
                updateLineAddedAndBatch(line, false, line.ADDITIONALFIELD_2)
                sharedViewModel.snackBarMessage("Removed ${line.LINEDESCRIPTION}")
                entryLine = null
                sharedViewModel.closePopup()
            } finally {
                issuing = false
            }
        }
    }

    /**
     * Loads the barcode of every item and descriptor on the order so scans can
     * be matched to lines. Runs separately so it doesn't delay showing the lines.
     */
    private fun loadLineBarcodes(lines: List<APICallTables.AssemblyLines>) {
        val codes = lines.map { it.LINECODE }.distinct()
        if (codes.isEmpty()) return
        viewModelScope.launch {
            val codeList = codes.joinToString(", ") { "'${sqlText(it)}'" }
            val barcodes: List<APICallTables.CodeBarcode>? = apiCall.query(
                "SELECT ITEMCODE AS CODE, ITEMBARCODE AS BARCODE FROM ITEMMASTER WHERE ITEMCODE IN ($codeList) " +
                        "UNION ALL " +
                        "SELECT DESCRIPTORCODE AS CODE, DESCRIPTORBARCODE AS BARCODE FROM DESCRIPTORMASTER WHERE DESCRIPTORCODE IN ($codeList)"
            )
            if (barcodes != null) {
                lineBarcodes = barcodes
                    .filter { !it.BARCODE.isNullOrBlank() }
                    .associate { it.CODE to it.BARCODE!! }
            }
        }
    }

    /**
     * Finds the assembly line a scanned barcode belongs to.
     *
     * A line matches when the scanned GTIN or raw code equals the line item's
     * barcode (ITEMMASTER.ITEMBARCODE) or its item code. If the item appears on
     * several lines, the first line not yet added is used.
     */
    private fun matchLine(scan: ScannedBarcode): AssemblyLinesItem? {
        val scannedCodes = scan.identifiers.map { BarcodeParser.normalise(it) }.filter { it.isNotEmpty() }
        val matches = assemblyDetailsLines.filter { line ->
            val lineCodes = listOfNotNull(line.LINECODE, lineBarcodes[line.LINECODE]).map { BarcodeParser.normalise(it) }
            scannedCodes.any { it in lineCodes }
        }
        return matches.firstOrNull { !it.ADDITIONALFIELD_1 } ?: matches.firstOrNull()
    }

    /**
     * Handles a hardware barcode scan on the assembly detail screen.
     *
     * The scanned item is matched to a line and the batch and qty window opens
     * for it, with the batch number filled in from the barcode when present.
     *
     * Scans are ignored with a message while editing the order, while another
     * window is open, or when the order is Finished or Closed.
     *
     * @param code The scanned text.
     */
    fun onBarcodeScanned(code: String) {
        when {
            editMode -> {
                sharedViewModel.snackBarMessage("Finish editing the order before scanning")
                return
            }
            sharedViewModel.showPopup.value || sharedViewModel.showMessagePopup.value -> {
                sharedViewModel.snackBarMessage("Close the open window before scanning")
                return
            }
            !canIssue -> {
                sharedViewModel.snackBarMessage("This order is ${assemblyHeader.firstOrNull()?.ORDERSTATUS}, items can't be added")
                return
            }
        }
        val scan = BarcodeParser.parse(code)
        val line = matchLine(scan)
        if (line == null) {
            sharedViewModel.snackBarMessage("Scanned item isn't on this order ($code)")
            return
        }
        lineEntryPressed(line, scan.batch, scan.labelQty)
    }

    /**
     * Handles a scan while the batch and qty window is open.
     *
     * Scanning the same item again fills in its batch number. Scanning a
     * different item shows a message, as the current entry must be saved or
     * cancelled first.
     */
    fun onBarcodeScannedInEntry(code: String) {
        val current = entryLine ?: return
        val scan = BarcodeParser.parse(code)
        val line = matchLine(scan)
        if (line == null || line.LINECODE != current.LINECODE) {
            sharedViewModel.snackBarMessage("Save or cancel ${current.LINEDESCRIPTION} before scanning another item")
            return
        }
        scan.batch?.let { entryBatch = it }
        scan.labelQty?.let { entryLabelQty = it }
    }

    /**
     * Opens the batch and qty window for an assembly line. Used when a line is
     * tapped and when a barcode is scanned.
     *
     * @param order The assembly line being added to.
     * @param scannedBatch The batch number from a scanned barcode, if any.
     * @param labelQty The qty printed on a scanned label, if any.
     */
    fun lineEntryPressed(order: AssemblyLinesItem, scannedBatch: String? = null, labelQty: String? = null) {
        if (!canIssue) {
            sharedViewModel.snackBarMessage("This order is ${assemblyHeader.firstOrNull()?.ORDERSTATUS}, items can't be added")
            return
        }
        entryLine = order
        entryQty = ""
        entryBatch = scannedBatch.orEmpty()
        entryLabelQty = labelQty
        popupDetails.width = 500
        popupDetails.height = 360
        popupDetails.content = {
            LineEntryScreen(this)
        }
        showPopupWindow.value = true
    }

    /** Updates the qty typed into the batch and qty window. */
    fun onEntryQtyChange(newValue: String) {
        if (newValue.matches(Regex("""-?\d*\.?\d*"""))) entryQty = newValue
    }

    /** Updates the batch number typed into the batch and qty window. */
    fun onEntryBatchChange(newValue: String) {
        entryBatch = newValue
    }

    /** Closes the batch and qty window without saving. */
    fun cancelLineEntry() {
        entryLine = null
        sharedViewModel.closePopup()
    }

    /**
     * Saves the batch and qty window.
     *
     * A qty is issued to the line straight away with the entered batch number
     * (see [issueToAssemblyLine]), so it becomes the line's actual qty. A
     * negative qty reverses an earlier mistake. The line is marked as added
     * while it has any qty issued, and the batch number is added to the
     * line's list of batches.
     *
     * If only a batch number is entered, the batch is recorded against the
     * line without issuing anything.
     */
    fun saveLineEntry() {
        val order = entryLine ?: return
        if (issuing) return
        if (fullQtyMode) {
            saveFullQtyEntry(order)
            return
        }
        val qty = entryQty.toDoubleOrNull()?.takeIf { it != 0.0 }
        val batch = entryBatch.trim()
        if (qty == null && batch.isEmpty()) {
            cancelLineEntry()
            return
        }

        issuing = true
        viewModelScope.launch {
            try {
                val line = loadAssemblyLine(apiCall, order.ORDERNUMBER, order.LINECODE, order.LINENUMBER)
                if (line == null) {
                    sharedViewModel.snackBarMessage("Error Loading ${order.LINEDESCRIPTION}, Please Try Again")
                    return@launch
                }
                var issuedTotal = line.TOTALISSUEDQTY
                if (qty != null) {
                    if (issuedTotal + qty < -0.0000001) {
                        sharedViewModel.snackBarMessage("Can't remove more than the ${line.TOTALISSUEDQTY} ${line.LINEUNIT} already added")
                        return@launch
                    }
                    if (!issueToAssemblyLine(apiCall, line, qty, batch, sharedViewModel.currentUser.value)) {
                        sharedViewModel.snackBarMessage("Error Adding ${line.LINEDESCRIPTION}, Please Try Again")
                        return@launch
                    }
                    issuedTotal += qty
                }

                val added = if (qty != null) issuedTotal > 0.0000001 else line.ADDITIONALFIELD_1.toBoolean()
                if (!updateLineAddedAndBatch(line, added, mergeBatches(line.ADDITIONALFIELD_2, batch))) {
                    sharedViewModel.snackBarMessage(
                        if (qty != null) "Qty added, but the batch number didn't save. Please enter it again"
                        else "Error Saving Batch Number, Please Try Again"
                    )
                }
                if (qty != null) {
                    sharedViewModel.snackBarMessage(
                        if (qty > 0) "Added ${lineQtyLabel(qty, line.LINEUNIT)} ${line.LINEDESCRIPTION}"
                        else "Removed ${lineQtyLabel(-qty, line.LINEUNIT)} ${line.LINEDESCRIPTION}"
                    )
                }
                entryLine = null
                sharedViewModel.closePopup()
            } finally {
                issuing = false
            }
        }
    }

    /**
     * Adds the line's full remaining planned quantity in one go, with no batch
     * number. Used when a line is long pressed.
     *
     * The remaining quantity is the planned quantity plus adjustments, less
     * anything already issued. The line is marked as added either way.
     */
    fun lineAddFull(order: AssemblyLinesItem) {
        if (!canIssue) {
            sharedViewModel.snackBarMessage("This order is ${assemblyHeader.firstOrNull()?.ORDERSTATUS}, items can't be added")
            return
        }
        if (issuing) return
        issuing = true
        viewModelScope.launch {
            try {
                val line = loadAssemblyLine(apiCall, order.ORDERNUMBER, order.LINECODE, order.LINENUMBER)
                if (line == null) {
                    sharedViewModel.snackBarMessage("Error Loading ${order.LINEDESCRIPTION}, Please Try Again")
                    return@launch
                }
                if (fullQtyMode) {
                    // Multi-batch order: planned qty for the batches done
                    if (!issueToFullQty(line, null)) {
                        sharedViewModel.snackBarMessage("Error Adding ${line.LINEDESCRIPTION}, Please Try Again")
                        return@launch
                    }
                    sharedViewModel.snackBarMessage("Added ${line.LINEDESCRIPTION} for $batchesDone of $totalBatches batches")
                    if (!line.ADDITIONALFIELD_1.toBoolean()) {
                        updateLineAddedAndBatch(line, true, line.ADDITIONALFIELD_2)
                    }
                    reload()
                    return@launch
                }
                val adjustQty = order.ADJUSTQTY.toDoubleOrNull() ?: 0.0
                val remaining = line.ORDERQTY + adjustQty - line.TOTALISSUEDQTY
                if (remaining > 0.0000001) {
                    if (!issueToAssemblyLine(apiCall, line, remaining, null, sharedViewModel.currentUser.value)) {
                        sharedViewModel.snackBarMessage("Error Adding ${line.LINEDESCRIPTION}, Please Try Again")
                        return@launch
                    }
                    sharedViewModel.snackBarMessage("Added ${lineQtyLabel(remaining, line.LINEUNIT)} ${line.LINEDESCRIPTION}")
                } else {
                    sharedViewModel.snackBarMessage("${line.LINEDESCRIPTION} has already been fully added")
                }
                if (!line.ADDITIONALFIELD_1.toBoolean()) {
                    updateLineAddedAndBatch(line, true, line.ADDITIONALFIELD_2)
                }
                reload()
            } finally {
                issuing = false
            }
        }
    }

    /** Formats a quantity for a message, e.g. "5.25 Kg". */
    private fun lineQtyLabel(qty: Double, unit: String): String =
        "${qty.toBigDecimal().setScale(4, java.math.RoundingMode.HALF_UP).stripTrailingZeros().toPlainString()} $unit"

    /**
     * Adds [batch] to a comma separated list of batch numbers, unless it is
     * blank or already listed.
     */
    private fun mergeBatches(existing: String, batch: String): String {
        val batches = existing.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toMutableList()
        if (batch.isNotBlank() && batches.none { it.equals(batch, ignoreCase = true) }) batches.add(batch)
        return batches.joinToString(", ")
    }

    /**
     * Saves a line's added flag (ADDITIONALFIELD_1) and batch numbers
     * (ADDITIONALFIELD_2).
     *
     * @return True if the update was saved.
     */
    private suspend fun updateLineAddedAndBatch(line: APICallTables.AssemblyLines, added: Boolean, batches: String): Boolean {
        val call = "UPDATE AssemblyLines SET " +
                "ADDITIONALFIELD_1 = '$added', " +
                "ADDITIONALFIELD_2 = '${sqlText(batches)}', " +
                "SYSUSERMODIFIED = '${sqlText(sharedViewModel.currentUser.value)}' " +
                "WHERE LINENUMBER = ${line.LINENUMBER} and " +
                "LINECODE = '${sqlText(line.LINECODE)}' and " +
                "ORDERNUMBER = '${sqlText(line.ORDERNUMBER)}'"
        return apiCall.insertUpdateDelete(call) == "200 OK"
    }

    /**
     * Opens a confirmation popup for deleting an assembly line.
     *
     * If confirmed, the selected assembly line is deleted from the API.
     *
     * @param order The assembly line to delete.
     */
    fun lineDelete(order: AssemblyLinesItem) {
        popupDetails.width = 450
        popupDetails.height = 230
        popupDetails.content = {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(all = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Delete Assembly Line",
                        style = MaterialTheme.typography.headlineLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "Are you sure you want to remove ${order.LINEDESCRIPTION} from this assembly?",
                        textAlign = TextAlign.Center,
                    )

                    Row(
                        modifier = Modifier.weight(2f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        Button(
                            modifier = Modifier
                                .weight(3f)
                                .padding(horizontal = 15.dp, vertical = 20.dp),
                            onClick = { sharedViewModel.closePopup() }
                        ) { Text(text = "No") }
                        Button(
                            modifier = Modifier
                                .weight(3f)
                                .padding(horizontal = 15.dp, vertical = 20.dp),
                            onClick = {
                                viewModelScope.launch {
                                    val call = "DELETE FROM AssemblyLines " +
                                            "WHERE LINENUMBER = ${order.LINENUMBER} and " +
                                            "LINECODE = '${order.LINECODE}' and " +
                                            "ORDERNUMBER = '${order.ORDERNUMBER}'"


                                    val response = apiCall.insertUpdateDelete(call)
                                    if (response == "200 OK") {
                                        sharedViewModel.closePopup()
                                    } else {
                                        sharedViewModel.snackBarMessage("Error Deleting Assembly Line, Please Try Again")
                                    }
                                }
                            }
                        ) { Text(text = "Yes") }
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
        showPopupWindow.value = true
    }

    /**
     * Opens a confirmation popup for deleting a test.
     *
     * The test type determines which Ostendo test table is used for the deletion.
     * This operation is only available while the assembly order is in edit mode.
     *
     * @param line The test record to delete. Supported test types include
     * viscosity, gel time, elongation, flammability, resistivity, and peak
     * exotherm tests.
     */
    fun testDelete(line: Any) {
        if (editMode) {
            popupDetails.width = 450
            popupDetails.height = 230
            popupDetails.content = {
                var testTable = ""
                var testName = ""
                var testNo = ""
                var testOrderNumber = ""
                var testID = ""
                when (line) {
                    is APICallTables.viscosityTest -> {
                        testTable = "OSTDEF_VISCOSITY_TESTS"
                        testName = "Viscosity"
                        testNo = line.TESTNO.toString()
                        testOrderNumber = line.ORDERNUMBER
                        testID = line.SYSUNIQUEID.toString()
                    }

                    is APICallTables.gelTimeTest -> {
                        testTable = "OSTDEF_GELTIME_TESTS"
                        testName = "Gel Time"
                        testNo = line.TESTNO.toString()
                        testOrderNumber = line.ORDERNUMBER
                        testID = line.SYSUNIQUEID.toString()
                    }

                    is APICallTables.ElongationalBreakTest -> {
                        testTable = "OSTDEF_ELONGATIONAL_TEST"
                        testName = "Elongation"
                        testNo = line.TESTNO.toString()
                        testOrderNumber = line.ORDERNUMBER
                        testID = line.SYSUNIQUEID.toString()
                    }

                    is APICallTables.FlammabilityTest -> {
                        testTable = "OSTDEF_FLAMMABILITY_TEST"
                        testName = "Flammability"
                        testNo = line.TESTNO.toString()
                        testOrderNumber = line.ORDERNUMBER
                        testID = line.SYSUNIQUEID.toString()
                    }

                    is APICallTables.ResistivityTest -> {
                        testTable = "OSTDEF_RESISTIVITY_TEST"
                        testName = "Resistivity"
                        testNo = line.TESTNO.toString()
                        testOrderNumber = line.ORDERNUMBER
                        testID = line.SYSUNIQUEID.toString()
                    }

                    is APICallTables.peakExothermTest -> {
                        testTable = "OSTDEF_PEAKEXOTHERM_TEST"
                        testName = "Peak Exotherm"
                        testNo = line.TESTNO.toString()
                        testOrderNumber = line.ORDERNUMBER
                        testID = line.SYSUNIQUEID.toString()
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(all = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Delete $testName Test",
                            style = MaterialTheme.typography.headlineLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(
                            text = "Are you sure you want to delete the $testName test from this assembly?",
                            textAlign = TextAlign.Center,
                        )

                        Row(
                            modifier = Modifier.weight(2f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Spacer(modifier = Modifier.weight(1f))
                            Button(
                                modifier = Modifier
                                    .weight(3f)
                                    .padding(horizontal = 15.dp, vertical = 20.dp),
                                onClick = { sharedViewModel.closePopup() }
                            ) { Text(text = "No") }
                            Button(
                                modifier = Modifier
                                    .weight(3f)
                                    .padding(horizontal = 15.dp, vertical = 20.dp),
                                onClick = {
                                    viewModelScope.launch {
                                        val call = "DELETE FROM $testTable " +
                                                "WHERE TESTNO = $testNo and " +
                                                "ORDERNUMBER = '$testOrderNumber' and " +
                                                "SYSUNIQUEID = $testID"


                                        val response = apiCall.insertUpdateDelete(call)
                                        if (response == "200 OK") {
                                            sharedViewModel.closePopup()
                                        } else {
                                            sharedViewModel.snackBarMessage("Error Deleting Test Line, Please Try Again")
                                        }
                                    }
                                }
                            ) { Text(text = "Yes") }
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
            showPopupWindow.value = true
        }
    }

    /**
     * Opens a confirmation popup for deleting an assembly adjustment.
     *
     * When confirmed, the adjustment is removed and, because adjustments are
     * issued as soon as they are saved, its quantity is reversed out of the
     * line's issues (unless the order is Finished or Closed). The assembly
     * line's planned quantity is not changed.
     *
     * @param line The adjustment record to delete.
     */
    fun adjustmentsDelete(line: APICallTables.assemblyAdjustment) {
        popupDetails.width = 450
        popupDetails.height = 200
        popupDetails.content = {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(all = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Delete Adjustment Line",
                        style = MaterialTheme.typography.headlineLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "Are you sure you want to delete the ${line.LINEDESCRIPTION} adjustment from this assembly?",
                        textAlign = TextAlign.Center,
                    )

                    Row(
                        modifier = Modifier.weight(2f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        Button(
                            modifier = Modifier
                                .weight(3f)
                                .padding(horizontal = 15.dp, vertical = 20.dp),
                            onClick = { sharedViewModel.closePopup() }
                        ) { Text(text = "No") }
                        Button(
                            modifier = Modifier
                                .weight(3f)
                                .padding(horizontal = 15.dp, vertical = 20.dp),
                            onClick = {
                                viewModelScope.launch {
                                    val call = "DELETE FROM OSTDEF_ADJUSTMENTS " +
                                            "WHERE ADJUSTNO = ${line.ADJUSTNO} and " +
                                            "ORDERNUMBER = '${line.ORDERNUMBER}' and " +
                                            "SYSUNIQUEID = ${line.SYSUNIQUEID}"

                                    val responseCall = apiCall.insertUpdateDelete(call)
                                    if (responseCall == "200 OK") {
                                        if (canIssue && line.ADJUSTQTY != 0.0) {
                                            val assemblyLine = loadAssemblyLine(apiCall, line.ORDERNUMBER, line.LINECODE, line.LINENUMBER)
                                            val reversed = assemblyLine != null &&
                                                    issueToAssemblyLine(apiCall, assemblyLine, -line.ADJUSTQTY, null, sharedViewModel.currentUser.value)
                                            if (!reversed) {
                                                sharedViewModel.snackBarMessage("Adjustment deleted, but its issued qty couldn't be reversed. Please See Daniel")
                                            }
                                        }
                                        closeDetailScreen.value = true
                                        sharedViewModel.closePopup()
                                    } else {
                                        sharedViewModel.snackBarMessage("Error Deleting Adjustment Line, Please Try Again")
                                    }
                                }
                            }
                        ) { Text(text = "Yes") }
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
        showPopupWindow.value = true
    }

    /**
     * Completes the current assembly order by creating its receipt.
     *
     * Material is issued to the lines as it is added (by scanning, entering a
     * qty, long pressing a line or saving an adjustment), so completing only
     * creates the assembly receipt, using the order quantity, item
     * information, receipt warehouse, receipt location, receipt date and
     * calculated receipt unit cost.
     *
     * If the operation succeeds, the detail screen is marked for closing.
     */
    fun completeOrderSend() {
        viewModelScope.launch {
            val date = toDateFormatYYYYMMDD(LocalDate.now())

            val itemCall: List<APICallTables.ItemMaster>? = apiCall.query("SELECT * FROM ITEMMASTER WHERE ITEMCODE = '${assemblyHeader.first().ITEMCODE}'")
            val assemblyHeaderCall: List<APICallTables.AssemblyHeader>? = apiCall.query("SELECT * FROM ASSEMBLYHEADER WHERE ORDERNUMBER = '${assemblyHeader.first().ORDERNUMBER}'")
            val receiptUnitCost = assemblyHeaderCall?.first()?.PLANNEDTOTALCOSTS?.div(assemblyHeader.first().ORDERQTY)


            val call = "INSERT INTO ASSEMBLYRECEIPTS" +
                    "(ORDERNUMBER, ITEMCODE, RECEIPTQTY, RECEIPTUNIT, RECEIPTDATE, RECEIPTUNITCOST, RECEIPTWAREHOUSE, RECEIPTLOCATION) " +
                    "VALUES " +
                    "('${assemblyHeader.first().ORDERNUMBER}','${assemblyHeader.first().ITEMCODE}',${assemblyHeader.first().ORDERQTY},'${assemblyHeader.first().ITEMUNIT}', " +
                    "'$date', '${receiptUnitCost}' ,'${itemCall?.first()?.DEFAULTRECEIPTWHOUSE}', '${itemCall?.first()?.DEFAULTRECEIPTLOCATION}')"


            val response = apiCall.insertUpdateDelete(call)
            if (response == "200 OK") {
                sharedViewModel.snackBarMessage("Order Completed")
                closeDetailScreen.value = true
            } else {
                sharedViewModel.snackBarMessage("Error Saving, Please Try Again")
            }
        }
    }

    /**
     * Checks whether the current assembly order is already marked as complete.
     *
     * If the order is open, it is completed immediately. If it has already been
     * completed, a confirmation popup is displayed asking the user whether they
     * want to continue.
     */
    fun completeOrder() {
        if (fullQtyMode && batchesDone < totalBatches) {
            popupMessageDetails.width = 380
            popupMessageDetails.height = 200
            popupMessageDetails.message = "Only $batchesDone of $totalBatches batches are done.\n\nComplete the whole order anyway?"
            popupMessageDetails.messageButton1Text = "No"
            popupMessageDetails.onClickAction1 = {
                sharedViewModel.closeMessagePopup()
            }
            popupMessageDetails.messageButton2Text = "Yes"
            popupMessageDetails.onClickAction2 = {
                sharedViewModel.closeMessagePopup()
                completeOrderChecked()
            }
            sharedViewModel.popupMessageDetails = popupMessageDetails
            sharedViewModel.openMessagePopup()
            return
        }
        completeOrderChecked()
    }

    /**
     * Completes the order straight away if it is open, otherwise asks first
     * as it looks like it has already been completed.
     */
    private fun completeOrderChecked() {
        if (assemblyHeader.first().ORDERSTATUS == "Open") {
            completeOrderSend()
        } else {
            popupMessageDetails.width = 380
            popupMessageDetails.height = 200
            popupMessageDetails.message = "It looks like this assembly order has \n already been marked as complete.\n\nWould you like to continue?"
            popupMessageDetails.messageButton1Text = "No"
            popupMessageDetails.onClickAction1 = {
                sharedViewModel.closeMessagePopup()
            }
            popupMessageDetails.messageButton2Text = "Yes"
            popupMessageDetails.onClickAction2 = {
                completeOrderSend()
                sharedViewModel.closeMessagePopup()
            }
            sharedViewModel.popupMessageDetails = popupMessageDetails
            sharedViewModel.openMessagePopup()
        }
    }

    /**
     * Opens a confirmation popup for deleting the current assembly order.
     *
     * When confirmed, all assembly lines associated with the order are deleted
     * before the assembly order header is deleted.
     */
    fun deleteAssemblyOrder() {
        popupDetails.width = 450
        popupDetails.height = 250
        popupDetails.content = {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(all = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Delete Assembly Order",
                        style = MaterialTheme.typography.headlineLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "Are you sure you want to delete the assembly order: ${assemblyHeader.firstOrNull()?.ORDERNUMBER} - ${assemblyHeader.firstOrNull()?.ITEMDESCRIPTION}?",
                        textAlign = TextAlign.Center,
                    )

                    Row(
                        modifier = Modifier.weight(2f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        Button(
                            modifier = Modifier
                                .weight(3f)
                                .padding(horizontal = 15.dp, vertical = 20.dp),
                            onClick = { sharedViewModel.closePopup() }
                        ) { Text(text = "No") }
                        Button(
                            modifier = Modifier
                                .weight(3f)
                                .padding(horizontal = 15.dp, vertical = 20.dp),
                            onClick = {
                                viewModelScope.launch {
                                    val assemblyLinesCall = "DELETE FROM ASSEMBLYLINES WHERE ORDERNUMBER = '${assemblyHeader.firstOrNull()?.ORDERNUMBER}'"
                                    val assemblyHeaderCall = "DELETE FROM AssemblyHeader WHERE ORDERNUMBER = '${assemblyHeader.firstOrNull()?.ORDERNUMBER}'"


                                    val responseAssembly = apiCall.insertUpdateDelete(assemblyLinesCall)
                                    val responseHeader = apiCall.insertUpdateDelete(assemblyHeaderCall)
                                    if (responseHeader == "200 OK" && responseAssembly == "200 OK") {
                                        sharedViewModel.closePopup()
                                        closeDetailScreen.value = true
                                        sharedViewModel.snackBarMessage("Assembly Order Deleted")

                                    } else {
                                        sharedViewModel.snackBarMessage("Error Deleting Assembly Order, Please Try Again")
                                    }
                                }
                            }
                        ) { Text(text = "Yes") }
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
        showPopupWindow.value = true
    }
}