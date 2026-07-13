package com.example.coretechv2.repository



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
import com.example.coretechv2.ui.component.mmToPixels



@Composable
fun labelToBitmap(width: Double, height: Double, content: @Composable () -> Unit): ImageBitmap? {
    val graphicsLayer = rememberGraphicsLayer()
    var scaleFactor = 1.0f
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var measuredSize by remember { mutableStateOf(IntSize.Zero) }
    val size = IntSize(mmToPixels(width),mmToPixels(height))
    Box(
        modifier = Modifier.onSizeChanged {
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
