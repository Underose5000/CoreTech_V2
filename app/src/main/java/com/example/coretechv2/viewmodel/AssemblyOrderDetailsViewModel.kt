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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import com.example.coretechv2.dataclasses.LabelElements
import com.example.coretechv2.dataclasses.LabelStyles
import com.example.coretechv2.dataclasses.MenuItem
import com.example.coretechv2.dataclasses.MessageItems
import com.example.coretechv2.dataclasses.NoteTypes
import com.example.coretechv2.dataclasses.PopupItems
import com.example.coretechv2.dataclasses.assemblydataclasses.AssemblyLinesItem
import com.example.coretechv2.repository.APICall
import com.example.coretechv2.repository.DataStoreManager
import com.example.coretechv2.repository.toDateFormatYYYYMMDD
import com.example.coretechv2.ui.component.OutlinedStyleTextLine
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
    val testAndAdjustments = mutableStateListOf<SnapshotStateList<Any>>()

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

    var batchNumber = mutableStateOf("")

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
     * Retrieves the assembly order header and assembly line details from the API.
     *
     * The retrieved information is used to populate the assembly order state,
     * calculate the number of batches and batch size, identify assembly steps,
     * and update the corresponding values in the shared ViewModel.
     *
     * The method also updates the assembly header with the calculated batch
     * information.
     */
    fun retrieveAssemblyDetails() {
        viewModelScope.launch {
            val assemblyHeaderCall: List<APICallTables.AssemblyHeader>? = apiCall.query("SELECT * FROM AssemblyHeader where OrderNumber = '${sharedViewModel.currentOrderNumber.value}'")
            assemblyHeader = assemblyHeaderCall ?: emptyList()
            orderQty.value = assemblyHeader.firstOrNull()?.ORDERQTY.toString()
            if (orderQty.value.isNotEmpty() && assemblyHeader.firstOrNull()?.ADDITIONALFIELD_13?.isNotEmpty() == true) {
                maxBatchSize.value = assemblyHeader.firstOrNull()?.ADDITIONALFIELD_13!!
                numberOfBatches.value = ceil(orderQty.value.toDouble() / maxBatchSize.value.toDouble()).toInt().toString()
                oldNumberOfBatches.value = ceil(orderQty.value.toDouble() / maxBatchSize.value.toDouble()).toInt().toString()
            }

            val assemblyDetailsLinesCall: List<APICallTables.AssemblyLines>? =
                apiCall.query("SELECT * FROM AssemblyLines where OrderNumber = '${sharedViewModel.currentOrderNumber.value}' order by LINENUMBER")
            assemblyDetailsLines.clear()
            batchSize.doubleValue = 0.0
            for (i in 0 until (assemblyDetailsLinesCall?.size ?: 0)) {
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
                batchSize.doubleValue = orderQty.value.toDouble()
                batchSizeUnit.value = assemblyHeader.first().ITEMUNIT
            }
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
        val testCount = mutableListOf<Int>()

        viewModelScope.launch {
            val viscosityTests: List<APICallTables.viscosityTest>? = apiCall.query("SELECT * FROM OSTDEF_VISCOSITY_TESTS where OrderNumber = '${sharedViewModel.currentOrderNumber.value}'")
            testCount.addAll(viscosityTests?.map { it.TESTNO } ?: emptyList())
            val gelTimeTests: List<APICallTables.gelTimeTest>? = apiCall.query("SELECT * FROM OSTDEF_GELTIME_TESTS where OrderNumber = '${sharedViewModel.currentOrderNumber.value}'")
            testCount.addAll(gelTimeTests?.map { it.TESTNO } ?: emptyList())
            val elongationTests: List<APICallTables.ElongationalBreakTest>? = apiCall.query("SELECT * FROM OSTDEF_ELONGATIONAL_TEST where OrderNumber = '${sharedViewModel.currentOrderNumber.value}'")
            testCount.addAll(elongationTests?.map { it.TESTNO } ?: emptyList())
            val flameTests: List<APICallTables.FlammabilityTest>? = apiCall.query("SELECT * FROM OSTDEF_FLAMMABILITY_TEST where OrderNumber = '${sharedViewModel.currentOrderNumber.value}'")
            testCount.addAll(flameTests?.map { it.TESTNO } ?: emptyList())
            val resistivityTests: List<APICallTables.ResistivityTest>? = apiCall.query("SELECT * FROM OSTDEF_RESISTIVITY_TEST where OrderNumber = '${sharedViewModel.currentOrderNumber.value}'")
            testCount.addAll(resistivityTests?.map { it.TESTNO } ?: emptyList())
            val peakExothermTests: List<APICallTables.peakExothermTest>? = apiCall.query("SELECT * FROM OSTDEF_PEAKEXOTHERM_TEST where OrderNumber = '${sharedViewModel.currentOrderNumber.value}'")
            testCount.addAll(peakExothermTests?.map { it.TESTNO } ?: emptyList())


            testCount.sortDescending()
            sharedViewModel.testCount.value = testCount.firstOrNull() ?: 0

            val adjustmentLines: List<APICallTables.assemblyAdjustment>? = apiCall.query("SELECT * FROM OSTDEF_ADJUSTMENTS where OrderNumber = '${sharedViewModel.currentOrderNumber.value}'")
            testCount.addAll(adjustmentLines?.map { it.ADJUSTNO } ?: emptyList())

            val notesline: List<APICallTables.notes>? =
                apiCall.query("SELECT * FROM OSTDEF_NOTES where IDNUMBER = '${sharedViewModel.currentOrderNumber.value}' and TYPE = '${NoteTypes.ASSEMBLY.toStringName()}'")


            val testAndAdjustmentsCount = testCount.toMutableList()
            if (testAndAdjustmentsCount.isEmpty()) {
                testAndAdjustmentsCount.add(0)
            }
            testAndAdjustmentsCount.sortDescending()


            testAndAdjustments.clear()
            for (testValue in 0 until testAndAdjustmentsCount.first()) {
                val testAndAdjustment = mutableStateListOf<Any>()
                val test = mutableStateListOf<Any>()
                val adjust = mutableStateListOf<Any>()
                for (tn in 0 until (viscosityTests?.size ?: 0)) {
                    if (viscosityTests?.get(tn)?.TESTNO == testValue + 1) test.add(viscosityTests[tn])
                }
                for (tn in 0 until (gelTimeTests?.size ?: 0)) {
                    if (gelTimeTests?.get(tn)?.TESTNO == testValue + 1) test.add(gelTimeTests[tn])
                }
                for (tn in 0 until (elongationTests?.size ?: 0)) {
                    if (elongationTests?.get(tn)?.TESTNO == testValue + 1) test.add(elongationTests[tn])
                }
                for (tn in 0 until (flameTests?.size ?: 0)) {
                    if (flameTests?.get(tn)?.TESTNO == testValue + 1) test.add(flameTests[tn])
                }
                for (tn in 0 until (resistivityTests?.size ?: 0)) {
                    if (resistivityTests?.get(tn)?.TESTNO == testValue + 1) test.add(resistivityTests[tn])
                }
                for (tn in 0 until (peakExothermTests?.size ?: 0)) {
                    if (peakExothermTests?.get(tn)?.TESTNO == testValue + 1) test.add(peakExothermTests[tn])
                }
                for (tn in 0 until (adjustmentLines?.size ?: 0)) {
                    if (adjustmentLines?.get(tn)?.ADJUSTNO == testValue + 1) adjust.add(adjustmentLines[tn])
                }

                testAndAdjustment.add(test)
                testAndAdjustment.add(adjust)

                testAndAdjustments.add(testAndAdjustment)
            }

            notes = ""
            for (NN in 0 until (notesline?.size ?: 0)) {
                notes += notesline?.get(NN)?.NOTE
                notes += "\n\n"
            }

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
     * @param field The assembly line being edited.
     * @param line The index of the assembly line within [assemblyDetailsLines].
     */
    fun onEditLineChange(newValue: String, field: AssemblyLinesItem, line: Int) {

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
            if (oldNumberOfBatches.value == numberOfBatches.value) {
                numberOfBatches.value = ceil(orderQty.value.toDouble() / maxBatchSize.value.toDouble()).toInt().toString()
            } else {
                maxBatchSize.value = (orderQty.value.toDouble() / numberOfBatches.value.toDouble()).toString()
            }
            viewModelScope.launch {
                val call = "UPDATE AssemblyHeader SET " +
                        "ORDERQTY = ${orderQty.value.toDouble()}, " +
                        "ADDITIONALFIELD_12 = ${numberOfBatches.value.toInt()}, " +
                        "ADDITIONALFIELD_13 = ${maxBatchSize.value.toDouble()}, " +
                        "SYSUSERMODIFIED = '${sharedViewModel.currentUser.value}' " +
                        "WHERE ORDERNUMBER = '${assemblyHeader.first().ORDERNUMBER}'"


                val response = apiCall.insertUpdateDelete(call)
                if (response == "200 OK") {
                    sharedViewModel.snackBarMessage("Qty Saved successfully")
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
                reload()
            }
        }
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
     * Toggles the checked state of an assembly line and saves the change to the API.
     *
     * If saving the updated checkbox state fails, the change is reverted and
     * an error message is displayed.
     *
     * @param order The assembly line whose checked state is being changed.
     */
    fun lineChecked(order: AssemblyLinesItem) {
        order.ADDITIONALFIELD_1 = !order.ADDITIONALFIELD_1
        reload()
        viewModelScope.launch {
            val call = "UPDATE AssemblyLines SET  " +
                    "ADDITIONALFIELD_1 = '${order.ADDITIONALFIELD_1}', " +
                    "SYSUSERMODIFIED = '${sharedViewModel.currentUser.value}' " +
                    "WHERE LINENUMBER = ${order.LINENUMBER} and " +
                    "LINECODE = '${order.LINECODE}' and " +
                    "ORDERNUMBER = '${order.ORDERNUMBER}'"


            val response = apiCall.insertUpdateDelete(call)
            if (response != "200 OK") {
                sharedViewModel.snackBarMessage("Error Saving Checkbox, Please Try Again")
                order.ADDITIONALFIELD_1 = !order.ADDITIONALFIELD_1
                reload()
            }
        }
    }

    /**
     * Opens a popup allowing the user to enter or update the batch number
     * associated with an assembly line.
     *
     * The entered batch number is saved to the assembly line through the API.
     *
     * @param order The assembly line for which the batch number is being entered.
     */
    fun batchEntered(order: AssemblyLinesItem) {
        popupDetails.width = 450
        popupDetails.height = 200
        popupDetails.content = {
            LaunchedEffect(Unit) {
                batchNumber.value = order.ADDITIONALFIELD_2
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
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Batch Number",
                        style = MaterialTheme.typography.headlineLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedStyleTextLine(
                        value = batchNumber.value,
                        onValueChange = { newValue ->
                            batchNumber.value = newValue
                        }
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
                        ) { Text(text = "Cancel") }
                        Button(
                            modifier = Modifier
                                .weight(3f)
                                .padding(horizontal = 15.dp, vertical = 20.dp),
                            onClick = {
                                if (batchNumber.value.isNotEmpty()) {
                                    viewModelScope.launch {
                                        val call = "UPDATE AssemblyLines SET  " +
                                                "ADDITIONALFIELD_2 = '${batchNumber.value}', " +
                                                "SYSUSERMODIFIED = '${sharedViewModel.currentUser.value}' " +
                                                "WHERE LINENUMBER = ${order.LINENUMBER} and " +
                                                "LINECODE = '${order.LINECODE}' and " +
                                                "ORDERNUMBER = '${order.ORDERNUMBER}'"


                                        val response = apiCall.insertUpdateDelete(call)
                                        if (response == "200 OK") {
                                            sharedViewModel.closePopup()
                                        } else {
                                            sharedViewModel.snackBarMessage("Error Saving Batch Number, Please Try Again")
                                        }
                                    }
                                } else {
                                    sharedViewModel.closePopup()
                                }
                            }
                        ) { Text(text = "Save") }
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
        showPopupWindow.value = true
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
     * When confirmed, the adjustment is removed and the corresponding adjustment
     * quantity is deducted from the assembly line.
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

                                    val assemblyLinesCall = "UPDATE ASSEMBLYLINES " +
                                            "SET ORDERQTY = ORDERQTY - ${line.ADJUSTQTY} " +
                                            "WHERE ORDERNUMBER = '${line.ORDERNUMBER}' AND " +
                                            "LINECODE = '${line.LINECODE}' and " +
                                            "LINENUMBER = '${line.LINENUMBER}'"


                                    val responseCall = apiCall.insertUpdateDelete(call)
                                    val responseAssembly = apiCall.insertUpdateDelete(assemblyLinesCall)
                                    if (responseCall == "200 OK" && responseAssembly == "200 OK") {
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
     * Completes the current assembly order by creating an assembly receipt.
     *
     * The receipt is created using the order quantity, item information,
     * receipt warehouse, receipt location, receipt date, and calculated
     * receipt unit cost.
     *
     * If the operation succeeds, the detail screen is marked for closing.
     */
    fun completeOrderSend() {
        viewModelScope.launch {
            val itemCall: List<APICallTables.ItemMaster>? = apiCall.query("SELECT * FROM ITEMMASTER WHERE ITEMCODE = '${assemblyHeader.first().ITEMCODE}'")
            val assemblyHeaderCall: List<APICallTables.AssemblyHeader>? = apiCall.query("SELECT * FROM ASSEMBLYHEADER WHERE ORDERNUMBER = '${assemblyHeader.first().ORDERNUMBER}'")
            val date = toDateFormatYYYYMMDD(LocalDate.now())
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