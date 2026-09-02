package com.example.coretechv2.repository


import android.content.Context
import com.example.coretechv2.dataclasses.PrinterType

/**
 * Discovers available Brother and Epson label printers.
 *
 * Printer discovery is performed for both supported printer types. The
 * [callback] is invoked whenever a printer is discovered, with the printer
 * information wrapped in a [PrinterType] object.
 *
 * If a printer cannot be identified by its discovery service, its name and
 * IP address are set to `"N/A"`.
 *
 * @param context Android context required to discover Epson label printers.
 * @param callback Callback invoked with each discovered printer.
 */

fun findPrinters(context: Context, callback: (printer: PrinterType) -> Unit) {
    discoverBrotherPrinter { foundBrotherPrinter ->
        val foundPrinter = PrinterType(
            name = foundBrotherPrinter?.modelName ?: "N/A",
            ipAddress = foundBrotherPrinter?.ipAddress ?: "N/A",
            brotherQL1110 = foundBrotherPrinter
        )
        callback(foundPrinter)
    }
    discoverEpsonLabelPrinter(context) { foundEpsonLabelPrinter ->
        val foundPrinter = PrinterType(
            name = foundEpsonLabelPrinter?.modelName ?: "N/A",
            ipAddress = foundEpsonLabelPrinter?.printerID ?: "N/A",
            epsonCWC6510 = foundEpsonLabelPrinter
        )
        callback(foundPrinter)
    }

}
