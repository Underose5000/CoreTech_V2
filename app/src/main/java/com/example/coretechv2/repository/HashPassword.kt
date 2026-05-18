package com.example.coretechv2.repository

import java.security.MessageDigest
import android.util.Base64

/**
 * Utility function for hashing a plain-text password using SHA-256
 * and encoding the result in Base64 format.
 *
 * This is used to securely transform user passwords before:
 * - Sending them to the API
 * - Storing or validating credentials
 *
 * Process:
 * 1. Convert password to UTF-8 byte array
 * 2. Apply SHA-256 hashing algorithm
 * 3. Encode resulting hash into Base64 string
 *
 * @param password Plain-text password input
 * @return SHA-256 hashed and Base64-encoded password string
 */
fun HashPassword(password: String): String {

    val bytes = password.toByteArray(Charsets.UTF_8)
    val digest = MessageDigest.getInstance("SHA-256")
    val hashBytes = digest.digest(bytes)

    return Base64.encodeToString(hashBytes, Base64.NO_WRAP)
}