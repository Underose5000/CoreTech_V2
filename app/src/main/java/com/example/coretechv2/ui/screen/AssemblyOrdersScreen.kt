package com.example.coretechv2.ui.screen

import android.content.res.Configuration
import android.util.Log
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.coretechv2.factory.AssemblyOrderDetailsViewModelFactory
import com.example.coretechv2.factory.AssemblyOrdersViewModelFactory
import com.example.coretechv2.ui.component.OutlinedStyleButton
import com.example.coretechv2.ui.component.OutlinedStyleDoubleNumberField
import com.example.coretechv2.ui.component.OutlinedStyleTextAndButtonField
import com.example.coretechv2.ui.component.PopupWindow
import com.example.coretechv2.ui.component.SearchResultBox
import com.example.coretechv2.ui.component.TopBar
import com.example.coretechv2.viewmodel.AssemblyOrderDetailsViewModel
import com.example.coretechv2.viewmodel.AssemblyOrdersViewModel
import com.example.coretechv2.viewmodel.SharedViewModel

/**
 * Displays the assembly orders screen.
 * This screen retrieves and displays a searchable list of assembly orders.
 * Users can:
 *      Search for existing assembly orders
 *      Select an assembly order to view its details
 *      Add a new assembly order
 *      View past assembly orders
 *      Refresh the assembly order list
 *
 * The screen also responds to navigation events to reload the order list
 * when returning from an assembly order details screen.
 *
 * @param navController [NavController] used to navigate between assembly
 * order screens.
 * @param sharedViewModel Shared [SharedViewModel] used to maintain state
 * shared between screens, including the currently selected order.
 */
@Composable
fun AssemblyOrdersScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val viewModel: AssemblyOrdersViewModel = viewModel(
        factory = AssemblyOrdersViewModelFactory(context, sharedViewModel)
    )
    val focusManager = LocalFocusManager.current
    val isLandscape =
        configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val screenWidthDp = configuration.screenWidthDp
    val isTablet = screenWidthDp >= 600
    LaunchedEffect(Unit) {
        viewModel.setAllAssemblyOrders()
    }
    LaunchedEffect(Unit) {
        viewModel.snackbarEvent.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }
    LaunchedEffect(viewModel.AssemblyOrdersList) {
        //viewModel.retrieveAllAssemblyOrders()
        viewModel.retrieveItems()
    }

    LaunchedEffect(sharedViewModel.navBack.value) {
        if (sharedViewModel.navBack.value) {
            viewModel.reload()
            sharedViewModel.navBack.value = false
        }
    }


    TopBar(
        navController = navController,
        title = "Assembly Orders",
        snackbarHostState = snackbarHostState,
        backshow = true,
        icon1 = Icons.Filled.Add,
        icon1Description = "Add",
        icon1action = { viewModel.addOrdersPressed(navController) },
        icon2 = Icons.Filled.FilterList,
        icon2Description = "Past Records",
        icon2action = { viewModel.pastOrdersPressed() },
        icon3 = Icons.Filled.Refresh,
        icon3Description = "Refresh Records",
        icon3action = { viewModel.reload() },
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
                value = viewModel.Searchfield,
                onValueChange = viewModel::onSearchFieldChange,
                label = { Text("Search") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(viewModel.AssemblyOrderListSearached) { order ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.getSelectedOrder(order)
                                sharedViewModel.currentOrderNumber.value = order.ORDERNUMBER
                                sharedViewModel.currentItemCode.value = order.ITEMCODE
                                navController.navigate("assemblyorderdetail")
                            }
                            .padding(16.dp)) {
                        Text(
                            text = order.ITEMDESCRIPTION,
                        )
                        Text(
                            text = "Order#: ${order.ORDERNUMBER}    Date: ${order.ORDERDATE}    Qty: ${order.ORDERQTY}",
                        )
                    }
                    HorizontalDivider(Modifier, DividerDefaults.Thickness, MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}

/**
 * Displays the form used to create a new assembly order.
 * The screen allows the user to:
 *      Search for and select an item
 *      Select an assembly version from the available BOM versions
 *      Enter the required assembly quantity
 *      Cancel the operation
 *      Submit the new assembly order
 *
 * The screen retrieves BOM information after an item is selected and
 * displays the available assembly versions for that item.
 *
 * @param navController [NavController] used to navigate between screens.
 * @param sharedViewModel Shared [SharedViewModel] containing the currently
 * selected item and other shared application state.
 * @param viewModel [AssemblyOrdersViewModel] responsible for managing the
 * assembly order creation state, item search, BOM information, validation,
 * and submission.
 */
@Composable
fun AddAssemblyOrderScreen(navController: NavController, sharedViewModel: SharedViewModel, viewModel: AssemblyOrdersViewModel) {
    val focusManager = LocalFocusManager.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(Unit) {
        viewModel.clearFields()
    }

    LaunchedEffect(sharedViewModel.currentItem.value) {
        viewModel.closeSearchBoxs()
        viewModel.retrieveBOMInfo()
        focusManager.clearFocus()
    }

    LaunchedEffect(currentRoute) {
        if (navController.currentDestination?.route != "assemblyorders") {
            viewModel.onAddCancel()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
            }) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Add Assembly Order",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(text = "Item")
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedStyleTextAndButtonField(
                    value = viewModel.addSearchField,
                    onValueChange = viewModel::onAddSearchFieldChange,
                    icon = Icons.Filled.Search,
                    onClick = { viewModel.openItemList() },
                    modifier = Modifier.weight(6f),
                )
            }
        }
        Box() {
            if (viewModel.showSearchBox) {
                SearchResultBox(viewModel.itemListSearched.toList(), sharedViewModel)
            }

        }
        Row(
            modifier = Modifier.weight(2f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Version")
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedStyleButton(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 20.dp),
                        text = viewModel.assemblyVersion,
                        onClick = { viewModel.onVersionPressedOpen() }
                    )
                    DropdownMenu(
                        expanded = viewModel.versionPressed,
                        onDismissRequest = { viewModel.onVersionPressedClose() }
                    ) {
                        if (viewModel.assemblyBOMHeaderList.isEmpty()) {
                            DropdownMenuItem(
                                onClick = {
                                    viewModel.onVersionPressedClose()
                                },
                                text = { Text("No Version found") })
                        } else {
                            for (x in 0 until viewModel.assemblyBOMHeaderList.size) {
                                DropdownMenuItem(
                                    onClick = {
                                        viewModel.onAssemblyVersion(viewModel.assemblyBOMHeaderList[x])
                                        viewModel.onVersionPressedClose()
                                    },
                                    text = { Text(viewModel.assemblyBOMHeaderList[x]!!.ASSEMBLYVERSION) })
                            }
                        }
                    }
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (sharedViewModel.currentItem.value.unit == "") {
                        "Qty"
                    } else {
                        "Qty (${sharedViewModel.currentItem.value.unit})"
                    }
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedStyleDoubleNumberField(
                        modifier = Modifier.weight(1f),
                        value = viewModel.assemblyQty,
                        onValueChange = { newValue -> viewModel.onAssemblyqty(newValue) }
                    )
                }
            }
        }

        Row(
            modifier = Modifier.weight(2f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Button(
                modifier = Modifier
                    .weight(3f)
                    .padding(horizontal = 20.dp, vertical = 40.dp),
                onClick = { viewModel.onAddCancel() }
            ) { Text(text = "Cancel") }
            Button(
                modifier = Modifier
                    .weight(3f)
                    .padding(horizontal = 20.dp, vertical = 40.dp),
                onClick = { viewModel.onAddSave(navController) },
                enabled = viewModel.submitButtonEnabled
            ) { Text(text = "Submit") }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
    if (viewModel.openItemList.value) {
        PopupWindow(viewModel.popupDetails)
    }
}