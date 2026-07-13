package com.example.coretechv2.dataclasses

import androidx.compose.runtime.Composable
import com.example.coretechv2.viewmodel.SharedViewModel

data class LabelElements(
    val itemInfo: APICallTables.assemblyLabelItemInfo,
    val classInfo: APICallTables.assemblyLabelClassInfo,
    var labelLayout: APICallTables.assemblyLabelLayout,
    val dgInfo: APICallTables.assemblyLabelDGInfo?,
    val sharedViewModel: SharedViewModel
)