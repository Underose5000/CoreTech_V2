package com.example.coretechv2.repository

import android.content.Context
import android.util.Log
import com.brother.sdk.lmprinter.*
import com.brother.sdk.lmprinter.setting.PrintImageSettings
import com.brother.sdk.lmprinter.setting.QLPrintSettings
import java.io.File
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Color
import android.widget.Toast
import android.os.Handler
import android.os.Looper
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.core.graphics.createBitmap
import androidx.core.graphics.toColorInt
import com.brother.sdk.lmprinter.*
import com.brother.sdk.lmprinter.setting.PrintImageSettings.Orientation
import com.brother.sdk.lmprinter.setting.PrintImageSettings.ScaleMode
import com.brother.sdk.lmprinter.Channel
import com.brother.sdk.lmprinter.OpenChannelError
import com.brother.sdk.lmprinter.PrinterDriverGenerator
import com.brother.ptouch.sdk.NetPrinter
import com.brother.ptouch.sdk.NetworkDiscovery
import com.brother.ptouch.sdk.NetworkDiscoveryListener
import com.epson.ijprinter.esclabelsdk.EPSLabelPrinter
import com.epson.ijprinter.esclabelsdk.EPSLabelPrinterDiscovery
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs

fun discoverBrotherPrinter(callback: (foundBrotherPrinter: NetPrinter?) -> Unit) {
    var discovery: NetworkDiscovery? = null
    var printercount = 0

    discovery = NetworkDiscovery { printer ->
        
        callback(printer)
        printercount += 1
    }

    CoroutineScope(Dispatchers.Default).launch {
        delay(5_000)
        
        if(printercount == 0){
            callback(null)
        }
        discovery.stop()
    }
    val started = discovery.start()
    if (!started) {
        
    }
}

fun printBrotherImage(
    context: Context,
    printerIp: String,
    imageBytes: ImageBitmap?,
    numOfCopies: Int

) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            if(imageBytes != null) {
                val original = imageBytes.asAndroidBitmap()
                val bitmap = original.copy(Bitmap.Config.ARGB_8888, false)

                
                val processedBitmap = createBitmap(bitmap.width, bitmap.height)
                val canvas = Canvas(processedBitmap)
                
                canvas.drawColor("#FFFFFF".toColorInt())
                
                canvas.drawBitmap(bitmap, 0f, 0f, null)
                

                val channel = Channel.newWifiChannel(printerIp)
                val openResult = PrinterDriverGenerator.openChannel(channel)
                val errorOpen = openResult.error
                if (errorOpen.getCode() != OpenChannelError.ErrorCode.NoError || openResult.driver == null) {
                    
                }
                val printerDriver = openResult.driver!!

                val printSettings = QLPrintSettings(PrinterModel.QL_1110NWB).apply {
                    labelSize = QLPrintSettings.LabelSize.RollW103
                    halftone = PrintImageSettings.Halftone.ErrorDiffusion
                    printOrientation = Orientation.Portrait
                    scaleMode = PrintImageSettings.ScaleMode.FitPageAspect
                    isAutoCut = true
                    numCopies = numOfCopies
                    try {
                        this.workPath = context.getExternalFilesDir(null)?.absolutePath
                            ?: context.filesDir.absolutePath
                    } catch (e: Exception) {
                        Log.w("Print", "Setting workPath failed: ${e.message}")
                    }
                }

                val printError = printerDriver.printImage(processedBitmap, printSettings)

                printerDriver.closeChannel()
                
            } else {
                
            }

        } catch (e: Exception) {
            Log.e("BrotherQL1110Printer", "Exception during printing", e)
        }
    }
}
