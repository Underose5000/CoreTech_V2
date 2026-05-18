package com.example.coretechv2.dataclasses

data class ItemDescriptorItem (
    var code: String = "",
    var description: String  = "",
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
    var sysID : Int? = null
)



