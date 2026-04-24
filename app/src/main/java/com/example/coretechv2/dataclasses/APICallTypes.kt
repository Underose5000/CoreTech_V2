package com.example.coretechv2.dataclasses

enum class APICallTypes {
    INSERT, UPDATE, DELETE;

    fun toStringName(): String {
        return when (this) {
            INSERT -> "Insert"
            UPDATE -> "Update"
            DELETE -> "Delete"
        }
    }
}