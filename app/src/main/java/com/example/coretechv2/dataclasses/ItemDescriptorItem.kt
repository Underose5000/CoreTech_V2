package com.example.coretechv2.dataclasses

/**
 * Represents the details and inventory information for an item.
 *
 * This data class is used to store identifying information about an item,
 * along with its stock quantities, category, type, and system identifier.
 *
 * @property code The unique item code used to identify the item.
 * @property description A description of the item.
 * @property unit The unit of measurement used for the item.
 * @property status The current status of the item.
 * @property barcode The barcode associated with the item.
 * @property category The category to which the item belongs.
 * @property onHandQty The quantity of the item currently held in stock.
 * @property supplyQty The quantity of the item currently available from supply.
 * @property demandQty The quantity of the item currently required or demanded.
 * @property availableQty The quantity of the item currently available for use.
 * @property freeQty The quantity of the item that is free or unallocated.
 * @property type The type or classification of the item.
 * @property sysID The unique system identifier for the item.
 */
data class ItemDescriptorItem(
    var code: String = "",
    var description: String = "",
    var unit: String = "",
    var status: String = "",
    var barcode: String = "",
    var category: String = "",
    var onHandQty: Double? = null,
    var supplyQty: Double? = null,
    var demandQty: Double? = null,
    var availableQty: Double? = null,
    var freeQty: Double? = null,
    var type: String = "",
    var sysID: Int? = null
)



