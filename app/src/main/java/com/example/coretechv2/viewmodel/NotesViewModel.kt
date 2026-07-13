package com.example.coretechv2.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coretechv2.dataclasses.APICallTables
import com.example.coretechv2.dataclasses.APICallTypes
import com.example.coretechv2.dataclasses.MessageItems
import com.example.coretechv2.dataclasses.NoteTypes
import com.example.coretechv2.dataclasses.PopupItems
import com.example.coretechv2.repository.APICall
import com.example.coretechv2.repository.DataStoreManager
import kotlinx.coroutines.launch


class NotesViewModel(private val dataStoreManager: DataStoreManager, var sharedViewModel: SharedViewModel) : ViewModel() {
    private val apiCall = APICall(dataStoreManager)
    var popupDetails = PopupItems().copy()
    var popupMessage = MessageItems()
    var closePopupMessage = mutableStateOf(false)
        private set
    var closeTestScreen = mutableStateOf(false)
        private set
    var openPopupMessage = mutableStateOf(false)
        private set
    var noteField by mutableStateOf("")
        private set
    var itemsList = mutableStateListOf<APICallTables.ItemDescriptor>()
        private set
    var itemListSearched = mutableStateListOf<APICallTables.ItemDescriptor>()
        private set

    /**
     * Resets the popup message visibility state.
     */
    fun closePopupMessage() {
        closePopupMessage.value = false
    }

    fun closeTestScreen(){
        closeTestScreen.value = false
    }

    /**
     * Closes the gel test screen and triggers navigation back.
     */


    /**
     * Resets the popup trigger state.
     */
    fun openPopupMessage() {
        openPopupMessage.value = false
    }

    /**
     * Toggles the catalyst dropdown visibility.
     */
    fun retrieveItems() {
        viewModelScope.launch {
            val call = "SELECT ITEMCODE AS CODE, ITEMDESCRIPTION AS DESCRIPTION, ITEMUNIT AS UNIT, ITEMSTATUS AS STATUS, ITEMBARCODE AS BARCODE, ITEMCATEGORY AS CATEGORY, " +
                    "ONHANDQTY, SUPPLYQTY, DEMANDQTY, AVAILABLEQTY, FREEQTY, 'Item Code' AS TYPE, SYSUNIQUEID FROM ITEMMASTER where ITEMSTATUS <> 'Obsolete' " +
                    "UNION ALL " +
                    "SELECT DESCRIPTORCODE AS CODE, DESCRIPTORDESCRIPTION AS DESCRIPTION, DESCRIPTORUNIT AS UNIT, DESCRIPTORSTATUS AS STATUS, DESCRIPTORBARCODE AS BARCODE, DESCRIPTORCATEGORY AS CATEGORY, " +
                    "NULL AS ONHANDQTY, NULL AS SUPPLYQTY, NULL AS DEMANDQTY, NULL AS AVAILABLEQTY, NULL AS FREEQTY, 'Descriptor Code' AS TYPE, SYSUNIQUEID FROM DESCRIPTORMASTER where DESCRIPTORSTATUS <> 'Obsolete'"

            val itemscall: List<APICallTables.ItemDescriptor>? = apiCall.query(call)

            itemsList.clear()
            itemListSearched.clear()
            itemscall?.let {
                itemsList.addAll(it)
                itemListSearched.addAll(it)
            }
        }
    }

    fun onNoteField(newValue: String) {
        noteField = newValue
    }

    /**
     * Saves the current Gel Time test to the database.
     *
     * This function:
     * - Converts hour/minute/second into HMMSS format
     * - Builds SQL INSERT or UPDATE query depending on save type
     * - Sends query to backend API
     * - Handles success or failure response
     *
     * On success:
     * - Closes test screen
     * - Shows success snackbar message
     *
     * On failure:
     * - Shows error snackbar message
     */
    fun onSave(notetype: NoteTypes) {
        viewModelScope.launch {
            val safeNote = noteField.replace("'","''")
            var call = ""
            var ID = ""
            when (notetype) {
                NoteTypes.ASSEMBLY -> ID = sharedViewModel.currentOrderNumber.value
                NoteTypes.SALES -> ID = ""
                NoteTypes.PURCHASE -> ID = ""
                NoteTypes.ITEM -> ID = ""
            }
            if(sharedViewModel.saveType == APICallTypes.INSERT){
                call = "INSERT INTO OSTDEF_NOTES " +
                        "(TYPE, IDNUMBER, NOTE, SYSUSERCREATED, SYSUSERMODIFIED) " +
                        "VALUES('${notetype.toStringName()}', '${ID}', '${safeNote}', " +
                        "'${sharedViewModel.currentUser.value}', '${sharedViewModel.currentUser.value}')"
            }
            if(sharedViewModel.saveType == APICallTypes.UPDATE){
                call = "UPDATE OSTDEF_NOTES SET " +
                        "NOTE = '${safeNote}'," +
                        "SYSUSERMODIFIED = '${sharedViewModel.currentUser.value}'" +
                        "WHERE IDNUMBER = '${ID}' and TYPE = '${notetype.toStringName()}'"
            }

            val response = apiCall.insertUpdateDelete(call)
            if (response == "200 OK") {
                    closeTestScreen.value = true
                    sharedViewModel.snackBarMessage("Note Saved successfully")
            } else {
                sharedViewModel.snackBarMessage("Error Saving, Please Try Again")
            }
        }
    }

    /**
     * Loads an existing gel time test into the UI or initializes a new one.
     *
     * If a test is provided:
     * - Maps database values into UI state
     * - Converts GELTIME into hour/minute/second format
     *
     * If no test is provided:
     * - Initializes default values from assembly header
     * - Retrieves next test number from database
     */
    fun onClear(notetype: NoteTypes) {
        var ID = ""
        viewModelScope.launch {
            when (notetype) {
                NoteTypes.ASSEMBLY -> ID = sharedViewModel.currentOrderNumber.value
                NoteTypes.SALES -> ID = ""
                NoteTypes.PURCHASE -> ID = ""
                NoteTypes.ITEM -> ID = ""
            }
            val notesline : List<APICallTables.notes>? = apiCall.query("SELECT * FROM OSTDEF_NOTES where IDNUMBER = '${ID}' and TYPE = '${notetype.toStringName()}'")
            noteField = notesline?.firstOrNull()?.NOTE.toString()
            if (noteField == "null"){
                sharedViewModel.updateSaveType(APICallTypes.INSERT)
                noteField = ""
            } else{
                sharedViewModel.updateSaveType(APICallTypes.UPDATE)
            }
        }
    }


    /**
     * Handles user cancellation of the gel test screen.
     *
     * If the test contains data:
     * - Shows confirmation popup before leaving without saving
     *
     * If no data exists:
     * - Immediately closes the test screen
     */
    fun onCancel() {
        if (noteField != "") {
            popupMessage.message = "Notes are not saved\nleave without saving?"
            popupMessage.messageButton1Text = "No"
            popupMessage.onClickAction1 = {
                closePopupMessage.value = true
            }
            popupMessage.messageButton2Text = "Yes"
            popupMessage.onClickAction2 = {
                closePopupMessage.value = true
                closeTestScreen.value = true
            }
            openPopupMessage.value = true

        } else {
            closeTestScreen.value = true
        }
    }
}