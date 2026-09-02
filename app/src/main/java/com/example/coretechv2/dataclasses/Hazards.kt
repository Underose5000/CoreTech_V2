package com.example.coretechv2.dataclasses

import androidx.compose.runtime.Composable
import com.example.coretechv2.R

/**
 * Represents the different hazard classifications that can be assigned
 * to an item or assembly.
 *
 * Each hazard corresponds to an image resource used when displaying
 * hazard information in the application.
 */
enum class Hazards {

    FLAMMABLE, CORROSIVE, ENVIRONMENT, GAS, HAZARD, MARK, OXIDIZER, SKULL, CLASS_3, CLASS_52, CLASS_61, CLASS_8, LIMITED_QUANTITES;

    /**
     * Returns the drawable resource associated with this hazard.
     *
     * @return the resource ID of the image representing this hazard.
     */
    @Composable
    fun hazardImage(): Int {
        return when (this) {
            FLAMMABLE -> R.drawable.flame
            CORROSIVE -> R.drawable.corrosive
            ENVIRONMENT -> R.drawable.environment
            GAS -> R.drawable.gas
            HAZARD -> R.drawable.hazard
            MARK -> R.drawable.mark
            OXIDIZER -> R.drawable.oxidizer
            SKULL -> R.drawable.skull
            CLASS_3 -> R.drawable.class_3
            CLASS_52 -> R.drawable.class_52
            CLASS_61 -> R.drawable.class_61
            CLASS_8 -> R.drawable.class_8
            LIMITED_QUANTITES -> R.drawable.limited_quantites
        }
    }


    companion object {
        /**
         * Converts a string representation of a hazard into its corresponding
         * [Hazards] enum value.
         *
         * The method accepts hazard names such as `"flame"` and `"corrosive"`,
         * as well as numerical dangerous goods classifications such as `"3"`,
         * `"5.2"`, `"6.1"`, and `"8"`.
         *
         * If the supplied value is null or does not match a recognised hazard,
         * [MARK] is returned as the default value.
         *
         * @param value the string representation of the hazard.
         * @return the corresponding [Hazards] value, or [MARK] if no match is found.
         */
        fun fromString(value: String?): Hazards {
            return when (value) {
                "flame" -> FLAMMABLE
                "corrosive" -> CORROSIVE
                "environment" -> ENVIRONMENT
                "gas" -> GAS
                "hazard" -> HAZARD
                "mark" -> MARK
                "oxidizer" -> OXIDIZER
                "skull" -> SKULL
                "3" -> CLASS_3
                "5.2" -> CLASS_52
                "6.1" -> CLASS_61
                "8" -> CLASS_8
                else -> MARK
            }
        }
    }
}