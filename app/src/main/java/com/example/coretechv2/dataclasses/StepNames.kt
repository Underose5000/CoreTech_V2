package com.example.coretechv2.dataclasses

/**
 * Represents the different steps that can be performed during a production
 * or assembly process.
 */
enum class StepNames {

    ASSEMBLY, DOWNFILL, PACKAGING, RECOVERY, SAMOSPROVIDED, MIXA, MIXB, MIXC;

    /**
     * Returns a human-readable name for this step.
     *
     * @return The display name corresponding to the step.
     */
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