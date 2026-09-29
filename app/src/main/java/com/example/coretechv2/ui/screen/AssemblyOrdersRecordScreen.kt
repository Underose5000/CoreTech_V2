package com.example.coretechv2.ui.screen

import android.util.Log
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.coretechv2.dataclasses.APICallTables
import com.example.coretechv2.factory.AssemblyOrdersRecordViewModelFactory
import com.example.coretechv2.ui.component.AdjustmentCard
import com.example.coretechv2.ui.component.ElongationalBreakCard
import com.example.coretechv2.ui.component.FlammabilityCard
import com.example.coretechv2.ui.component.GelTimeCard
import com.example.coretechv2.ui.component.Menu
import com.example.coretechv2.ui.component.PeakExothermCard
import com.example.coretechv2.ui.component.ResistivityCard
import com.example.coretechv2.ui.component.TopBar
import com.example.coretechv2.ui.component.ViscosityCard
import com.example.coretechv2.viewmodel.AssemblyOrdersRecordViewModel
import com.example.coretechv2.viewmodel.SharedViewModel
import kotlinx.coroutines.launch
import kotlin.collections.contains

/**
 * Displays the assembly order record lookup screen.
 *
 * This screen retrieves assembly orders from the Ostendo API and displays
 * them in a searchable, filterable list. It provides functionality for
 * selecting orders for comparison, expanding individual order details,
 * and navigating to the assembly order detail screen.
 *
 * The screen provides the following functionality:
 * - Searching assembly orders by item description, item code, order number,
 *   order date, and other enabled search fields.
 * - Filtering orders based on whether they contain tests, adjustments,
 *   or notes.
 * - Refreshing the currently loaded assembly orders.
 * - Selecting multiple orders for comparison.
 * - Opening an individual assembly order's detail screen using a long press.
 * - Expanding and collapsing order details.
 * - Displaying associated test records using their corresponding test cards.
 * - Displaying adjustment records and assembly notes.
 * - Loading additional assembly orders when requested.
 * - Displaying loading indicators while assembly order or test data is retrieved.
 * - Displaying a filter menu for configuring search and filter options.
 *
 * Assembly order data, search state, filter settings, selected orders,
 * expanded details, and loading states are managed by
 * [AssemblyOrdersRecordViewModel].
 *
 * Shared application state, including the currently selected order number
 * and item code, is managed through [SharedViewModel].
 *
 * Navigation is handled using the supplied [NavController]. Snackbar messages
 * emitted by the ViewModel are displayed through the screen's
 * [SnackbarHostState].
 *
 * @param navController Navigation controller used to navigate between the
 * assembly order lookup screen and the assembly order detail screen.
 * @param sharedViewModel Shared ViewModel containing application state used
 * by the assembly order screens.
 */
@Composable
fun AssemblyOrdersRecordLookupScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val viewModel: AssemblyOrdersRecordViewModel = viewModel(
        factory = AssemblyOrdersRecordViewModelFactory(context, sharedViewModel)
    )
    val focusManager = LocalFocusManager.current
    LaunchedEffect(Unit) {
        viewModel.clearSelectedOrderList()
        viewModel.clearShowDetail()
        if (viewModel.newEntry){
            viewModel.clearSearchField()
            viewModel.clearNewEntry()
        }
        viewModel.retrieveAssemblyOrders()

    }

    LaunchedEffect(Unit) {
        viewModel.snackbarEvent.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    TopBar(
        navController = navController,
        title = "Assembly Records",
        snackbarHostState = snackbarHostState,
        backshow = true,
        icon1 = Icons.Filled.FilterList,
        icon1Description = "Filter Records",
        icon1action = { viewModel.menuPressed() },
        icon2 = if (viewModel.compareOrders.value) {
            Icons.Filled.Check
        } else {
            Icons.AutoMirrored.Filled.CompareArrows
        },
        icon2Description = "Compare Records",
        icon2action = { viewModel.compareOrdersPressed(navController) },
        icon3 = Icons.Filled.Refresh,
        icon3Description = "Refresh Records",
        icon3action = { scope.launch {viewModel.retrieveAssemblyOrders()} },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    focusManager.clearFocus()
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        )
        {
            OutlinedTextField(
                value = viewModel.searchField,
                onValueChange = viewModel::onSearchFieldChange,
                label = { Text("Search") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        focusManager.clearFocus()
                    }
                )
            )
            if (viewModel.testLoading) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(40.dp),
                        strokeWidth = 5.dp
                    )
                    Text("Loading Assemblies", style = MaterialTheme.typography.titleMedium)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(viewModel.assemblyOrderListSearched) { order ->
                        val background = if (viewModel.selectedOrderList.contains(order.ORDERNUMBER)) {
                            MaterialTheme.colorScheme.surfaceBright
                        } else {
                            MaterialTheme.colorScheme.background
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(background)
                                .combinedClickable(
                                    enabled = order.TESTSEXISTS == 1 || order.ADJUSTSEXISTS == 1 || order.NOTESEXISTS == 1 || viewModel.compareOrders.value,
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = if (viewModel.compareOrders.value) {
                                        null
                                    } else {
                                        LocalIndication.current
                                    },
                                    onClick = {
                                        if (viewModel.compareOrders.value) {
                                            if (viewModel.selectedOrderList.contains(order.ORDERNUMBER)) {
                                                viewModel.removeSelectedOrder(order.ORDERNUMBER)
                                            } else {
                                                viewModel.addSelectedOrder(order.ORDERNUMBER)
                                            }
                                        } else {
                                            scope.launch {viewModel.retrieveTestDetail(order)}
                                            if (viewModel.showDetail.contains(order.ORDERNUMBER)) {
                                                viewModel.removeShowDetail(order.ORDERNUMBER)
                                            } else {
                                                viewModel.addShowDetail(order.ORDERNUMBER)
                                            }
                                        }
                                    },
                                    onLongClick = {
                                        if (!(order.TESTSEXISTS == 1 || order.ADJUSTSEXISTS == 1 || order.NOTESEXISTS == 1)){
                                            Log.d("Hidden","Nothing is meant to happen here")
                                        }else if (viewModel.compareOrders.value) {
                                            if (viewModel.showDetail.contains(order.ORDERNUMBER)) {
                                                viewModel.removeShowDetail(order.ORDERNUMBER)
                                            } else {
                                                viewModel.addShowDetail(order.ORDERNUMBER)
                                            }
                                        } else {
                                            viewModel.clearSelectedOrderList()
                                            viewModel.addSelectedOrder(order.ORDERNUMBER)
                                            viewModel.navToRecordDetails(navController)
                                        }
                                    }
                                )
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(3f)) {
                                Text(
                                    text = order.ITEMDESCRIPTION,
                                )
                                Text(
                                    text = "Order#: ${order.ORDERNUMBER}    Date: ${order.ORDERDATE}    Qty: ${order.ORDERQTY}",
                                )
                            }
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Tests")
                                Icon(
                                    imageVector = if (order.TESTSEXISTS == 1)
                                        Icons.Filled.CheckBox
                                    else
                                        Icons.Filled.CheckBoxOutlineBlank,
                                    contentDescription = null,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 0.dp)
                                )
                            }
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Adjusts")
                                Icon(
                                    imageVector = if (order.ADJUSTSEXISTS == 1)
                                        Icons.Filled.CheckBox
                                    else
                                        Icons.Filled.CheckBoxOutlineBlank,
                                    contentDescription = null,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 0.dp)
                                )
                            }
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Notes")
                                Icon(
                                    imageVector = if (order.NOTESEXISTS == 1)
                                        Icons.Filled.CheckBox
                                    else
                                        Icons.Filled.CheckBoxOutlineBlank,
                                    contentDescription = null,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 0.dp)
                                )
                            }
                        }
                        if (viewModel.showDetail.contains(order.ORDERNUMBER) && !viewModel.testLoaded && !viewModel.testAndAdjustments.contains(order.ORDERNUMBER)){
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(40.dp),
                                    strokeWidth = 5.dp
                                )
                                Text("Loading Test", style = MaterialTheme.typography.titleMedium)
                            }
                        } else if (viewModel.showDetail.contains(order.ORDERNUMBER)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                            ) {
                                val dividerColor = MaterialTheme.colorScheme.outlineVariant
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .drawBehind {
                                            drawLine(
                                                color = dividerColor,
                                                start = Offset(size.width, 0f),
                                                end = Offset(size.width, size.height),
                                                strokeWidth = 1.dp.toPx()
                                            )
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(MaterialTheme.colorScheme.outlineVariant)
                                    ) {
                                        Text("Tests and Adjustments", Modifier.padding(vertical = 2.dp, horizontal = 5.dp), style = MaterialTheme.typography.titleSmall)
                                    }
                                    val ordersTestAndAdjustments = viewModel.testAndAdjustments[order.ORDERNUMBER]
                                    if (!ordersTestAndAdjustments.isNullOrEmpty()) {
                                        Log.d("Assembly Details", "Test found")
                                        ordersTestAndAdjustments.forEachIndexed { index, detail ->
                                            val tests = detail[0] as SnapshotStateList<*>
                                            val adjustments = detail[1] as SnapshotStateList<*>

                                            if (!tests.isEmpty()) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                                ) {
                                                    Text("Test ${index + 1}", Modifier.padding(vertical = 2.dp, horizontal = 5.dp), style = MaterialTheme.typography.titleSmall)
                                                }
                                                Spacer(modifier = Modifier.height(1.dp))
                                                for (test in tests) {
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                    )
                                                    {
                                                        when (test) {
                                                            is APICallTables.viscosityTest -> {
                                                                ViscosityCard(test)
                                                            }

                                                            is APICallTables.gelTimeTest -> {
                                                                GelTimeCard(test)
                                                            }

                                                            is APICallTables.ElongationalBreakTest -> {
                                                                ElongationalBreakCard(test)
                                                            }

                                                            is APICallTables.FlammabilityTest -> {
                                                                FlammabilityCard(test)
                                                            }

                                                            is APICallTables.ResistivityTest -> {
                                                                ResistivityCard(test)
                                                            }

                                                            is APICallTables.peakExothermTest -> {
                                                                PeakExothermCard(test)
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            if (!adjustments.isEmpty()) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .background(MaterialTheme.colorScheme.primary)
                                                ) {
                                                    Text("Adjustment ${index + 1}", Modifier.padding(vertical = 2.dp, horizontal = 5.dp), style = MaterialTheme.typography.titleSmall)
                                                }
                                                Spacer(modifier = Modifier.height(5.dp))
                                                for (adjust in adjustments) {
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                    ) {
                                                        when (adjust) {
                                                            is APICallTables.assemblyAdjustment -> {
                                                                AdjustmentCard(adjust)
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                        }
                                    }
                                }
                                VerticalDivider(thickness = DividerDefaults.Thickness, color = MaterialTheme.colorScheme.outlineVariant)

                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(MaterialTheme.colorScheme.outlineVariant)
                                    ) {
                                        Text("Notes", Modifier.padding(vertical = 2.dp, horizontal = 5.dp), style = MaterialTheme.typography.titleSmall)
                                    }
                                    val ordersNotes = viewModel.notes[order.ORDERNUMBER]
                                    if (!ordersNotes.isNullOrEmpty()) {
                                        Log.d("Assembly Details", "Notes found")

                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                        ) {
                                            Text(text = ordersNotes, Modifier.padding(vertical = 2.dp, horizontal = 5.dp))
                                        }
                                    }
                                }
                            }
                            HorizontalDivider(Modifier, 10.dp, MaterialTheme.colorScheme.outlineVariant)
                        }

                        HorizontalDivider(Modifier, DividerDefaults.Thickness, MaterialTheme.colorScheme.outline)
                    }
                    items(1) { _ ->
                        if (viewModel.allTestLoading) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(40.dp),
                                    strokeWidth = 5.dp
                                )
                                Text("Loading Assemblies", style = MaterialTheme.typography.titleMedium)
                            }
                        } else if(viewModel.allTestLoaded){
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text("End of Records", Modifier.padding(vertical = 2.dp, horizontal = 5.dp), style = MaterialTheme.typography.titleLarge)
                            }
                        }else {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .clickable(onClick = { scope.launch { viewModel.retrieveAllAssemblyOrders() }}),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text("Load More", Modifier.padding(vertical = 2.dp, horizontal = 5.dp), style = MaterialTheme.typography.titleLarge)
                            }
                        }
                    }
                }
            }
        }
        if (viewModel.showFilterMenu) {
            Box(modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null ,
                    onClick = { viewModel.menuClose() })
            ){
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.TopEnd
            ) {
                Menu(menuList = viewModel.filtersMenuList, width = 250)
            }
        }
        }
    }
}