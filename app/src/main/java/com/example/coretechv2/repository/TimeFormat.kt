package com.example.coretechv2.repository

import android.util.Log

/**
 * Converts separate hour, minute, and second strings into a formatted time string.
 *
 * Accepts numeric string values for hours, minutes, and seconds, normalises
 * overflow values (e.g. 90 seconds becomes 1 minute 30 seconds), and returns
 * the result in H:MM:SS format.
 *
 * Examples:
 * - toTimeFormatHMMSS("1", "2", "3") -> "1:02:03"
 * - toTimeFormatHMMSS("0", "61", "75") -> "1:02:15"
 *
 * @param H Hour value as a string.
 * @param M Minute value as a string.
 * @param S Second value as a string.
 * @return A formatted H:MM:SS string, or "Error" if any input is invalid.
 */
fun toTimeFormatHMMSS(H: String, M: String, S: String): String {
    if (!validTime(H) || !validTime(M) || !validTime(S)) return "Error"

    var hours = H.toIntOrNull() ?: 0
    var minutes = M.toIntOrNull() ?: 0
    var seconds = S.toIntOrNull() ?: 0

    minutes += seconds / 60
    seconds %= 60

    hours += minutes / 60
    minutes %= 60

    return "%d:%02d:%02d".format(hours, minutes, seconds)
}

/**
 * Validates whether a string contains only numeric characters.
 *
 * Empty strings are considered valid due to the regex used.
 *
 * Examples:
 * - validTime("123") -> true
 * - validTime("") -> true
 * - validTime("12a") -> false
 *
 * @param t The string to validate.
 * @return True if the string contains only digits, otherwise false.
 */
fun validTime(t: String): Boolean {
    Log.d("Time","Time = " + t)
    return t.matches(Regex("^\\d*\$"))
}

/**
 * Splits a formatted H:MM:SS time string into its individual components.
 *
 * The input string must contain exactly 3 colon-separated values.
 *
 * Examples:
 * - fromTimeFormatHMMSS("1:02:03")
 *      -> Triple("1", "02", "03")
 *
 * - fromTimeFormatHMMSS("invalid")
 *      -> Triple("Error", "Error", "Error")
 *
 * @param time A time string in H:MM:SS format.
 * @return A Triple containing hour, minute, and second strings,
 *         or Triple("Error","Error","Error") if the format is invalid.
 */
fun fromTimeFormatHMMSS(time: String) : Triple<String, String, String>{
    val timesplit = time.split(":")
    if (timesplit.size == 3){
        return Triple(timesplit[0],timesplit[1],timesplit[2])
    }
    return Triple("Error","Error","Error")
}