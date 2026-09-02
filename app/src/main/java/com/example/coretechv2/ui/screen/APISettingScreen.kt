package com.example.coretechv2.ui.screen

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coretechv2.R
import com.example.coretechv2.factory.APISettingViewModelFactory
import com.example.coretechv2.ui.component.QRScanner
import com.example.coretechv2.viewmodel.APISettingViewModel
import com.example.coretechv2.viewmodel.SharedViewModel

/**
 * API Settings screen used to configure backend connection details.
 *
 * This screen allows the user to input and save:
 * - Server URL
 * - Server Port
 * - API Key
 *
 * It is typically shown on first app launch or when connection
 * settings are invalid or missing.
 *
 * Features:
 * - Displays company logo at the top
 * - Input fields for API configuration
 * - Error message display for failed connections
 * - Connect button to validate and save settings
 * - Automatic navigation trigger when connection succeeds
 *
 * Navigation:
 * - Calls [onConnectedSuccess] when API connection is validated successfully
 *
 * @param onConnectedSuccess Callback triggered when connection succeeds
 * and the app should navigate away from the settings screen.
 */
@Composable
fun APISettingsScreen(
    onConnectedSuccess: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: APISettingViewModel = viewModel(
        factory = APISettingViewModelFactory(context)
    )
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->

        if (granted) {
            viewModel.onCameraAccess()

        }
    }
    LaunchedEffect(Unit) {
        launcher.launch(
            Manifest.permission.CAMERA
        )
    }

    val sharedViewModel: SharedViewModel = viewModel()

    if (viewModel.ConnectedSuccess) {
        onConnectedSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row {
            Spacer(modifier = Modifier.weight(1f))
            Image(
                painter = painterResource(id = R.drawable.company_logo_light),
                contentDescription = null,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(30.dp))

        OutlinedTextField(
            value = viewModel.newurl,
            onValueChange = viewModel::onUrlChange,
            label = { Text("Server URL") },
            modifier = Modifier.padding(16.dp)
        )

        OutlinedTextField(
            value = viewModel.newport,
            onValueChange = viewModel::onPortChange,
            label = { Text("Port") },
            modifier = Modifier.padding(16.dp)
        )

        OutlinedTextField(
            value = viewModel.newkey,
            onValueChange = viewModel::onKeyChange,
            label = { Text("API Key") },
            modifier = Modifier.padding(16.dp),
        )

        if (viewModel.showErrorMessage.value) {
            Text(
                viewModel.errorMessage.value,
                color = Color.Red,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
        } else {
            Spacer(modifier = Modifier.height(20.dp))
        }
        Row() {
            OutlinedButton(
                onClick = { viewModel.scanQR() }
            ) {
                Text("Scan QR")
            }
            Spacer(modifier = Modifier.width(30.dp))
            OutlinedButton(
                onClick = { viewModel.connect() }
            ) {
                Text("connect")
            }
        }
    }

    if (viewModel.showScanner.value) {
        Box(modifier = Modifier.fillMaxSize()) {
            QRScanner { scannedCode ->

                viewModel.onQRCodeScanned(scannedCode)
            }
            Button(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 80.dp),
                onClick = { viewModel.closeScanner() }
            ) {
                Text("Close Scanner")
            }
        }
    }
}