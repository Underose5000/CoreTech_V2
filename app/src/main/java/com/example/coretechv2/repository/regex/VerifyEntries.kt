package com.example.coretechv2.repository.regex

import kotlin.text.all

fun verifyVisReading(value: String): Double? {
    val regex = Regex("""^\d+(\.\d)?$""")
    var newValue = 0.0
    if (regex.matches(value)) {
            newValue = value.toDouble()
            return newValue
    }
    if (value.all{it.isDigit()}){
        newValue = "${value}.0".toDouble()
        return newValue
    }
    return null
}