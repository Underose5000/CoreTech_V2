package com.example.coretechv2.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coretechv2.dataclasses.APICallTables
import com.example.coretechv2.repository.APICall
import com.example.coretechv2.repository.DataStoreManager
import com.example.coretechv2.repository.HashPassword
import kotlinx.coroutines.launch



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

    fun onUsernameChange(newValue: String){
        showErrorMessage.value = false
        username = newValue
    }

    fun onPasswordChange(newValue: String){
        showErrorMessage.value = false
        password = newValue
    }
    fun onShowPassword(){
        showPassword.value = !showPassword.value
    }

    fun consumeLoginSuccess(){
        loginSuccess = false
        password = ""
    }

    fun login(){
        val hashedPassword = HashPassword(password)
        val lowercaseUsername = username.lowercase().trim()
        viewModelScope.launch {
            val passwordConfirmed : List<APICallTables.VerifyUserPassword>? = apiCall.query("SELECT * FROM VERIFY_USER_PASSWORD('$lowercaseUsername','$hashedPassword')")

            if (passwordConfirmed?.firstOrNull()?.IS_VALID == 1){
                loginSuccess = true
                currentuser.value = lowercaseUsername
            } else{
                errorMessage.value = "Username or Password Incorrect\nPlease try again"
                showErrorMessage.value = true
            }

        }
    }
}