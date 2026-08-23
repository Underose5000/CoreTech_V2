package com.example.coretechv2.ui.screen.assemblytestsandadjustments

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coretechv2.dataclasses.APICallTables
import com.example.coretechv2.dataclasses.APICallTypes
import com.example.coretechv2.dataclasses.StepNames
import com.example.coretechv2.dataclasses.assemblydataclasses.VisSettings
import com.example.coretechv2.factory.assemblytests.AddLineViewModelFactory
import com.example.coretechv2.factory.assemblytests.AdjustmentsViewModelFactory
import com.example.coretechv2.ui.component.OutlinedStyleButton
import com.example.coretechv2.ui.component.OutlinedStyleDoubleNumberField
import com.example.coretechv2.ui.component.OutlinedStyleTextAndButtonField
import com.example.coretechv2.ui.component.PopupWindow
import com.example.coretechv2.ui.component.SearchResultBox
import com.example.coretechv2.viewmodel.SharedViewModel
import com.example.coretechv2.viewmodel.assemblytests.AddLineViewModel
import com.example.coretechv2.viewmodel.assemblytests.AdjustmentsViewModel

@Composable
fun AddLineScreen(sharedViewModel: SharedViewModel, stepNames: SnapshotStateList<Any>) {
    val context = LocalContext.current
    val viewModel: AddLineViewModel = viewModel(
        factory = AddLineViewModelFactory(context, sharedViewModel),
    )
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit){
        Log.d("add Line", "LaunchedEffect(Unit) Search Field = ${viewModel.Searchfield}")
        viewModel.retrieveItems()
        viewModel.onStepNamesLoad(stepNames)
    }

    LaunchedEffect(sharedViewModel.currentItem.value){
        if (sharedViewModel.currentItem.value.code.isNotEmpty()) {
            viewModel.closeSearchBoxs()
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
    if(viewModel.closeTestScreen.value){
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
                text = "Add Line",
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
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically) {
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
            Box(){
                if (viewModel.showSearchBox) {
                SearchResultBox(viewModel.itemListSearched.toList(), sharedViewModel)
            }}

        }
        Row(
            modifier = Modifier.weight(2f).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Column(
                modifier = Modifier
                    .weight(5f)
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Step Name")
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedStyleButton(
                        modifier = Modifier.weight(1f),
                        text = viewModel.stepName.value,
                        onClick = { viewModel.stepPressed() })
                    DropdownMenu(
                        expanded = viewModel.showStepList,
                        onDismissRequest = { viewModel.stepPressed() }
                    ) {
                        for (i in 0 until viewModel.stepNameList.size) {
                            DropdownMenuItem(
                                onClick = { viewModel.onDropDownChange(viewModel.stepNameList[i]) },
                                text = { Text(text = viewModel.stepNameList[i].toString()) })
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Column(
                modifier = Modifier
                    .weight(5f)
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Qty")
                Row(modifier = Modifier.fillMaxWidth()){
                OutlinedStyleDoubleNumberField(
                    modifier = Modifier.weight(1f),
                    value = viewModel.lineQty.value,
                    onValueChange = { newValue -> viewModel.onAddLineQty(newValue) }
                )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
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
                onClick = { viewModel.onSave() }
            ) { Text(text = "Save") }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
    if (viewModel.openItemList.value) {
        PopupWindow(viewModel.popupDetails)
    }
}