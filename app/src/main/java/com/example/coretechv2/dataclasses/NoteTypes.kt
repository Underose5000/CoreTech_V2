package com.example.coretechv2.dataclasses

/**
 * Represents the different types of notes that can be associated with
 * records within the application.
 */
enum class NoteTypes {

    ASSEMBLY, SALES, PURCHASE, ITEM;

    /**
     * Returns a human-readable name for this note type.
     *
     * @return The display name corresponding to the note type.
     */
    fun toStringName(): String {
        return when (this) {
            ASSEMBLY -> "Assembly"
            SALES -> "Sales"
            PURCHASE -> "Purchase"
            ITEM -> "Item"
        }
    }
}