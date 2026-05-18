package com.example.coretechv2.ui.screen.assemblytestsandadjustments

import android.util.Log
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
import com.example.coretechv2.factory.assemblytests.AdjustmentsViewModelFactory
import com.example.coretechv2.ui.component.OutlinedStyleDoubleNumberField
import com.example.coretechv2.ui.component.OutlinedStyleTextAndButtonField
import com.example.coretechv2.ui.component.PopupWindow
import com.example.coretechv2.ui.component.SearchResultBox
import com.example.coretechv2.viewmodel.SharedViewModel
import com.example.coretechv2.viewmodel.assemblytests.AdjustmentsViewModel

@Composable
fun AdjustmentsScreen(sharedViewModel: SharedViewModel) {
    val context = LocalContext.current
    val viewModel: AdjustmentsViewModel = viewModel(
        factory = AdjustmentsViewModelFactory(context, sharedViewModel),
    )
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit){
        viewModel.retrieveItems()
    }
    LaunchedEffect(sharedViewModel.currentItem.value){
        Log.d("Launched Effect", "CurrentItem")
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
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically) {
                OutlinedStyleTextAndButtonField(
                    value = viewModel.Searchfield,
                    onValueChange = viewModel::onSearchFieldChange,
                    icon = Icons.Filled.Search,
                    onClick = { viewModel.openItemList() },
                    modifier = Modifier.weight(6f),
                )
            }
            Box(){
                if (viewModel.showSearchBox) {
                SearchResultBox(viewModel.itemListSearched.toList(), sharedViewModel)
            }}

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