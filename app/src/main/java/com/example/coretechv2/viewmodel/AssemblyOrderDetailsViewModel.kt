package com.example.coretechv2.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coretechv2.dataclasses.APICallTypes
import com.example.coretechv2.dataclasses.GelField
import com.example.coretechv2.dataclasses.GelTimeItem
import com.example.coretechv2.dataclasses.MenuItem
import com.example.coretechv2.dataclasses.TestTypes
import com.example.coretechv2.dataclasses.TestTypes.FLAME
import com.example.coretechv2.dataclasses.TestTypes.GEL_TIME
import com.example.coretechv2.dataclasses.TestTypes.VISCOSITY
import com.example.coretechv2.dataclasses.VisField
import com.example.coretechv2.dataclasses.VisSettings
import com.example.coretechv2.dataclasses.ViscosityItem
import com.example.coretechv2.dataclasses.visHasValue
import com.example.coretechv2.repository.APICall
import com.example.coretechv2.repository.DataStoreManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlin.collections.firstOrNull

class AssemblyOrderDetailsViewModel(private val dataStoreManager: DataStoreManager) : ViewModel() {

    private val apiCall = APICall(dataStoreManager)
    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent = _snackbarEvent.asSharedFlow()

    val actionMenuList = listOf(
        MenuItem(
            title = "Edit",
            onClick = {},
        ),
        MenuItem(
            title = "Product Labels",
            onClick = {}
        ),
        MenuItem(
            title = "Box Labels",
            onClick = {}
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
                onVisClear()
                addListItemPressed(showViscosityScreen)
                saveType = APICallTypes.INSERT
                      },
        ),
        MenuItem(
            title = "Gel Time Test",
            onClick = {
                onGelClear()
                addListItemPressed(showGelScreen)
                saveType = APICallTypes.INSERT
            },
        ),
        MenuItem(
            title = "Adjustment",
            onClick = {}
        ),
    )

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
    var visReading = mutableStateOf(ViscosityItem())
    var gelReading = mutableStateOf(GelTimeItem())
    var assemblyHeader by mutableStateOf<List<APICall.AssemblyHeader>>(emptyList())
        private set
    var assemblyDetailsLines by mutableStateOf<List<APICall.AssemblyLines>>(emptyList())
        private set
    var visTestNumberCount by mutableStateOf("")
    var gelTestNumberCount by mutableStateOf("")

//region Popup windows Showing Variables
    var showActionMenu by mutableStateOf(false)
        private set
    var showAddMenu by mutableStateOf(false)
        private set
    var showViscosityScreen = mutableStateOf(false)
        private set
    var showGelScreen = mutableStateOf(false)
        private set
    var showMessageOne = mutableStateOf(false)
        private set
    var showMessageTwo = mutableStateOf(false)
        private set
    var showSpindleList by mutableStateOf(false)
        private set
    var showindexList by mutableStateOf(false)
        private set
    var showtestNumber by mutableStateOf(false)
        private set
    var saveType by mutableStateOf(APICallTypes.INSERT)
        private set
//endregion

    fun retrieveAssemblyDetails(orderNumber: String){
        viewModelScope.launch {
            val assemblyHeaderCall : List<APICall.AssemblyHeader>? = apiCall.query("SELECT * FROM AssemblyHeader where OrderNumber = '${orderNumber}'")
            if(assemblyHeaderCall?.first()?.ADDITIONALFIELD_1 == ""||assemblyHeaderCall?.firstOrNull()?.ADDITIONALFIELD_1 ==null){assemblyHeaderCall?.first()?.ADDITIONALFIELD_1 = "N/A" }
            if(assemblyHeaderCall?.first()?.ADDITIONALFIELD_2 == ""||assemblyHeaderCall?.firstOrNull()?.ADDITIONALFIELD_2 ==null){assemblyHeaderCall?.first()?.ADDITIONALFIELD_2 = "N/A" }
            if(assemblyHeaderCall?.first()?.ADDITIONALFIELD_3 == ""||assemblyHeaderCall?.firstOrNull()?.ADDITIONALFIELD_3 ==null){assemblyHeaderCall?.first()?.ADDITIONALFIELD_3 = "N/A" }
            if(assemblyHeaderCall?.first()?.ADDITIONALFIELD_4 == ""||assemblyHeaderCall?.firstOrNull()?.ADDITIONALFIELD_4 ==null){assemblyHeaderCall?.first()?.ADDITIONALFIELD_4 = "N/A" }
            if(assemblyHeaderCall?.first()?.ADDITIONALFIELD_5 == ""||assemblyHeaderCall?.firstOrNull()?.ADDITIONALFIELD_5 ==null){assemblyHeaderCall?.first()?.ADDITIONALFIELD_5 = "N/A" }
            if(assemblyHeaderCall?.first()?.ADDITIONALFIELD_6 == ""||assemblyHeaderCall?.firstOrNull()?.ADDITIONALFIELD_6 ==null){assemblyHeaderCall?.first()?.ADDITIONALFIELD_6 = "N/A" }
            if(assemblyHeaderCall?.first()?.ADDITIONALFIELD_7 == ""||assemblyHeaderCall?.firstOrNull()?.ADDITIONALFIELD_7 ==null){assemblyHeaderCall?.first()?.ADDITIONALFIELD_7 = "N/A" }
            if(assemblyHeaderCall?.first()?.ADDITIONALFIELD_8 == ""||assemblyHeaderCall?.firstOrNull()?.ADDITIONALFIELD_8 ==null){assemblyHeaderCall?.first()?.ADDITIONALFIELD_8 = "N/A" }
            if(assemblyHeaderCall?.first()?.ADDITIONALFIELD_9 == ""||assemblyHeaderCall?.firstOrNull()?.ADDITIONALFIELD_9 ==null){assemblyHeaderCall?.first()?.ADDITIONALFIELD_9 = "N/A" }
            if(assemblyHeaderCall?.first()?.ADDITIONALFIELD_10 == ""||assemblyHeaderCall?.firstOrNull()?.ADDITIONALFIELD_10 ==null){assemblyHeaderCall?.first()?.ADDITIONALFIELD_10 = "N/A" }
            if(assemblyHeaderCall?.first()?.ADDITIONALFIELD_11 == ""||assemblyHeaderCall?.firstOrNull()?.ADDITIONALFIELD_11 ==null){assemblyHeaderCall?.first()?.ADDITIONALFIELD_11 = "N/A" }
            if(assemblyHeaderCall?.first()?.ADDITIONALFIELD_12 == ""||assemblyHeaderCall?.firstOrNull()?.ADDITIONALFIELD_12 ==null){assemblyHeaderCall?.first()?.ADDITIONALFIELD_12 = "N/A" }
            assemblyHeader = assemblyHeaderCall ?: emptyList()

            val assemblyDetailsLinesCall : List<APICall.AssemblyLines>? = apiCall.query("SELECT * FROM AssemblyLines where OrderNumber = '${orderNumber}'")
            assemblyDetailsLines = assemblyDetailsLinesCall ?: emptyList()

            val viscosityLinesCall : List<APICall.viscosityTest>? = apiCall.query("SELECT * FROM OSTDEF_VISCOSITY_TESTS where OrderNumber = '${orderNumber}'")
            visTestNumberCount = (viscosityLinesCall?.count().toString())

            val gelTimeLinesCall : List<APICall.viscosityTest>? = apiCall.query("SELECT * FROM OSTDEF_GELTIME_TESTS where OrderNumber = '${orderNumber}'")
            gelTestNumberCount = (gelTimeLinesCall?.count().toString())

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

    fun addListItemPressed(selection : MutableState<Boolean>){
        selection.value = true
        showAddMenu = false
    }
    fun hideMessage(){
        showMessageOne.value = false
    }


//region Viscosity Functions
    fun spindlePressed(){
        showSpindleList = !showSpindleList
    }
    fun indexPressed(){
        showindexList = !showindexList
    }
    fun testNumberPressed(){
        showtestNumber = !showtestNumber
    }
    fun onDropDownChange(newValue: String, field : VisSettings){
        visReading.value = when (field) {
            VisSettings.SPINDLE -> visReading.value.copy(spindle = newValue)
            VisSettings.INDEX -> visReading.value.copy(indexRange = newValue)
            VisSettings.TESTNUMBER -> visReading.value.copy(testNumber = newValue)
        }
        if (field == VisSettings.SPINDLE){
            showSpindleList = false
        }
        if (field == VisSettings.INDEX){
            showindexList = false
        }
        if (field == VisSettings.TESTNUMBER){
            showtestNumber = false
        }
    }

    fun onVisChange(newValue: String, field : VisField){
        visReading.value = when (field) {
            VisField.VIS60 -> visReading.value.copy(vis60 = newValue)
            VisField.VIS30 -> visReading.value.copy(vis30 = newValue)
            VisField.VIS12 -> visReading.value.copy(vis12 = newValue)
            VisField.VIS06  -> visReading.value.copy(vis06 = newValue)
            VisField.VIS03  -> visReading.value.copy(vis03 = newValue)
            VisField.VIS1_5 -> visReading.value.copy(vis1_5 = newValue)
            VisField.VIS0_6 -> visReading.value.copy(vis0_6 = newValue)
            VisField.VIS0_3 -> visReading.value.copy(vis0_3 = newValue)
        }
    }
    fun onGelChange(newValue: String, field : GelField){
        gelReading.value = when (field) {
            GelField.HOUR -> gelReading.value.copy(hour = newValue)
            GelField.MINUTE -> gelReading.value.copy(minute = newValue)
            GelField.SECOND -> gelReading.value.copy(second = newValue)
            GelField.CATPERCENT -> gelReading.value.copy(catPercent = newValue)
            GelField.CATALYST -> gelReading.value.copy(catalyst = newValue)
            GelField.TESTNUMBER -> gelReading.value.copy(testNumber = newValue)


        }
    }

    fun toDatabaseValues(test: TestTypes): List<Any>{
        var ratio = 0.0
        if (test == VISCOSITY){
            if (visReading.value.indexRange == "6/60"){
                ratio = visReading.value.vis06.toDouble() / visReading.value.vis60.toDouble()
            }
            if (visReading.value.indexRange == "3/30"){
                ratio = visReading.value.vis03.toDouble() / visReading.value.vis30.toDouble()
            }
            if (visReading.value.indexRange == "0.6/6"){
                ratio = visReading.value.vis0_6.toDouble() / visReading.value.vis06.toDouble()
            }
            if (visReading.value.indexRange == "0.3/3"){
                ratio = visReading.value.vis0_3.toDouble() / visReading.value.vis03.toDouble()
            }
        }
        val indexResult = kotlin.math.round(ratio * 10 * 10) / 10
        Log.d("Test",indexResult.toString())
        return when (test) {
            VISCOSITY -> listOf(visReading.value.testNumber.toDouble(), visReading.value.spindle, indexResult, visReading.value.vis60.toDouble(), visReading.value.vis30.toDouble(), visReading.value.vis12.toDouble(), visReading.value.vis06.toDouble(), visReading.value.vis03.toDouble(), visReading.value.vis1_5.toDouble(), visReading.value.vis0_6.toDouble(), visReading.value.vis0_3.toDouble())
            GEL_TIME -> listOf("Render")
            FLAME -> listOf("Cladding")
        }
    }
    fun updateDatabaseValues(values : List<Any>, Headings: List<String>): String {
        var call = String()
        for(i in Headings.indices)
            if (values[i] is Double || values[i] is Int){
                call += "${Headings[i]} = ${values[i]}, "
            }
            else {
                call += "${Headings[i]} = '${values[i]}', "
            }
        return call
    }
    fun insertDatabaseValues(values : List<Any>,): String {
        var call = String()
        for(i in values.indices)
            if (values[i] is Double || values[i] is Int){
                call += "${values[i]}, "
            }
            else {
                call += "'${values[i]}', "
            }
        return call
    }
    fun onTestSave(currentUser: String, test: TestTypes, selection : MutableState<Boolean>){
        viewModelScope.launch {
            var call = ""
            if(saveType == APICallTypes.INSERT){
                call = "INSERT INTO ${test.toDatabaseHeadingName()} (ITEMCODE, ORDERNUMBER, ITEMDESCRIPTION, ${test.toDatabaseFieldName().joinToString(", ")}, SYSUSERCREATED, SYSUSERMODIFIED) VALUES('${assemblyHeader.firstOrNull()?.ITEMCODE.toString()}', '${assemblyHeader.firstOrNull()?.ORDERNUMBER.toString()}', '${assemblyHeader.firstOrNull()?.ITEMDESCRIPTION.toString()}', ${insertDatabaseValues(toDatabaseValues(test))} '${currentUser}', '${currentUser}')"
            }
            if(saveType == APICallTypes.UPDATE){
                call = "UPDATE ${test.toDatabaseHeadingName()} SET  ${updateDatabaseValues(toDatabaseValues(test),test.toDatabaseFieldName())}  SYSUSERMODIFIED = ${currentUser})"
            }

            Log.d("testsave","response =" + call)
            val response = apiCall.insertUpdateDelete(call)
            if(response == "200 OK"){
                selection.value = false
                _snackbarEvent.emit("Saved successfully")
            } else{
                _snackbarEvent.emit("Error Saving, Please Try Again")
            }
        }
    }
    fun onVisClear(){
        visReading.value = ViscosityItem()
        visReading.value.spindle = assemblyHeader.firstOrNull()?.ADDITIONALFIELD_3.toString()
        visReading.value.indexRange = assemblyHeader.firstOrNull()?.ADDITIONALFIELD_8.toString()
        visReading.value.testNumber = visTestNumberCount
    }

    fun onGelClear(){
        gelReading.value = GelTimeItem()
        gelReading.value.catPercent = assemblyHeader.firstOrNull()?.ADDITIONALFIELD_9.toString()
        gelReading.value.testNumber = gelTestNumberCount
    }
    fun onCancel(test: TestTypes){
        if(test == VISCOSITY){
            if (visHasValue(visReading)){
            message.value = "Test Results are not saved\nleave without saving?"
            messageButton1Text.value = "No"
            messageButton1Action.value = {
                showMessageTwo.value = false
            }
            messageButton2Action.value = {
                onVisClear()
                showMessageTwo.value = false
                showViscosityScreen.value = false
            }
            messageButton2Text.value = "Yes"
            showMessageTwo.value = true
            } else {
                onVisClear()
                showViscosityScreen.value = false
            }
        }else if(test == GEL_TIME){

        }

    }
//endregion
}