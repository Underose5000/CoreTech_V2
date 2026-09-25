package com.example.coretechv2.ui.component

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

/**

 * Displays a camera preview and scans QR codes using the device's back camera.
 *
 * The scanner uses CameraX to display the camera preview and analyse incoming
 * camera frames. Google ML Kit is used to detect barcodes and QR codes from
 * the camera image.
 *
 * When a QR code is successfully detected, its raw value is passed to
 * [onCodeScanned]. Once a code has been detected, scanning is stopped to
 * prevent additional codes from being reported during the same scan.
 *
 * The camera is bound to the current lifecycle and is unbound when the
 * composable leaves the composition.
 *
 * Camera access must already be granted before this composable is displayed.
 * If camera permission has not been granted, the scanner does not start.
 *
 * @param onCodeScanned Callback invoked with the raw value of the first
 * successfully detected QR code.
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