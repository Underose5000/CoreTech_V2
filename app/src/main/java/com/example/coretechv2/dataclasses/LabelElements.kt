package com.example.coretechv2.dataclasses

import com.example.coretechv2.viewmodel.SharedViewModel

data class LabelElements(
    val itemInfo: APICallTables.assemblyLabelItemInfo,
    val classInfo: APICallTables.assemblyLabelClassInfo,
    var labelLayout: APICallTables.assemblyLabelLayout,
    val dgInfo: APICallTables.itemDGInfo?,
    val sharedViewModel: SharedViewModel,
    var kitset: Boolean = false
)