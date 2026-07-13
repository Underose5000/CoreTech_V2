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
import androidx.compose.ui.platform.LocalContext
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


fun discoverEpsonLabelPrinter(context: Context, callback: (foundEpsonLabelPrinter: EPSLabelPrinter?) -> Unit) {
    val discovery = EPSLabelPrinterDiscovery(context)
    var called = false

    val timeoutRunnable = Runnable {
        if (!called) {
            called = true
            Handler(Looper.getMainLooper()).post { callback(null) }
            discovery.stop()
        }
    }

    // Post the timeout after 5 seconds
    Handler(Looper.getMainLooper()).postDelayed(timeoutRunnable, 5000)

    discovery.start(
        EPSLabelPrinterDiscovery.CONNECTION_TYPE_NETWORK,
        { printerList ->
            if (!called && printerList.isNotEmpty()) {
                called = true
                Handler(Looper.getMainLooper()).post {
                    callback(printerList.first())
                }
                discovery.stop()
            }
        },
        5000
    )
}

fun rotateBitmap(source: Bitmap, angle: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(angle) // angle in degrees
    return Bitmap.createBitmap(
        source, 0, 0, source.width, source.height, matrix, true
    )
}
private fun replaceColorInBitmap(
    bitmap: Bitmap,
    originalColor: Int,
    replacementColor: Int
): Bitmap {
    val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
    val width = mutableBitmap.width
    val height = mutableBitmap.height

    for (x in 0 until width) {
        for (y in 0 until height) {
            if (colorsAreClose(mutableBitmap.getPixel(x, y), originalColor)) {
                mutableBitmap.setPixel(x, y, replacementColor)
            }
        }
    }

    return mutableBitmap
}

fun colorsAreClose(c1: Int, c2: Int, tolerance: Int = 10): Boolean {
    val r1 = Color.red(c1)
    val g1 = Color.green(c1)
    val b1 = Color.blue(c1)
    val r2 = Color.red(c2)
    val g2 = Color.green(c2)
    val b2 = Color.blue(c2)
    return (abs(r1 - r2) <= tolerance &&
            abs(g1 - g2) <= tolerance &&
            abs(b1 - b2) <= tolerance)
}

fun printWithEpson(
    printer: EPSLabelPrinter,
    //imageBytes: ByteArray,
    imageBytes: ImageBitmap?,
    width: Int,
    height: Int,
    numOfCopies: Int
) {
    val settings = printer.getDefaultPrintSettings().apply {
        this[EPSLabelPrinter.KEY_PAPER_SIZE_TYPE] = EPSLabelPrinter.PAPER_SIZE_TYPE_CUSTOM
        this[EPSLabelPrinter.KEY_CUSTOM_PAPER_WIDTH] = width
        this[EPSLabelPrinter.KEY_CUSTOM_PAPER_HEIGHT] = height
        this[EPSLabelPrinter.KEY_MEDIA_FORM] = EPSLabelPrinter.MEDIA_FORM_DIE_CUT_GAP
        this[EPSLabelPrinter.KEY_MEDIA_TYPE] = EPSLabelPrinter.MEDIA_TYPE_GLOSSY_FILM
        this[EPSLabelPrinter.KEY_QUALITY] = EPSLabelPrinter.PRINT_QUALITY_QUALITY
        this[EPSLabelPrinter.KEY_COLOR_ADJUSTMENT] = EPSLabelPrinter.COLOR_ADJUSTMENT_VIVID
        this[EPSLabelPrinter.KEY_BRIGHTNESS] = 3
        this[EPSLabelPrinter.KEY_CONTRAST] = 0
        this[EPSLabelPrinter.KEY_SATURATION] = -10
        this[EPSLabelPrinter.KEY_COPIES] = numOfCopies
        this[EPSLabelPrinter.KEY_ACTION_MODE] = EPSLabelPrinter.ACTION_MODE_FEED_TO_CUT_POSITION
        this[EPSLabelPrinter.KEY_PRINTING_SPEED] = 3
    }

    if(imageBytes != null) {
    Thread {
        //val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        val original = imageBytes.asAndroidBitmap()


        val originalColor = Color.rgb(64, 193, 243)
        val replacementColor = Color.rgb(103, 220, 230)

        val colorReplacedBitmap = replaceColorInBitmap(original, originalColor, replacementColor)

        val rotatedBitmap = rotateBitmap(colorReplacedBitmap, 90f)

        val renderer = object : EPSLabelPrinter.Renderer {
            override fun draw(canvas: Canvas, pageIndex: Int, pageWidth: Int, pageHeight: Int, targetRect: Rect): Boolean {
                Log.d("Epson", "Bitmap: ${rotatedBitmap.width} x ${rotatedBitmap.height}")
                Log.d("Epson", "Page: $pageWidth x $pageHeight")
                Log.d("Epson", "TargetRect: $targetRect")
                val paint = Paint()
                val srcRect = RectF(0f, 0f, rotatedBitmap.width.toFloat(), rotatedBitmap.height.toFloat())
                val dstRect = RectF(0f, 0f, pageWidth.toFloat(), pageHeight.toFloat())
                val matrix = Matrix().apply { setRectToRect(srcRect, dstRect, Matrix.ScaleToFit.CENTER) }
                canvas.drawBitmap(rotatedBitmap, matrix, paint)
                return false
            }
        }

        try {
            Log.d("EpsonPrint", "Starting print job on ${printer.modelName}")
            val resultCode = printer.print(settings, renderer, object : EPSLabelPrinter.ProgressListener {
                override fun onProgress(pageIndex: Int) {
                    Log.d("EpsonPrint", "Printing page $pageIndex")
                }
            })
            Log.d("EpsonPrint", "Printer.print() finished with code $resultCode")
        } catch (e: Exception) {
            Log.e("EpsonPrint", "Exception during print", e)
        } finally {
            original.recycle()
            rotatedBitmap.recycle()
        }
    }.start()
}
}