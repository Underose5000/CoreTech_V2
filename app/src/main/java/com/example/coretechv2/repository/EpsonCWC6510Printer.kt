package com.example.coretechv2.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import com.epson.ijprinter.esclabelsdk.EPSLabelPrinter
import com.epson.ijprinter.esclabelsdk.EPSLabelPrinterDiscovery
import kotlin.math.abs

/**
 * Discovers Epson label printers available on the local network.
 *
 * The discovery process runs for a maximum of five seconds. If a printer is
 * found before the timeout, the first discovered printer is returned through
 * [callback]. If no printer is discovered within the timeout period, * [callback] is invoked with `null`.
 *
 * @param context Android context used to initialise the Epson printer
 * discovery service.
 * @param callback Callback invoked with the first discovered printer, or
 * `null` if no printer is found within the discovery period.
 */
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

/**
 * Creates a rotated copy of the supplied bitmap.
 *
 * @param source Bitmap to rotate.
 * @param angle Rotation angle in degrees.
 * @return A new bitmap containing the rotated image.
 */

fun rotateBitmap(source: Bitmap, angle: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(angle) // angle in degrees
    return Bitmap.createBitmap(
        source, 0, 0, source.width, source.height, matrix, true
    )
}

/**
 * Replaces pixels matching a specified colour with another colour.
 *
 * A tolerance is applied when comparing colours, allowing pixels that are
 * slightly different from [originalColor] to also be replaced.
 *
 * @param bitmap Bitmap whose colours will be modified.
 * @param originalColor Colour to search for.
 * @param replacementColor Colour to use in place of the original colour.
 * @return A new mutable bitmap with the matching colours replaced.
 */

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

/**
 * Determines whether two colours are sufficiently similar.
 *
 * Each RGB channel is compared independently. The colours are considered
 * similar when the difference between each corresponding RGB channel is
 * within [tolerance].
 *
 * @param c1 First colour to compare.
 * @param c2 Second colour to compare.
 * @param tolerance Maximum permitted difference for each RGB channel.
 * @return `true` if the colours are within the specified tolerance,
 * otherwise `false`.
 */

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

/**
 * Prints a label image using an Epson label printer.
 *
 * The supplied [ImageBitmap] is converted to an Android [Bitmap], its colours
 * are adjusted, and the image is rotated 90 degrees before being rendered
 * onto the printer's page.
 *
 * Printing is performed on a background thread to avoid blocking the main
 * application thread.
 *
 * @param printer Epson label printer to use for printing.
 * @param imageBytes Image to print. If `null`, no printing is performed.
 * @param width Custom label width used by the Epson printer settings.
 * @param height Custom label height used by the Epson printer settings.
 * @param numOfCopies Number of copies to print.
 */

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

    if (imageBytes != null) {
        Thread {
            //val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            val original = imageBytes.asAndroidBitmap()


            val originalColor = Color.rgb(64, 193, 243)
            val replacementColor = Color.rgb(103, 220, 230)

            val colorReplacedBitmap = replaceColorInBitmap(original, originalColor, replacementColor)

            val rotatedBitmap = rotateBitmap(colorReplacedBitmap, 90f)

            val renderer = object : EPSLabelPrinter.Renderer {
                override fun draw(canvas: Canvas, pageIndex: Int, pageWidth: Int, pageHeight: Int, targetRect: Rect): Boolean {


                    val paint = Paint()
                    val srcRect = RectF(0f, 0f, rotatedBitmap.width.toFloat(), rotatedBitmap.height.toFloat())
                    val dstRect = RectF(0f, 0f, pageWidth.toFloat(), pageHeight.toFloat())
                    val matrix = Matrix().apply { setRectToRect(srcRect, dstRect, Matrix.ScaleToFit.CENTER) }
                    canvas.drawBitmap(rotatedBitmap, matrix, paint)
                    return false
                }
            }

            try {

                val resultCode = printer.print(settings, renderer, object : EPSLabelPrinter.ProgressListener {
                    override fun onProgress(pageIndex: Int) {

                    }
                })

            } catch (e: Exception) {
                Log.e("EpsonPrint", "Exception during print", e)
            } finally {
                original.recycle()
                rotatedBitmap.recycle()
            }
        }.start()
    }
}