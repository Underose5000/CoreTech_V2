package com.example.coretechv2.viewmodel

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coretechv2.dataclasses.APICallTables
import com.example.coretechv2.dataclasses.ItemDescriptorItem
import com.example.coretechv2.dataclasses.MenuItem
import com.example.coretechv2.dataclasses.PopupItems
import com.example.coretechv2.dataclasses.SwitchItem
import com.example.coretechv2.dataclasses.assemblydataclasses.AssemblyLinesItem
import com.example.coretechv2.repository.APICall
import com.example.coretechv2.repository.DataStoreManager
import com.example.coretechv2.repository.testAndAdjustmentLookup
import com.example.coretechv2.repository.testAndAdjustmentLookupCall
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.time.LocalDate


/**
 * ViewModel responsible for retrieving and storing the detailed information
 * associated with one or more assembly orders.
 *
 * For each assembly order, the ViewModel retrieves:
 * - The assembly order header.
 * - The assembly order lines.
 * - Any associated tests.
 * - Any associated adjustments.
 * - Any associated notes.
 *
 * The retrieved information is stored in [AssemblyOrdersList], where each
 * assembly order is represented by a [SnapshotStateList] containing the
 * different sections of the order details.
 *
 * @param dataStoreManager Provides access to the application's stored API
 * configuration and connection details.
 * @param sharedViewModel Shared ViewModel containing application-wide state
 * that may be required by this ViewModel.
 */
class AssemblyOrdersRecordDetailsViewModel(private val dataStoreManager: DataStoreManager, var sharedViewModel: SharedViewModel) : ViewModel() {

    private val apiCall = APICall(dataStoreManager)
    var popupDetails = PopupItems().copy()
    var itemListSearched = mutableStateListOf<APICallTables.ItemDescriptor>()
        private set
    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent = _snackbarEvent.asSharedFlow()
    var AssemblyOrdersList = mutableStateListOf<SnapshotStateList<Any>>()
        private set
    var Searchfield by mutableStateOf("")
        private set
    val notes = mutableMapOf<String, String>()

    /**
     * Retrieves the complete details for each assembly order specified in
     * [compareList].
     *
     * For each order number, the method retrieves the assembly header and
     * assembly lines from the API. It also retrieves the associated tests,
     * adjustments, and notes using [testAndAdjustmentLookupCall].
     *
     * The retrieved information is combined into a single
     * [SnapshotStateList] and added to [AssemblyOrdersList]. The resulting
     * state list can then be observed by Compose and used to display the
     * assembly order details.
     *
     * If an assembly header cannot be found for an order, the header is
     * omitted from that order's detail list. The remaining sections are still
     * added.
     *
     * The API operations are performed inside [viewModelScope], allowing the
     * work to continue within the ViewModel's lifecycle and be cancelled when
     * the ViewModel is cleared.
     *
     * @param compareList List of assembly order numbers whose details should
     * be retrieved.
     */
    fun retrieveAssemblyDetails(compareList: List<String>) {
        viewModelScope.launch {
            AssemblyOrdersList.clear()
            for (order in 0 until compareList.size){
                val assemblyDetail = mutableStateListOf<Any>()
                var assemblyHeader: APICallTables.AssemblyHeader?
                val assemblyDetailsLines = mutableStateListOf<AssemblyLinesItem>()
                var notes by mutableStateOf("")

                val assemblyHeaderCall: List<APICallTables.AssemblyHeader>? = apiCall.query("SELECT * FROM AssemblyHeader where OrderNumber = '${compareList[order]}'")
                assemblyHeader = assemblyHeaderCall?.firstOrNull()

                val assemblyDetailsLinesCall: List<APICallTables.AssemblyLines>? = apiCall.query("SELECT * FROM AssemblyLines where OrderNumber = '${compareList[order]}' order by LINENUMBER")
                assemblyDetailsLines.clear()
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
                }
                val (newTest, newAdjustments, newNotes) = testAndAdjustmentLookupCall(dataStoreManager, compareList[order])
                val tests: SnapshotStateMap<Int, Any> = newTest
                val adjustments: SnapshotStateMap<Int, List<APICallTables.assemblyAdjustment>> = newAdjustments
                notes = newNotes

                assemblyDetail.clear()
                if (assemblyHeader != null) {
                    assemblyDetail.add(assemblyHeader)
                }
                assemblyDetail.add(assemblyDetailsLines)
                assemblyDetail.add(tests)
                assemblyDetail.add(adjustments)
                assemblyDetail.add(notes)
                AssemblyOrdersList.add(assemblyDetail)
            }
        }
    }

}

