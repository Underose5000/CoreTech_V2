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
import com.example.coretechv2.dataclasses.assemblydataclasses.PeakExothermField
import com.example.coretechv2.dataclasses.assemblydataclasses.PeakExothermItem
import com.example.coretechv2.factory.assemblytests.GelTimeViewModelFactory
import com.example.coretechv2.factory.assemblytests.PeakExothermViewModelFactory
import com.example.coretechv2.ui.component.OutlinedStyleButton
import com.example.coretechv2.ui.component.OutlinedStyleDoubleNumberField
import com.example.coretechv2.ui.component.OutlinedStyleIntNumberField
import com.example.coretechv2.viewmodel.SharedViewModel
import com.example.coretechv2.viewmodel.assemblytests.GelTimeViewModel
import com.example.coretechv2.viewmodel.assemblytests.PeakExothermViewModel

/**
 * Gel Time test screen
 *
 * A popup screen that allows users to either enter Gel Time test results to the database or
 * edit previously entered Gel Time test results.
 *
 * @param viewModel the Shared viewModel to which holds app wide data
 */
@Composable
fun PeakExothermScreen(sharedViewModel: SharedViewModel, test: APICallTables.peakExothermTest? = null) {
    val context = LocalContext.current
    val viewModel: PeakExothermViewModel = viewModel(
        factory = PeakExothermViewModelFactory(context, sharedViewModel),
    )
    LaunchedEffect(Unit){
        viewModel.onClear(test)
    }
    val focusManager = LocalFocusManager.current

    if(viewModel.closePopupMessage.value){
        sharedViewModel.closeMessagePopup()
        viewModel.closePopupMessage()
    }
    if(viewModel.closeTestScreen.value){
        sharedViewModel.closePopup()
        viewModel.closeTestScreen()
    }
    if(viewModel.openPopupMessage.value){
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
                        text = viewModel.peakReading.value.catalyst,
                        onClick = { viewModel.catalystPressed() })
                    DropdownMenu(
                        expanded = viewModel.showcatalystList,
                        onDismissRequest = { viewModel.catalystPressed() }
                    ) {
                        DropdownMenuItem(
                            onClick = { viewModel.onGelChange("BPO", PeakExothermField.CATALYST) },
                            text = { Text(text = "BPO") })
                        DropdownMenuItem(
                            onClick = { viewModel.onGelChange("MEKP", PeakExothermField.CATALYST) },
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
                        value = viewModel.peakReading.value.catPercent,
                        onValueChange = { newValue -> viewModel.onGelChange(newValue, PeakExothermField.CATPERCENT) }
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
                        value = viewModel.peakReading.value.testNumber,
                        onValueChange = { newValue -> viewModel.onGelChange(newValue, PeakExothermField.TESTNUMBER) }
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
                            value = viewModel.peakReading.value.hour,
                            onValueChange = { newValue -> viewModel.onGelChange(newValue, PeakExothermField.HOUR) }
                        )
                    }
                   Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                       Text(text = "Minute")
                       OutlinedStyleIntNumberField(
                            value = viewModel.peakReading.value.minute,
                            onValueChange = { newValue -> viewModel.onGelChange(newValue, PeakExothermField.MINUTE) }
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text(text = "Second")
                        OutlinedStyleIntNumberField(
                            value = viewModel.peakReading.value.second,
                            onValueChange = { newValue -> viewModel.onGelChange(newValue, PeakExothermField.SECOND) }
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text(text = "Temperature")
                        OutlinedStyleDoubleNumberField(
                            value = viewModel.peakReading.value.temperature,
                            onValueChange = { newValue -> viewModel.onGelChange(newValue, PeakExothermField.TEMPERATURE) }
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