package com.example.coretechv2.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coretechv2.factory.ItemLookUpViewModelFactory
import com.example.coretechv2.viewmodel.ItemLookUpViewModel
import com.example.coretechv2.viewmodel.SharedViewModel

/**
 * Displays the item lookup screen.
 *
 * Creates and manages an [ItemLookUpViewModel] using the current application
 * context and shared view model. Items are retrieved when the screen is first
 * displayed and are presented in a searchable list.
 *
 * Selecting an item delegates the selection to the view model. The screen also
 * handles communication between the item lookup view model and the shared view
 * model for displaying and closing popup messages and test screens.
 *
 * @param sharedViewModel Shared view model used to manage application-wide
 * state and popup messages.
 */
@Composable
fun ItemLookUpScreen(
    sharedViewModel: SharedViewModel
) {
    val context = LocalContext.current
    val viewModel: ItemLookUpViewModel = viewModel(
        factory = ItemLookUpViewModelFactory(context, sharedViewModel)
    )

    LaunchedEffect(Unit) {
        viewModel.retrieveItems()
    }

    if (viewModel.closePopupMessage.value) {
        sharedViewModel.closeMessagePopup()
        viewModel.closePopupMessage()
    }
    if (viewModel.closeTestScreen.value) {
        sharedViewModel.closePopup()
        viewModel.closeTestScreen()
    }
    if (viewModel.openPopupMessage.value) {
        sharedViewModel.popupMessageDetails = viewModel.popupMessage
        sharedViewModel.openMessagePopup()
        viewModel.openPopupMessage()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    )
    {
        OutlinedTextField(
            value = viewModel.Searchfield,
            onValueChange = viewModel::onSearchFieldChange,
            label = { Text("Search") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(viewModel.itemListSearched) { item ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.getSelectedItem(item)
                        }
                        .padding(16.dp)) {
                    Text(
                        text = item.DESCRIPTION,
                    )
                    Text(
                        text = "Order#: ${item.CODE}    Type: ${item.TYPE}    Available Qty: ${item.AVAILABLEQTY}    Required Qty: ${item.DEMANDQTY}",
                    )
                }
                HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
            }
        }
    }
}
