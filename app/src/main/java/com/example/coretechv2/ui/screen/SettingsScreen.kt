package com.example.coretechv2.ui.screen

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.coretechv2.factory.APISettingViewModelFactory
import com.example.coretechv2.factory.SettingsViewModelFactory
import androidx.core.net.toUri
import com.example.coretechv2.ui.component.BarcodeScanEffect
import com.example.coretechv2.ui.component.QRScanner
import com.example.coretechv2.ui.component.TopBar
import com.example.coretechv2.viewmodel.APISettingViewModel
import com.example.coretechv2.viewmodel.SettingsViewModel
import com.example.coretechv2.viewmodel.SharedViewModel

/**
 * Displays the settings screen opened from the home screen.
 *
 * The screen allows the user to:
 *      Change the API server URL, port and key, either manually or by
 *      scanning a QR code. The connection is tested before the new
 *      settings are kept, otherwise the previous settings are restored.
 *      Change the password of the currently logged in user.
 *
 * The API section uses [APISettingViewModel], the same ViewModel used by
 * [APISettingsScreen], and the password section uses [SettingsViewModel].
 *
 * @param navController [NavController] used to navigate back to the home screen.
 * @param sharedViewModel Shared [SharedViewModel] containing the current user
 * and used to display snackbar messages.
 */
@Composable
fun SettingsScreen(navController: NavController, sharedViewModel: SharedViewModel) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current
    val apiViewModel: APISettingViewModel = viewModel(
        factory = APISettingViewModelFactory(context)
    )
    val viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(context, sharedViewModel)
    )
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            apiViewModel.onCameraAccess()
            apiViewModel.scanQR()
        } else {
            sharedViewModel.snackBarMessage("Camera Permission Needed to Scan QR")
        }
    }

    LaunchedEffect(sharedViewModel) {
        sharedViewModel.snackbarEvent.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // Hardware barcode scanner: scanning the API QR code fills in the API details,
    // the same as the camera's Scan QR, without opening the camera
    BarcodeScanEffect { code ->
        if (code.toUri().host.isNullOrEmpty()) {
            sharedViewModel.snackBarMessage("That isn't an API settings QR code")
        } else {
            apiViewModel.onQRCodeScanned(code)
            sharedViewModel.snackBarMessage("API details scanned, press Save & Connect to use them")
        }
    }

    LaunchedEffect(apiViewModel.ConnectedSuccess) {
        if (apiViewModel.ConnectedSuccess) {
            sharedViewModel.snackBarMessage("API Connected, Settings Saved")
            apiViewModel.consumeConnectedSuccess()
        }
    }

    val passwordTransformation = if (viewModel.showPassword.value) {
        VisualTransformation.None
    } else {
        PasswordVisualTransformation()
    }
    val passwordToggle = @Composable {
        IconButton(onClick = { viewModel.onShowPassword() }) {
            if (viewModel.showPassword.value) {
                Icon(
                    imageVector = Icons.Filled.VisibilityOff,
                    contentDescription = "Hide Password"
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Visibility,
                    contentDescription = "Show Password"
                )
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        TopBar(
            navController = navController,
            title = "Settings",
            snackbarHostState = snackbarHostState,
            backshow = true,
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        focusManager.clearFocus()
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "API Settings",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = apiViewModel.newurl,
                    onValueChange = apiViewModel::onUrlChange,
                    label = { Text("Server URL") },
                    singleLine = true,
                    modifier = Modifier.padding(16.dp)
                )

                OutlinedTextField(
                    value = apiViewModel.newport,
                    onValueChange = apiViewModel::onPortChange,
                    label = { Text("Port") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.padding(16.dp)
                )

                OutlinedTextField(
                    value = apiViewModel.newkey,
                    onValueChange = apiViewModel::onKeyChange,
                    label = { Text("API Key") },
                    singleLine = true,
                    modifier = Modifier.padding(16.dp)
                )

                if (apiViewModel.showErrorMessage.value) {
                    Text(
                        apiViewModel.errorMessage.value,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                } else {
                    Spacer(modifier = Modifier.height(20.dp))
                }

                Row {
                    OutlinedButton(
                        onClick = { cameraLauncher.launch(Manifest.permission.CAMERA) }
                    ) {
                        Text("Scan QR")
                    }
                    Spacer(modifier = Modifier.width(30.dp))
                    OutlinedButton(
                        onClick = {
                            focusManager.clearFocus()
                            apiViewModel.connect()
                        }
                    ) {
                        Text("Save & Connect")
                    }
                }

                HorizontalDivider(
                    Modifier.padding(horizontal = 20.dp, vertical = 30.dp),
                    DividerDefaults.Thickness,
                    MaterialTheme.colorScheme.outline
                )

                Text(
                    text = "Change Password",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "User: ${sharedViewModel.currentUser.value.lowercase().replaceFirstChar { it.uppercase() }}",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )

                OutlinedTextField(
                    value = viewModel.currentPassword,
                    onValueChange = viewModel::onCurrentPasswordChange,
                    label = { Text("Current Password") },
                    singleLine = true,
                    visualTransformation = passwordTransformation,
                    trailingIcon = passwordToggle,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.padding(16.dp)
                )

                OutlinedTextField(
                    value = viewModel.newPassword,
                    onValueChange = viewModel::onNewPasswordChange,
                    label = { Text("New Password") },
                    singleLine = true,
                    visualTransformation = passwordTransformation,
                    trailingIcon = passwordToggle,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.padding(16.dp)
                )

                OutlinedTextField(
                    value = viewModel.confirmPassword,
                    onValueChange = viewModel::onConfirmPasswordChange,
                    label = { Text("Confirm New Password") },
                    singleLine = true,
                    visualTransformation = passwordTransformation,
                    trailingIcon = passwordToggle,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.padding(16.dp)
                )

                if (viewModel.showErrorMessage.value) {
                    Text(
                        viewModel.errorMessage.value,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                } else {
                    Spacer(modifier = Modifier.height(20.dp))
                }

                OutlinedButton(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.updatePassword()
                    },
                    enabled = viewModel.updateButtonEnabled
                ) {
                    Text("Update Password")
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }

        if (apiViewModel.showScanner.value) {
            Box(modifier = Modifier.fillMaxSize()) {
                QRScanner { scannedCode ->
                    apiViewModel.onQRCodeScanned(scannedCode)
                }
                Button(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 80.dp),
                    onClick = { apiViewModel.closeScanner() }
                ) {
                    Text("Close Scanner")
                }
            }
        }
    }
}
