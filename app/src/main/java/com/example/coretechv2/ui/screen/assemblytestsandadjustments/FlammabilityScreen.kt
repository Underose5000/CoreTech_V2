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
import com.example.coretechv2.dataclasses.assemblydataclasses.FlammabilityField
import com.example.coretechv2.factory.assemblytests.FlammabilityViewModelFactory
import com.example.coretechv2.ui.component.OutlinedStyleDoubleNumberField
import com.example.coretechv2.ui.component.OutlinedStyleIntNumberField
import com.example.coretechv2.viewmodel.SharedViewModel
import com.example.coretechv2.viewmodel.assemblytests.FlammabilityViewModel

/**
 * Displays the flammability test entry screen.
 *
 * Creates and manages a [FlammabilityViewModel] using the provided
 * [SharedViewModel]. When the screen is first displayed, the test form is
 * initialised using the supplied test record, if one is provided.
 *
 * The screen allows the user to enter the number of days set, burn length,
 * test number, and the recorded hour, minute, and second values. The
 * flammability reading fields are updated through the view model using
 * [FlammabilityField] identifiers.
 *
 * Controls are provided to clear the current values, cancel the operation,
 * or save the flammability test results.
 *
 * Popup messages and test screen state are synchronised between the
 * [FlammabilityViewModel] and [SharedViewModel].
 *
 * Tapping outside an input field clears the current focus.
 *
 * @param sharedViewModel Shared view model used to manage application-wide
 * state and popup messages.
 * @param test Existing flammability test record to edit, or `null` when
 * entering a new test.
 */
@Composable
fun FlammabilityScreen(sharedViewModel: SharedViewModel, test: APICallTables.FlammabilityTest? = null) {
    val context = LocalContext.current
    val viewModel: FlammabilityViewModel = viewModel(
        factory = FlammabilityViewModelFactory(context, sharedViewModel),
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
                text = "Flammability",
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
                Text(text = "Days Set")
                OutlinedStyleIntNumberField(
                    value = viewModel.flameReading.value.daysSet,
                    onValueChange = { newValue -> viewModel.onFlameChange(newValue, FlammabilityField.DAYSSET) }
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Length (mm)")
                OutlinedStyleDoubleNumberField(
                    value = viewModel.flameReading.value.burnLength,
                    onValueChange = { newValue -> viewModel.onFlameChange(newValue, FlammabilityField.BURNLENGTH) }
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
                    value = viewModel.flameReading.value.testNumber,
                    onValueChange = { newValue -> viewModel.onFlameChange(newValue, FlammabilityField.TESTNUMBER) }
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
                        value = viewModel.flameReading.value.hour,
                        onValueChange = { newValue -> viewModel.onFlameChange(newValue, FlammabilityField.HOUR) }
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(text = "Minute")
                    OutlinedStyleIntNumberField(
                        value = viewModel.flameReading.value.minute,
                        onValueChange = { newValue -> viewModel.onFlameChange(newValue, FlammabilityField.MINUTE) }
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(text = "Second")
                    OutlinedStyleIntNumberField(
                        value = viewModel.flameReading.value.second,
                        onValueChange = { newValue -> viewModel.onFlameChange(newValue, FlammabilityField.SECOND) }
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