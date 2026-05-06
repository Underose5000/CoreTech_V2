package com.example.coretechv2.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.coretechv2.factory.AssemblyOrdersViewModelFactory
import com.example.coretechv2.factory.LoginViewModelFactory
import com.example.coretechv2.ui.component.TopBar
import com.example.coretechv2.viewmodel.AssemblyOrdersViewModel
import com.example.coretechv2.viewmodel.LoginViewModel
import com.example.coretechv2.viewmodel.SharedViewModel

@Composable
fun AssemblyOrdersScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val viewModel: AssemblyOrdersViewModel = viewModel(
        factory = AssemblyOrdersViewModelFactory(context)
    )
    viewModel.retrieveAssemblyOrders()
    LaunchedEffect(Unit) {
        viewModel.snackbarEvent.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    TopBar(
        navController = navController,
        title = "Assembly Orders",
        snackbarHostState = snackbarHostState,
        backshow = true,
        icon1 = Icons.Filled.Add,
        icon1Description = "Add",
        icon1action = { },
        icon2 = Icons.Filled.History,
        icon2Description = "Past Records",
        icon2action = { },
    ) { innerPadding ->
        Column(
            modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center)
        {
            OutlinedTextField(
                value = viewModel.Searchfield,
                onValueChange = viewModel::onSerachfieldChange,
                label = { Text("Search") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            LazyColumn (
                modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
            ){
                items(viewModel.AssemblyOrderListSearached) { order ->
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.getSelectedOrder(order)
                            sharedViewModel.currentOrderNumber.value = order.ORDERNUMBER
                            navController.navigate("assemblyorderdetail") }
                        .padding(16.dp)) {
                        Text(
                            text = order.ITEMDESCRIPTION,
                        )
                        Text(
                            text = "Order#: ${order.ORDERNUMBER}    Date: ${order.ORDERDATE}    Qty: ${order.ORDERQTY}",
                        )
                    }
                    HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
                }
            }
        }
    }
}