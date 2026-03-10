package com.example.coretechv2.repository

import java.security.MessageDigest
import android.util.Base64

fun HashPassword(password: String): String {

    val bytes = password.toByteArray(Charsets.UTF_8)
    val digest = MessageDigest.getInstance("SHA-256")
    val hashBytes = digest.digest(bytes)

    return Base64.encodeToString(hashBytes, Base64.NO_WRAP)
}