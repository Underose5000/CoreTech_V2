package com.example.coretechv2.repository

import android.graphics.Bitmap
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.qrcode.QRCodeWriter

val hints = mapOf(
    EncodeHintType.MARGIN to 0
)

/**
 * Generates a QR code bitmap from the supplied text.
 *
 * The generated QR code uses no additional margin around the barcode and
 * produces a square bitmap using the specified size.
 *
 * @param text Text to encode into the QR code.
 * @param size Width and height of the generated QR code in pixels.
 * @return A bitmap containing the generated QR code.
 */
fun generateQRCode(text: String, size: Int): Bitmap {
    val bits = QRCodeWriter().encode(
        text,
        BarcodeFormat.QR_CODE,
        size,
        size,
        hints
    )

    val bitmap = createBitmap(size, size, Bitmap.Config.RGB_565)

    for (x in 0 until size) {
        for (y in 0 until size) {
            bitmap[x, y] = if (bits[x, y]) android.graphics.Color.BLACK
            else android.graphics.Color.WHITE
        }
    }

    return bitmap
}

/**
 * Generates an EAN-13 barcode bitmap from the supplied text.
 *
 * The barcode is generated using the specified width and height, with no
 * additional margin around the barcode.
 *
 * @param text EAN-13 value to encode into the barcode.
 * @param width Width of the generated barcode in pixels.
 * @param height Height of the generated barcode in pixels.
 * @return A bitmap containing the generated EAN-13 barcode.
 */
fun generateEAN13Barcode(text: String, width: Int, height: Int): Bitmap {
    val bits = MultiFormatWriter().encode(
        text,
        BarcodeFormat.EAN_13,
        width,
        height,
        hints
    )

    val bitmap = createBitmap(width, height, Bitmap.Config.RGB_565)

    for (x in 0 until width) {
        for (y in 0 until height) {
            bitmap[x, y] = if (bits[x, y]) android.graphics.Color.BLACK
            else android.graphics.Color.WHITE
        }
    }

    return bitmap
}

/**
 * Generates a CODE 128 barcode bitmap from the supplied text.
 *
 * Any unused white space at the left and right edges of the generated
 * barcode is removed so that the returned bitmap contains only the
 * horizontal area occupied by the barcode.
 *
 * @param text Text to encode into the CODE 128 barcode.
 * @param width Requested width of the barcode in pixels.
 * @param height Height of the generated barcode in pixels.
 * @return A bitmap containing the generated CODE 128 barcode with
 *         unnecessary horizontal margins removed.
 */

fun generateCODE128Barcode(text: String, width: Int, height: Int): Bitmap {
    val bits = MultiFormatWriter().encode(
        text,
        BarcodeFormat.CODE_128,
        width,
        height,
        hints
    )

    var firstBlack = -1
    var lastBlack = -1

    for (x in 0 until bits.width) {
        var hasBlack = false

        for (y in 0 until bits.height) {
            if (bits[x, y]) {
                hasBlack = true
                break
            }
        }

        if (hasBlack) {
            if (firstBlack == -1) firstBlack = x
            lastBlack = x
        }
    }
    val bitmapWidth = lastBlack - firstBlack + 1
    val bitmap = createBitmap(bitmapWidth, height, Bitmap.Config.RGB_565)

    for (x in firstBlack until lastBlack + 1) {
        for (y in 0 until height) {
            bitmap[x - firstBlack, y] = if (bits[x, y]) android.graphics.Color.BLACK
            else android.graphics.Color.WHITE
        }
    }

    return bitmap
}