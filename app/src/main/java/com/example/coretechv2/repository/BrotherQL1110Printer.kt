package com.example.coretechv2.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.core.graphics.createBitmap
import androidx.core.graphics.toColorInt
import com.brother.ptouch.sdk.NetPrinter
import com.brother.ptouch.sdk.NetworkDiscovery
import com.brother.sdk.lmprinter.Channel
import com.brother.sdk.lmprinter.OpenChannelError
import com.brother.sdk.lmprinter.PrinterDriverGenerator
import com.brother.sdk.lmprinter.PrinterModel
import com.brother.sdk.lmprinter.setting.PrintImageSettings
import com.brother.sdk.lmprinter.setting.PrintImageSettings.Orientation
import com.brother.sdk.lmprinter.setting.QLPrintSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Searches the local network for an available Brother printer.
 *
 * Network discovery runs for up to five seconds. Each printer discovered
 * during this period is passed to [callback]. If no printer is discovered
 * before the discovery period ends, the callback is invoked with `null`.
 *
 * @param callback Function invoked when a Brother printer is discovered.
 * Receives the discovered [NetPrinter], or `null` when no printer is found
 * within the discovery period.
 */
fun discoverBrotherPrinter(callback: (foundBrotherPrinter: NetPrinter?) -> Unit) {
    var discovery: NetworkDiscovery?
    var printercount = 0

    discovery = NetworkDiscovery { printer ->

        callback(printer)
        printercount += 1
    }

    CoroutineScope(Dispatchers.Default).launch {
        delay(5_000)

        if (printercount == 0) {
            callback(null)
        }
        discovery.stop()
    }
    discovery.start()

}

/**
 * Prints an [ImageBitmap] to a Brother QL-1110NWB label printer over Wi-Fi.
 *
 * The supplied image is converted to an Android [Bitmap] and copied onto
 * a white background before being sent to the printer. Printing is performed
 * on an IO coroutine so that printer communication does not block the
 * application's main thread.
 *
 * The printer is configured for a 103 mm roll, portrait orientation,
 * error-diffusion halftoning, automatic cutting and the requested number
 * of copies. The image is scaled to fit the label's page aspect ratio.
 *
 * @param context Android [Context] used to determine the printer driver's
 * working directory.
 * @param printerIp IP address of the Brother printer to connect to.
 * @param imageBytes Image to print. If `null`, no printing operation is
 * performed.
 * @param numOfCopies Number of copies of the label to print.
 */
fun printBrotherImage(
    context: Context,
    printerIp: String,
    imageBytes: ImageBitmap?,
    numOfCopies: Int

) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            if (imageBytes != null) {
                val original = imageBytes.asAndroidBitmap()
                val bitmap = original.copy(Bitmap.Config.ARGB_8888, false)


                val processedBitmap = createBitmap(bitmap.width, bitmap.height)
                val canvas = Canvas(processedBitmap)

                canvas.drawColor("#FFFFFF".toColorInt())

                canvas.drawBitmap(bitmap, 0f, 0f, null)


                val channel = Channel.newWifiChannel(printerIp)
                val openResult = PrinterDriverGenerator.openChannel(channel)
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

                printerDriver.printImage(processedBitmap, printSettings)

                printerDriver.closeChannel()

            }

        } catch (e: Exception) {
            Log.e("BrotherQL1110Printer", "Exception during printing", e)
        }
    }
}
