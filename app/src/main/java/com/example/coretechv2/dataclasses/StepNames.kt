package com.example.coretechv2.dataclasses

import androidx.compose.runtime.Composable
import com.example.coretechv2.dataclasses.APICallTypes.DELETE
import com.example.coretechv2.dataclasses.APICallTypes.INSERT
import com.example.coretechv2.dataclasses.APICallTypes.UPDATE
import com.example.coretechv2.printlayouts.SimpleLabel
import com.example.coretechv2.printlayouts.ThreeSectionLabel
import com.example.coretechv2.printlayouts.TuffStickThreeSectionLabel
import com.example.coretechv2.printlayouts.TuffStickTwoSectionLabel
import com.example.coretechv2.printlayouts.TwoSectionLabel

enum class StepNames {

    ASSEMBLY,DOWNFILL,PACKAGING,RECOVERY,SAMOSPROVIDED,MIXA,MIXB,MIXC;

    fun toStringName(): String {
        return when (this) {
            ASSEMBLY -> "Assembly"
            DOWNFILL -> "Downfill"
            PACKAGING -> "Packaging"
            RECOVERY -> "Recovery"
            SAMOSPROVIDED -> "Samos Provided"
            MIXA -> "Mix A"
            MIXB -> "Mix B"
            MIXC -> "Mix C"
        }
    }

}