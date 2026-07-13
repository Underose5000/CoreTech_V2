package com.example.coretechv2.repository



import android.content.Context
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.rememberGraphicsLayer
import com.example.coretechv2.printlayouts.SimpleLabel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.runtime.setValue
import com.example.coretechv2.dataclasses.LabelElements
import kotlinx.coroutines.android.awaitFrame
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import com.brother.ptouch.sdk.NetPrinter
import com.epson.ijprinter.esclabelsdk.EPSLabelPrinter
import com.example.coretechv2.dataclasses.PrinterType
import com.example.coretechv2.ui.component.mmToPixels


fun findPrinters(context: Context, callback: (printer: PrinterType) -> Unit) {
    discoverBrotherPrinter{ foundBrotherPrinter ->
        val foundPrinter = PrinterType(
            name = foundBrotherPrinter?.modelName ?: "N/A",
            ipAddress = foundBrotherPrinter?.ipAddress ?: "N/A",
            brotherQL1110 = foundBrotherPrinter
        )
        callback(foundPrinter)
    }
    discoverEpsonLabelPrinter(context){ foundEpsonLabelPrinter ->
        val foundPrinter = PrinterType(
            name = foundEpsonLabelPrinter?.modelName ?: "N/A",
            ipAddress = foundEpsonLabelPrinter?.printerID ?: "N/A",
            epsonCWC6510 = foundEpsonLabelPrinter
        )
        callback(foundPrinter)
    }

}
