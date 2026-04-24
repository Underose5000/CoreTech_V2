package com.example.coretechv2.dataclasses

import android.util.Log
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector

data class ViscosityItem (
    var spindle: String = "N/A",
    var indexRange: String  = "N/A",
    var testNumber: String = "1",
    val vis60: String = "",
    val vis30: String = "",
    val vis12: String = "",
    val vis06: String = "",
    val vis03: String = "",
    val vis1_5: String = "",
    val vis0_6: String = "",
    val vis0_3: String = "",
)

enum class VisSettings{
    SPINDLE, INDEX, TESTNUMBER
}
enum class VisField {
    VIS60, VIS30, VIS12, VIS06, VIS03, VIS1_5, VIS0_6, VIS0_3
}


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
fun visHasValue(item: MutableState<ViscosityItem>): Boolean {
    Log.d("Vis Reading (Value Check)", item.value.vis60+item.value.vis30)
    if (item.value.vis60.isNotBlank() or
        item.value.vis30.isNotBlank() or
        item.value.vis12.isNotBlank() or
        item.value.vis06.isNotBlank() or
        item.value.vis03.isNotBlank() or
        item.value.vis1_5.isNotBlank() or
        item.value.vis0_6.isNotBlank() or
        item.value.vis0_3.isNotBlank()){
        return true
    }
    return false
}

