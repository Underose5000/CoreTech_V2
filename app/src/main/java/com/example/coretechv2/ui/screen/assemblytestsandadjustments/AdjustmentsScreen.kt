package com.example.coretechv2.ui.screen.assemblytestsandadjustments

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import com.example.coretechv2.dataclasses.APICallTables
import com.example.coretechv2.dataclasses.APICallTypes
import com.example.coretechv2.factory.assemblytests.AdjustmentsViewModelFactory
import com.example.coretechv2.ui.component.OutlinedStyleDoubleNumberField
import com.example.coretechv2.ui.component.OutlinedStyleTextAndButtonField
import com.example.coretechv2.ui.component.PopupWindow
import com.example.coretechv2.ui.component.SearchResultBox
import com.example.coretechv2.viewmodel.SharedViewModel
import com.example.coretechv2.viewmodel.assemblytests.AdjustmentsViewModel

/**
 * Displays the adjustment entry screen for an assembly item.
 *
 * Creates and manages an [AdjustmentsViewModel] using the provided
 * [SharedViewModel]. When the screen is first displayed, available items are
 * retrieved and the adjustment form is initialised using the supplied
 * adjustment record, if one is provided.
 *
 * The screen allows the user to select an item and enter an adjustment number
 * and quantity. When an existing adjustment is being updated, the item
 * selection field is read-only; otherwise, the user can open the item
 * selection popup.
 *
 * The screen provides controls for clearing the current values, cancelling
 * the operation, and saving the adjustment. Popup messages and screen state
 * are synchronised between the [AdjustmentsViewModel] and [SharedViewModel].
 *
 * Tapping outside an input field clears the current focus.
 *
 * @param sharedViewModel Shared view model used to manage application-wide
 * state, including the current item, save type, and popup messages.
 * @param adjustment Existing adjustment record to edit, or `null` when creating
 * a new adjustment.
 */
@Composable
fun AdjustmentsScreen(sharedViewModel: SharedViewModel, adjustment: APICallTables.assemblyAdjustment? = null) {
    val context = LocalContext.current
    val viewModel: AdjustmentsViewModel = viewModel(
        factory = AdjustmentsViewModelFactory(context, sharedViewModel),
    )
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.retrieveItems()
        viewModel.onClear(adjustment)

    }
    LaunchedEffect(sharedViewModel.currentItem.value) {

        viewModel.closeSearchBoxs()
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
                text = "Adjustments",
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

            Text(text = "Item")
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (sharedViewModel.saveType == APICallTypes.UPDATE) {
                    OutlinedStyleTextAndButtonField(
                        value = viewModel.Searchfield,
                        onValueChange = viewModel::onSearchFieldChange,
                        icon = Icons.Filled.Search,
                        onClick = {},
                        modifier = Modifier.weight(6f),
                        readOnly = true
                    )
                } else {
                    OutlinedStyleTextAndButtonField(
                        value = viewModel.Searchfield,
                        onValueChange = viewModel::onSearchFieldChange,
                        icon = Icons.Filled.Search,
                        onClick = { viewModel.openItemList() },
                        modifier = Modifier.weight(6f),
                    )
                }
            }
            Box() {
                if (viewModel.showSearchBox) {
                    SearchResultBox(viewModel.itemListSearched.toList(), sharedViewModel)
                }
            }

        }
        Row(
            modifier = Modifier.weight(2f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Adjustment Number")
                OutlinedStyleDoubleNumberField(
                    value = viewModel.adjustmentRecord.value.adjustmentNumber,
                    onValueChange = { newValue -> viewModel.onAdjustmentNumber(newValue) }
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Qty")
                OutlinedStyleDoubleNumberField(
                    value = viewModel.adjustmentRecord.value.qty,
                    onValueChange = { newValue -> viewModel.onAdjustmentqty(newValue) }
                )
            }
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
                onClick = { viewModel.onClear(adjustment) }
            ) { Text(text = "Clear") }
            Button(
                modifier = Modifier
                    .weight(2f)
                    .padding(horizontal = 20.dp, vertical = 40.dp),
                onClick = { viewModel.onCancel(adjustment) }
            ) { Text(text = "Cancel") }
            Button(
                modifier = Modifier
                    .weight(2f)
                    .padding(horizontal = 20.dp, vertical = 40.dp),
                onClick = { viewModel.onSave(adjustment) }
            ) { Text(text = "Save") }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
    if (viewModel.openItemList.value) {
        PopupWindow(viewModel.popupDetails)
    }
}