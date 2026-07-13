package com.example.coretechv2.dataclasses

enum class NoteTypes {

    ASSEMBLY, SALES, PURCHASE, ITEM;

    fun toStringName(): String {
        return when (this) {
            ASSEMBLY -> "Assembly"
            SALES -> "Sales"
            PURCHASE -> "Purchase"
            ITEM -> "Item"
        }
    }
}