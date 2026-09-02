package com.example.coretechv2.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coretechv2.dataclasses.NoteTypes
import com.example.coretechv2.factory.NotesViewModelFactory
import com.example.coretechv2.ui.component.OutlinedStyleTextField
import com.example.coretechv2.viewmodel.NotesViewModel
import com.example.coretechv2.viewmodel.SharedViewModel

/**
 * Displays the notes screen for a specified note type.
 *
 * Creates and manages a [NotesViewModel] using the provided [SharedViewModel].
 * When the screen is first displayed, existing items are retrieved and the
 * note field is cleared for the selected note type.
 *
 * The screen provides a text field for entering a note and buttons for either
 * cancelling or saving the note. Tapping outside the text field clears the
 * current input focus.
 *
 * Popup and test screen state is synchronised between the [NotesViewModel]
 * and [SharedViewModel] to allow messages and other application-level
 * popups to be displayed and dismissed.
 *
 * @param sharedViewModel Shared view model used to manage application-wide
 * state and popup messages.
 * @param notetype Type of note being created or edited.
 */
@Composable
fun NotesScreen(sharedViewModel: SharedViewModel, notetype: NoteTypes) {
    val context = LocalContext.current
    val viewModel: NotesViewModel = viewModel(
        factory = NotesViewModelFactory(context, sharedViewModel),
    )
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.retrieveItems()
        viewModel.onClear(notetype)

    }

    if (viewModel.closePopupMessage.value) {
        sharedViewModel.closeMessagePopup()
        viewModel.closePopupMessage()
    }
    if (viewModel.openPopupMessage.value) {
        sharedViewModel.popupMessageDetails = viewModel.popupMessage
        sharedViewModel.openMessagePopup()
        viewModel.openPopupMessage()
    }
    if (viewModel.closeTestScreen.value) {
        sharedViewModel.closePopup()
        viewModel.closeTestScreen()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
            }) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${notetype.toStringName()} Notes",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedStyleTextField(
                value = viewModel.noteField,
                onValueChange = { newValue -> viewModel.onNoteField(newValue) })

        }

        Row(
            modifier = Modifier.weight(2f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Button(
                modifier = Modifier
                    .weight(2f)
                    .padding(horizontal = 20.dp, vertical = 40.dp),
                onClick = { viewModel.onCancel() }
            ) { Text(text = "Cancel") }
            Button(
                modifier = Modifier
                    .weight(2f)
                    .padding(horizontal = 20.dp, vertical = 40.dp),
                onClick = { viewModel.onSave(notetype) }
            ) { Text(text = "Save") }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}