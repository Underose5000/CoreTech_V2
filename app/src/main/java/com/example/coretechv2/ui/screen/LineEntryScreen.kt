package com.example.coretechv2.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.coretechv2.ui.component.BarcodeScanEffect
import com.example.coretechv2.viewmodel.AssemblyOrderDetailsViewModel

/**
 * Window for adding material to an assembly line, opened by tapping a line or
 * scanning its barcode.
 *
 * Single batch orders (multi batch disabled): the user enters the batch
 * number (filled in automatically when the scanned barcode contains one) and
 * the quantity actually added. Saving issues that quantity straight away. A
 * negative quantity corrects a mistake, and entering only a batch number
 * records the batch without issuing anything.
 *
 * Multi-batch orders: there is no quantity. Saving issues the line's planned
 * quantity for the batches done and records the batch number. Remove takes
 * the line back off if it was added by mistake.
 *
 * While this window is open, scanning the same item again fills in its batch
 * number.
 *
 * @param viewModel The [AssemblyOrderDetailsViewModel] holding the entry state.
 */
@Composable
fun LineEntryScreen(viewModel: AssemblyOrderDetailsViewModel) {
    val line = viewModel.entryLine ?: return
    val fullQty = viewModel.fullQtyMode
    val focus = remember { FocusRequester() }

    BarcodeScanEffect { code -> viewModel.onBarcodeScannedInEntry(code) }

    LaunchedEffect(line) {
        focus.requestFocus()
    }

    val planned = line.ORDERQTY.toDoubleOrNull() ?: 0.0
    val adjust = line.ADJUSTQTY.toDoubleOrNull() ?: 0.0
    val added = line.TOTALISSUEDQTY.toDoubleOrNull() ?: 0.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = line.LINEDESCRIPTION,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        if (fullQty) {
            val remaining = viewModel.fullQtyRemaining(line)
            Text(
                text = when {
                    remaining > 0.0000001 ->
                        "Adds ${lineQtyText(remaining, "1", line.LINEUNIT)} for ${viewModel.batchesDone} of ${viewModel.totalBatches} batches"
                    line.ADDITIONALFIELD_1 ->
                        "Already added for ${viewModel.batchesDone} of ${viewModel.totalBatches} batches"
                    else ->
                        "Nothing more to add for ${viewModel.batchesDone} of ${viewModel.totalBatches} batches"
                },
                textAlign = TextAlign.Center,
            )
        } else {
            Text(
                text = buildString {
                    append("Planned: ${lineQtyText(planned + adjust, viewModel.numberOfBatches.value, line.LINEUNIT)}")
                    if (added != 0.0) append("    Added so far: ${lineQtyText(added, "1", line.LINEUNIT)}")
                },
                textAlign = TextAlign.Center,
            )
            viewModel.entryLabelQty?.let {
                Text(
                    text = "Qty on label: $it",
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

        OutlinedTextField(
            value = viewModel.entryBatch,
            onValueChange = viewModel::onEntryBatchChange,
            label = { Text("Batch Number") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = if (fullQty) ImeAction.Done else ImeAction.Next),
            keyboardActions = KeyboardActions(onDone = { viewModel.saveLineEntry() }),
            modifier = Modifier
                .fillMaxWidth()
                .then(if (fullQty) Modifier.focusRequester(focus) else Modifier)
        )
        if (!fullQty) {
            OutlinedTextField(
                value = viewModel.entryQty,
                onValueChange = viewModel::onEntryQtyChange,
                label = { Text("Qty Added (${line.LINEUNIT})") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { viewModel.saveLineEntry() }),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focus)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = { viewModel.cancelLineEntry() },
                enabled = !viewModel.issuing
            ) { Text(text = "Cancel") }
            if (fullQty && line.ADDITIONALFIELD_1) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.removeFullQtyEntry() },
                    enabled = !viewModel.issuing
                ) { Text(text = "Remove") }
            }
            Button(
                modifier = Modifier.weight(1f),
                onClick = { viewModel.saveLineEntry() },
                enabled = !viewModel.issuing
            ) {
                if (viewModel.issuing) {
                    CircularProgressIndicator(modifier = Modifier.height(20.dp), strokeWidth = 2.dp)
                } else {
                    Text(text = if (fullQty) "Add" else "Save")
                }
            }
        }
    }
}
