package com.example.coretechv2.dataclasses

import androidx.compose.ui.graphics.vector.ImageVector
import com.brother.ptouch.sdk.NetPrinter
import com.epson.ijprinter.esclabelsdk.EPSLabelPrinter


data class PrinterType(
    val name: String,
    val ipAddress: String,
    val brotherQL1110: NetPrinter? = null,
    val epsonCWC6510: EPSLabelPrinter? =null,
)