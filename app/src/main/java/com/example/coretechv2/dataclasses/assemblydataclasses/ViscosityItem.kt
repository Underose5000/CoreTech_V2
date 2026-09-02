package com.example.coretechv2.dataclasses.assemblydataclasses

import androidx.compose.runtime.MutableState

/**
 * Represents a viscosity test entry used in UI and API operations.
 *
 * Holds all viscosity readings across different spindle speeds
 * along with metadata such as spindle type, index range, and test number.
 *
 * @property spindle Selected spindle type (e.g. A, B, C)
 * @property indexRange Selected index ratio range (e.g. 6/60, 3/30)
 * @property testNumber Sequential test identifier
 * @property vis60 Viscosity reading at 60
 * @property vis30 Viscosity reading at 30
 * @property vis12 Viscosity reading at 12
 * @property vis06 Viscosity reading at 6
 * @property vis03 Viscosity reading at 3
 * @property vis1_5 Viscosity reading at 1.5
 * @property vis0_6 Viscosity reading at 0.6
 * @property vis0_3 Viscosity reading at 0.3
 * @property sysID Database unique identifier (nullable for new records)
 */
data class ViscosityItem(
    var spindle: String = "N/A",
    var indexRange: String = "N/A",
    var testNumber: String = "1",
    var vis60: String = "",
    var vis30: String = "",
    var vis12: String = "",
    var vis06: String = "",
    var vis03: String = "",
    var vis1_5: String = "",
    var vis0_6: String = "",
    var vis0_3: String = "",
    var sysID: Int? = null
)


/**
 * Defines dropdown selection fields for viscosity configuration.
 */
enum class VisSettings {
    SPINDLE, INDEX, TESTNUMBER
}

/**
 * Defines viscosity reading input fields used in UI binding.
 */
enum class VisField {
    VIS60, VIS30, VIS12, VIS06, VIS03, VIS1_5, VIS0_6, VIS0_3
}


/**
 * Checks whether a viscosity test contains any entered values.
 *
 * This is used to determine if the user has started entering data
 * before navigating away or cancelling the screen.
 *
 * @param item Mutable state holding a [ViscosityItem]
 * @return true if at least one viscosity field is not blank
 */
fun visHasValue(item: MutableState<ViscosityItem>): Boolean {
    if (item.value.vis60.isNotBlank() ||
        item.value.vis30.isNotBlank() ||
        item.value.vis12.isNotBlank() ||
        item.value.vis06.isNotBlank() ||
        item.value.vis03.isNotBlank() ||
        item.value.vis1_5.isNotBlank() ||
        item.value.vis0_6.isNotBlank() ||
        item.value.vis0_3.isNotBlank()
    ) {
        return true
    }
    return false
}

