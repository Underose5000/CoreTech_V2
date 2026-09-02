package com.example.coretechv2.repository


import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import com.example.coretechv2.ui.component.mmToPixels
import kotlinx.coroutines.android.awaitFrame

/**
 * Renders Compose content to an [ImageBitmap] at the specified physical size.
 *
 * The supplied width and height are interpreted as millimetres and converted
 * to pixels before the content is recorded. The composable content is scaled
 * to match the requested output dimensions.
 *
 * A frame is awaited before the graphics layer is converted to an image to
 * ensure that the composable content has been measured and rendered.
 *
 * This function is useful for converting product label composables into
 * bitmap images that can subsequently be printed or processed as an image.
 *
 * @param width The desired output width in millimetres.
 * @param height The desired output height in millimetres.
 * @param content The composable content to render into the bitmap.
 * @return The rendered [ImageBitmap], or `null` until the graphics layer has
 * been rendered and captured.
 */
@Composable
fun labelToBitmap(width: Double, height: Double, content: @Composable () -> Unit): ImageBitmap? {
    val graphicsLayer = rememberGraphicsLayer()
    var scaleFactor = 1.0f
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var measuredSize by remember { mutableStateOf(IntSize.Zero) }
    val size = IntSize(mmToPixels(width), mmToPixels(height))
    Box(
        modifier = Modifier
            .onSizeChanged {
                measuredSize = it
                scaleFactor = (size.width.toDouble() / measuredSize.width.toDouble()).toFloat()
            }
            .drawWithContent {
                graphicsLayer.record(size = size) {
                    withTransform({
                        scale(scaleFactor, pivot = Offset.Zero)
                    }) {
                        this@drawWithContent.drawContent()
                    }
                }
                drawLayer(graphicsLayer)
            }
    ) {
        content()
    }
    LaunchedEffect(Unit) {
        awaitFrame()
        imageBitmap = graphicsLayer.toImageBitmap()
    }
    return imageBitmap
}
