package com.example.coretechv2.ui.component

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coretechv2.dataclasses.APICallTables
import com.example.coretechv2.dataclasses.LabelElements
import com.example.coretechv2.dataclasses.LabelStyles
import com.example.coretechv2.repository.printBrotherImage
import com.example.coretechv2.dataclasses.PrinterType
import com.example.coretechv2.repository.findPrinters
import com.example.coretechv2.repository.labelToBitmap
import com.example.coretechv2.repository.printWithEpson
import com.example.coretechv2.ui.theme.screenBackground
import com.example.coretechv2.viewmodel.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

fun mmToDp(mm: Double): Dp {
    return (mm * 3.78).dp
}

fun ptToSp(pt: Double): TextUnit {
    val dp = pt * (96f / 72f)
    return dp.sp
}

fun mmToPixels(mm: Double, dpi: Int = 300): Int {
    return ((mm / 25.4f) * dpi).toInt()
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabelPreview(
    labelElement: LabelElements,
    numOfCopies: Int = 1,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val foundPrinters = remember { mutableStateListOf<PrinterType>() }
    val selectedPrinter = remember { mutableStateOf<PrinterType?>(null) }
    var settingsWindow by remember { mutableStateOf(false) }
    var indexPressed by remember { mutableStateOf(false) }
    var numberOfCopies = remember { mutableStateOf("1") }
    val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent = _snackbarEvent


    LaunchedEffect(Unit){
        numberOfCopies.value = numOfCopies.toString()
        findPrinters(context){ printer ->
            foundPrinters.add(printer)
        }
    }

    LaunchedEffect(_snackbarEvent) {
        snackbarEvent.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    LaunchedEffect(foundPrinters.size){
        if (selectedPrinter.value == null && foundPrinters.isNotEmpty()) {
            if (labelElement.itemInfo.LABELSTYLE.printerSection() == "brotherQL1110"){
                for (x in 0 until foundPrinters.size){
                    if(foundPrinters[x].name != "N/A" && foundPrinters[x].brotherQL1110 != null) {
                      selectedPrinter.value = foundPrinters[x]
                      break
                    }
                }
            } else if (labelElement.itemInfo.LABELSTYLE.printerSection() == "epsonCWC6510"){
                for (x in 0 until foundPrinters.size) {
                    if (foundPrinters[x].name != "N/A" && foundPrinters[x].epsonCWC6510 != null) {
                        selectedPrinter.value = foundPrinters[x]
                        break
                    }
                }
            }
        }
    }

    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    val density = LocalDensity.current
    val size = with(density) {
        DpSize(
            mmToPixels(labelElement.labelLayout.PAGEWIDTH).toDp(),
            mmToPixels(labelElement.labelLayout.PAGEHEIGHT).toDp()
        )
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Product Label")
                },
                navigationIcon = {
                        IconButton(onClick = {labelElement.sharedViewModel.closeLabelPreview()}){
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                },
                actions = {
                        IconButton(onClick = { settingsWindow = !settingsWindow }){
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "Print Settings"
                            )
                        }
                        IconButton(onClick = {
                            if(selectedPrinter.value == null){
                                CoroutineScope(Dispatchers.Default).launch {
                                    _snackbarEvent.emit("No Printer Selected")
                                }
                            }
                            else if (selectedPrinter.value!!.epsonCWC6510 != null) {
                                CoroutineScope(Dispatchers.Default).launch {
                                    _snackbarEvent.emit("Printing on Epson CW-C6510")
                                }
                                printWithEpson(
                                    printer = selectedPrinter.value!!.epsonCWC6510!!,
                                    imageBytes = imageBitmap,
                                    width = mmToPixels(labelElement.labelLayout.PAGEHEIGHT),
                                    height = mmToPixels(labelElement.labelLayout.PAGEWIDTH),
                                    numOfCopies = numberOfCopies.value.toInt()
                                )
                            }
                            else if (selectedPrinter.value!!.brotherQL1110 != null) {
                                CoroutineScope(Dispatchers.Default).launch {
                                    _snackbarEvent.emit("Printing on Brother QL-1110")
                                }
                                printBrotherImage(
                                    context = context,
                                    printerIp = selectedPrinter.value!!.brotherQL1110!!.ipAddress,
                                    imageBytes = imageBitmap,
                                    numOfCopies = numberOfCopies.value.toInt()
                                )
                            }
                        }){
                            Icon(
                                imageVector = Icons.Filled.Print,
                                contentDescription = "Print"
                            )
                        }
                }
            )
        }
    ) {innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(color = Color.Gray),
            contentAlignment = Alignment.Center
        ) {
            var width = size.width
            var height = size.height
            var userZoom by remember { mutableFloatStateOf(1.0f) }

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, _, zoomChange, _ ->
                            userZoom = (userZoom * zoomChange)
                                .coerceIn(0.25f, 10f)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                val fitScale = minOf(
                    this.maxWidth.value / width.value,
                    this.maxHeight.value / height.value
                ) * 0.95f

                val finalScale = fitScale * userZoom

                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            scaleX = finalScale
                            scaleY = finalScale
                        }
                        .requiredSize(size.width,size.height),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .requiredSize(size.width,size.height)
                    ) {
                        imageBitmap = labelToBitmap(labelElement.labelLayout.PAGEWIDTH, labelElement.labelLayout.PAGEHEIGHT,{ labelElement.itemInfo.LABELSTYLE.FunctionCall(labelElement) })
                    }

                }
            }
        }

    }
    }
    if(settingsWindow){
        Box(modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
        Box(
            modifier = Modifier
                .size(400.dp,300.dp)
                .background(screenBackground)
                .border(1.dp, Black)
        ){
            Column(){
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    Text("Printer")
                    OutlinedStyleButton(modifier = Modifier.weight(6f).padding(start = 20.dp), text = selectedPrinter.value?.name ?: "No Printers Found", onClick = { indexPressed = true })
                    DropdownMenu(
                        expanded = indexPressed,
                        onDismissRequest = { indexPressed = false }
                    ) {
                        for (x in 0 until foundPrinters.size){
                            if(foundPrinters[x].name != "N/A") {
                                DropdownMenuItem(
                                    onClick = {
                                        selectedPrinter.value = foundPrinters[x]
                                        indexPressed = false
                                    },
                                    text = { Text(foundPrinters[x].name) })
                            }
                        }
                    }
                    IconButton(onClick = {
                        findPrinters(context){ printer ->
                            foundPrinters.clear()
                            foundPrinters.add(printer)
                        }
                    }){
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Refresh Printer List"
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = "Number of Labels")
                    OutlinedStyleIntNumberField(
                        value = numberOfCopies.value,
                        onValueChange = { newValue -> numberOfCopies.value = newValue }
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        modifier = Modifier
                            .weight(2f),
                        onClick = { settingsWindow = false }
                    ) { Text(text = "OK") }
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
    }
}


@Preview(device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun LabelViewPreview() {

    val labelLayout = APICallTables.assemblyLabelLayout(
        LABELID = LabelStyles.TUFFL200,
        PAGEWIDTH = 260.0,
        PAGEHEIGHT = 145.0,
        PAGEPAD = 2.0,
        MIDWIDTH = 150.0,
        STRIPSPOS = 47.0,
        LOGOPOS = 5.0,
        LOGOSIZE = 63.0,
        LOGOOFFSET = 5.0,
        LOGOSMALLSIZE = 27.0,
        ADDPIGMENTSIZE = 35.0,
        ROADMARINESIZE = 12.0,
        QRCODESIZE = 12.0,
        PICTOSIZE = 18.0,
        COLORPOS = 100.0,
        TOPNAMEPOS = 2.0,
        BOTTOMNAMEPOS = 21.0,
        MIDNAMEPOS = 15.0,
        DGPOS = 3.0,
        VARIANTPOS = 16.0,
        VARIANTPADHOZ = 4.0,
        VARIANTPADVER = 2.0,
        SIZEPOS = 30.0,
        WARNINGPOS = 18.0,
        PICTOGAP = 32.0,
        TOPNAMEFS = 16.0,
        BOTTOMNAMEFS = 48.0,
        DGFS = 7.0,
        VARIANTFS = 16.0,
        SIZEFS = 17.0,
        HEADINGFS = 10.0,
        SUBHEADFS = 6.0,
        BODYFS = 5.0,
        SPACEING = 3.0,
    )
    
    val sharedViewModel: SharedViewModel = viewModel()
    val itemInfo = APICallTables.assemblyLabelItemInfo(
        HEADERSYSUNIQUEID = 1.0,
        SYSUNIQUEID = 1.0,
        ITEMCODE = "028016",
        TOPNAME = "",
        MIDDLENAME = "TuffStick",
        BOTTOMNAME = "",
        SIZE = "1 Litre",
        QRCODE = "www.scottech.com.au",
        VARIANT = "Standard",
        BESTBEFORE = 365,
        LABELSTYLE = LabelStyles.TUFFL20,
        BOXQTY = 5,
        ITEMBARCODE = "abcd"
    )
    val classInfo = APICallTables.assemblyLabelClassInfo(
        HEADERSYSUNIQUEID = 1.0,
        SYSUNIQUEID = 1.0,
        GROUPTYPE = "Flowcoat",
        WARNINGSIGN = "Warning",
        DIRECTIONS = "1. Weigh out resin in compatible container\n" +
                "2. Add 1.5-2.5% \"MEKP Catalyst\" (sold separately) \n" +
                "3. Stir thoroughly with flat stirrer\n" +
                "4. Apply to project \n" +
                "5. Clean up with \"Acetone\" (sold separately)",
        HELPTIPTIN = "• Use pippette or measuring cup to accurately measure MEKP Catalyst\n" +
                "• Do not catalyse more than can be used in 15 minutes\n" +
                "• 20° days use 2% MEKP Catalyst = 80 drops per 100 mL of Resin\n" +
                "• Hot days use 1.5% MEKP Catalyst = 60 drops per 100mL of Resin",
        HELPTIPPAIL = "To avoid curing issues:\n" +
                "•  Catalyse between 1%min - 2%max with MEKP catalyst\n" +
                "•  Apply Resin in temperatures greater than 15°C, and less than 35°C \n" +
                "•  Do NOT apply in relative humidity greater than 70%\n" +
                "•  Do NOT apply a Resin layer thinner than 0.6mm.\n" +
                "\n" +
                "If the surface is not properly prepared prior to application, poor adhesion between the Resin and surface can occur which can lead to delamination.",
        PRECAUTIONS = "Do not breathe vapours. Keep away from sources of ignition.\n" +
                "Do not get in eyes, on skin or clothing.\n" +
                "Additional information is listed in the  Safety Data Sheet.",
        INGESTION = "Call Poisons Information Centre Ph: 13 11 26.",
        SKINCONTACT = "Remove contaminated clothing and wash skin thoroughly with water and soap.",
        EYECONTACT = "Hold eye open, Flood with water for at least 15 minutes and seek medical attention",
        SAFESTORAGE = "Store in cool dry place away from sunlight and sources of ignition.",
        PICTOGRAM1 = "Mark",
        PICTOGRAM2 = "Flame",
        COLOUR =     "00000000" //"FFFF9F00"//
    )
    val dgInfo = APICallTables.itemDGInfo(
        UNNUMBER = "1886",
        PACKINGGROUP = "III",
        DGCLASS = "3",
        DGQUANTITY = 10.0
    )

    
    val labelElement = LabelElements(
        itemInfo = itemInfo,
        classInfo = classInfo,
        labelLayout = labelLayout,
        dgInfo = null,//dgInfo,
        sharedViewModel = sharedViewModel
    )
    
    LabelPreview(labelElement, 1)
    
}

