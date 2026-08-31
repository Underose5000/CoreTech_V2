package com.example.coretechv2.dataclasses

import com.example.coretechv2.viewmodel.SharedViewModel

/**
 * Contains the information required to construct and display an item label.
 *
 * This data class groups together item information, dangerous goods information,
 * label layout information, and the shared application state required when
 * generating or displaying a label.
 *
 * @property itemInfo Contains the item information retrieved from the API
 * used to populate the label.
 * @property classInfo Contains the classification information retrieved from
 * the API for the item.
 * @property labelLayout Contains the layout configuration used to determine
 * how the label elements are positioned and displayed.
 * @property dgInfo Contains dangerous goods information for the item, if
 * applicable. This value may be null when the item is not classified as
 * dangerous goods.
 * @property sharedViewModel Provides access to shared application state and
 * data required during label generation.
 * @property kitset Indicates whether the label is being generated for a
 * kitset. Defaults to false.
 */
data class LabelElements(
    val itemInfo: APICallTables.assemblyLabelItemInfo,
    val classInfo: APICallTables.assemblyLabelClassInfo,
    var labelLayout: APICallTables.assemblyLabelLayout,
    val dgInfo: APICallTables.itemDGInfo?,
    val sharedViewModel: SharedViewModel,
    var kitset: Boolean = false
)