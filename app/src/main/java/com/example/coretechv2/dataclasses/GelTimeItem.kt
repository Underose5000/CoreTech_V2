package com.example.coretechv2.dataclasses

import androidx.compose.runtime.MutableState

data class GelTimeItem (
    var catPercent: String  = "2",
    var testNumber: String = "1",
    var catalyst : String = "",
    val hour: String = "",
    val minute: String = "",
    val second: String = "",
)

enum class GelField {
    HOUR, MINUTE, SECOND, CATPERCENT, CATALYST, TESTNUMBER
}


fun verifyGelReading(value: String): Double? {
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
fun gelHasValue(item: MutableState<GelTimeItem>): Boolean {
    if (item.value.hour.isNotBlank() or
        item.value.minute.isNotBlank() or
        item.value.second.isNotBlank()){
        return true
    }
    return false
}

