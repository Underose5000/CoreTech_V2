package com.example.coretechv2.dataclasses

/**
 * AssemblyHeader additional fields used for batching.
 *
 * Kept in one place so the field numbers only need changing here if they
 * move again.
 */
object AssemblyHeaderFields {

    /**
     * "Disable Multi Batch": 'true' or 'false', the same as the line added
     * flag (AssemblyLines ADDITIONALFIELD_1). Ostendo copies it from the item
     * master when the order is created.
     *
     * - 'true': an intense mix. The order is always a single batch and the
     *   quantity added is entered for each line.
     * - 'false' or blank: the order can be split into batches. Lines are
     *   added at their planned quantity for the batches done, and only the
     *   batch number is recorded.
     */
    const val DISABLE_MULTI_BATCH = "ADDITIONALFIELD_13"

    /** The maximum batch size, used to work out the number of batches. */
    const val MAX_BATCH_SIZE = "ADDITIONALFIELD_14"

    /** The number of batches the order is split into. */
    const val NUMBER_OF_BATCHES = "ADDITIONALFIELD_12"
}
