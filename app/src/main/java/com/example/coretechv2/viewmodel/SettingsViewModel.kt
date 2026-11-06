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
 * ViewModel responsible for the user settings that are not API related,
 * currently changing the logged in user's password.
 *
 * Passwords are hashed using [HashPassword] before being sent to the
 * database. The current password is checked with `VERIFY_USER_PASSWORD`,
 * the new password is saved with the `UPDATE_USER_PASSWORD` procedure and
 * the change is then confirmed by verifying the new password.
 *
 * @param dataStoreManager Provides access to application settings and API
 * configuration required by [APICall].
 * @property sharedViewModel Provides the current user and snackbar messages.
 */
class SettingsViewModel(private val dataStoreManager: DataStoreManager, var sharedViewModel: SharedViewModel) : ViewModel() {

    private val apiCall = APICall(dataStoreManager)

    var currentPassword by mutableStateOf("")
        private set

    var newPassword by mutableStateOf("")
        private set

    var confirmPassword by mutableStateOf("")
        private set

    var showPassword = mutableStateOf(false)
        private set

    var errorMessage = mutableStateOf("")
        private set

    var showErrorMessage = mutableStateOf(false)
        private set

    var updateButtonEnabled by mutableStateOf(true)
        private set

    /**
     * Updates the current password field and hides any displayed error.
     *
     * @param newValue The current password entered by the user.
     */
    fun onCurrentPasswordChange(newValue: String) {
        showErrorMessage.value = false
        currentPassword = newValue
    }

    /**
     * Updates the new password field and hides any displayed error.
     *
     * @param newValue The new password entered by the user.
     */
    fun onNewPasswordChange(newValue: String) {
        showErrorMessage.value = false
        newPassword = newValue
    }

    /**
     * Updates the confirm password field and hides any displayed error.
     *
     * @param newValue The new password re-entered by the user.
     */
    fun onConfirmPasswordChange(newValue: String) {
        showErrorMessage.value = false
        confirmPassword = newValue
    }

    /**
     * Toggles the visibility of the password fields.
     */
    fun onShowPassword() {
        showPassword.value = !showPassword.value
    }

    /**
     * Changes the password of the currently logged in user.
     *
     * The entered values are validated before the current password is
     * checked against the database. The new password is then saved and
     * verified. The password fields are cleared once the change is confirmed,
     * otherwise an error message is displayed.
     */
    fun updatePassword() {
        val username = sharedViewModel.currentUser.value.lowercase().trim()

        if (username.isEmpty()) {
            showError("No User Logged In")
            return
        }
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showError("Please Fill In All Password Fields")
            return
        }
        if (newPassword != confirmPassword) {
            showError("New Passwords Do Not Match")
            return
        }
        if (newPassword == currentPassword) {
            showError("New Password Must Be Different\nto the Current Password")
            return
        }

        val currentHash = HashPassword(currentPassword)
        val newHash = HashPassword(newPassword)
        updateButtonEnabled = false

        viewModelScope.launch {
            if (!verifyPassword(username, currentHash)) {
                showError("Current Password Incorrect")
                updateButtonEnabled = true
                return@launch
            }

            val response = apiCall.insertUpdateDelete("EXECUTE PROCEDURE UPDATE_USER_PASSWORD('$username', '$currentHash', '$newHash')")

            if (response == "200 OK" && verifyPassword(username, newHash)) {
                currentPassword = ""
                newPassword = ""
                confirmPassword = ""
                sharedViewModel.snackBarMessage("Password Updated")
            } else {
                showError("Password Update Failed\nPlease Try Again")
            }
            updateButtonEnabled = true
        }
    }

    /**
     * Checks a hashed password against the database.
     *
     * @param username The lowercase username to check.
     * @param hashedPassword The password hashed with [HashPassword].
     * @return True if the password is valid for the user.
     */
    private suspend fun verifyPassword(username: String, hashedPassword: String): Boolean {
        val passwordConfirmed: List<APICallTables.VerifyUserPassword>? = apiCall.query("SELECT * FROM VERIFY_USER_PASSWORD('$username','$hashedPassword')")
        return passwordConfirmed?.firstOrNull()?.IS_VALID == 1
    }

    /**
     * Displays an error message below the password fields.
     *
     * @param message The error message to display.
     */
    private fun showError(message: String) {
        errorMessage.value = message
        showErrorMessage.value = true
    }
}
