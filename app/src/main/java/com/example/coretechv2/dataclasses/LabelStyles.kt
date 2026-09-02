package com.example.coretechv2.dataclasses

import androidx.compose.runtime.Composable
import com.example.coretechv2.printlayouts.SimpleLabel
import com.example.coretechv2.printlayouts.ThreeSectionLabel
import com.example.coretechv2.printlayouts.TuffStickThreeSectionLabel
import com.example.coretechv2.printlayouts.TuffStickTwoSectionLabel
import com.example.coretechv2.printlayouts.TwoSectionLabel

/**
 * Defines the available label styles used by the application.
 *
 * Each label style determines the layout used to display the label,
 * whether the product is packaged in a tin, and which printer is used
 * to print the label.
 */
enum class LabelStyles {

    ML250, ML500, ML500PL, L1, L2, L4, L20, L200, BULK, BOX, TUFFL1, TUFFL4, TUFFL20, TUFFL200, ERROR;

    @Composable
    fun FunctionCall(labelElement: LabelElements) {
        when (this) {
            ML250 -> {
                ThreeSectionLabel(labelElement)
            }

            ML500 -> {
                ThreeSectionLabel(labelElement)
            }

            ML500PL -> {
                TwoSectionLabel(labelElement)
            }

            L1 -> {
                ThreeSectionLabel(labelElement)
            }

            L2 -> {
                ThreeSectionLabel(labelElement)
            }

            L4 -> {
                TwoSectionLabel(labelElement)
            }

            L20 -> {
                ThreeSectionLabel(labelElement)
            }

            L200 -> {
                ThreeSectionLabel(labelElement)
            }

            BULK -> {
                SimpleLabel(labelElement)
            }

            BOX -> {
                SimpleLabel(labelElement)
            }

            TUFFL1 -> {
                TuffStickThreeSectionLabel(labelElement)
            }

            TUFFL4 -> {
                TuffStickTwoSectionLabel(labelElement)
            }

            TUFFL20 -> {
                TuffStickThreeSectionLabel(labelElement)
            }

            TUFFL200 -> {
                TuffStickThreeSectionLabel(labelElement)
            }

            else -> {}
        }
    }

    fun ProductIsTin(): Boolean {
        return when (this) {
            ML250 -> true
            ML500 -> true
            ML500PL -> true
            L1 -> true
            L2 -> true
            L4 -> true
            TUFFL1 -> true
            TUFFL4 -> true
            else -> false
        }
    }

    fun printerSection(): String {
        return when (this) {
            ML250 -> "epsonCWC6510"
            ML500 -> "epsonCWC6510"
            ML500PL -> "epsonCWC6510"
            L1 -> "epsonCWC6510"
            L2 -> "epsonCWC6510"
            L4 -> "epsonCWC6510"
            L20 -> "epsonCWC6510"
            L200 -> "epsonCWC6510"
            BULK -> "brotherQL1110"
            BOX -> "brotherQL1110"
            TUFFL1 -> "epsonCWC6510"
            TUFFL4 -> "epsonCWC6510"
            TUFFL20 -> "epsonCWC6510"
            TUFFL200 -> "epsonCWC6510"
            else -> "N/A"
        }
    }


    companion object {
        fun fromLabelSize(value: String?): LabelStyles {
            return when (value) {
                "4" -> L4
                "500PL" -> ML500PL
                "250" -> ML250
                "500" -> ML500
                "1" -> L1
                "2" -> L2
                "Bulk" -> BULK
                else -> ERROR
            }
        }
    }
}