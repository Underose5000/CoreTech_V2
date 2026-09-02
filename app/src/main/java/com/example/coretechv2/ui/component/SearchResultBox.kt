package com.example.coretechv2.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.coretechv2.dataclasses.APICallTables
import com.example.coretechv2.dataclasses.ItemDescriptorItem
import com.example.coretechv2.viewmodel.SharedViewModel

/**
 * Displays a scrollable list of search results and allows the user to
 * select an item from the results.
 *
 * The displayed results are determined by the type of the first element
 * in [searchedList]. Currently, [APICallTables.ItemDescriptor] results
 * are supported. Selecting an item creates an [ItemDescriptorItem] from
 * the selected result and stores it in the supplied [SharedViewModel].
 *
 * If [searchedList] is empty, no search results are displayed.
 *
 * @param searchedList The list of search results to display. The list is
 * expected to contain objects of a supported search result type.
 * @param sharedViewModel The [SharedViewModel] used to store the item
 * selected by the user.
 */
@Composable
fun SearchResultBox(searchedList: List<Any>, sharedViewModel: SharedViewModel) {
    Column(
        modifier = Modifier
            .width(600.dp)
            .background(Color.White)
            .padding(horizontal = 8.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            when (searchedList.firstOrNull()) {

                is APICallTables.ItemDescriptor -> {
                    items(searchedList.filterIsInstance<APICallTables.ItemDescriptor>()) { item ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    sharedViewModel.currentItem.value = ItemDescriptorItem(
                                        code = item.CODE,
                                        description = item.DESCRIPTION,
                                        unit = item.UNIT,
                                        status = item.STATUS,
                                        barcode = item.BARCODE,
                                        category = item.CATEGORY,
                                        onHandQty = item.ONHANDQTY,
                                        supplyQty = item.SUPPLYQTY,
                                        demandQty = item.DEMANDQTY,
                                        availableQty = item.AVAILABLEQTY,
                                        freeQty = item.FREEQTY,
                                        type = item.TYPE,
                                        sysID = item.SYSUNIQUEID
                                    )
                                }
                                .padding(16.dp)) {
                            Text(
                                text = item.DESCRIPTION,
                            )
                            Text(
                                text = "Order#: ${item.CODE}    Type: ${item.TYPE}",
                            )
                        }
                    }
                }
            }

        }

    }
}