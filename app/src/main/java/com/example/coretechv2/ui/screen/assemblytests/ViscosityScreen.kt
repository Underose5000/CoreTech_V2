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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coretechv2.dataclasses.VisField
import com.example.coretechv2.dataclasses.VisSettings
import com.example.coretechv2.factory.AssemblyOrderDetailsViewModelFactory
import com.example.coretechv2.ui.component.OutlinedStyleButton
import com.example.coretechv2.ui.component.OutlinedStyleNumberField
import com.example.coretechv2.ui.component.PopupWindow
import com.example.coretechv2.viewmodel.AssemblyOrderDetailsViewModel


@Composable
fun ViscosityScreen(
    viewModel: AssemblyOrderDetailsViewModel, currentUser: String, orderNumber: String
) {
    val focusManager = LocalFocusManager.current
    PopupWindow(width = 700, height = 500) {
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
                        /*DropdownMenuItem(
                            onClick = {viewModel.onDropDownChange("N/A",viewModel.spindle)},
                            text = { Text(text = "N/A") })*/
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
                    OutlinedStyleButton(text = viewModel.visReading.value.testNumber, onClick = { viewModel.testNumberPressed() })
                    DropdownMenu(
                        expanded = viewModel.showtestNumber,
                        onDismissRequest = { viewModel.testNumberPressed() }
                    ) {
                        DropdownMenuItem(
                            onClick = { viewModel.onDropDownChange("1", VisSettings.TESTNUMBER) },
                            text = { Text(text = "1") })
                        DropdownMenuItem(
                            onClick = { viewModel.onDropDownChange("2", VisSettings.TESTNUMBER) },
                            text = { Text(text = "2") })
                        DropdownMenuItem(
                            onClick = { viewModel.onDropDownChange("3", VisSettings.TESTNUMBER) },
                            text = { Text(text = "3") })
                        DropdownMenuItem(
                            onClick = { viewModel.onDropDownChange("4", VisSettings.TESTNUMBER) },
                            text = { Text(text = "4") })
                        DropdownMenuItem(
                            onClick = { viewModel.onDropDownChange("5", VisSettings.TESTNUMBER) },
                            text = { Text(text = "5") })
                        DropdownMenuItem(
                            onClick = { viewModel.onDropDownChange("6", VisSettings.TESTNUMBER) },
                            text = { Text(text = "6") })
                        DropdownMenuItem(
                            onClick = { viewModel.onDropDownChange("7", VisSettings.TESTNUMBER) },
                            text = { Text(text = "7") })
                        DropdownMenuItem(
                            onClick = { viewModel.onDropDownChange("8", VisSettings.TESTNUMBER) },
                            text = { Text(text = "8") })
                        DropdownMenuItem(
                            onClick = { viewModel.onDropDownChange("9", VisSettings.TESTNUMBER) },
                            text = { Text(text = "9") })
                        DropdownMenuItem(
                            onClick = { viewModel.onDropDownChange("10", VisSettings.TESTNUMBER) },
                            text = { Text(text = "10") })
                    }
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
                        OutlinedStyleNumberField(
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
                        OutlinedStyleNumberField(
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
                        OutlinedStyleNumberField(
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
                        OutlinedStyleNumberField(
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
                        OutlinedStyleNumberField(
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
                        OutlinedStyleNumberField(
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
                        OutlinedStyleNumberField(
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
                        OutlinedStyleNumberField(
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
                    onClick = { viewModel.onVisClear() }
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
                    onClick = { viewModel.onVisSave() }
                ) { Text(text = "Save") }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}


@Preview(device = "spec:width=700dp,height=500dp,dpi=240,orientation=portrait", showSystemUi = false, showBackground = true)
@Composable
fun ViscosityScreenPreview() {
    val context = LocalContext.current
    val viewModel: AssemblyOrderDetailsViewModel = viewModel(
        factory = AssemblyOrderDetailsViewModelFactory(context)
    )
    ViscosityScreen(viewModel, "Daniel", "A4812")
}