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

/**
 * ViewModel responsible for managing note creation, editing, and lookup.
 *
 * [NotesViewModel] manages the note entry field, retrieves available items and
 * descriptors, loads existing notes, saves notes to the database, and handles
 * cancellation of the note entry screen.
 *
 * The ViewModel uses [SharedViewModel] to access shared application state such
 * as the current order number, current user, and save type.
 *
 * @param dataStoreManager Provides access to application settings and API
 * configuration required by [APICall].
 * @param sharedViewModel Shared ViewModel used to access and update application
 * state shared between screens.
 */
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
     * Resets the popup message close state.
     *
     * Sets [closePopupMessage] to false after the UI has processed the
     * popup close event.
     */
    fun closePopupMessage() {
        closePopupMessage.value = false
    }

    /**
     * Resets the note entry screen close state.
     *
     * Sets [closeTestScreen] to false after the UI has processed the
     * screen close event.
     */
    fun closeTestScreen() {
        closeTestScreen.value = false
    }

    /**
     * Resets the popup message open state.
     *
     * Sets [openPopupMessage] to false after the UI has processed the
     * popup open event.
     */
    fun openPopupMessage() {
        openPopupMessage.value = false
    }

    /**
     * Retrieves all non-obsolete items and descriptors from the database.
     *
     * Item and descriptor records are combined into a single result set.
     * The retrieved records are stored in both [itemsList] and
     * [itemListSearched].
     *
     * The database query is executed asynchronously using [viewModelScope].
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

    /**
     * Updates the note field with the supplied text.
     *
     * @param newValue The new text entered into the note field.
     */
    fun onNoteField(newValue: String) {
        noteField = newValue
    }

    /**
     * Saves the current note to the database.
     *
     * The note is either inserted or updated depending on the current
     * [APICallTypes] stored in [SharedViewModel.saveType].
     *
     * Before being included in the SQL query, apostrophes in the note text
     * are escaped to prevent them from prematurely terminating the SQL string.
     *
     * The note's associated ID is determined by [NoteTypes]. Assembly notes
     * use the current order number, while the other note types currently use
     * an empty ID.
     *
     * On successful completion, the note entry screen is closed and a
     * success snackbar message is displayed. If the database operation fails,
     * an error snackbar message is displayed instead.
     *
     * @param notetype The type of note being saved.
     */
    fun onSave(notetype: NoteTypes) {
        viewModelScope.launch {
            val safeNote = noteField.replace("'", "''")
            var call = ""
            var ID = ""
            when (notetype) {
                NoteTypes.ASSEMBLY -> ID = sharedViewModel.currentOrderNumber.value
                NoteTypes.SALES -> ID = ""
                NoteTypes.PURCHASE -> ID = ""
                NoteTypes.ITEM -> ID = ""
            }
            if (sharedViewModel.saveType == APICallTypes.INSERT) {
                call = "INSERT INTO OSTDEF_NOTES " +
                        "(TYPE, IDNUMBER, NOTE, SYSUSERCREATED, SYSUSERMODIFIED) " +
                        "VALUES('${notetype.toStringName()}', '${ID}', '${safeNote}', " +
                        "'${sharedViewModel.currentUser.value}', '${sharedViewModel.currentUser.value}')"
            }
            if (sharedViewModel.saveType == APICallTypes.UPDATE) {
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
     * Loads an existing note for the specified note type.
     *
     * The associated ID is determined from [NoteTypes]. For assembly notes,
     * the current order number from [SharedViewModel.currentOrderNumber] is
     * used.
     *
     * If an existing note is found, its contents are loaded into [noteField]
     * and the save type is set to [APICallTypes.UPDATE]. If no existing note
     * is found, the note field is cleared and the save type is set to
     * [APICallTypes.INSERT].
     *
     * @param notetype The type of note to load.
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
            val notesline: List<APICallTables.notes>? = apiCall.query("SELECT * FROM OSTDEF_NOTES where IDNUMBER = '${ID}' and TYPE = '${notetype.toStringName()}'")
            noteField = notesline?.firstOrNull()?.NOTE.toString()
            if (noteField == "null") {
                sharedViewModel.updateSaveType(APICallTypes.INSERT)
                noteField = ""
            } else {
                sharedViewModel.updateSaveType(APICallTypes.UPDATE)
            }
        }
    }


    /**
     * Handles cancellation of the note entry screen.
     *
     * If [noteField] contains text, a confirmation popup is displayed asking
     * the user whether they want to leave without saving.
     *
     * Selecting "No" closes the confirmation popup without leaving the screen.
     * Selecting "Yes" closes both the popup and the note entry screen.
     *
     * If the note field is empty, the note entry screen is closed immediately
     * without displaying a confirmation popup.
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