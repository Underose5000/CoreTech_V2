package com.example.coretechv2.dataclasses

/**
 * Represents supported database/API operation types.
 *
 * Used throughout the application to determine which type of
 * backend action should be performed, such as:
 * - Creating new records
 * - Updating existing records
 * - Deleting records
 *
 * Commonly referenced by ViewModels when building SQL queries
 * or determining save behaviour.
 */
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