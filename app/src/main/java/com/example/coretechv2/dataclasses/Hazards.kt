package com.example.coretechv2.dataclasses

import androidx.compose.runtime.Composable
import com.example.coretechv2.R


enum class Hazards {

    FLAMMABLE, CORROSIVE, ENVIRONMENT, GAS, HAZARD, MARK, OXIDIZER, SKULL, CLASS_3, CLASS_52, CLASS_61, CLASS_8, LIMITED_QUANTITES;

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
            "3"-> CLASS_3
            "5.2"-> CLASS_52
            "6.1"-> CLASS_61
            "8"-> CLASS_8
            else -> MARK
        }
    }
}
}