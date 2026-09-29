package com.example.coretechv2.ui.screen

import android.content.res.Configuration
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.coretechv2.dataclasses.APICallTables
import com.example.coretechv2.dataclasses.assemblydataclasses.AssemblyLinesItem
import com.example.coretechv2.factory.AssemblyOrdersRecordDetailsViewModelFactory
import com.example.coretechv2.factory.AssemblyOrdersRecordViewModelFactory
import com.example.coretechv2.ui.component.AdjustmentCard
import com.example.coretechv2.ui.component.ElongationalBreakCard
import com.example.coretechv2.ui.component.FlammabilityCard
import com.example.coretechv2.ui.component.GelTimeCard
import com.example.coretechv2.ui.component.Menu
import com.example.coretechv2.ui.component.OutlinedStyleDoubleNumberField
import com.example.coretechv2.ui.component.OutlinedStyleIntNumberField
import com.example.coretechv2.ui.component.PeakExothermCard
import com.example.coretechv2.ui.component.ResistivityCard
import com.example.coretechv2.ui.component.TopBar
import com.example.coretechv2.ui.component.ViscosityCard
import com.example.coretechv2.viewmodel.AssemblyOrdersRecordDetailsViewModel
import com.example.coretechv2.viewmodel.AssemblyOrdersRecordViewModel
import com.example.coretechv2.viewmodel.SharedViewModel
import kotlinx.coroutines.launch
import java.text.DecimalFormat

/**
 * Displays the detailed information for one or more selected assembly orders.
 *
 * The screen retrieves assembly order details using the order numbers stored in
 * [SharedViewModel.compareList] and displays the results in a vertically
 * scrollable list.
 *
 * Each assembly order can be expanded to display header information such as:
 * - Order number and item description.
 * - Order quantity and completed quantity.
 * - Item code and order status.
 * - Batch size and number of batches.
 * - Required date.
 * - Catalyst and catalyst percentage.
 * - Viscosity and gel-time configuration.
 * - Thixotropic index settings.
 *
 * The screen also displays the assembly lines for each order, including:
 * - Line description.
 * - Planned quantity.
 * - Adjustment quantities grouped by adjustment number.
 * - Total issued quantity.
 *
 * Test records are grouped by test number and displayed using the appropriate
 * test-specific composable:
 * [ViscosityCard], [GelTimeCard], [ElongationalBreakCard],
 * [FlammabilityCard], [ResistivityCard], and [PeakExothermCard].
 *
 * Assembly notes are displayed alongside the test information.
 *
 * The screen uses the current device configuration to determine whether the
 * device is operating in landscape orientation or on a tablet. The details
 * are presented using Compose state and retrieved through
 * [AssemblyOrdersRecordDetailsViewModel].
 *
 * @param navController Navigation controller used by the [TopBar] to navigate
 *        back to the previous screen.
 * @param sharedViewModel Shared ViewModel containing the assembly order numbers
 *        selected for comparison.
 */
@Composable
fun AssemblyOrdersRecordDetailsScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
) {
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val viewModel: AssemblyOrdersRecordDetailsViewModel = viewModel(
        factory = AssemblyOrdersRecordDetailsViewModelFactory(context, sharedViewModel)
    )
    val focusManager = LocalFocusManager.current
    val isLandscape =
        configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val screenWidthDp = configuration.screenWidthDp
    val isTablet = screenWidthDp >= 600
    LaunchedEffect(Unit) {
        viewModel.retrieveAssemblyDetails(sharedViewModel.compareList)
    }
    LaunchedEffect(Unit) {
        viewModel.snackbarEvent.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    TopBar(
        navController = navController,
        title = "Record Details",
        snackbarHostState = snackbarHostState,
        backshow = true,
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            items(viewModel.AssemblyOrdersList) { order ->
                val header = order[0] as APICallTables.AssemblyHeader
                val lines = order[1] as SnapshotStateList<AssemblyLinesItem>
                val tests = order[2] as SnapshotStateMap<Int, List<Any>>
                val adjustments = order[3] as SnapshotStateMap<Int, List<APICallTables.assemblyAdjustment>>
                val notes = order[4] as String
                val formatter = DecimalFormat("0.000")
                val dividerColor = MaterialTheme.colorScheme.outlineVariant
                val strokeWidth = 2.dp
                var showDetail by remember { mutableStateOf(false) }
                var batchSize = 0.0
                var batchSizeUnit = "Kg"
                for (index in 0 until lines.size) {
                    if (lines[index].STEPNAME == "Assembly") {
                        batchSize += lines[index].ORDERQTY.toDouble()
                    }
                }
                if (batchSize <= 0) {
                    batchSize = header.ORDERQTY
                    batchSizeUnit = header.ITEMUNIT
                }


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp)
                        .background(MaterialTheme.colorScheme.primary),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "${header.ORDERNUMBER} - ${header.ITEMDESCRIPTION}",
                        Modifier.padding(vertical = 2.dp, horizontal = 5.dp),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = { showDetail = !showDetail }) {
                        if (showDetail) {
                            Icon(
                                imageVector = Icons.Filled.ExpandLess,
                                contentDescription = "Expand Less"
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.ExpandMore,
                                contentDescription = "Expand More"
                            )
                        }
                    }
                }
                if (showDetail) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                    {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .drawWithContent {
                                    drawContent()
                                    drawLine(
                                        color = dividerColor,
                                        start = Offset(size.width - strokeWidth.toPx() / 2, 0f),
                                        end = Offset(size.width - strokeWidth.toPx() / 2, size.height),
                                        strokeWidth = strokeWidth.toPx()
                                    )
                                }
                        ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(MaterialTheme.colorScheme.background)
                                        .padding(horizontal = 10.dp),
                                ) {
                                    Row(modifier = Modifier.weight(2f), verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = buildAnnotatedString {
                                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                    append("Order Number: ")
                                                }
                                                append(header.ORDERNUMBER)
                                            },
                                            modifier = Modifier.weight(2f)
                                        )
                                        Text(
                                            text = buildAnnotatedString {
                                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                    append("Order Qty: ")
                                                }
                                                append(header.ORDERQTY.toString())
                                                append(" " + header.ITEMUNIT)
                                            },
                                            modifier = Modifier.weight(2f)
                                        )
                                    }
                                    Row(modifier = Modifier.weight(2f), verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = buildAnnotatedString {
                                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                    append("Item Code: ")
                                                }
                                                append(header.ITEMCODE)
                                            },
                                            modifier = Modifier.weight(2f)
                                        )
                                        Text(
                                            text = buildAnnotatedString {
                                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                    append("Completed Qty: ")
                                                }
                                                append(header.COMPLETEQTY.toString())
                                                append(" " + header.ITEMUNIT)
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
                                                append(header.ORDERSTATUS)
                                            },
                                            modifier = Modifier.weight(2f)
                                        )
                                        Text(
                                            text = buildAnnotatedString {
                                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                    append("Batch Size: ")
                                                }
                                                append("%.3f".format(batchSize / header.ADDITIONALFIELD_12.toDouble()))
                                                append(" $batchSizeUnit")

                                            },
                                            modifier = Modifier.weight(2f)
                                        )
                                    }
                                    Row(modifier = Modifier.weight(2f), verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = buildAnnotatedString {
                                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                    append("Required Date: ")
                                                }
                                                append(header.REQUIREDDATE)
                                            },
                                            modifier = Modifier.weight(2f)
                                        )
                                        Text(
                                            text = buildAnnotatedString {
                                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                    append("Number of Batches: ")
                                                }
                                                append(header.ADDITIONALFIELD_12)
                                            },
                                            modifier = Modifier.weight(2f)
                                        )
                                    }

                            }
                        }
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .drawWithContent {
                                    drawContent()
                                    drawLine(
                                        color = dividerColor,
                                        start = Offset(size.width - strokeWidth.toPx() / 2, 0f),
                                        end = Offset(size.width - strokeWidth.toPx() / 2, size.height),
                                        strokeWidth = strokeWidth.toPx()
                                    )
                                }
                        ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(MaterialTheme.colorScheme.background)
                                        .padding(horizontal = 10.dp),
                                ) {
                                    Row(modifier = Modifier.weight(2f), verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = buildAnnotatedString {
                                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                    append("Catalyst: ")
                                                }
                                                append(header.ADDITIONALFIELD_1)
                                            },
                                            modifier = Modifier.weight(2f)
                                        )
                                        Text(
                                            text = buildAnnotatedString {
                                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                    append("Catalyst Percentage: ")
                                                }
                                                append(header.ADDITIONALFIELD_9)
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
                                                append(header.ADDITIONALFIELD_3)
                                            },
                                            modifier = Modifier.weight(2f)
                                        )
                                        Text(
                                            text = buildAnnotatedString {
                                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                    append("Vis Reference Reading: ")
                                                }
                                                append(header.ADDITIONALFIELD_11)
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
                                                append(header.ADDITIONALFIELD_8)
                                            },
                                            modifier = Modifier.weight(2f)
                                        )
                                        Text(
                                            text = buildAnnotatedString {
                                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                    append("Thix Index Range: ")
                                                }
                                                append(header.ADDITIONALFIELD_5)

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
                                                append(header.ADDITIONALFIELD_2)
                                            },
                                            modifier = Modifier.weight(2f)
                                        )
                                        Text(
                                            text = buildAnnotatedString {
                                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                    append("Viscosity Range: ")
                                                }
                                                append(header.ADDITIONALFIELD_4)
                                            },
                                            modifier = Modifier.weight(2f)
                                        )
                                    }
                                }
                        }
                    }
                }




                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .weight(3f)
                            .drawWithContent {
                                    drawContent()
                                    drawLine(
                                        color = dividerColor,
                                        start = Offset(size.width - strokeWidth.toPx() / 2, 0f),
                                        end = Offset(size.width - strokeWidth.toPx() / 2, size.height),
                                        strokeWidth = strokeWidth.toPx()
                                    )
                                }
                    ) {
                        Text(
                            "Description",
                            Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(vertical = 2.dp, horizontal = 10.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .drawWithContent {
                                    drawContent()
                                    drawLine(
                                        color = dividerColor,
                                        start = Offset(size.width - strokeWidth.toPx() / 2, 0f),
                                        end = Offset(size.width - strokeWidth.toPx() / 2, size.height),
                                        strokeWidth = strokeWidth.toPx()
                                    )
                                }
                    ) {
                        Text(
                            "Planned Qty",
                            Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(vertical = 2.dp, horizontal = 10.dp),
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Right
                        )
                    }
                    val numberOfColumns = adjustments.keys.maxOrNull() ?: 0
                    for (col in 1 until numberOfColumns) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .drawWithContent {
                                    drawContent()
                                    drawLine(
                                        color = dividerColor,
                                        start = Offset(size.width - strokeWidth.toPx() / 2, 0f),
                                        end = Offset(size.width - strokeWidth.toPx() / 2, size.height),
                                        strokeWidth = strokeWidth.toPx()
                                    )
                                }
                        ) {
                            Text(
                                "Addjustment $col",
                                Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .padding(vertical = 2.dp, horizontal = 10.dp),
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Right
                            )
                        }
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .drawWithContent {
                                    drawContent()
                                    drawLine(
                                        color = dividerColor,
                                        start = Offset(size.width - strokeWidth.toPx() / 2, 0f),
                                        end = Offset(size.width - strokeWidth.toPx() / 2, size.height),
                                        strokeWidth = strokeWidth.toPx()
                                    )
                                }
                    ) {
                        Text(
                            "Total Qty",
                            Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(vertical = 2.dp, horizontal = 10.dp),
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Right
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawWithContent {
                                    drawContent()
                                    drawLine(
                                        color = dividerColor,
                                        start = Offset(size.width - strokeWidth.toPx() / 2, 0f),
                                        end = Offset(size.width - strokeWidth.toPx() / 2, size.height),
                                        strokeWidth = strokeWidth.toPx()
                                    )
                                }
                ) {
                    Column(
                        modifier = Modifier
                            .weight(3f)
                            .drawWithContent {
                                    drawContent()
                                    drawLine(
                                        color = dividerColor,
                                        start = Offset(size.width - strokeWidth.toPx() / 2, 0f),
                                        end = Offset(size.width - strokeWidth.toPx() / 2, size.height),
                                        strokeWidth = strokeWidth.toPx()
                                    )
                                }
                    ) {
                        for (index in 0 until lines.size) {
                            Text(
                                lines[index].LINEDESCRIPTION, Modifier
                                    .fillMaxWidth()
                                    .background(if (index % 2 == 1) MaterialTheme.colorScheme.surfaceDim else MaterialTheme.colorScheme.background)
                                    .padding(vertical = 2.dp, horizontal = 15.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                            //HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
                        }
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .drawWithContent {
                                    drawContent()
                                    drawLine(
                                        color = dividerColor,
                                        start = Offset(size.width - strokeWidth.toPx() / 2, 0f),
                                        end = Offset(size.width - strokeWidth.toPx() / 2, size.height),
                                        strokeWidth = strokeWidth.toPx()
                                    )
                                }
                    ) {
                        for (index in 0 until lines.size) {
                            Text(
                                formatter.format(lines[index].ORDERQTY.toDouble()),
                                Modifier
                                    .fillMaxWidth()
                                    .background(if (index % 2 == 1) MaterialTheme.colorScheme.surfaceDim else MaterialTheme.colorScheme.background)
                                    .padding(vertical = 2.dp, horizontal = 15.dp),
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Right
                            )
                        }
                    }

                    val numberOfColumns = adjustments.keys.maxOrNull() ?: 0
                    for (col in 1 until numberOfColumns) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .drawWithContent {
                                    drawContent()
                                    drawLine(
                                        color = dividerColor,
                                        start = Offset(size.width - strokeWidth.toPx() / 2, 0f),
                                        end = Offset(size.width - strokeWidth.toPx() / 2, size.height),
                                        strokeWidth = strokeWidth.toPx()
                                    )
                                }
                        ) {
                            for (index in 0 until lines.size) {

                                val adjustment = adjustments[col]
                                    ?.find {
                                        it.LINECODE == lines[index].LINECODE &&
                                                it.LINENUMBER == lines[index].LINENUMBER
                                    }

                                if (adjustment != null) {
                                    Text(
                                        formatter.format(adjustment.ADJUSTQTY),
                                        Modifier
                                            .fillMaxWidth()
                                            .background(
                                                if (index % 2 == 1)
                                                    MaterialTheme.colorScheme.surfaceDim
                                                else
                                                    MaterialTheme.colorScheme.background
                                            )
                                            .padding(vertical = 2.dp, horizontal = 15.dp),
                                        style = MaterialTheme.typography.bodyLarge,
                                        textAlign = TextAlign.Right
                                    )
                                } else {
                                    Text(
                                        "",
                                        Modifier
                                            .fillMaxWidth()
                                            .background(
                                                if (index % 2 == 1)
                                                    MaterialTheme.colorScheme.surfaceDim
                                                else
                                                    MaterialTheme.colorScheme.background
                                            )
                                            .padding(vertical = 2.dp, horizontal = 15.dp),
                                        style = MaterialTheme.typography.bodyLarge,
                                        textAlign = TextAlign.Right
                                    )
                                }
                            }
                            /*for (index in 0 until lines.size) {
                                for (adjust in 0 until (adjustments[col]?.size ?: 0)) {
                                    val adjustment = adjustments[col]?.get(adjust)
                                    if (lines[index].LINECODE == adjustment?.LINECODE && lines[index].LINENUMBER == adjustment.LINENUMBER) {
                                        Text(
                                            formatter.format(adjustment.ADJUSTQTY), Modifier
                                                .fillMaxWidth()
                                                .background(if (index % 2 == 1) MaterialTheme.colorScheme.surfaceDim else MaterialTheme.colorScheme.background)
                                                .padding(vertical = 2.dp, horizontal = 15.dp), style = MaterialTheme.typography.bodyLarge,
                                            textAlign = TextAlign.Right
                                        )
                                    } else if (adjust == 0 && adjustments[col].LINENUMBER.contains((adjust + 1 ) * 10) {
                                        Text(
                                            "test", Modifier
                                                .fillMaxWidth()
                                                .background(if (index % 2 == 1) MaterialTheme.colorScheme.surfaceDim else MaterialTheme.colorScheme.background)
                                                .padding(vertical = 2.dp, horizontal = 15.dp), style = MaterialTheme.typography.bodyLarge,
                                            textAlign = TextAlign.Right
                                        )
                                    }
                                    // HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
                                }
                            }*/
                        }
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .drawWithContent {
                                    drawContent()
                                    drawLine(
                                        color = dividerColor,
                                        start = Offset(size.width - strokeWidth.toPx() / 2, 0f),
                                        end = Offset(size.width - strokeWidth.toPx() / 2, size.height),
                                        strokeWidth = strokeWidth.toPx()
                                    )
                                }
                    ) {
                        for (index in 0 until lines.size) {
                            Text(
                                formatter.format(lines[index].TOTALISSUEDQTY.toDouble()), Modifier
                                    .fillMaxWidth()
                                    .background(if (index % 2 == 1) MaterialTheme.colorScheme.surfaceDim else MaterialTheme.colorScheme.background)
                                    .padding(vertical = 2.dp, horizontal = 15.dp), style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Right
                            )
                            //HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
                        }
                    }
                }
                //HorizontalDivider(Modifier, 3.dp, dividerColor)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .background(dividerColor)
                ) {}
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .drawWithContent {
                                drawContent()
                                drawLine(
                                    color = dividerColor,
                                    start = Offset(size.width - strokeWidth.toPx() / 2, 0f),
                                    end = Offset(size.width - strokeWidth.toPx() / 2, size.height),
                                    strokeWidth = strokeWidth.toPx()
                                )
                            }
                    ) { Text("Tests", Modifier.padding(vertical = 2.dp, horizontal = 10.dp), style = MaterialTheme.typography.titleMedium) }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Text("Notes", Modifier.padding(vertical = 2.dp, horizontal = 10.dp), style = MaterialTheme.typography.titleMedium)
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .drawWithContent {
                                drawContent()
                                drawLine(
                                    color = dividerColor,
                                    start = Offset(size.width - strokeWidth.toPx() / 2, 0f),
                                    end = Offset(size.width - strokeWidth.toPx() / 2, size.height),
                                    strokeWidth = strokeWidth.toPx()
                                )
                            }
                    ) {
                        tests.forEach { (testNumber, testList) ->

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.primary)
                            ) {
                                Text("Test $testNumber", Modifier.padding(vertical = 2.dp, horizontal = 15.dp), style = MaterialTheme.typography.bodyLarge)
                            }

                            testList.forEach { test ->
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
                    Column(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Text(notes, Modifier.padding(vertical = 2.dp, horizontal = 10.dp), style = MaterialTheme.typography.bodyLarge)
                    }
                }
                HorizontalDivider(Modifier, 3.dp, dividerColor)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)

                ) {}
            }
        }
    }
}

