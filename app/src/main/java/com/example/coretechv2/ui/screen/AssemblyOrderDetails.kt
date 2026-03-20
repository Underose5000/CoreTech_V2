package com.example.coretechv2.ui.screen

import android.graphics.Color.blue
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.coretechv2.factory.AssemblyOrderDetailsViewModelFactory
import com.example.coretechv2.factory.AssemblyOrdersViewModelFactory
import com.example.coretechv2.ui.component.TopBar
import com.example.coretechv2.ui.theme.scottBlue
import com.example.coretechv2.viewmodel.AssemblyOrderDetailsViewModel
import com.example.coretechv2.viewmodel.AssemblyOrdersViewModel

@Composable
fun AssemblyOrderDetails(
    navController: NavController,
    currentUser: String,
    orderNumber: String
) {
    val context = LocalContext.current
    val viewModel: AssemblyOrderDetailsViewModel = viewModel(
        factory = AssemblyOrderDetailsViewModelFactory(context)
    )
    Log.d("Assembly Details","Order Number= ${orderNumber}")
    viewModel.retrieveAssemblyDetails(orderNumber)


    Log.d("Assembly Details","${viewModel.assemblyHeader}")
    Log.d("Assembly Details","${viewModel.assemblyDetailsLines}")


    TopBar(
        navController = navController,
        title = "${viewModel.assemblyHeader.firstOrNull()?.ITEMDESCRIPTION}",
        backshow = true,
        addshow = true,
        pastshow = true,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center)
        {
            Row(modifier = Modifier
                .fillMaxWidth()
                .background(color = scottBlue)
                .padding(start = 32.dp, top = 8.dp, end = 32.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween) {
                Column(){
                    Text(
                        text = "Order Number: ${viewModel.assemblyHeader.firstOrNull()?.ORDERNUMBER}",
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Batch Size: ${viewModel.assemblyHeader.firstOrNull()?.ADDITIONALFIELD_6} ${viewModel.assemblyHeader.firstOrNull()?.ITEMUNIT}"
                    )
                }
                Column(){
                    Text(
                        text = "Item Code: ${viewModel.assemblyHeader.firstOrNull()?.ITEMCODE}",
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "No of Batches: ${viewModel.assemblyHeader.firstOrNull()?.ADDITIONALFIELD_12}"
                    )
                }
            }
            LazyColumn (
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ){
                items(viewModel.assemblyDetailsLines) { order ->
                    Row(
                        modifier = Modifier
                            .clickable {}
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        Column(
                            modifier = Modifier
                                .padding(16.dp)) {
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
                            modifier = Modifier
                                .padding(16.dp)) {
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
    }
}