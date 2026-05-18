package com.example.coretechv2.dataclasses.assemblytests

import androidx.compose.runtime.MutableState
import com.example.coretechv2.dataclasses.ItemDescriptorItem


data class AdjustmentItem (
    var item: ItemDescriptorItem? = null,
    var adjustmentNumber: String = "1",
    var qty : String = "",
    var sysID: Int? = null
)

enum class adjustmentField {
    ITEM, ADJUSTMENTNUMBER, QTY
}


fun adjustmentHasValue(item: MutableState<AdjustmentItem>): Boolean {
    if (item.value.item != null ||
        item.value.qty.isNotBlank()){
        return true
        }
    return false
}

