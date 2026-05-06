package com.example.coretechv2.ui.screen

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.coretechv2.dataclasses.APICallTables
import com.example.coretechv2.factory.AssemblyOrderDetailsViewModelFactory
import com.example.coretechv2.ui.component.ButtonMessage
import com.example.coretechv2.ui.component.Menu
import com.example.coretechv2.ui.component.PopupWindow
import com.example.coretechv2.ui.component.TopBar
import com.example.coretechv2.ui.theme.scottBlue
import com.example.coretechv2.viewmodel.AssemblyOrderDetailsViewModel
import com.example.coretechv2.viewmodel.SharedViewModel

@Composable
fun AssemblyOrderDetails(
    navController: NavController, sharedViewModel: SharedViewModel
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val viewModel: AssemblyOrderDetailsViewModel = viewModel(
        factory = AssemblyOrderDetailsViewModelFactory(context, sharedViewModel)
    )
    viewModel.retrieveAssemblyDetails(sharedViewModel.currentOrderNumber.value)
    sharedViewModel.currentAssemblyHeader = viewModel.assemblyHeader.firstOrNull()
    sharedViewModel.currentAssemblyLines = viewModel.assemblyDetailsLines

    LaunchedEffect(sharedViewModel) {
        sharedViewModel.snackbarEvent.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    if(viewModel.showPopupWindow.value){
        sharedViewModel.popupDetails = viewModel.popupDetails
        sharedViewModel.openPopup()
        Log.d("popup","show popup called")
        viewModel.togglePopup(viewModel.showPopupWindow)

    }
    if(viewModel.showPopupMessage.value){
        sharedViewModel.popupMessageDetails = viewModel.popupMessageDetails
        sharedViewModel.openMessagePopup()
        viewModel.togglePopup(viewModel.showPopupMessage)
    }

    TopBar(
        navController = navController,
        title = "${viewModel.assemblyHeader.firstOrNull()?.ITEMDESCRIPTION}",
        snackbarHostState = snackbarHostState,
        backshow = true,
        icon1 = Icons.Filled.Menu,
        icon1Description = "Menu",
        icon1action = { viewModel.menuPressed() },
        icon2 = Icons.Filled.Add,
        icon2Description = "Add",
        icon2action = { viewModel.addPressed() }

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = scottBlue)
                    .padding(start = 32.dp, top = 8.dp, end = 32.dp, bottom = 8.dp), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column() {
                    Text(
                        text = "Order Number: ${viewModel.assemblyHeader.firstOrNull()?.ORDERNUMBER}",
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Batch Size: ${viewModel.assemblyHeader.firstOrNull()?.ADDITIONALFIELD_6} ${viewModel.assemblyHeader.firstOrNull()?.ITEMUNIT}"
                    )
                }
                Column() {
                    Text(
                        text = "Item Code: ${viewModel.assemblyHeader.firstOrNull()?.ITEMCODE}",
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "No of Batches: ${viewModel.assemblyHeader.firstOrNull()?.ADDITIONALFIELD_12}"
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Assembly"
                    )
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(2f)
                    ) {
                        items(viewModel.assemblyDetailsLines) { order ->
                            Row(
                                modifier = Modifier
                                    .clickable {}
                                    .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = order.LINEDESCRIPTION,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight(800),
                                    )
                                    Text(
                                        text = order.LINECODE,
                                    )
                                }
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "${order.ORDERQTY} ${order.LINEUNIT}",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight(800),
                                    )
                                }
                            }
                            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
                        }
                    }
                }
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Tests and Adjustments"
                    )
                    LazyColumn() {
                        items(viewModel.assemblyDetailsLines) { tests ->
                        }
                    }
                }
            }
        }
        if (viewModel.showActionMenu) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Menu(viewModel.actionMenuList)
                }
            }
            if (viewModel.showAddMenu) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Menu(viewModel.addMenuList)
                }
            }
    }
}


@SuppressLint("ViewModelConstructorInComposable")
@Preview(device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun AssemblyOrderDetailsPreview() {

    val navController = rememberNavController()
    val sharedViewModel: SharedViewModel = viewModel()

    // Mock data
    sharedViewModel.currentOrderNumber.value = "A4975"
    sharedViewModel.currentUser.value = "Steve"


    Box {
        AssemblyOrderDetails(navController, sharedViewModel)
        if (sharedViewModel.showPopup.value) {
            PopupWindow(sharedViewModel.popupDetails)
        }

        if (sharedViewModel.showMessagePopup.value) {
            ButtonMessage(sharedViewModel.popupMessageDetails)
        }
    }
}