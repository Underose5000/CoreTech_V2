package com.example.coretechv2.dataclasses.assemblydataclasses

import androidx.compose.runtime.MutableState
import com.example.coretechv2.dataclasses.ItemDescriptorItem


data class AssemblyLinesItem (
    val ORDERNUMBER: String,
    val STEPNAME: String,
    val STEPSEQUENCE: Int,
    val LINESTATUS: String,
    val LINENUMBER: Int,
    val CODETYPE: String,
    val LINECODE: String,
    val LINEDESCRIPTION: String,
    val LINEUNIT : String,
    var ORDERQTY: String,
    val TOTALISSUEDQTY: String,
    val REMAININGQTY: String,
    val POSITIONREFERENCE: String,
    val LINENOTES: String,
    val HEADERSYSUNIQUEID: String,
    val ADDITIONALFIELD_1: String,
    val ADDITIONALFIELD_2: String,
    val ADDITIONALFIELD_3: String,
    val ADDITIONALFIELD_4: String,
    val ADDITIONALFIELD_6: String,
)


enum class AssemblyLinesField {
    ITEM, ADJUSTMENTNUMBER, QTY
}


fun assemblyLinesHasValue(item: MutableState<AdjustmentItem>): Boolean {
    if (item.value.item != null ||
        item.value.qty.isNotBlank()){
        return true
        }
    return false
}

