package com.example.coretechv2.dataclasses.assemblydataclasses

import androidx.compose.runtime.MutableState

/**
 * Represents a Gel Time test entry used for UI state and API operations.
 *
 * Stores catalyst information, test number, and time split into
 * hour, minute, and second components.
 *
 * @property catPercent Percentage of catalyst used in the test
 * @property testNumber Sequential identifier for the test
 * @property catalyst Name or type of catalyst used
 * @property hour Hour component of gel time
 * @property minute Minute component of gel time
 * @property second Second component of gel time
 * @property sysID Database unique identifier (nullable for new records)
 */
data class PeakExothermItem (
    var testNumber: String = "1",
    var catPercent: String  = "2",
    var temperature: String = "",
    var catalyst : String = "N/A",
    var hour: String = "",
    var minute: String = "",
    var second: String = "",
    var sysID: Int? = null
)

/**
 * Defines editable fields for Gel Time input handling in the UI.
 */
enum class PeakExothermField {
    HOUR, MINUTE, SECOND, CATPERCENT, CATALYST, TESTNUMBER, TEMPERATURE
}

/**
 * Checks whether a Gel Time test contains any time values entered.
 *
 * This is used to determine whether the user has started entering
 * a test before navigating away or cancelling.
 *
 * @param item Mutable state holding a [GelTimeItem]
 * @return true if at least one time field (hour/minute/second) is not blank
 */
fun peakExoHasValue(item: MutableState<PeakExothermItem>): Boolean {
    if (item.value.hour.isNotBlank() ||
        item.value.minute.isNotBlank() ||
        item.value.second.isNotBlank() ||
        item.value.temperature.isNotBlank()){
        return true
    }
    return false
}

