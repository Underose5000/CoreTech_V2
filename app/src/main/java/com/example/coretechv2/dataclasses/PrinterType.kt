package com.example.coretechv2.dataclasses

import com.brother.ptouch.sdk.NetPrinter
import com.epson.ijprinter.esclabelsdk.EPSLabelPrinter

/**
 * Represents a printer and its connection details.
 *
 * @property name The name used to identify the printer.
 * @property ipAddress The IP address of the printer on the network.
 * @property brotherQL1110 The Brother QL-1110 printer connection, if applicable.
 * @property epsonCWC6510 The Epson CW-C6510 printer connection, if applicable.
 */
data class PrinterType(
    val name: String,
    val ipAddress: String,
    val brotherQL1110: NetPrinter? = null,
    val epsonCWC6510: EPSLabelPrinter? = null,
)