package com.example.coretechv2.repository

import android.graphics.Bitmap
import android.util.Log
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter

val hints = mapOf(
    EncodeHintType.MARGIN to 0
)
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
    val bitmap = createBitmap(bitmapWidth, height,Bitmap.Config.RGB_565)

    for (x in firstBlack until lastBlack + 1) {
        for (y in 0 until height) {
            bitmap[x-firstBlack, y] = if (bits[x, y]) android.graphics.Color.BLACK
            else android.graphics.Color.WHITE
        }
    }

    return bitmap
}