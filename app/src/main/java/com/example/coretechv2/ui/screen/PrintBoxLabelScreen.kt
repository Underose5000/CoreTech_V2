package com.example.coretechv2.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.coretechv2.dataclasses.ItemDescriptorItem
import com.example.coretechv2.factory.PrintBoxLabelViewModelFactory
import com.example.coretechv2.ui.component.LabelPreview
import com.example.coretechv2.ui.component.OutlinedStyleButton
import com.example.coretechv2.ui.component.OutlinedStyleIntNumberField
import com.example.coretechv2.ui.component.OutlinedStyleTextAndButtonField
import com.example.coretechv2.ui.component.OutlinedStyleTextLine
import com.example.coretechv2.ui.component.PopupWindow
import com.example.coretechv2.ui.component.SearchResultBox
import com.example.coretechv2.ui.theme.borderColor
import com.example.coretechv2.viewmodel.PrintBoxLabelViewModel
import com.example.coretechv2.viewmodel.SharedViewModel

/**
 * Displays the box label printing screen.
 *
 * Creates and manages a [PrintBoxLabelViewModel] responsible for retrieving
 * items, selecting an item and label variant, entering box information, and
 * preparing the data required for printing box labels.
 *
 * When the screen is first displayed, the current order number and selected
 * item are reset and the available items are retrieved. Selecting an item
 * triggers the corresponding box label data to be loaded and closes the item
 * search results.
 *
 * The screen allows the user to select a label variant when multiple variants
 * are available, enter a batch number, specify whether the box is a kit, enter
 * the quantity per box, and specify the number of boxes to be produced.
 *
 * Popup messages and label preview state are coordinated between the
 * [PrintBoxLabelViewModel] and [SharedViewModel]. The screen returns to the
 * previous navigation destination when the label preview is closed and the
 * view model indicates that the screen should be closed.
 *
 * @param navController Navigation controller used to return to the previous
 * screen after the label printing workflow is completed or cancelled.
 * @param sharedViewModel Shared view model used to manage application-wide
 * state, including the currently selected item, order number, popup messages,
 * and label preview state.
 */
@Composable
fun PrintBoxLabelsScreen(navController: NavController, sharedViewModel: SharedViewModel) {
    val context = LocalContext.current
    val viewModel: PrintBoxLabelViewModel = viewModel(
        factory = PrintBoxLabelViewModelFactory(context, sharedViewModel),
    )
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.retrieveItems()
        sharedViewModel.currentOrderNumber.value = ""
        viewModel.onSearchFieldChange("")
        sharedViewModel.currentItem.value = ItemDescriptorItem()
    }
    LaunchedEffect(sharedViewModel.currentItem.value) {
        if (sharedViewModel.currentItem.value.code != "") {
            viewModel.onBoxLabelData()
            viewModel.closeSearchBoxs()
        }
    }
    LaunchedEffect(sharedViewModel.showLabelPreview.value) {
        if (!sharedViewModel.showLabelPreview.value && viewModel.closeTestScreen.value) {
            navController.popBackStack()
        }
    }
    LaunchedEffect(viewModel.closeTestScreen.value) {
        if (!sharedViewModel.showLabelPreview.value && viewModel.closeTestScreen.value) {
            navController.popBackStack()
        }
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
    /* if (viewModel.closeTestScreen.value) {
         navController.popBackStack()
         viewModel.closeTestScreen()
     }*/

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .width(700.dp)
                .height(550.dp)
                .border(1.dp, borderColor)
                .background(Color.White)
                .align(Alignment.Center)
        ) {
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
                        text = "Print Box Labels",
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
                        OutlinedStyleTextAndButtonField(
                            value = viewModel.Searchfield,
                            onValueChange = viewModel::onSearchFieldChange,
                            icon = Icons.Filled.Search,
                            onClick = { viewModel.openItemList() },
                            modifier = Modifier.weight(6f)
                        )
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
                    if (viewModel.boxLabelDataList.size > 1) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 20.dp, vertical = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "Label Variant")
                            Row(modifier = Modifier.fillMaxWidth()) {
                                OutlinedStyleButton(
                                    text = viewModel.variantName.value,
                                    onClick = { viewModel.variantPressed() },
                                    modifier = Modifier
                                        .weight(1f)
                                )
                                DropdownMenu(
                                    expanded = viewModel.showIndexList,
                                    onDismissRequest = { viewModel.variantPressed() }
                                ) {
                                    for (x in 0 until viewModel.boxLabelDataList.size) {
                                        DropdownMenuItem(
                                            onClick = { viewModel.onVariantChange(x) },
                                            text = {
                                                Text(
                                                    text = viewModel.boxLabelDataList[x].itemInfo.MIDDLENAME.ifEmpty {
                                                        "${viewModel.boxLabelDataList[x].itemInfo.TOPNAME} ${viewModel.boxLabelDataList[x].itemInfo.BOTTOMNAME}"
                                                    }
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 20.dp, vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Batch Number")
                        OutlinedStyleTextLine(
                            value = viewModel.batchNumber.value,
                            onValueChange = { newValue -> viewModel.onBatchNumber(newValue) }
                        )
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
                        Text(text = "Kit")
                        Checkbox(
                            checked = viewModel.kitSet.value,
                            onCheckedChange = { newValue -> viewModel.onKitSet(newValue) }
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 20.dp, vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Box Qty")
                        OutlinedStyleIntNumberField(
                            value = viewModel.boxQty.value,
                            onValueChange = { newValue -> viewModel.onBoxQty(newValue) }
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 20.dp, vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Number of Boxes")
                        OutlinedStyleIntNumberField(
                            value = viewModel.numberOfBoxes.value,
                            onValueChange = { newValue -> viewModel.onNumberOfBoxes(newValue) }
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
                        onClick = { viewModel.onCancel() }
                    ) { Text(text = "Cancel") }
                    Button(
                        modifier = Modifier
                            .weight(2f)
                            .padding(horizontal = 20.dp, vertical = 40.dp),
                        onClick = { viewModel.onOk() }
                    ) { Text(text = "Ok") }
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            if (viewModel.openItemList.value) {
                PopupWindow(viewModel.popupDetails)
            }
        }
        if (viewModel.showLabel && !viewModel.boxLabelDataList.isEmpty()) {
            LabelPreview(viewModel.boxLabelDataList[viewModel.labelIndex.value], viewModel.numberOfBoxes.value.toInt())
        }
    }
}