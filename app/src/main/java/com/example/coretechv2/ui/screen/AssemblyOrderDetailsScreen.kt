package com.example.coretechv2.ui.screen

import android.R
import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.coretechv2.dataclasses.APICallTables
import com.example.coretechv2.dataclasses.APICallTypes
import com.example.coretechv2.dataclasses.LabelStyles
import com.example.coretechv2.dataclasses.NoteTypes
import com.example.coretechv2.factory.AssemblyOrderDetailsViewModelFactory
import com.example.coretechv2.ui.component.AdjustmentCard
import com.example.coretechv2.ui.component.ButtonMessage
import com.example.coretechv2.ui.component.GelTimeCard
import com.example.coretechv2.ui.component.LabelPreview
import com.example.coretechv2.ui.component.Menu
import com.example.coretechv2.ui.component.OutlinedStyleDoubleNumberField
import com.example.coretechv2.ui.component.PopupWindow
import com.example.coretechv2.ui.component.TopBar
import com.example.coretechv2.ui.component.ViscosityCard
import com.example.coretechv2.ui.screen.assemblytestsandadjustments.AdjustmentsScreen
import com.example.coretechv2.ui.screen.assemblytestsandadjustments.GelTimeScreen
import com.example.coretechv2.ui.screen.assemblytestsandadjustments.ViscosityScreen
import com.example.coretechv2.ui.theme.fontLightTheme
import com.example.coretechv2.ui.theme.lightBlue
import com.example.coretechv2.ui.theme.midLightBlue
import com.example.coretechv2.ui.theme.scottBlue
import com.example.coretechv2.viewmodel.AssemblyOrderDetailsViewModel
import com.example.coretechv2.viewmodel.SharedViewModel
import java.text.DecimalFormat

@Composable
fun AssemblyOrderDetails(
    navController: NavController, sharedViewModel: SharedViewModel
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val viewModel: AssemblyOrderDetailsViewModel = viewModel(
        factory = AssemblyOrderDetailsViewModelFactory(context, sharedViewModel)
    )
    var icon2 = Icons.Filled.Add
    var lineButtonAction = { viewModel.hideAssemblyDetails() }
    var detailsButtonAction = { viewModel.showAssemblyDetails() }


        LaunchedEffect(viewModel.assemblyHeader) {
        viewModel.retrieveLabelData()
    }


    LaunchedEffect(sharedViewModel.showPopup.value) {
        if (!sharedViewModel.showPopup.value) {
            viewModel.reload()
        }
    }

    LaunchedEffect(sharedViewModel) {
        sharedViewModel.snackbarEvent.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    if (viewModel.showPopupWindow.value) {
        sharedViewModel.popupDetails = viewModel.popupDetails
        sharedViewModel.openPopup()
        viewModel.togglePopup(viewModel.showPopupWindow)

    }
    if (viewModel.showPopupMessage.value) {
        sharedViewModel.popupMessageDetails = viewModel.popupMessageDetails
        sharedViewModel.openMessagePopup()
        viewModel.togglePopup(viewModel.showPopupMessage)
    }

    if (viewModel.editMode) {
        icon2 = Icons.Filled.Warning
        lineButtonAction = {}
        detailsButtonAction = {}
    }

    TopBar(
        navController = navController,
        title = "${viewModel.assemblyHeader.firstOrNull()?.ORDERNUMBER} - ${viewModel.assemblyHeader.firstOrNull()?.ITEMDESCRIPTION}",
        snackbarHostState = snackbarHostState,
        backshow = true,
        icon1 = Icons.Filled.Menu,
        icon1Description = "Menu",
        icon1action = { viewModel.menuPressed() },
        icon2 = icon2,
        icon2Description = "Add",
        icon2action = { viewModel.addPressed() }

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Button(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 5.dp),
                            shape = RoundedCornerShape(4.dp), // match OutlinedTextField corners
                            border = BorderStroke(1.dp, Color.Gray),
                            onClick = lineButtonAction
                        ) {
                            Text(text = "Lines")
                        }
                        Button(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 5.dp),
                            shape = RoundedCornerShape(4.dp), // match OutlinedTextField corners
                            border = BorderStroke(1.dp, Color.Gray),
                            onClick = detailsButtonAction
                        ) {
                            Text(text = "Details")
                        }
                    }
                    if (viewModel.showAssemblyDetails.value) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 10.dp),
                        )
                        {
                            Column(
                                modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                            ){
                                Row(modifier = Modifier.weight(2f), verticalAlignment = Alignment.CenterVertically){
                                    Text(
                                        text = buildAnnotatedString {
                                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                append("Order Number: ")
                                            }
                                            append(viewModel.assemblyHeader.firstOrNull()?.ORDERNUMBER ?: "")
                                        },
                                        modifier = Modifier.weight(2f)
                                    )
                                    if (viewModel.editMode) {
                                        Row(modifier = Modifier.width(300.dp).weight(2f), verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "Order Qty:",fontWeight = FontWeight.Bold
                                            )
                                            OutlinedStyleDoubleNumberField(
                                                modifier = Modifier.weight(1f),
                                                value = viewModel.orderQty.value,
                                                onValueChange = { newValue -> viewModel.orderQty.value = newValue }
                                            )
                                        }
                                    } else{
                                        Text(
                                            text = buildAnnotatedString {
                                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                    append("Order Qty: ")
                                                }
                                                append(viewModel.assemblyHeader.firstOrNull()?.ORDERQTY.toString())
                                                append(" " + viewModel.assemblyHeader.firstOrNull()?.ITEMUNIT)
                                            },
                                            modifier = Modifier.weight(2f)
                                        )
                                    }
                                }
                                Row(modifier = Modifier.weight(2f), verticalAlignment = Alignment.CenterVertically){
                                    Text(
                                        text = buildAnnotatedString {
                                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                append("Item Code: ")
                                            }
                                            append(viewModel.assemblyHeader.firstOrNull()?.ITEMCODE ?: "")
                                        },
                                        modifier = Modifier.weight(2f)
                                    )
                                    Text(
                                        text = buildAnnotatedString {
                                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                append("Completed Qty: ")
                                            }
                                            append(viewModel.assemblyHeader.firstOrNull()?.COMPLETEQTY.toString())
                                            append(" " + viewModel.assemblyHeader.firstOrNull()?.ITEMUNIT)
                                        },
                                        modifier = Modifier.weight(2f)
                                    )
                                }
                                Row(modifier = Modifier.weight(2f), verticalAlignment = Alignment.CenterVertically){
                                    Text(
                                        text = buildAnnotatedString {
                                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                append("Order Status: ")
                                            }
                                            append(viewModel.assemblyHeader.firstOrNull()?.ORDERSTATUS ?: "")
                                        },
                                        modifier = Modifier.weight(2f)
                                    )
                                    Text(
                                        text = buildAnnotatedString {
                                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                append("Batch Size: ")
                                            }
                                            append("%.3f".format(viewModel.assemblyHeader.firstOrNull()?.ADDITIONALFIELD_6?.toDouble()))/////////////////////////////////////////////////////////////////////
                                            append(" " + viewModel.assemblyHeader.firstOrNull()?.ITEMUNIT)

                                        },
                                        modifier = Modifier.weight(2f)
                                    )
                                }
                                Row(modifier = Modifier.weight(2f), verticalAlignment = Alignment.CenterVertically){
                                    Text(
                                        text = buildAnnotatedString {
                                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                append("Order Date: ")
                                            }
                                            append(viewModel.assemblyHeader.firstOrNull()?.ORDERDATE ?: "")
                                        },
                                        modifier = Modifier.weight(2f)
                                    )
                                    if (viewModel.editMode) {
                                        Row(modifier = Modifier.width(300.dp).weight(2f), verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "No of Batches:",fontWeight = FontWeight.Bold
                                            )
                                            OutlinedStyleDoubleNumberField(
                                                modifier = Modifier.weight(1f),
                                                value = viewModel.noOfBatches.value,
                                                onValueChange = { newValue -> viewModel.noOfBatches.value = newValue }
                                            )
                                        }
                                    }else{
                                        Text(
                                            text = buildAnnotatedString {
                                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                    append("No of Batches: ")
                                                }
                                                append(viewModel.assemblyHeader.firstOrNull()?.ADDITIONALFIELD_12 ?: "")
                                            },
                                            modifier = Modifier.weight(2f)//////////////////////////////////////////////////////////////////////////////////////////////////////////
                                        )
                                    }
                                }
                            }
                            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            ){
                                Row(modifier = Modifier.weight(2f), verticalAlignment = Alignment.CenterVertically){
                                    Text(
                                        text = buildAnnotatedString {
                                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                append("Catalyst: ")
                                            }
                                            append(viewModel.assemblyHeader.firstOrNull()?.ADDITIONALFIELD_1 ?: "")
                                        },
                                        modifier = Modifier.weight(2f)
                                    )
                                    Text(
                                        text = buildAnnotatedString {
                                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                append("Catalyst Percentage: ")
                                            }
                                            append(viewModel.assemblyHeader.firstOrNull()?.ADDITIONALFIELD_9 ?: "")
                                        },
                                        modifier = Modifier.weight(2f)
                                    )
                                }
                                Row(modifier = Modifier.weight(2f), verticalAlignment = Alignment.CenterVertically){
                                    Text(
                                        text = buildAnnotatedString {
                                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                append("Viscosity Spindle: ")
                                            }
                                            append(viewModel.assemblyHeader.firstOrNull()?.ADDITIONALFIELD_3 ?: "")
                                        },
                                        modifier = Modifier.weight(2f)
                                    )
                                    Text(
                                        text = buildAnnotatedString {
                                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                append("Vis Reference Reading: ")
                                            }
                                            append(viewModel.assemblyHeader.firstOrNull()?.ADDITIONALFIELD_11 ?: "")
                                        },
                                        modifier = Modifier.weight(2f)
                                    )
                                }
                                Row(modifier = Modifier.weight(2f), verticalAlignment = Alignment.CenterVertically){
                                    Text(
                                        text = buildAnnotatedString {
                                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                append("Thix Index Speeds: ")
                                            }
                                            append(viewModel.assemblyHeader.firstOrNull()?.ADDITIONALFIELD_8 ?: "")
                                        },
                                        modifier = Modifier.weight(2f)
                                    )
                                    Text(
                                        text = buildAnnotatedString {
                                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                append("Thix Index Range: ")
                                            }
                                            append(viewModel.assemblyHeader.firstOrNull()?.ADDITIONALFIELD_5 ?: "")

                                        },
                                        modifier = Modifier.weight(2f)
                                    )
                                }
                                Row(modifier = Modifier.weight(2f), verticalAlignment = Alignment.CenterVertically){
                                    Text(
                                        text = buildAnnotatedString {
                                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                append("Gel Time: ")
                                            }
                                            append(viewModel.assemblyHeader.firstOrNull()?.ADDITIONALFIELD_2 ?: "")
                                        },
                                        modifier = Modifier.weight(2f)
                                    )
                                    Text(
                                        text = buildAnnotatedString {
                                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                append("Viscosity Range: ")
                                            }
                                            append(viewModel.assemblyHeader.firstOrNull()?.ADDITIONALFIELD_4 ?: "")
                                        },
                                        modifier = Modifier.weight(2f)
                                    )
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(2f)
                        ) {
                            itemsIndexed(viewModel.assemblyDetailsLines) { index, order ->
                                if (viewModel.editMode) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .combinedClickable(
                                                onClick = {},
                                                onLongClick = {
                                                    ///////////////////////////////////////////////////////////////////////Add delete system here
                                                }
                                            ),
                                        horizontalArrangement = Arrangement.SpaceBetween) {
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
                                            Row(modifier = Modifier.width(300.dp)) {
                                                OutlinedStyleDoubleNumberField(
                                                    modifier = Modifier.weight(1f),
                                                    value = order.ORDERQTY,
                                                    onValueChange = { newValue -> viewModel.onEditLineChange(newValue, order, index) }
                                                )
                                                Text(
                                                    text = order.LINEUNIT,
                                                    fontSize = 20.sp,
                                                    fontWeight = FontWeight(800),
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .combinedClickable(
                                                onClick = {
                                                    ///////////////////////////////////////////////////////////////////////Add marking off system here
                                                },
                                                onLongClick = {
                                                    ///////////////////////////////////////////////////////////////////////Add batch number system here
                                                }
                                            ),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ){
                                        Box(modifier = Modifier.weight(1f)){
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
                                        }
                                        Box(modifier = Modifier.weight(1f)){

                                        }








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
                                        Row(
                                            modifier = Modifier.padding(16.dp).fillMaxHeight().background(Black),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            var fontColor = fontLightTheme
                                            var formatter = DecimalFormat("0.000")
                                            if (order.ORDERQTY.toDouble() < 0.004) {
                                                formatter = DecimalFormat("0.0000")
                                                fontColor = Color.Red
                                            } else {
                                                formatter = DecimalFormat("0.000")
                                                fontColor = fontLightTheme
                                            }
                                            Text(
                                                text = "${formatter.format(order.ORDERQTY.toDouble())} ${order.LINEUNIT}",
                                                modifier = Modifier.border(BorderStroke(1.dp,Black)),
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight(800),
                                                color = fontColor,
                                            )
                                        }
                                    }
                                }
                                HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
                            }
                        }
                    }
                }
                VerticalDivider(modifier = Modifier.fillMaxHeight())
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Button(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 5.dp),
                            shape = RoundedCornerShape(4.dp), // match OutlinedTextField corners
                            border = BorderStroke(1.dp, Color.Gray),
                            onClick = { viewModel.hideTestAndAdjustments() }
                        ) {
                            Text(text = "Instructions and Notes")
                        }
                        Button(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 5.dp),
                            shape = RoundedCornerShape(4.dp), // match OutlinedTextField corners
                            border = BorderStroke(1.dp, Color.Gray),
                            onClick = { viewModel.showTestAndAdjustments() }
                        ) {
                            Text(text = "Tests and Adjustments")
                        }
                    }
                    if (viewModel.showTestAndAdjustments.value) {
                        if (viewModel.testAndAdjustments.isEmpty()) {
                            Text("No Tests Or Adjustments")
                        }

                        LazyColumn() {
                            var testnumber = 1
                            items(viewModel.testAndAdjustments) { values ->

                                val tests = values[0] as SnapshotStateList<*>
                                val adjustments = values[1] as SnapshotStateList<*>

                                if (!tests.isEmpty()) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(lightBlue)
                                    ) {
                                        Text("Test " + testnumber, Modifier.padding(vertical = 2.dp, horizontal = 5.dp), style = MaterialTheme.typography.titleSmall)
                                    }
                                    Spacer(modifier = Modifier.height(1.dp))
                                    for (test in tests) {
                                        Row(modifier = Modifier.clickable {
                                            when (test) {
                                                is APICallTables.viscosityTest -> {
                                                    sharedViewModel.updateSaveType(APICallTypes.UPDATE)
                                                    viewModel.popupDetails.width = 700
                                                    viewModel.popupDetails.height = 500
                                                    viewModel.popupDetails.content = {
                                                        ViscosityScreen(sharedViewModel, test)
                                                    }
                                                    viewModel.showPopupWindow.value = true
                                                }

                                                is APICallTables.gelTimeTest -> {
                                                    sharedViewModel.updateSaveType(APICallTypes.UPDATE)
                                                    viewModel.popupDetails.width = 700
                                                    viewModel.popupDetails.height = 500
                                                    viewModel.popupDetails.content = {
                                                        GelTimeScreen(sharedViewModel, test)
                                                    }
                                                    viewModel.showPopupWindow.value = true
                                                }
                                            }
                                        }
                                        )
                                        {
                                            when (test) {
                                                is APICallTables.viscosityTest -> {
                                                    ViscosityCard(test)
                                                }

                                                is APICallTables.gelTimeTest -> {
                                                    GelTimeCard(test)
                                                }
                                            }
                                        }
                                    }
                                }
                                if (!adjustments.isEmpty()) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(midLightBlue)
                                    ) {
                                        Text("Adjustment " + testnumber, Modifier.padding(vertical = 2.dp, horizontal = 5.dp), style = MaterialTheme.typography.titleSmall)
                                    }
                                    Spacer(modifier = Modifier.height(5.dp))
                                    for (adjust in adjustments) {
                                        Row(modifier = Modifier.clickable {
                                            when (adjust) {
                                                is APICallTables.assemblyAdjustment -> {
                                                    sharedViewModel.updateSaveType(APICallTypes.UPDATE)
                                                    viewModel.popupDetails.width = 700
                                                    viewModel.popupDetails.height = 500
                                                    viewModel.popupDetails.content = {
                                                        AdjustmentsScreen(sharedViewModel, adjust)
                                                    }
                                                    viewModel.showPopupWindow.value = true
                                                }
                                            }
                                        }) {
                                            when (adjust) {
                                                is APICallTables.assemblyAdjustment -> {
                                                    AdjustmentCard(adjust)
                                                }
                                            }
                                        }
                                    }
                                }
                                testnumber += 1
                                Spacer(modifier = Modifier.height(10.dp))

                            }
                        }
                    } else {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(lightBlue)
                            ) {
                                Text("Instructions", Modifier.padding(vertical = 2.dp, horizontal = 5.dp), style = MaterialTheme.typography.titleSmall)
                            }
                            if (viewModel.assemblyHeader.firstOrNull()?.ORDERNOTES.toString() != "null") {
                                Text(text = viewModel.assemblyHeader.firstOrNull()?.ORDERNOTES.toString(), Modifier.padding(vertical = 2.dp, horizontal = 5.dp))
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(lightBlue)
                            ) {
                                Text("Notes", Modifier.padding(top = 2.dp, bottom = 2.dp, start = 5.dp, end = 2.dp), style = MaterialTheme.typography.titleSmall)
                            }
                            Box(modifier = Modifier.fillMaxSize()) {
                                Text(text = viewModel.notes, Modifier.padding(vertical = 2.dp, horizontal = 5.dp))
                                IconButton(
                                    modifier = Modifier.align(Alignment.TopEnd),
                                    onClick = { viewModel.notesPressed() }) {
                                    Icon(
                                        imageVector = Icons.Filled.EditNote,
                                        contentDescription = "Edit Notes"
                                    )
                                }
                            }
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
    if (sharedViewModel.showLabelPreview.value) {
        if (viewModel.labelStyle != LabelStyles.BOX) {
            LabelPreview(viewModel.productLabelDataList[viewModel.labelIndex], "1")
        } else {
            LabelPreview(viewModel.boxLabelDataList[viewModel.labelIndex], "1")
        }
    }
}


@SuppressLint("ViewModelConstructorInComposable")
@Preview(device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun AssemblyOrderDetailsPreview() {

    val navController = rememberNavController()
    val sharedViewModel: SharedViewModel = viewModel()


    sharedViewModel.currentOrderNumber.value = "A5300"//"A4975"
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