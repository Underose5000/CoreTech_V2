package com.example.coretechv2.ui.screen.assemblytests

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
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
import com.example.coretechv2.dataclasses.VisField
import com.example.coretechv2.dataclasses.VisSettings
import com.example.coretechv2.factory.assemblytests.ViscosityViewModelFactory
import com.example.coretechv2.ui.component.OutlinedStyleButton
import com.example.coretechv2.ui.component.OutlinedStyleDoubleNumberField
import com.example.coretechv2.viewmodel.SharedViewModel
import com.example.coretechv2.viewmodel.assemblytests.ViscosityViewModel

/**
 * Viscosity test screen
 *
 * A popup screen that allows users to either enter viscosity test results to the database or
 * edit previously entered viscosity test results.
 *
 * @param viewModel the Shared viewModel to which holds app wide data
 */
@Composable
fun ViscosityScreen(sharedViewModel: SharedViewModel) {
    val context = LocalContext.current
    val viewModel: ViscosityViewModel = viewModel(
        factory = ViscosityViewModelFactory(context, sharedViewModel),
    )
    LaunchedEffect(Unit){
        viewModel.onClear()
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
                    text = "Viscosity",
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
                    Text(text = "Spindle")
                    OutlinedStyleButton(
                        text = viewModel.visReading.value.spindle,
                        onClick = { viewModel.spindlePressed() })
                    DropdownMenu(
                        expanded = viewModel.showSpindleList,
                        onDismissRequest = { viewModel.spindlePressed() }
                    ) {
                        DropdownMenuItem(
                            onClick = { viewModel.onDropDownChange("1", VisSettings.SPINDLE) },
                            text = { Text(text = "1") })
                        DropdownMenuItem(
                            onClick = { viewModel.onDropDownChange("2", VisSettings.SPINDLE) },
                            text = { Text(text = "2") })
                        DropdownMenuItem(
                            onClick = { viewModel.onDropDownChange("3", VisSettings.SPINDLE) },
                            text = { Text(text = "3") })
                        DropdownMenuItem(
                            onClick = { viewModel.onDropDownChange("4", VisSettings.SPINDLE) },
                            text = { Text(text = "4") })
                        DropdownMenuItem(
                            onClick = { viewModel.onDropDownChange("A", VisSettings.SPINDLE) },
                            text = { Text(text = "A") })
                        DropdownMenuItem(
                            onClick = { viewModel.onDropDownChange("B", VisSettings.SPINDLE) },
                            text = { Text(text = "B") })
                        DropdownMenuItem(
                            onClick = { viewModel.onDropDownChange("C", VisSettings.SPINDLE) },
                            text = { Text(text = "C") })
                        DropdownMenuItem(
                            onClick = { viewModel.onDropDownChange("D", VisSettings.SPINDLE) },
                            text = { Text(text = "D") })
                        DropdownMenuItem(
                            onClick = { viewModel.onDropDownChange("E", VisSettings.SPINDLE) },
                            text = { Text(text = "E") })
                        DropdownMenuItem(
                            onClick = { viewModel.onDropDownChange("F", VisSettings.SPINDLE) },
                            text = { Text(text = "F") })
                        DropdownMenuItem(
                            onClick = { viewModel.onDropDownChange("G", VisSettings.SPINDLE) },
                            text = { Text(text = "G") })
                    }
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Index Range")
                    OutlinedStyleButton(text = viewModel.visReading.value.indexRange, onClick = { viewModel.indexPressed() })
                    DropdownMenu(
                        expanded = viewModel.showindexList,
                        onDismissRequest = { viewModel.indexPressed() }
                    ) {
                        DropdownMenuItem(
                            onClick = { viewModel.onDropDownChange("6/60", VisSettings.INDEX) },
                            text = { Text(text = "6/60") })
                        DropdownMenuItem(
                            onClick = { viewModel.onDropDownChange("3/30", VisSettings.INDEX) },
                            text = { Text(text = "3/30") })
                        DropdownMenuItem(
                            onClick = { viewModel.onDropDownChange("0.6/6", VisSettings.INDEX) },
                            text = { Text(text = "0.6/6") })
                        DropdownMenuItem(
                            onClick = { viewModel.onDropDownChange("0.3/3", VisSettings.INDEX) },
                            text = { Text(text = "0.3/3") })
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Test Number")
                    OutlinedStyleDoubleNumberField(
                        value = viewModel.visReading.value.testNumber,
                        onValueChange = { newValue -> viewModel.onDropDownChange(newValue, VisSettings.TESTNUMBER) }
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
                        Text(text = "60")
                        OutlinedStyleDoubleNumberField(
                            value = viewModel.visReading.value.vis60,
                            onValueChange = { newValue -> viewModel.onVisChange(newValue, VisField.VIS60) }
                        )
                    }
                   Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text(text = "30")
                       OutlinedStyleDoubleNumberField(
                            value = viewModel.visReading.value.vis30,
                            onValueChange = { newValue -> viewModel.onVisChange(newValue, VisField.VIS30) }
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text(text = "12")
                        OutlinedStyleDoubleNumberField(
                            value = viewModel.visReading.value.vis12,
                            onValueChange = { newValue -> viewModel.onVisChange(newValue, VisField.VIS12) }
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text(text = "6")
                        OutlinedStyleDoubleNumberField(
                            value = viewModel.visReading.value.vis06,
                            onValueChange = { newValue -> viewModel.onVisChange(newValue, VisField.VIS06) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text(text = "3")
                        OutlinedStyleDoubleNumberField(
                            value = viewModel.visReading.value.vis03,
                            onValueChange = { newValue -> viewModel.onVisChange(newValue, VisField.VIS03) }
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text(text = "1.5")
                        OutlinedStyleDoubleNumberField(
                            value = viewModel.visReading.value.vis1_5,
                            onValueChange = { newValue -> viewModel.onVisChange(newValue, VisField.VIS1_5) }
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text(text = "0.6")
                        OutlinedStyleDoubleNumberField(
                            value = viewModel.visReading.value.vis0_6,
                            onValueChange = { newValue -> viewModel.onVisChange(newValue, VisField.VIS0_6) }
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text(text = "0.3")
                        OutlinedStyleDoubleNumberField(
                            value = viewModel.visReading.value.vis0_3,
                            onValueChange = { newValue -> viewModel.onVisChange(newValue, VisField.VIS0_3) }
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
                        .padding(horizontal = 20.dp, vertical = 40.dp),
                    onClick = { viewModel.onClear() }
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
                    onClick = { viewModel.onTestSave() }
                ) { Text(text = "Save") }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }