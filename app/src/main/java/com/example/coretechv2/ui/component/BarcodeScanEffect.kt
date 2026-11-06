package com.example.coretechv2.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import com.example.coretechv2.repository.BarcodeScanner

/**
 * Listens for hardware barcode scans while this composable is on screen.
 *
 * Place it in a screen (or a popup's content) to receive scans from the
 * barcode scanner. When several are on screen at once, only the most recently
 * shown one receives scans, so a popup takes priority over the screen behind
 * it.
 *
 * @param enabled Whether scans should currently be received.
 * @param onScan Called with the scanned text.
 */
@Composable
fun BarcodeScanEffect(enabled: Boolean = true, onScan: (String) -> Unit) {
    val latestOnScan by rememberUpdatedState(onScan)
    DisposableEffect(enabled) {
        val listener: (String) -> Unit = { code -> latestOnScan(code) }
        if (enabled) BarcodeScanner.register(listener)
        onDispose { BarcodeScanner.unregister(listener) }
    }
}
