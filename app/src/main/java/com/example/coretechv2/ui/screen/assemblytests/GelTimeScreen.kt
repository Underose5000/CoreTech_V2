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
import com.example.coretechv2.dataclasses.TestTypes
import com.example.coretechv2.dataclasses.VisField
import com.example.coretechv2.dataclasses.VisSettings
import com.example.coretechv2.factory.AssemblyOrderDetailsViewModelFactory
import com.example.coretechv2.ui.component.OutlinedStyleButton
import com.example.coretechv2.ui.component.OutlinedStyleIntNumberField
import com.example.coretechv2.ui.component.PopupWindow
import com.example.coretechv2.viewmodel.AssemblyOrderDetailsViewModel

/**
 *
 */
@Composable
fun GelTimeScreen(
    viewModel: AssemblyOrderDetailsViewModel, currentUser: String, orderNumber: String
) {
    val focusManager = LocalFocusManager.current
    PopupWindow(width = 700, height = 250) {
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
                    Text(text = "Catalyst %")
                    OutlinedStyleIntNumberField(
                        value = viewModel.visReading.value.vis12,
                        onValueChange = { newValue -> viewModel.onVisChange(newValue, VisField.VIS12) }
                    )
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
                        Text(text = "Hours(H)")
                        OutlinedStyleIntNumberField(
                            value = viewModel.visReading.value.vis60,
                            onValueChange = { newValue -> viewModel.onVisChange(newValue, VisField.VIS60) }
                        )
                    }
                   Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text(text = "Minutes(MM)")
                       OutlinedStyleIntNumberField(
                            value = viewModel.visReading.value.vis30,
                            onValueChange = { newValue -> viewModel.onVisChange(newValue, VisField.VIS30) }
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text(text = "Seconds(SS)")
                        OutlinedStyleIntNumberField(
                            value = viewModel.visReading.value.vis12,
                            onValueChange = { newValue -> viewModel.onVisChange(newValue, VisField.VIS12) }
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
                    onClick = { viewModel.onCancel(TestTypes.GEL_TIME) }
                ) { Text(text = "Cancel") }
                Button(
                    modifier = Modifier
                        .weight(2f)
                        .padding(horizontal = 20.dp, vertical = 40.dp),
                    onClick = { viewModel.onTestSave(currentUser, TestTypes.VISCOSITY, viewModel.showViscosityScreen) }
                ) { Text(text = "Save") }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}


@Preview(device = "spec:width=700dp,height=500dp,dpi=240,orientation=portrait", showSystemUi = false, showBackground = true)
@Composable
fun GelTimePreview() {
    val context = LocalContext.current
    val viewModel: AssemblyOrderDetailsViewModel = viewModel(
        factory = AssemblyOrderDetailsViewModelFactory(context)
    )
    ViscosityScreen(viewModel, "Daniel", "A4812")
}