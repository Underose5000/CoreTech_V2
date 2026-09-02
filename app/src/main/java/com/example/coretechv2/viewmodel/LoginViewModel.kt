package com.example.coretechv2.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coretechv2.dataclasses.APICallTables
import com.example.coretechv2.repository.APICall
import com.example.coretechv2.repository.DataStoreManager
import com.example.coretechv2.repository.HashPassword
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing user login and authentication.
 *
 * [LoginViewModel] stores the username and password entered by the user,
 * communicates with the API to verify the supplied credentials, and exposes
 * authentication and error states to the UI.
 *
 * Passwords are hashed using [HashPassword] before being included in the
 * authentication query.
 *
 * @param dataStoreManager Provides access to application settings and API
 * configuration required by [APICall].
 */
class LoginViewModel(private val dataStoreManager: DataStoreManager) : ViewModel() {

    private val apiCall = APICall(dataStoreManager)

    var loginSuccess by mutableStateOf(false)
        private set

    var errorMessage = mutableStateOf("")
        private set

    var showErrorMessage = mutableStateOf(false)
        private set
    var showPassword = mutableStateOf(false)
        private set

    var username by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var currentuser = mutableStateOf("")
        private set

    /**
     * Updates the username entered into the login form.
     *
     * Changing the username also hides any currently displayed login error.
     *
     * @param newValue The new username entered by the user.
     */
    fun onUsernameChange(newValue: String) {
        showErrorMessage.value = false
        username = newValue
    }

    /**
     * Updates the password entered into the login form.
     *
     * Changing the password also hides any currently displayed login error.
     *
     * @param newValue The new password entered by the user.
     */
    fun onPasswordChange(newValue: String) {
        showErrorMessage.value = false
        password = newValue
    }

    /**
     * Toggles the visibility of the password field.
     *
     * Changes [showPassword] between true and false, allowing the UI to
     * switch between displaying the password as plain text and masking it.
     */
    fun onShowPassword() {
        showPassword.value = !showPassword.value
    }

    /**
     * Consumes the successful login event.
     *
     * Resets [loginSuccess] after the UI has responded to a successful login
     * and clears the stored password from the ViewModel.
     */
    fun consumeLoginSuccess() {
        loginSuccess = false
        password = ""
    }

    /**
     * Attempts to authenticate the currently entered user credentials.
     *
     * The username is trimmed and converted to lowercase before authentication.
     * The password is hashed using [HashPassword] before being sent to the
     * database through [APICall].
     *
     * If the credentials are valid, [loginSuccess] is set to true and
     * [currentuser] is updated with the authenticated username.
     *
     * If authentication fails, [errorMessage] is populated and
     * [showErrorMessage] is set to true.
     *
     * The authentication request is performed asynchronously using
     * [viewModelScope].
     */
    fun login() {
        val hashedPassword = HashPassword(password)
        val lowercaseUsername = username.lowercase().trim()
        viewModelScope.launch {
            val passwordConfirmed: List<APICallTables.VerifyUserPassword>? = apiCall.query("SELECT * FROM VERIFY_USER_PASSWORD('$lowercaseUsername','$hashedPassword')")

            if (passwordConfirmed?.firstOrNull()?.IS_VALID == 1) {
                loginSuccess = true
                currentuser.value = lowercaseUsername
            } else {
                errorMessage.value = "Username or Password Incorrect\nPlease try again"
                showErrorMessage.value = true
            }

        }
    }
}