package com.example.coretechv2.ui.component

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

/**
 * Displays a styled outlined button with optional icon content.
 *
 * The button uses a transparent background, grey border, and rounded
 * corners to provide a consistent outlined appearance. An optional icon
 * can be displayed alongside the button text.
 *
 * If [text] is blank, only the icon is displayed when an icon is supplied.
 * If [icon] is `null`, no icon is displayed.
 *
 * @param modifier Optional [Modifier] used to customise the button's layout
 * or appearance.
 * @param text The text displayed inside the button.
 * @param onClick Callback invoked when the button is pressed.
 * @param icon Optional [ImageVector] displayed alongside the button text.
 */
@OptIn(ExperimentalGetImage::class)
@Composable
fun QRScanner(
    onCodeScanned: (String) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var scanned by remember { mutableStateOf(false) }

    val hasPermission = remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }




    if (!hasPermission.value) {
        Log.e("QR", "Camera permission missing")
        return
    }


    val previewView = remember {
        PreviewView(context).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
    }


    DisposableEffect(Unit) {

        val cameraProviderFuture =
            ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({

            try {

                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(
                            previewView.surfaceProvider
                        )
                    }


                val scanner = BarcodeScanning.getClient()


                val analysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(
                        ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
                    )
                    .build()


                analysis.setAnalyzer(
                    ContextCompat.getMainExecutor(context)
                ) { imageProxy ->


                    if (scanned) {
                        imageProxy.close()
                        return@setAnalyzer
                    }


                    val mediaImage = imageProxy.image

                    if (mediaImage != null) {

                        val image = InputImage.fromMediaImage(
                            mediaImage,
                            imageProxy.imageInfo.rotationDegrees
                        )


                        scanner.process(image)
                            .addOnSuccessListener { barcodes ->

                                val value =
                                    barcodes.firstOrNull()?.rawValue


                                if (!value.isNullOrEmpty()) {

                                    scanned = true

                                    Log.d(
                                        "QR",
                                        "Scanned: $value"
                                    )

                                    onCodeScanned(value)
                                }

                            }
                            .addOnFailureListener {
                                Log.e(
                                    "QR",
                                    "Scanner failed",
                                    it
                                )
                            }
                            .addOnCompleteListener {
                                imageProxy.close()
                            }

                    } else {
                        imageProxy.close()
                    }
                }


                cameraProvider.unbindAll()


                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    analysis
                )


                Log.d(
                    "QR",
                    "Camera started"
                )


            } catch (e: Exception) {

                Log.e(
                    "QR",
                    "Camera failed",
                    e
                )
            }


        }, ContextCompat.getMainExecutor(context))


        onDispose {

            try {
                val provider =
                    cameraProviderFuture.get()

                provider.unbindAll()

            } catch (_: Exception) {

            }
        }

    }
    AndroidView(
        factory = {
            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}