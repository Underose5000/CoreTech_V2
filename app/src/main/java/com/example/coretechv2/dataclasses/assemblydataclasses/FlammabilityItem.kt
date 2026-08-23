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
data class FlammabilityItem (
    var daysSet: String  = "",
    var testNumber: String = "1",
    var burnLength : String = "",
    var hour: String = "",
    var minute: String = "",
    var second: String = "",
    var sysID: Int? = null
)

/**
 * Defines editable fields for Gel Time input handling in the UI.
 */
enum class FlammabilityField {
    HOUR, MINUTE, SECOND, DAYSSET, BURNLENGTH, TESTNUMBER
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
fun flameHasValue(item: MutableState<FlammabilityItem>): Boolean {
    if (item.value.hour.isNotBlank() ||
        item.value.minute.isNotBlank() ||
        item.value.second.isNotBlank() ||
        item.value.burnLength.isNotBlank() ||
        item.value.daysSet.isNotBlank()){
        return true
    }
    return false
}

