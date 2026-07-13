package com.example.coretechv2.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
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
import com.example.coretechv2.ui.screen.NotesScreen
import com.example.coretechv2.ui.screen.assemblytestsandadjustments.AdjustmentsScreen
import com.example.coretechv2.ui.screen.assemblytestsandadjustments.GelTimeScreen
import com.example.coretechv2.ui.screen.assemblytestsandadjustments.ViscosityScreen
import kotlinx.coroutines.launch
import kotlin.String
import kotlin.collections.emptyList
import kotlin.collections.firstOrNull

class AssemblyOrderDetailsViewModel(private val dataStoreManager: DataStoreManager, var sharedViewModel: SharedViewModel) : ViewModel() {

    private val apiCall = APICall(dataStoreManager)
    var popupDetails = PopupItems().copy()
    var popupMessageDetails = MessageItems()
    var showPopupWindow = mutableStateOf(false)
        private set
    var showPopupMessage = mutableStateOf(false)
        private set
    var savedactionMenuList: MutableList<MenuItem> = mutableListOf()

    var actionMenuList: MutableList<MenuItem> = mutableListOf(
        MenuItem(
            title = "Refresh page",
            onClick = {
                reload()
                showActionMenu = false
            }
        ),
        MenuItem(
            title = "Edit Order",
            onClick = {
                savedactionMenuList = actionMenuList.toMutableList()
                editMode = true
                showActionMenu = false
                actionMenuList = mutableListOf(
                    MenuItem(
                        title = "Save Edits",
                        onClick = {
                            actionMenuList = savedactionMenuList.toMutableList()
                            editMode = false
                            saveEditsChange()
                            showActionMenu = false
                        }
                    ),
                    MenuItem(
                        title = "Cancel",
                        onClick = {
                            actionMenuList = savedactionMenuList.toMutableList()
                            editMode = false
                            reload()
                            showActionMenu = false
                    }
                )
                )
            },
        ),
        MenuItem(
            title = "Complete",
            onClick = {}
        ),

    )

    val addMenuList = listOf(
        MenuItem(
            title = "Viscosity Test",
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
            title = "Gel Time Test",
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
            title = "Adjustment",
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

    var notes by mutableStateOf("")
        private set
    var labelIndex: Int = 0

    var labelStyle: LabelStyles = LabelStyles.ERROR

    var editMode by mutableStateOf(false)
        private set

    var orderQty = mutableStateOf("")

    var noOfBatches = mutableStateOf("")






    fun togglePopup(window: MutableState<Boolean>){
        window.value = !window.value
    }

    fun showAssemblyDetails(){
        showAssemblyDetails.value = true
    }
    fun hideAssemblyDetails(){
        showAssemblyDetails.value = false
    }

    fun showTestAndAdjustments(){
        showTestAndAdjustments.value = true
    }
    fun hideTestAndAdjustments(){
        showTestAndAdjustments.value = false
    }
    fun retrieveAssemblyDetails(){
        viewModelScope.launch {
            val assemblyHeaderCall : List<APICallTables.AssemblyHeader>? = apiCall.query("SELECT * FROM AssemblyHeader where OrderNumber = '${sharedViewModel.currentOrderNumber.value}'")
            assemblyHeader = assemblyHeaderCall ?: emptyList()
            orderQty.value = assemblyHeader.first().ORDERQTY.toString()
            noOfBatches.value = assemblyHeader.first().ADDITIONALFIELD_12


            val assemblyDetailsLinesCall : List<APICallTables.AssemblyLines>? = apiCall.query("SELECT * FROM AssemblyLines where OrderNumber = '${sharedViewModel.currentOrderNumber.value}'")
            assemblyDetailsLines.clear()
            for (i in 0 until (assemblyDetailsLinesCall?.size ?: 0)) {
                val assemblyDetailsLine: AssemblyLinesItem = AssemblyLinesItem(
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
                    ADDITIONALFIELD_1 = assemblyDetailsLinesCall[i].ADDITIONALFIELD_1,
                    ADDITIONALFIELD_2 = assemblyDetailsLinesCall[i].ADDITIONALFIELD_2,
                    ADDITIONALFIELD_3 = assemblyDetailsLinesCall[i].ADDITIONALFIELD_3,
                    ADDITIONALFIELD_4 = assemblyDetailsLinesCall[i].ADDITIONALFIELD_4,
                    ADDITIONALFIELD_6 = assemblyDetailsLinesCall[i].ADDITIONALFIELD_6,
                )
                assemblyDetailsLines.add(assemblyDetailsLine)
            }
            sharedViewModel.currentAssemblyHeader = assemblyHeader.firstOrNull()
            sharedViewModel.currentAssemblyLines = assemblyDetailsLinesCall
            Log.d("Test","assemblyDetailsLines size = ${assemblyDetailsLines.size} string = ${assemblyDetailsLines}")


        }
    }

    fun retrieveTestDetails(){
        testAndAdjustments.clear()
        val testCount = mutableListOf<Int>()

        viewModelScope.launch {
            val viscosityTests : List<APICallTables.viscosityTest>? = apiCall.query("SELECT * FROM OSTDEF_VISCOSITY_TESTS where OrderNumber = '${sharedViewModel.currentOrderNumber.value}'")
            testCount.addAll(viscosityTests?.map { it.TESTNO } ?: emptyList())
            val gelTimeTests : List<APICallTables.gelTimeTest>? = apiCall.query("SELECT * FROM OSTDEF_GELTIME_TESTS where OrderNumber = '${sharedViewModel.currentOrderNumber.value}'")
            testCount.addAll(gelTimeTests?.map { it.TESTNO } ?: emptyList())

            testCount.sortDescending()
            sharedViewModel.testCount.value = testCount.firstOrNull() ?: 0

            val adjustmentLines : List<APICallTables.assemblyAdjustment>? = apiCall.query("SELECT * FROM OSTDEF_ADJUSTMENTS where OrderNumber = '${sharedViewModel.currentOrderNumber.value}'")
            testCount.addAll(adjustmentLines?.map { it.ADJUSTNO } ?: emptyList())

            val notesline : List<APICallTables.notes>? = apiCall.query("SELECT * FROM OSTDEF_NOTES where IDNUMBER = '${sharedViewModel.currentOrderNumber.value}' and TYPE = '${NoteTypes.ASSEMBLY.toStringName()}'")


            val testAndAdjustmentsCount = testCount.toMutableList()
            if (testAndAdjustmentsCount.isEmpty()){
                testAndAdjustmentsCount.add(0)
            }
            testAndAdjustmentsCount.sortDescending()


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
            Log.d("test",notes)
        }
    }

    fun retrieveLabelData() {
        viewModelScope.launch {
            val itemInfoCall: List<APICallTables.assemblyLabelItemInfo>? = apiCall.query("SELECT LII.HEADERSYSUNIQUEID, LII.SYSUNIQUEID, LII.ITEMCODE, LII.TOPNAME, LII.MIDDLENAME, LII.BOTTOMNAME, LII.SIZE, LII.QRCODE, LII.VARIANT, LII.BESTBEFORE, LII.LABELSTYLE, LII.BOXQTY, IM.ITEMBARCODE FROM OSTDEF_LABELITEMINFO AS LII JOIN ITEMMASTER AS IM on LII.ITEMCODE = IM.ITEMCODE where LII.ITEMCODE = '${assemblyHeader.firstOrNull()?.ITEMCODE}'")
            itemInfo = itemInfoCall ?: emptyList()

            for (x in 0 until itemInfo.size){
                val classInfoCall: List<APICallTables.assemblyLabelClassInfo>? = apiCall.query("Select * from OSTDEF_LABELCLASSINFO where SYSUNIQUEID = '${itemInfo[x].HEADERSYSUNIQUEID}'")
                val labelLayoutCall: List<APICallTables.assemblyLabelLayout>? = apiCall.query("Select * from OSTDEF_LABELLAYOUTINFO where LABELID = '${itemInfo[x].LABELSTYLE}'")
                val boxLayoutCall: List<APICallTables.assemblyLabelLayout>? = apiCall.query("Select * from OSTDEF_LABELLAYOUTINFO where LABELID = '${LabelStyles.BOX}'")
                val dgInfoCall: List<APICallTables.assemblyLabelDGInfo>? = apiCall.query("select dgi.UNNUMBER, dgi.PACKINGGROUP, dgi.DGCLASS, dgl.DGQUANTITY from OSTDEF_DGINFO as dgi " +
                        "join OSTDEF_DGLINES as dgl on dgl.HEADERSYSUNIQUEID = dgi.SYSUNIQUEID and dgl.LINECODE = '${itemInfo[x].ITEMCODE}' and dgl.CODETYPE = 'Item Code'")
                if (classInfoCall != null && labelLayoutCall != null){
                    productLabelDataList.add(LabelElements(itemInfo[x], classInfoCall.first(), labelLayoutCall.first(), dgInfoCall?.first(), sharedViewModel))
                    if (boxLayoutCall != null){
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
                                title = "${itemName}Product Labels",
                                onClick = {
                                    labelStyle = itemInfo[x].LABELSTYLE
                                    labelIndex = x
                                    sharedViewModel.openLabelPreview()
                                    showActionMenu = false
                                }
                            ),
                            MenuItem(
                                title = "${itemName}Box Labels",
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

    fun menuPressed(){
        showAddMenu = false
        showActionMenu = !showActionMenu
    }

    fun addPressed(){
        showActionMenu = false
        showAddMenu = !showAddMenu
    }

    fun notesPressed(){
        popupDetails.width = 700
        popupDetails.height = 500
        popupDetails.content = {
            sharedViewModel.updateSaveType(APICallTypes.INSERT)
            NotesScreen(sharedViewModel, NoteTypes.ASSEMBLY)
        }
        showPopupWindow.value = true
        showAddMenu = false
    }

    fun addListItemPressed(selection : MutableState<Boolean>){
        selection.value = true
        showAddMenu = false
    }

    fun onEditLineChange(newValue: String, field : AssemblyLinesItem, line: Int){


        Log.d("Qty", assemblyDetailsLines[line].ORDERQTY)
        val updatedList = assemblyDetailsLines

        updatedList[line] = updatedList[line].copy(
            ORDERQTY = newValue
        )

        assemblyDetailsLines = updatedList
        Log.d("update Qty", assemblyDetailsLines[line].ORDERQTY)
    }

    fun saveEditsChange() {
        if (showAssemblyDetails.value) {
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
    fun reload(){
        retrieveAssemblyDetails()
        retrieveTestDetails()
    }
}