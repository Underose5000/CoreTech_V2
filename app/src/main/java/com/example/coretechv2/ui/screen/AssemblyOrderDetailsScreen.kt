package com.example.coretechv2.ui.screen

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
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
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
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
import com.example.coretechv2.factory.AssemblyOrderDetailsViewModelFactory
import com.example.coretechv2.ui.component.AdjustmentCard
import com.example.coretechv2.ui.component.ButtonMessage
import com.example.coretechv2.ui.component.ElongationalBreakCard
import com.example.coretechv2.ui.component.FlammabilityCard
import com.example.coretechv2.ui.component.GelTimeCard
import com.example.coretechv2.ui.component.LabelPreview
import com.example.coretechv2.ui.component.Menu
import com.example.coretechv2.ui.component.OutlinedStyleDoubleNumberField
import com.example.coretechv2.ui.component.OutlinedStyleIntNumberField
import com.example.coretechv2.ui.component.PeakExothermCard
import com.example.coretechv2.ui.component.PopupWindow
import com.example.coretechv2.ui.component.ResistivityCard
import com.example.coretechv2.ui.component.TopBar
import com.example.coretechv2.ui.component.ViscosityCard
import com.example.coretechv2.ui.screen.assemblytestsandadjustments.AdjustmentsScreen
import com.example.coretechv2.ui.screen.assemblytestsandadjustments.ElongationalBreakScreen
import com.example.coretechv2.ui.screen.assemblytestsandadjustments.FlammabilityScreen
import com.example.coretechv2.ui.screen.assemblytestsandadjustments.GelTimeScreen
import com.example.coretechv2.ui.screen.assemblytestsandadjustments.PeakExothermScreen
import com.example.coretechv2.ui.screen.assemblytestsandadjustments.ResistivityScreen
import com.example.coretechv2.ui.screen.assemblytestsandadjustments.ViscosityScreen
import com.example.coretechv2.viewmodel.AssemblyOrderDetailsViewModel
import com.example.coretechv2.viewmodel.SharedViewModel
import java.text.DecimalFormat

@Composable
fun AssemblyOrderDetails(
    navController: NavController, sharedViewModel: SharedViewModel
) {
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val viewModel: AssemblyOrderDetailsViewModel = viewModel(
        factory = AssemblyOrderDetailsViewModelFactory(context, sharedViewModel)
    )
    val focusManager = LocalFocusManager.current
    viewModel.isLandscape =
        configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val screenWidthDp = configuration.screenWidthDp
    val screenDensity = configuration.densityDpi
    viewModel.isTablet = false

    if (screenWidthDp >=550 && screenDensity >= 500){
        viewModel.onFlipPhone()
    } else if (screenWidthDp >=600){
        viewModel.isTablet = true
    }

    var icon2 = Icons.Filled.Add

    LaunchedEffect(Unit) {
        viewModel.reload()
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

    LaunchedEffect(viewModel.closeDetailScreen.value) {
        if (viewModel.closeDetailScreen.value) {
            navController.popBackStack()
            sharedViewModel.navBack.value = true
            viewModel.openDetailScreen()
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
    if (viewModel.editMode && viewModel.showAssemblyDetails.value) {
        icon2 = Icons.Filled.Warning
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
        if (viewModel.isFlipPhone) {
            AssemblyOrderDetailsFlipPhoneLayout(viewModel, sharedViewModel, innerPadding, focusManager)
        } else if (viewModel.isLandscape and viewModel.isTablet) {
            AssemblyOrderDetailsTabletLandscapeLayout(viewModel, sharedViewModel, innerPadding, focusManager)
        } else if (!viewModel.isLandscape and viewModel.isTablet){
            AssemblyOrderDetailsTabletPortraitLayout(viewModel, sharedViewModel, innerPadding, focusManager)
        } else if (viewModel.isLandscape and !viewModel.isTablet){
            AssemblyOrderDetailsPhoneLandscapeLayout(viewModel, sharedViewModel, innerPadding, focusManager)
        } else {
            AssemblyOrderDetailsPhonePortraitLayout(viewModel, sharedViewModel, innerPadding, focusManager)
        }
    }
    if (sharedViewModel.showLabelPreview.value && !viewModel.productLabelDataList.isEmpty()) {
        if (viewModel.labelStyle != LabelStyles.BOX) {
            LabelPreview(viewModel.productLabelDataList[viewModel.labelIndex], viewModel.orderQty.value.toDouble().toInt())
        } else {
            LabelPreview(viewModel.boxLabelDataList[viewModel.labelIndex], viewModel.orderQty.value.toDouble().toInt())
        }
    }
}

@Composable
fun AssemblyLines(viewModel: AssemblyOrderDetailsViewModel) {
    if (!viewModel.showAssemblyDetails.value) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
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
                                    onLongClick = { viewModel.lineDelete(order) }
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
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

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .padding(16.dp), contentAlignment = Alignment.CenterEnd
                            ) {
                                Row(modifier = Modifier.width(300.dp), verticalAlignment = Alignment.CenterVertically) {
                                    OutlinedStyleDoubleNumberField(
                                        modifier = Modifier.weight(1f),
                                        value = order.ORDERQTY,
                                        onValueChange = { newValue -> viewModel.onEditLineChange(newValue, order, index) },
                                        TextStyle(
                                            textAlign = TextAlign.Center,
                                            color = MaterialTheme.colorScheme.onBackground)
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
                                        viewModel.lineChecked(order)
                                    },
                                    onLongClick = {
                                        viewModel.batchEntered(order)
                                    }
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(32.dp)
                                    .padding(start = 16.dp)
                            ) {
                                Icon(
                                    imageVector = if (order.ADDITIONALFIELD_1)
                                        Icons.Filled.CheckBox
                                    else
                                        Icons.Filled.CheckBoxOutlineBlank,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Box(modifier = Modifier.weight(6f)) {
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
                            Box(
                                modifier = Modifier
                                    .weight(4f)
                                    .fillMaxHeight()
                                    .padding(16.dp), contentAlignment = Alignment.CenterEnd
                            ) {
                                var fontColor = MaterialTheme.colorScheme.onBackground
                                var formatter = DecimalFormat("0.000")
                                if (order.ORDERQTY.toDouble() < 0.004) {
                                    formatter = DecimalFormat("0.0000")
                                    fontColor = MaterialTheme.colorScheme.primary
                                } else {
                                    formatter = DecimalFormat("0.000")
                                    fontColor = MaterialTheme.colorScheme.onBackground
                                }
                                Text(
                                    text = "${formatter.format((order.ORDERQTY.toDouble()/viewModel.numberOfBatches.value.toDouble()))} ${order.LINEUNIT}",
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
}

@Composable
fun AssemblyDetails(viewModel: AssemblyOrderDetailsViewModel) {
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
            ) {
                Row(modifier = Modifier.weight(2f), verticalAlignment = Alignment.CenterVertically) {
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
                        Row(
                            modifier = Modifier
                                .width(300.dp)
                                .weight(2f), verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Order Qty:", fontWeight = FontWeight.Bold
                            )
                            OutlinedStyleDoubleNumberField(
                                modifier = Modifier.weight(1f),
                                value = viewModel.orderQty.value,
                                onValueChange = { newValue -> viewModel.orderQty(newValue) },
                                TextStyle(
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onBackground)
                            )
                        }
                    } else {
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
                Row(modifier = Modifier.weight(2f), verticalAlignment = Alignment.CenterVertically) {
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
                Row(modifier = Modifier.weight(2f), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Order Status: ")
                            }
                            append(viewModel.assemblyHeader.firstOrNull()?.ORDERSTATUS ?: "")
                        },
                        modifier = Modifier.weight(2f)
                    )
                    if (viewModel.editMode) {
                        Row(
                            modifier = Modifier
                                .width(300.dp)
                                .weight(2f), verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Max Batch Size:", fontWeight = FontWeight.Bold
                            )
                            OutlinedStyleDoubleNumberField(
                                modifier = Modifier.weight(1f),
                                value = viewModel.maxBatchSize.value,
                                onValueChange = { newValue -> viewModel.onMaxBatchSizeChange(newValue) },
                                TextStyle(
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onBackground)
                            )
                        }
                    } else {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append("Batch Size: ")
                                }
                                append("%.3f".format(viewModel.batchSize.doubleValue/viewModel.numberOfBatches.value.toDouble()))
                                append(" " + viewModel.batchSizeUnit.value)

                            },
                            modifier = Modifier.weight(2f)
                        )
                    }
                }
                Row(modifier = Modifier.weight(2f), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Required Date: ")
                            }
                            append(viewModel.assemblyHeader.firstOrNull()?.REQUIREDDATE ?: "")
                        },
                        modifier = Modifier.weight(2f)
                    )
                    if (viewModel.editMode) {
                        Row(
                            modifier = Modifier
                                .width(300.dp)
                                .weight(2f), verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Number of Batches:", fontWeight = FontWeight.Bold
                            )
                            OutlinedStyleIntNumberField(
                                modifier = Modifier.weight(1f),
                                value = viewModel.numberOfBatches.value,
                                onValueChange = { newValue -> viewModel.onNumberOfBatchesChange(newValue) },
                                TextStyle(
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onBackground)
                            )
                        }
                    } else {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append("Number of Batches: ")
                                }
                                append(viewModel.numberOfBatches.value)
                            },
                            modifier = Modifier.weight(2f)
                        )
                    }
                }
            }
            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Row(modifier = Modifier.weight(2f), verticalAlignment = Alignment.CenterVertically) {
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
                Row(modifier = Modifier.weight(2f), verticalAlignment = Alignment.CenterVertically) {
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
                Row(modifier = Modifier.weight(2f), verticalAlignment = Alignment.CenterVertically) {
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
                Row(modifier = Modifier.weight(2f), verticalAlignment = Alignment.CenterVertically) {
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
    }
}

@Composable
fun AssemblyNotesAndTest(viewModel: AssemblyOrderDetailsViewModel, sharedViewModel: SharedViewModel){
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
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
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Text("Test " + testnumber, Modifier.padding(vertical = 2.dp, horizontal = 5.dp), style = MaterialTheme.typography.titleSmall)
                        }
                        Spacer(modifier = Modifier.height(1.dp))
                        for (test in tests) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .combinedClickable(
                                        onClick = {
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

                                                is APICallTables.ElongationalBreakTest -> {
                                                    sharedViewModel.updateSaveType(APICallTypes.UPDATE)
                                                    viewModel.popupDetails.width = 600
                                                    viewModel.popupDetails.height = 350
                                                    viewModel.popupDetails.content = {
                                                        ElongationalBreakScreen(sharedViewModel, test)
                                                    }
                                                    viewModel.showPopupWindow.value = true
                                                }

                                                is APICallTables.FlammabilityTest -> {
                                                    sharedViewModel.updateSaveType(APICallTypes.UPDATE)
                                                    viewModel.popupDetails.width = 700
                                                    viewModel.popupDetails.height = 500
                                                    viewModel.popupDetails.content = {
                                                        FlammabilityScreen(sharedViewModel, test)
                                                    }
                                                    viewModel.showPopupWindow.value = true
                                                }

                                                is APICallTables.ResistivityTest -> {
                                                    sharedViewModel.updateSaveType(APICallTypes.UPDATE)
                                                    viewModel.popupDetails.width = 600
                                                    viewModel.popupDetails.height = 350
                                                    viewModel.popupDetails.content = {
                                                        ResistivityScreen(sharedViewModel, test)
                                                    }
                                                    viewModel.showPopupWindow.value = true
                                                }

                                                is APICallTables.peakExothermTest -> {
                                                    sharedViewModel.updateSaveType(APICallTypes.UPDATE)
                                                    viewModel.popupDetails.width = 700
                                                    viewModel.popupDetails.height = 500
                                                    viewModel.popupDetails.content = {
                                                        PeakExothermScreen(sharedViewModel, test)
                                                    }
                                                    viewModel.showPopupWindow.value = true
                                                }
                                            }
                                        },
                                        onLongClick = {
                                            viewModel.testDelete(test!!)
                                        }
                                    )
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
                            Text("Adjustment " + testnumber, Modifier.padding(vertical = 2.dp, horizontal = 5.dp), style = MaterialTheme.typography.titleSmall)
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                        for (adjust in adjustments) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .combinedClickable(
                                        onClick = {
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
                                        },
                                        onLongClick = { viewModel.adjustmentsDelete(adjust as APICallTables.assemblyAdjustment) }
                                    )
                            ) {
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
                        .background(MaterialTheme.colorScheme.primaryContainer)
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
                        .background(MaterialTheme.colorScheme.primaryContainer)
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

@Composable
fun AssemblyButtons(viewModel: AssemblyOrderDetailsViewModel){
    var lineButtonAction = { viewModel.hideAssemblyDetails() }
    var detailsButtonAction = { viewModel.showAssemblyDetails() }
    var notesButtonAction = { viewModel.hideTestAndAdjustments() }
    var testingButtonAction = { viewModel.showTestAndAdjustments() }
    var buttonEnabled = true

    if (viewModel.editMode) {
        buttonEnabled = false
    }
    if (viewModel.isFlipPhone || (viewModel.isLandscape and !viewModel.isTablet)) {
        lineButtonAction = {
            viewModel.showAllDetails()
            viewModel.hideAssemblyDetails()
            viewModel.hideTestAndAdjustments()
        }
        detailsButtonAction = {
            viewModel.hideAllDetails()
            viewModel.showAssemblyDetails()
            viewModel.hideTestAndAdjustments()
        }
        notesButtonAction = {
            viewModel.hideAllDetails()
            viewModel.hideAssemblyDetails()
            viewModel.hideTestAndAdjustments()
        }
        testingButtonAction = {
            viewModel.hideAllDetails()
            viewModel.hideAssemblyDetails()
            viewModel.showTestAndAdjustments()
        }
    }
    Row(modifier = Modifier.fillMaxWidth()) {
        Button(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 5.dp),
            shape = RoundedCornerShape(4.dp),
            onClick = lineButtonAction,
            enabled = buttonEnabled
        ) {
            Text(text = "Lines")
        }
        Button(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 5.dp),
            shape = RoundedCornerShape(4.dp),
            onClick = detailsButtonAction,
            enabled = buttonEnabled
        ) {
            Text(text = "Details")
        }
        Button(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 5.dp),
            shape = RoundedCornerShape(4.dp),
            onClick = notesButtonAction,
            enabled = buttonEnabled
        ) {
            Text(text = "Notes")
        }
        Button(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 5.dp),
            shape = RoundedCornerShape(4.dp),
            onClick = testingButtonAction,
            enabled = buttonEnabled
        ) {
            Text(text = "Testing")
        }
    }
    HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
}

@Composable
fun AssemblyOrderDetailsFlipPhoneLayout(
    viewModel: AssemblyOrderDetailsViewModel,
    sharedViewModel: SharedViewModel,
    innerPadding: PaddingValues,
    focusManager: FocusManager
){
    LaunchedEffect(Unit) {
        viewModel.showAllDetails()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
                viewModel.closeMenus()
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        AssemblyButtons(viewModel)
        if (viewModel.showAssemblyDetails.value) {
            AssemblyDetails(viewModel)
        } else if (viewModel.hideAllDetails.value) {
            AssemblyLines(viewModel)
        } else {
            AssemblyNotesAndTest(viewModel, sharedViewModel)
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

@Composable
fun AssemblyOrderDetailsTabletLandscapeLayout(
    viewModel: AssemblyOrderDetailsViewModel,
    sharedViewModel: SharedViewModel,
    innerPadding: PaddingValues,
    focusManager: FocusManager
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
                viewModel.closeMenus()
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        if(viewModel.hideAllDetails.value){
                AssemblyLines(viewModel)
        }else {
            AssemblyButtons(viewModel)
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    AssemblyDetails(viewModel)
                    AssemblyLines(viewModel)
                }
                VerticalDivider(modifier = Modifier.fillMaxHeight())
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    AssemblyNotesAndTest(viewModel, sharedViewModel)
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

@Composable
fun AssemblyOrderDetailsTabletPortraitLayout(
    viewModel: AssemblyOrderDetailsViewModel,
    sharedViewModel: SharedViewModel,
    innerPadding: PaddingValues,
    focusManager: FocusManager
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
                viewModel.closeMenus()
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        if(viewModel.hideAllDetails.value){
            AssemblyLines(viewModel)
        }else {
            AssemblyButtons(viewModel)
            Column(
                modifier = if (viewModel.showAssemblyDetails.value){Modifier.weight(1f)}else{Modifier.weight(2f)}
            ) {
                AssemblyDetails(viewModel)
                AssemblyLines(viewModel)
            }
            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
            Column(
                modifier = Modifier.weight(1f)
            ) {
                AssemblyNotesAndTest(viewModel, sharedViewModel)
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

@Composable
fun AssemblyOrderDetailsPhoneLandscapeLayout(
    viewModel: AssemblyOrderDetailsViewModel,
    sharedViewModel: SharedViewModel,
    innerPadding: PaddingValues,
    focusManager: FocusManager
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
                viewModel.closeMenus()
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        if(viewModel.hideAllDetails.value){
            AssemblyLines(viewModel)
        }else {
            AssemblyButtons(viewModel)
            Column(
                modifier = if (viewModel.showAssemblyDetails.value){Modifier.weight(1f)}else{Modifier.weight(2f)}
            ) {
                AssemblyDetails(viewModel)
                AssemblyLines(viewModel)
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                AssemblyNotesAndTest(viewModel, sharedViewModel)
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

@Composable
fun AssemblyOrderDetailsPhonePortraitLayout(
    viewModel: AssemblyOrderDetailsViewModel,
    sharedViewModel: SharedViewModel,
    innerPadding: PaddingValues,
    focusManager: FocusManager
){
    LaunchedEffect(Unit) {
        viewModel.showAllDetails()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
                viewModel.closeMenus()
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        AssemblyButtons(viewModel)
        if (viewModel.showAssemblyDetails.value) {
            AssemblyDetails(viewModel)
        } else if (viewModel.hideAllDetails.value) {
            AssemblyLines(viewModel)
        } else {
            AssemblyNotesAndTest(viewModel, sharedViewModel)
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


@SuppressLint("ViewModelConstructorInComposable")
@Preview()
@Composable
fun AssemblyOrderDetailsPreview() {

    val navController = rememberNavController()
    val sharedViewModel: SharedViewModel = viewModel()


    sharedViewModel.currentOrderNumber.value = "A5372"//"A4975"
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
