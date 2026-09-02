package com.example.coretechv2.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coretechv2.R
import com.example.coretechv2.factory.LoginViewModelFactory
import com.example.coretechv2.viewmodel.LoginViewModel

/**
 * Displays the login screen for the application.
 *
 * Creates and manages a [LoginViewModel] responsible for handling username and
 * password input, password visibility, login attempts, and login errors.
 * The screen also provides keyboard navigation between the username and password
 * fields and hides the keyboard when a login is submitted through the keyboard.
 *
 * When the login is successful, [onLoginSuccess] is invoked. If the view model
 * detects that a different user has been entered, [ondifferentUser] is invoked
 * with the updated username.
 *
 * The company logo is selected according to the system's current light or dark
 * theme.
 *
 * @param currentuser Username currently associated with the application session.
 * @param onLoginSuccess Callback invoked when the login is successfully completed.
 * @param ondifferentUser Callback invoked when the view model detects a different
 * user, receiving the updated username as its parameter.
 */
@Composable
fun LoginScreen(
    currentuser: String,
    onLoginSuccess: () -> Unit,
    ondifferentUser: (String) -> Unit
) {
    val context = LocalContext.current
    val passwordFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val viewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(context)
    )

    if (viewModel.currentuser.value != currentuser) {

        ondifferentUser(viewModel.currentuser.value)
    }
    if (viewModel.loginSuccess) {

        onLoginSuccess()
        viewModel.consumeLoginSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row {
            Spacer(modifier = Modifier.weight(1f))
            Image(
                painter = if (isSystemInDarkTheme()) {
                    painterResource(id = R.drawable.company_logo_dark)
                } else {
                    painterResource(id = R.drawable.company_logo_light)
                },
                contentDescription = null,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(30.dp))
        OutlinedTextField(
            value = viewModel.username,
            onValueChange = viewModel::onUsernameChange,
            label = { Text("Username") },
            modifier = Modifier.padding(16.dp),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    passwordFocusRequester.requestFocus()
                }
            )
        )

        OutlinedTextField(
            value = viewModel.password,
            onValueChange = viewModel::onPasswordChange,
            label = { Text("Password") },
            modifier = Modifier
                .padding(16.dp)
                .focusRequester(passwordFocusRequester),
            visualTransformation = if (viewModel.showPassword.value) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
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
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    viewModel.login()
                    keyboardController?.hide()
                }
            )
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
            onClick = { viewModel.login() }
        ) {
            Text("Login")
        }
    }

}

