package com.example.coretechv2.ui.screen.assemblytestsandadjustments

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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import com.example.coretechv2.dataclasses.assemblydataclasses.GelField
import com.example.coretechv2.factory.assemblytests.GelTimeViewModelFactory
import com.example.coretechv2.ui.component.OutlinedStyleButton
import com.example.coretechv2.ui.component.OutlinedStyleDoubleNumberField
import com.example.coretechv2.ui.component.OutlinedStyleIntNumberField
import com.example.coretechv2.viewmodel.SharedViewModel
import com.example.coretechv2.viewmodel.assemblytests.GelTimeViewModel

/**
 * Displays the gel time test screen.
 *
 * The screen allows the user to enter or select the catalyst type,
 * catalyst percentage, test number, and the measured gel time in
 * hours, minutes, and seconds. The entered values can be cleared,
 * cancelled, or saved through the corresponding actions.
 *
 * When an existing test is supplied, its values are loaded into the
 * screen for editing.
 *
 * @param sharedViewModel Shared view model used to manage application-wide
 * state and popup messages.
 * @param test Optional existing gel time test to load into the screen.
 * If null, the screen is prepared for a new test.
 */
@Composable
fun GelTimeScreen(sharedViewModel: SharedViewModel, test: APICallTables.gelTimeTest? = null) {
    val context = LocalContext.current
    val viewModel: GelTimeViewModel = viewModel(
        factory = GelTimeViewModelFactory(context, sharedViewModel),
    )
    LaunchedEffect(Unit) {
        viewModel.onClear(test)
    }
    val focusManager = LocalFocusManager.current

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
                text = "Gel Time",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
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
                Text(text = "Catalyst")
                OutlinedStyleButton(
                    text = viewModel.gelReading.value.catalyst,
                    onClick = { viewModel.catalystPressed() })
                DropdownMenu(
                    expanded = viewModel.showcatalystList,
                    onDismissRequest = { viewModel.catalystPressed() }
                ) {
                    DropdownMenuItem(
                        onClick = { viewModel.onGelChange("BPO", GelField.CATALYST) },
                        text = { Text(text = "BPO") })
                    DropdownMenuItem(
                        onClick = { viewModel.onGelChange("MEKP", GelField.CATALYST) },
                        text = { Text(text = "MEKP") })
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Catalyst %")
                OutlinedStyleDoubleNumberField(
                    value = viewModel.gelReading.value.catPercent,
                    onValueChange = { newValue -> viewModel.onGelChange(newValue, GelField.CATPERCENT) }
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Test Number")
                OutlinedStyleDoubleNumberField(
                    value = viewModel.gelReading.value.testNumber,
                    onValueChange = { newValue -> viewModel.onGelChange(newValue, GelField.TESTNUMBER) }
                )
            }

        }
        Column(
            modifier = Modifier.weight(4f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(text = "Hour")
                    OutlinedStyleIntNumberField(
                        value = viewModel.gelReading.value.hour,
                        onValueChange = { newValue -> viewModel.onGelChange(newValue, GelField.HOUR) }
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(text = "Minute")
                    OutlinedStyleIntNumberField(
                        value = viewModel.gelReading.value.minute,
                        onValueChange = { newValue -> viewModel.onGelChange(newValue, GelField.MINUTE) }
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(text = "Second")
                    OutlinedStyleIntNumberField(
                        value = viewModel.gelReading.value.second,
                        onValueChange = { newValue -> viewModel.onGelChange(newValue, GelField.SECOND) }
                    )
                }
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
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                onClick = { viewModel.onClear(test) }
            ) { Text(text = "Clear") }
            Button(
                modifier = Modifier
                    .weight(2f)
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                onClick = { viewModel.onCancel(test) }
            ) { Text(text = "Cancel") }
            Button(
                modifier = Modifier
                    .weight(2f)
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                onClick = { viewModel.onSave() }
            ) { Text(text = "Save") }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}