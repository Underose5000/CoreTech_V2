package com.example.coretechv2.printlayouts

import android.R.attr.width
import android.R.attr.height
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.widget.FrameLayout
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import androidx.core.graphics.toColorInt
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.coretechv2.R
import com.example.coretechv2.dataclasses.APICallTables
import com.example.coretechv2.dataclasses.Hazards
import com.example.coretechv2.dataclasses.LabelElements
import com.example.coretechv2.dataclasses.LabelStyles
import com.example.coretechv2.repository.generateCODE128Barcode
import com.example.coretechv2.repository.generateEAN13Barcode
import com.example.coretechv2.repository.generateQRCode
import com.example.coretechv2.ui.component.ButtonMessage
import com.example.coretechv2.ui.component.LabelPreview
import com.example.coretechv2.ui.component.PopupWindow
import com.example.coretechv2.ui.component.mmToDp
import com.example.coretechv2.ui.component.mmToPixels
import com.example.coretechv2.ui.component.ptToSp
import com.example.coretechv2.ui.screen.AssemblyOrderDetails
import com.example.coretechv2.ui.theme.Helvetica
import com.example.coretechv2.ui.theme.PurpleGrey40
import com.example.coretechv2.ui.theme.scottBlue
import com.example.coretechv2.viewmodel.SharedViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ThreeSectionLabel(labelElement: LabelElements) {
    Box(modifier = Modifier
        .width(mmToDp(labelElement.labelLayout.PAGEWIDTH))
        .height(mmToDp(labelElement.labelLayout.PAGEHEIGHT))
        .background(color = White)) {
        Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.Bottom) {
            Column(modifier = Modifier
                .width(mmToDp(((labelElement.labelLayout.PAGEWIDTH - labelElement.labelLayout.MIDWIDTH) / 2)))
                .padding(start = mmToDp(labelElement.labelLayout.PAGEPAD), top = mmToDp(labelElement.labelLayout.PAGEPAD), bottom = mmToDp(labelElement.labelLayout.PAGEPAD), end = mmToDp(labelElement.labelLayout.PAGEPAD))) {
                if (labelElement.classInfo.DIRECTIONS.isNotEmpty()) {
                    Text(text = "DIRECTIONS:", style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.SUBHEADFS)))
                    Text(text = labelElement.classInfo.DIRECTIONS, style = ProductLabelBody.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                    Spacer(modifier = Modifier.height(mmToDp(labelElement.labelLayout.SPACEING)))
                }
                if (labelElement.classInfo.HELPTIPTIN.isNotEmpty() && labelElement.itemInfo.LABELSTYLE.ProductIsTin()) {
                    Text(text = "HELPFUL TIPS:", style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.SUBHEADFS)))
                    Text(text = labelElement.classInfo.HELPTIPTIN, style = ProductLabelBody.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                    Spacer(modifier = Modifier.height(mmToDp(labelElement.labelLayout.SPACEING)))
                } else if (labelElement.classInfo.HELPTIPPAIL.isNotEmpty()){
                    Text(text = "HELPFUL TIPS:", style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.SUBHEADFS)))
                    Text(text = labelElement.classInfo.HELPTIPPAIL, style = ProductLabelBody.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                    Spacer(modifier = Modifier.height(mmToDp(labelElement.labelLayout.SPACEING)))
                }

                Image(
                    painter = painterResource(id = R.drawable.company_logo),
                    contentDescription = null,
                    modifier = Modifier
                        .width(mmToDp(labelElement.labelLayout.LOGOSMALLSIZE))
                        .padding(top = mmToDp(labelElement.labelLayout.SPACEING), bottom = mmToDp(1.0)),
                    alignment = Alignment.BottomStart
                )
                Text(
                    "Unit 1/28 Lee Holm Road St. Marys NSW 2760\nP: (02) 9623 6444   E: sales@scottech.com.au\nwww.scottech.com.au",
                    style = ProductLabelBody.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS))
                )
            }
            Column(modifier = Modifier.width(mmToDp(labelElement.labelLayout.MIDWIDTH)), horizontalAlignment = Alignment.CenterHorizontally) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(mmToDp(labelElement.labelLayout.COLORPOS)),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_short),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = mmToDp(labelElement.labelLayout.LOGOOFFSET), bottom = mmToDp(labelElement.labelLayout.LOGOPOS))
                            .size(mmToDp(labelElement.labelLayout.LOGOSIZE)),
                        alignment = Alignment.BottomCenter
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1F)
                ) {
                    var fontColor = White
                    var colorModifier = Modifier.fillMaxSize().background(Color("#${labelElement.classInfo.COLOUR}".toColorInt()))
                    var variantModifier: Modifier = Modifier
                    var sizeModifier: Modifier = Modifier
                    if (labelElement.classInfo.COLOUR == "00000000") {
                        fontColor = Black
                        colorModifier = Modifier.fillMaxSize().border(BorderStroke((mmToDp(2.0)), Black))
                        variantModifier = Modifier.background(White)
                        sizeModifier = Modifier.background(White).border(BorderStroke(1.dp, Black)).padding(vertical = mmToDp(labelElement.labelLayout.VARIANTPADVER), horizontal = mmToDp(labelElement.labelLayout.VARIANTPADHOZ))
                    }

                    Box(
                        modifier = colorModifier.clipToBounds()
                    ) {
                        if (labelElement.classInfo.COLOUR == "00000000") {
                            Image(
                                painter = painterResource(id = R.drawable.strips_red),
                                contentDescription = null,
                                modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter).offset(y = mmToDp(labelElement.labelLayout.STRIPSPOS))
                            )
                        }
                        if (labelElement.itemInfo.MIDDLENAME.isNotEmpty()) {
                            Column(modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.TopCenter)
                                .padding(top = mmToDp(labelElement.labelLayout.MIDNAMEPOS)), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = labelElement.itemInfo.MIDDLENAME.uppercase(),textAlign = TextAlign.Center, style = ProductLabelHeader.copy(fontSize = ptToSp(labelElement.labelLayout.BOTTOMNAMEFS), color = fontColor)
                                )
                                Text(
                                    text = DGLine(labelElement.dgInfo),
                                    modifier = Modifier
                                        .padding(top = mmToDp(labelElement.labelLayout.DGPOS)),textAlign = TextAlign.Center,
                                    style = ProductLabelNormal.copy(fontSize = ptToSp(labelElement.labelLayout.DGFS))
                                )
                            }

                        } else{
                            Text(
                                text = labelElement.itemInfo.TOPNAME.uppercase(),
                                modifier = Modifier
                                    .padding(top = mmToDp(labelElement.labelLayout.TOPNAMEPOS))
                                    .align(Alignment.TopCenter),
                                style = ProductLabelHeader.copy(fontSize = ptToSp(labelElement.labelLayout.TOPNAMEFS), color = fontColor)
                            )
                            Column(modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.TopCenter)
                                .padding(top = mmToDp(labelElement.labelLayout.BOTTOMNAMEPOS)), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = labelElement.itemInfo.BOTTOMNAME.uppercase(),textAlign = TextAlign.Center, style = ProductLabelHeader.copy(fontSize = ptToSp(labelElement.labelLayout.BOTTOMNAMEFS), color = fontColor)
                                )
                                Text(
                                    text = DGLine(labelElement.dgInfo),
                                    modifier = Modifier
                                        .padding(top = mmToDp(labelElement.labelLayout.DGPOS)),textAlign = TextAlign.Center,
                                    style = ProductLabelNormal.copy(fontSize = ptToSp(labelElement.labelLayout.DGFS))
                                )
                            }
                        }
                        Text(
                            text = labelElement.itemInfo.VARIANT.uppercase(),
                            modifier = Modifier
                                .padding(top = mmToDp(labelElement.labelLayout.VARIANTPOS))
                                .align(Alignment.TopCenter)
                                .then(variantModifier)
                                .border(BorderStroke(1.dp, Black))
                                .padding(vertical = mmToDp(labelElement.labelLayout.VARIANTPADVER), horizontal = mmToDp(labelElement.labelLayout.VARIANTPADHOZ)),
                            style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.VARIANTFS))
                        )
                        Text(
                            text = labelElement.itemInfo.SIZE,
                            modifier = Modifier
                                .padding(top = mmToDp(labelElement.labelLayout.SIZEPOS))
                                .align(Alignment.TopCenter)
                                .then(sizeModifier),
                            style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.SIZEFS))
                        )
                        val fields = listOf(
                            labelElement.itemInfo.TOPNAME,
                            labelElement.itemInfo.BOTTOMNAME,
                            labelElement.itemInfo.MIDDLENAME,
                            labelElement.itemInfo.VARIANT
                        )
                        val hasNeutral = fields.any { it.contains("neutral", ignoreCase = true) }
                        val hasGelOrFlow = fields.any { it.contains("gelcoat", ignoreCase = true) || it.contains("flowcoat", ignoreCase = true) }
                        if (hasNeutral && hasGelOrFlow) {
                            Image(
                                painter = painterResource(id = R.drawable.add_pigment_banner),
                                contentDescription = null,
                                modifier = Modifier.size(mmToDp(labelElement.labelLayout.ADDPIGMENTSIZE))
                            )
                        }

                    }
                }
            }
            Column(modifier = Modifier
                .width(mmToDp(((labelElement.labelLayout.PAGEWIDTH - labelElement.labelLayout.MIDWIDTH) / 2)))
                .padding(start = mmToDp(labelElement.labelLayout.PAGEPAD), top = mmToDp(labelElement.labelLayout.PAGEPAD), bottom = mmToDp(labelElement.labelLayout.PAGEPAD), end = mmToDp(labelElement.labelLayout.PAGEPAD))) {
                if (labelElement.classInfo.PICTOGRAM1.isNotEmpty() || labelElement.classInfo.PICTOGRAM2.isNotEmpty() || labelElement.classInfo.WARNINGSIGN.isNotEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        val pictogramL = Hazards.fromString(labelElement.classInfo.PICTOGRAM1.lowercase())
                        val pictogramR = Hazards.fromString(labelElement.classInfo.PICTOGRAM2.lowercase())
                        Image(
                            painter = painterResource(id = pictogramL.hazardImage()),
                            contentDescription = null,
                            modifier = Modifier
                                .height(mmToDp(labelElement.labelLayout.PICTOSIZE))
                                .padding(end = mmToDp(labelElement.labelLayout.PICTOGAP)),
                            alignment = Alignment.BottomStart
                        )
                        Image(
                            painter = painterResource(id = pictogramR.hazardImage()),
                            contentDescription = null,
                            modifier = Modifier
                                .height(mmToDp(labelElement.labelLayout.PICTOSIZE))
                                .padding(start = mmToDp(labelElement.labelLayout.PICTOGAP)),
                            alignment = Alignment.BottomStart
                        )
                        Text(
                            text = labelElement.classInfo.WARNINGSIGN.uppercase(),
                            modifier = Modifier.padding(top = mmToDp(labelElement.labelLayout.WARNINGPOS)),
                            style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.HEADINGFS))
                        )
                    }
                    Spacer(modifier = Modifier.height(mmToDp(labelElement.labelLayout.SPACEING)))
                    Spacer(modifier = Modifier.height(mmToDp(labelElement.labelLayout.SPACEING)))
                }
                if (labelElement.classInfo.PRECAUTIONS.isNotEmpty()) {
                    Text(text = "PRECAUTIONS:", style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.SUBHEADFS)))
                    Text(text = labelElement.classInfo.PRECAUTIONS, style = ProductLabelBody.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                    Spacer(modifier = Modifier.height(mmToDp(labelElement.labelLayout.SPACEING)))
                }
                if (labelElement.classInfo.INGESTION.isNotEmpty() || labelElement.classInfo.SKINCONTACT.isNotEmpty() || labelElement.classInfo.EYECONTACT.isNotEmpty()) {
                    Text(text = "FIRST AID:", style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.SUBHEADFS)))
                    if (labelElement.classInfo.INGESTION.isNotEmpty()) {
                        Text(text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("INGESTION: ") }
                            append(labelElement.classInfo.INGESTION)
                        }, style = ProductLabelBody.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                    }
                    if (labelElement.classInfo.SKINCONTACT.isNotEmpty()) {
                        Text(text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("SKIN CONTACT: ") }
                            append(labelElement.classInfo.SKINCONTACT)
                        }, style = ProductLabelBody.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                    }
                    if (labelElement.classInfo.EYECONTACT.isNotEmpty()) {
                        Text(text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("EYE CONTACT: ") }
                            append(labelElement.classInfo.EYECONTACT)
                        }, style = ProductLabelBody.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                    }
                    Spacer(modifier = Modifier.height(mmToDp(labelElement.labelLayout.SPACEING)))
                }
                if (labelElement.classInfo.SAFESTORAGE.isNotEmpty()) {
                    Text(text = "SAFE STORAGE:", style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.SUBHEADFS)))
                    Text(text = labelElement.classInfo.SAFESTORAGE, style = ProductLabelBody.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                    Spacer(modifier = Modifier.height(mmToDp(labelElement.labelLayout.SPACEING)))
                }
                if (labelElement.itemInfo.TOPNAME.contains("LSS/F6", ignoreCase = true)) {
                    Image(
                        painter = painterResource(id = R.drawable.transport_roads_logo),
                        contentDescription = null,
                        modifier = Modifier.height(mmToDp(labelElement.labelLayout.ROADMARINESIZE)),
                        alignment = Alignment.BottomStart
                    )
                }
                if (labelElement.itemInfo.ITEMBARCODE.isNotEmpty()) {
                    var codeText: String
                    if(labelElement.itemInfo.LABELSTYLE.ProductIsTin()){
                        codeText = labelElement.itemInfo.ITEMBARCODE
                    }else{
                        codeText ="(01)${labelElement.itemInfo.ITEMBARCODE}(10)${labelElement.sharedViewModel.currentOrderNumber.value}"
                    }
                    val qrBitmap = try {
                        generateEAN13Barcode(
                            text = codeText, width = 600, height = 120
                        )
                    } catch (_: Exception) {
                        generateCODE128Barcode(
                            text = codeText, width = 600, height = 120
                        )
                    }
                    Column(
                        modifier = Modifier
                            .width(mmToDp(((labelElement.labelLayout.PAGEWIDTH - labelElement.labelLayout.MIDWIDTH) / 2) * 0.60))
                            .align(Alignment.CenterHorizontally), horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(mmToDp(labelElement.labelLayout.SPACEING)))
                        Image(
                            bitmap = qrBitmap.asImageBitmap(),
                            contentDescription = "Barcode",
                        )
                        Text(text = labelElement.itemInfo.ITEMBARCODE, textAlign = TextAlign.Center, style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                    }
                } else {
                    Spacer(modifier = Modifier.height(mmToDp(labelElement.labelLayout.SPACEING)))
                }

            }
        }
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(all = mmToDp(labelElement.labelLayout.PAGEPAD)),contentAlignment = Alignment.TopEnd) {
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "Batch: #${labelElement.sharedViewModel.currentOrderNumber.value}", style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                val currentDate = LocalDate.now()
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                if (labelElement.itemInfo.BESTBEFORE <= 0) {
                    Text(text = "Packed: ${currentDate.format(formatter)}", style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                }else{
                    Text(text = "Best Before: ${currentDate.plusDays(labelElement.itemInfo.BESTBEFORE.toLong()).format(formatter)}", style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))

                }
            }
        }
        Box(modifier = Modifier.padding(all = mmToDp(labelElement.labelLayout.PAGEPAD))) {
            if (labelElement.itemInfo.QRCODE.isNotEmpty()) {
                val qrBitmap = generateQRCode(
                    text = labelElement.itemInfo.QRCODE, size = mmToPixels(labelElement.labelLayout.QRCODESIZE)
                )
                Column(modifier = Modifier.width(mmToDp(labelElement.labelLayout.QRCODESIZE)), horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        bitmap = qrBitmap.asImageBitmap(),
                        contentDescription = "QR Code"
                    )
                    Text(text = "Scan for\nmore Info", textAlign = TextAlign.Center, style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                }
            }
        }
    }
}

@Composable
fun TwoSectionLabel(labelElement: LabelElements) {
    Box(modifier = Modifier
        .width(mmToDp(labelElement.labelLayout.PAGEWIDTH))
        .height(mmToDp(labelElement.labelLayout.PAGEHEIGHT))
        .background(color = White)) {
        Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.Bottom) {
            Column(modifier = Modifier.width(mmToDp(labelElement.labelLayout.MIDWIDTH)), horizontalAlignment = Alignment.CenterHorizontally) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(mmToDp(labelElement.labelLayout.COLORPOS)),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_short),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = mmToDp(labelElement.labelLayout.LOGOOFFSET), bottom = mmToDp(labelElement.labelLayout.LOGOPOS))
                            .size(mmToDp(labelElement.labelLayout.LOGOSIZE)),
                        alignment = Alignment.BottomCenter
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1F)
                ) {
                    var fontColor = White
                    var colorModifier = Modifier.fillMaxSize().background(Color("#${labelElement.classInfo.COLOUR}".toColorInt()))
                    var variantModifier: Modifier = Modifier
                    var sizeModifier: Modifier = Modifier
                    if (labelElement.classInfo.COLOUR == "00000000") {
                        fontColor = Black
                        colorModifier = Modifier.fillMaxSize().border(BorderStroke((mmToDp(2.0)), Black))
                        variantModifier = Modifier.background(White)
                        sizeModifier = Modifier.background(White).border(BorderStroke(1.dp, Black)).padding(vertical = mmToDp(labelElement.labelLayout.VARIANTPADVER), horizontal = mmToDp(labelElement.labelLayout.VARIANTPADHOZ))
                    }
                    Box(
                        modifier = colorModifier.clipToBounds()

                    ) {
                        if (labelElement.classInfo.COLOUR == "00000000") {
                            Image(
                                painter = painterResource(id = R.drawable.strips_red),
                                contentDescription = null,
                                modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter).offset(y = mmToDp(labelElement.labelLayout.STRIPSPOS))
                            )
                        }
                        if (labelElement.itemInfo.MIDDLENAME.isNotEmpty()) {
                            Column(modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.TopCenter)
                                .padding(top = mmToDp(labelElement.labelLayout.MIDNAMEPOS)), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = labelElement.itemInfo.MIDDLENAME.uppercase(),textAlign = TextAlign.Center, style = ProductLabelHeader.copy(fontSize = ptToSp(labelElement.labelLayout.BOTTOMNAMEFS), color = fontColor)
                                )
                                Text(
                                    text = DGLine(labelElement.dgInfo),
                                    modifier = Modifier
                                        .padding(top = mmToDp(labelElement.labelLayout.DGPOS)),textAlign = TextAlign.Center,
                                    style = ProductLabelNormal.copy(fontSize = ptToSp(labelElement.labelLayout.DGFS))
                                )
                            }

                        } else{
                            Text(
                                text = labelElement.itemInfo.TOPNAME.uppercase(),
                                modifier = Modifier
                                    .padding(top = mmToDp(labelElement.labelLayout.TOPNAMEPOS))
                                    .align(Alignment.TopCenter),
                                style = ProductLabelHeader.copy(fontSize = ptToSp(labelElement.labelLayout.TOPNAMEFS), color = fontColor)
                            )
                            Column(modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.TopCenter)
                                .padding(top = mmToDp(labelElement.labelLayout.BOTTOMNAMEPOS)), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = labelElement.itemInfo.BOTTOMNAME.uppercase(),textAlign = TextAlign.Center, style = ProductLabelHeader.copy(fontSize = ptToSp(labelElement.labelLayout.BOTTOMNAMEFS), color = fontColor)
                                )
                                Text(
                                    text = DGLine(labelElement.dgInfo),
                                    modifier = Modifier
                                        .padding(top = mmToDp(labelElement.labelLayout.DGPOS)),textAlign = TextAlign.Center,
                                    style = ProductLabelNormal.copy(fontSize = ptToSp(labelElement.labelLayout.DGFS))
                                )
                            }
                        }
                        Text(
                            text = labelElement.itemInfo.VARIANT.uppercase(),
                            modifier = Modifier
                                .padding(top = mmToDp(labelElement.labelLayout.VARIANTPOS))
                                .align(Alignment.TopCenter)
                                .then(variantModifier)
                                .border(BorderStroke(1.dp, Black))
                                .padding(vertical = mmToDp(labelElement.labelLayout.VARIANTPADVER), horizontal = mmToDp(labelElement.labelLayout.VARIANTPADHOZ)),
                            style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.VARIANTFS))
                        )
                        Text(
                            text = labelElement.itemInfo.SIZE,
                            modifier = Modifier
                                .padding(top = mmToDp(labelElement.labelLayout.SIZEPOS))
                                .align(Alignment.TopCenter)
                                .then(sizeModifier),
                            style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.SIZEFS))
                        )
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = mmToDp(labelElement.labelLayout.SIZEPOS + labelElement.labelLayout.SIZEFS))){
                            Spacer(modifier = Modifier.weight(7f))
                            Column() {
                                if (labelElement.classInfo.DIRECTIONS.isNotEmpty()) {
                                    Text(text = "DIRECTIONS:", style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.SUBHEADFS)))
                                    Text(text = labelElement.classInfo.DIRECTIONS, style = ProductLabelBody.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                                    Spacer(modifier = Modifier.height(mmToDp(labelElement.labelLayout.SPACEING)))
                                }
                            }
                            Spacer(modifier = Modifier.weight(2f))
                            Column() {
                                if (labelElement.classInfo.HELPTIPTIN.isNotEmpty() && labelElement.itemInfo.LABELSTYLE.ProductIsTin()) {
                                    Text(text = "HELPFUL TIPS:", style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.SUBHEADFS)))
                                    Text(text = labelElement.classInfo.HELPTIPTIN, style = ProductLabelBody.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                                    Spacer(modifier = Modifier.height(mmToDp(labelElement.labelLayout.SPACEING)))
                                } else if (labelElement.classInfo.HELPTIPPAIL.isNotEmpty()){
                                    Text(text = "HELPFUL TIPS:", style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.SUBHEADFS)))
                                    Text(text = labelElement.classInfo.HELPTIPPAIL, style = ProductLabelBody.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                                    Spacer(modifier = Modifier.height(mmToDp(labelElement.labelLayout.SPACEING)))
                                }
                            }
                            Spacer(modifier = Modifier.weight(5f))
                        }
                        val fields = listOf(
                            labelElement.itemInfo.TOPNAME,
                            labelElement.itemInfo.BOTTOMNAME,
                            labelElement.itemInfo.MIDDLENAME,
                            labelElement.itemInfo.VARIANT
                        )
                        val hasNeutral = fields.any { it.contains("neutral", ignoreCase = true) }
                        val hasGelOrFlow = fields.any { it.contains("gelcoat", ignoreCase = true) || it.contains("flowcoat", ignoreCase = true) }
                        if (hasNeutral && hasGelOrFlow) {
                            Image(
                                painter = painterResource(id = R.drawable.add_pigment_banner),
                                contentDescription = null,
                                modifier = Modifier.size(mmToDp(labelElement.labelLayout.ADDPIGMENTSIZE))
                            )
                        }

                    }
                }
            }
            Column(modifier = Modifier
                .width(mmToDp(((labelElement.labelLayout.PAGEWIDTH - labelElement.labelLayout.MIDWIDTH))))
                .padding(start = mmToDp(labelElement.labelLayout.PAGEPAD), top = mmToDp(labelElement.labelLayout.PAGEPAD), bottom = mmToDp(labelElement.labelLayout.PAGEPAD), end = mmToDp(labelElement.labelLayout.PAGEPAD))) {
                if (labelElement.classInfo.PICTOGRAM1.isNotEmpty() || labelElement.classInfo.PICTOGRAM2.isNotEmpty() || labelElement.classInfo.WARNINGSIGN.isNotEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        val pictogramL = Hazards.fromString(labelElement.classInfo.PICTOGRAM1.lowercase())
                        val pictogramR = Hazards.fromString(labelElement.classInfo.PICTOGRAM2.lowercase())
                        Image(
                            painter = painterResource(id = pictogramL.hazardImage()),
                            contentDescription = null,
                            modifier = Modifier
                                .height(mmToDp(labelElement.labelLayout.PICTOSIZE))
                                .padding(end = mmToDp(labelElement.labelLayout.PICTOGAP)),
                            alignment = Alignment.BottomStart
                        )
                        Image(
                            painter = painterResource(id = pictogramR.hazardImage()),
                            contentDescription = null,
                            modifier = Modifier
                                .height(mmToDp(labelElement.labelLayout.PICTOSIZE))
                                .padding(start = mmToDp(labelElement.labelLayout.PICTOGAP)),
                            alignment = Alignment.BottomStart
                        )
                        Text(
                            text = labelElement.classInfo.WARNINGSIGN.uppercase(),
                            modifier = Modifier.padding(top = mmToDp(labelElement.labelLayout.WARNINGPOS)),
                            style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.HEADINGFS))
                        )
                    }
                    Spacer(modifier = Modifier.height(mmToDp(labelElement.labelLayout.SPACEING)))
                    Spacer(modifier = Modifier.height(mmToDp(labelElement.labelLayout.SPACEING)))

                }
                if (labelElement.classInfo.PRECAUTIONS.isNotEmpty()) {
                    Text(text = "PRECAUTIONS:", style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.SUBHEADFS)))
                    Text(text = labelElement.classInfo.PRECAUTIONS, style = ProductLabelBody.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                    Spacer(modifier = Modifier.height(mmToDp(labelElement.labelLayout.SPACEING)))
                }
                if (labelElement.classInfo.INGESTION.isNotEmpty() || labelElement.classInfo.SKINCONTACT.isNotEmpty() || labelElement.classInfo.EYECONTACT.isNotEmpty()) {
                    Text(text = "FIRST AID:", style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.SUBHEADFS)))
                    if (labelElement.classInfo.INGESTION.isNotEmpty()) {
                        Text(text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("INGESTION: ") }
                            append(labelElement.classInfo.INGESTION)
                        }, style = ProductLabelBody.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                    }
                    if (labelElement.classInfo.SKINCONTACT.isNotEmpty()) {
                        Text(text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("SKIN CONTACT: ") }
                            append(labelElement.classInfo.SKINCONTACT)
                        }, style = ProductLabelBody.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                    }
                    if (labelElement.classInfo.EYECONTACT.isNotEmpty()) {
                        Text(text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("EYE CONTACT: ") }
                            append(labelElement.classInfo.EYECONTACT)
                        }, style = ProductLabelBody.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                    }
                    Spacer(modifier = Modifier.height(mmToDp(labelElement.labelLayout.SPACEING)))
                }
                if (labelElement.classInfo.SAFESTORAGE.isNotEmpty()) {
                    Text(text = "SAFE STORAGE:", style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.SUBHEADFS)))
                    Text(text = labelElement.classInfo.SAFESTORAGE, style = ProductLabelBody.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                    Spacer(modifier = Modifier.height(mmToDp(labelElement.labelLayout.SPACEING)))
                }
                if (labelElement.itemInfo.TOPNAME.contains("LSS/F6", ignoreCase = true)) {
                    Image(
                        painter = painterResource(id = R.drawable.transport_roads_logo),
                        contentDescription = null,
                        modifier = Modifier.height(mmToDp(labelElement.labelLayout.ROADMARINESIZE)),
                        alignment = Alignment.BottomStart
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(top = mmToDp(labelElement.labelLayout.SPACEING/2)), color = scottBlue)
                Image(
                    painter = painterResource(id = R.drawable.company_logo),
                    contentDescription = null,
                    modifier = Modifier
                        .width(mmToDp(labelElement.labelLayout.LOGOSMALLSIZE))
                        .padding(top = mmToDp(labelElement.labelLayout.SPACEING / 2), bottom = mmToDp(1.0)),
                    alignment = Alignment.BottomStart
                )
                Text(
                    "Unit 1/28 Lee Holm Road St. Marys NSW 2760\nP: (02) 9623 6444   E: sales@scottech.com.au\nwww.scottech.com.au",
                    style = ProductLabelBody.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS))
                )

                if (labelElement.itemInfo.ITEMBARCODE.isNotEmpty()) {
                    val codeText: String
                    if(labelElement.itemInfo.LABELSTYLE.ProductIsTin()){
                        codeText = labelElement.itemInfo.ITEMBARCODE
                    }else{
                        codeText ="(01)${labelElement.itemInfo.ITEMBARCODE}(10)${labelElement.sharedViewModel.currentOrderNumber.value}"
                    }
                    val qrBitmap = try {
                        generateEAN13Barcode(
                            text = codeText, width = 600, height = 120
                        )
                    } catch (_: Exception) {
                        generateCODE128Barcode(
                            text = codeText, width = 600, height = 120
                        )
                    }
                    Column(
                        modifier = Modifier
                            .width(mmToDp(((labelElement.labelLayout.PAGEWIDTH - labelElement.labelLayout.MIDWIDTH)) * 0.60))
                            .align(Alignment.CenterHorizontally), horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(mmToDp(labelElement.labelLayout.SPACEING)))
                        Image(
                            bitmap = qrBitmap.asImageBitmap(),
                            contentDescription = "Barcode",
                        )
                        Text(text = labelElement.itemInfo.ITEMBARCODE, textAlign = TextAlign.Center, style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                    }
                }
            }
        }

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(all = mmToDp(labelElement.labelLayout.PAGEPAD)),contentAlignment = Alignment.TopEnd) {
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "Batch: #${labelElement.sharedViewModel.currentOrderNumber.value}", style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                val currentDate = LocalDate.now()
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                if (labelElement.itemInfo.BESTBEFORE <= 0) {
                    Text(text = "Packed: ${currentDate.format(formatter)}", style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                }else{
                    Text(text = "Best Before: ${currentDate.plusDays(labelElement.itemInfo.BESTBEFORE.toLong()).format(formatter)}", style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))

                }
                Spacer(modifier = Modifier.height(mmToDp(labelElement.labelLayout.SPACEING)))
                Spacer(modifier = Modifier.height(mmToDp(labelElement.labelLayout.SPACEING)))
                Spacer(modifier = Modifier.height(mmToDp(labelElement.labelLayout.SPACEING)))
                if (labelElement.itemInfo.QRCODE.isNotEmpty()) {
                    val qrBitmap = generateQRCode(
                        text = labelElement.itemInfo.QRCODE, size = mmToPixels(labelElement.labelLayout.QRCODESIZE)
                    )
                    Column(modifier = Modifier.width(mmToDp(labelElement.labelLayout.QRCODESIZE)), horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            bitmap = qrBitmap.asImageBitmap(),
                            contentDescription = "QR Code"
                        )
                        Text(text = "Scan for\nmore Info", textAlign = TextAlign.Center, style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                    }
                }
            }
        }
    }
}

@Composable
fun SimpleLabel(labelElement: LabelElements) {
    Box(
        modifier = Modifier
            .width(mmToDp(labelElement.labelLayout.PAGEWIDTH))
            .height(mmToDp(labelElement.labelLayout.PAGEHEIGHT))
            .background(color = White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = mmToDp(3.0), vertical = mmToDp(5.0))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(mmToDp(47.0))
                    .border(BorderStroke(1.dp, Black)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var barcodePresent = false
                if (labelElement.itemInfo.ITEMBARCODE.isNotEmpty()){
                    barcodePresent = true
                }
                if (labelElement.itemInfo.MIDDLENAME.isNotEmpty()) {
                    Text(text = labelElement.itemInfo.MIDDLENAME.uppercase(), textAlign = TextAlign.Center, style = ProductLabelHeader.copy(fontSize = ptToSp(SimpleLabelResize(labelElement.labelLayout.BOTTOMNAMEFS, barcodePresent))))
                } else {
                    Text(text = labelElement.itemInfo.TOPNAME.uppercase(), style = ProductLabelHeader.copy(fontSize = ptToSp(SimpleLabelResize(labelElement.labelLayout.TOPNAMEFS, barcodePresent))))
                    Spacer(modifier = Modifier.height(mmToDp(SimpleLabelResize(labelElement.labelLayout.SPACEING, barcodePresent)/2)))
                    Text(text = labelElement.itemInfo.BOTTOMNAME.uppercase(), textAlign = TextAlign.Center, style = ProductLabelHeader.copy(fontSize = ptToSp(SimpleLabelResize(labelElement.labelLayout.BOTTOMNAMEFS, barcodePresent))))
                }
                Spacer(modifier = Modifier.height(mmToDp(SimpleLabelResize(labelElement.labelLayout.SPACEING, barcodePresent)/2)))
                Spacer(modifier = Modifier.height(mmToDp(SimpleLabelResize(labelElement.labelLayout.SPACEING, barcodePresent)/2)))
                if (labelElement.itemInfo.BOXQTY > 0) {
                    Text(
                        text = "${labelElement.itemInfo.BOXQTY} X ${labelElement.itemInfo.SIZE}", textAlign = TextAlign.Center, style = ProductLabelHeader.copy(fontSize = ptToSp(labelElement.labelLayout.TOPNAMEFS))
                    )
                } else {
                    Text(
                        text = labelElement.itemInfo.SIZE, textAlign = TextAlign.Center, style = ProductLabelHeader.copy(fontSize = ptToSp(labelElement.labelLayout.TOPNAMEFS))
                    )
                }

                if (barcodePresent) {
                    var codeText: String
                    if (labelElement.itemInfo.BOXQTY > 0 && labelElement.sharedViewModel.currentOrderNumber.value.isNotEmpty()) {
                        codeText = "(01)${labelElement.itemInfo.ITEMBARCODE}(10)${labelElement.sharedViewModel.currentOrderNumber.value}(37)${labelElement.itemInfo.BOXQTY}"
                    } else if (labelElement.itemInfo.BOXQTY > 0) {
                        codeText = "(01)${labelElement.itemInfo.ITEMBARCODE}(37)${labelElement.itemInfo.BOXQTY}"
                    } else if (labelElement.sharedViewModel.currentOrderNumber.value.isNotEmpty()) {
                        codeText = "(01)${labelElement.itemInfo.ITEMBARCODE}(10)${labelElement.sharedViewModel.currentOrderNumber.value}"
                    } else {
                        codeText = "(01)${labelElement.itemInfo.ITEMBARCODE}"
                    }
                    val barcodeBitmap = generateCODE128Barcode(text = codeText, width = 600, height = 120)

                    Spacer(modifier = Modifier.height(mmToDp(3.0)))

                        Image(
                            bitmap = barcodeBitmap.asImageBitmap(),
                            contentDescription = "Barcode",
                            modifier = Modifier.height(mmToDp(14.0))
                        )
                        Spacer(modifier = Modifier.height(mmToDp(1.0)))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Spacer(modifier = Modifier.weight(1f))
                            val currentDate = LocalDate.now()
                            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                            Text(text = "Boxed: ${currentDate.format(formatter)}", style = ProductLabelNormal.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                            Spacer(modifier = Modifier.width(mmToDp(10.0)))
                            Text(text = labelElement.itemInfo.ITEMBARCODE, textAlign = TextAlign.Center, style = ProductLabelNormal.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                            if (labelElement.sharedViewModel.currentOrderNumber.value.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(mmToDp(10.0)))
                                Text(text = "Batch: #${labelElement.sharedViewModel.currentOrderNumber.value}", style = ProductLabelNormal.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                            }
                            Spacer(modifier = Modifier.weight(1f))
                        }


                }else{
                    Spacer(modifier = Modifier.height(mmToDp(SimpleLabelResize(labelElement.labelLayout.SPACEING, barcodePresent)/2)))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        val currentDate = LocalDate.now()
                        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                        Text(text = "Boxed: ${currentDate.format(formatter)}", style = ProductLabelNormal.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                        if (labelElement.sharedViewModel.currentOrderNumber.value.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(mmToDp(10.0)))
                            Text(text = "Batch: #${labelElement.sharedViewModel.currentOrderNumber.value}", style = ProductLabelNormal.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                        }
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            Row(modifier = Modifier
                .fillMaxWidth()
            ){
                Column(modifier = Modifier
                    .width(mmToDp(labelElement.labelLayout.PAGEWIDTH/2)).fillMaxHeight()) {
                    Spacer(modifier = Modifier.height(mmToDp(2.5)))
                    Image(
                        painter = painterResource(id = R.drawable.company_logo),
                        contentDescription = null,
                        modifier = Modifier
                            .height(mmToDp(labelElement.labelLayout.LOGOSMALLSIZE))
                            .padding(top = mmToDp(labelElement.labelLayout.SPACEING / 2), bottom = mmToDp(1.0)),
                        alignment = Alignment.BottomStart
                    )
                }
                Column(modifier = Modifier
                    .width(mmToDp(labelElement.labelLayout.PAGEWIDTH/2)).fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    if (labelElement.dgInfo != null){
                        Spacer(modifier = Modifier.height(mmToDp(0.5)))
                        if(labelElement.dgInfo.DGQUANTITY > 0){
                        Image(
                            painter = painterResource(id = Hazards.fromString(labelElement.dgInfo.DGCLASS).hazardImage()),
                            contentDescription = null,
                            modifier = Modifier.size(mmToDp(labelElement.labelLayout.PICTOSIZE))
                        )
                        }else{
                            Image(
                                painter = painterResource(id = Hazards.LIMITED_QUANTITES.hazardImage()),
                                contentDescription = null,
                                modifier = Modifier.size(mmToDp(labelElement.labelLayout.PICTOSIZE))
                            )
                        }
                        Text(DGLine(labelElement.dgInfo), style = ProductLabelBody.copy(fontSize = ptToSp(labelElement.labelLayout.SUBHEADFS)))
                    }else{
                        Box(modifier = Modifier.fillMaxSize()){
                            Text(DGLine(labelElement.dgInfo), modifier = Modifier.align(Alignment.Center), textAlign = TextAlign.Center, style = ProductLabelBody.copy(fontSize = ptToSp(labelElement.labelLayout.SUBHEADFS)))
                        }
                    }

                }

            }
        }
    }
}

@Composable
fun TuffStickThreeSectionLabel(labelElement: LabelElements) {
    Box(modifier = Modifier
        .width(mmToDp(labelElement.labelLayout.PAGEWIDTH))
        .height(mmToDp(labelElement.labelLayout.PAGEHEIGHT))
        .background(color = White)) {
        Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.Bottom) {
            Column(modifier = Modifier.width(mmToDp(((labelElement.labelLayout.PAGEWIDTH - labelElement.labelLayout.MIDWIDTH) / 2))).padding(start = mmToDp(labelElement.labelLayout.PAGEPAD), top = mmToDp(labelElement.labelLayout.PAGEPAD), bottom = mmToDp(labelElement.labelLayout.PAGEPAD), end = mmToDp(labelElement.labelLayout.PAGEPAD))) {
                Text(text = labelElement.classInfo.HELPTIPPAIL, style = ProductLabelBody.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                Spacer(modifier = Modifier.height(mmToDp(labelElement.labelLayout.SPACEING)))
                Text(text = "DIRECTIONS:", style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.SUBHEADFS)))
                Text(text = labelElement.classInfo.DIRECTIONS, style = ProductLabelBody.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                Spacer(modifier = Modifier.height(mmToDp(labelElement.labelLayout.SPACEING)))

                Image(
                    painter = painterResource(id = R.drawable.company_logo),
                    contentDescription = null,
                    modifier = Modifier
                        .width(mmToDp(labelElement.labelLayout.LOGOSMALLSIZE))
                        .padding(top = mmToDp(labelElement.labelLayout.SPACEING), bottom = mmToDp(1.0)),
                    alignment = Alignment.BottomStart
                )
                Text(
                    "Unit 1/28 Lee Holm Road St. Marys NSW 2760\nP: (02) 9623 6444   E: sales@scottech.com.au\nwww.scottech.com.au",
                    style = ProductLabelBody.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS))
                )
            }
            Column(modifier = Modifier.width(mmToDp(labelElement.labelLayout.MIDWIDTH)), horizontalAlignment = Alignment.CenterHorizontally) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(mmToDp(labelElement.labelLayout.COLORPOS)),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.tuff_logo),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth(),
                        alignment = Alignment.BottomCenter
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1F)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = "FIRE RETARDANT ADHESIVE/SEALER",
                            modifier = Modifier
                                .padding(top = mmToDp(labelElement.labelLayout.TOPNAMEPOS))
                                .align(Alignment.TopCenter)
                                .padding(vertical = mmToDp(labelElement.labelLayout.VARIANTPADVER), horizontal = mmToDp(labelElement.labelLayout.VARIANTPADHOZ)),

                            style = ProductLabelHeader.copy(fontSize = ptToSp(labelElement.labelLayout.TOPNAMEFS)), color = Color(0xFF787878)
                        )
                        Text(
                            text = labelElement.itemInfo.VARIANT.uppercase(),
                            modifier = Modifier
                                .padding(top = mmToDp(labelElement.labelLayout.VARIANTPOS))
                                .align(Alignment.TopCenter)
                                .border(BorderStroke(1.dp, Black))
                                .padding(vertical = mmToDp(labelElement.labelLayout.VARIANTPADVER), horizontal = mmToDp(labelElement.labelLayout.VARIANTPADHOZ)),
                            style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.VARIANTFS))
                        )
                        Text(
                            text = labelElement.itemInfo.SIZE,
                            modifier = Modifier
                                .padding(top = mmToDp(labelElement.labelLayout.SIZEPOS))
                                .align(Alignment.TopCenter),
                            style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.SIZEFS))
                        )
                    }
                }
            }
            Column(modifier = Modifier.width(mmToDp(((labelElement.labelLayout.PAGEWIDTH - labelElement.labelLayout.MIDWIDTH) / 2))).padding(start = mmToDp(labelElement.labelLayout.PAGEPAD), top = mmToDp(labelElement.labelLayout.PAGEPAD), bottom = mmToDp(labelElement.labelLayout.PAGEPAD), end = mmToDp(labelElement.labelLayout.PAGEPAD))) {
                if (labelElement.classInfo.PICTOGRAM1.isNotEmpty() || labelElement.classInfo.PICTOGRAM2.isNotEmpty() || labelElement.classInfo.WARNINGSIGN.isNotEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        val pictogramL = Hazards.fromString(labelElement.classInfo.PICTOGRAM1.lowercase())
                        val pictogramR = Hazards.fromString(labelElement.classInfo.PICTOGRAM2.lowercase())
                        Image(
                            painter = painterResource(id = pictogramL.hazardImage()),
                            contentDescription = null,
                            modifier = Modifier
                                .height(mmToDp(labelElement.labelLayout.PICTOSIZE))
                                .padding(end = mmToDp(labelElement.labelLayout.PICTOGAP)),
                            alignment = Alignment.BottomStart
                        )
                        Image(
                            painter = painterResource(id = pictogramR.hazardImage()),
                            contentDescription = null,
                            modifier = Modifier
                                .height(mmToDp(labelElement.labelLayout.PICTOSIZE))
                                .padding(start = mmToDp(labelElement.labelLayout.PICTOGAP)),
                            alignment = Alignment.BottomStart
                        )
                        Text(
                            text = labelElement.classInfo.WARNINGSIGN.uppercase(),
                            modifier = Modifier.padding(top = mmToDp(labelElement.labelLayout.WARNINGPOS)),
                            style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.HEADINGFS))
                        )
                    }
                    Spacer(modifier = Modifier.height(mmToDp(labelElement.labelLayout.SPACEING)))
                    Spacer(modifier = Modifier.height(mmToDp(labelElement.labelLayout.SPACEING)))
                }
                if (labelElement.classInfo.PRECAUTIONS.isNotEmpty()) {
                    Text(text = "PRECAUTIONS:", style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.SUBHEADFS)))
                    Text(text = labelElement.classInfo.PRECAUTIONS, style = ProductLabelBody.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                    Spacer(modifier = Modifier.height(mmToDp(labelElement.labelLayout.SPACEING)))
                }
                if (labelElement.classInfo.INGESTION.isNotEmpty() || labelElement.classInfo.SKINCONTACT.isNotEmpty() || labelElement.classInfo.EYECONTACT.isNotEmpty()) {
                    Text(text = "FIRST AID:", style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.SUBHEADFS)))
                    if (labelElement.classInfo.INGESTION.isNotEmpty()) {
                        Text(text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("INGESTION: ") }
                            append(labelElement.classInfo.INGESTION)
                        }, style = ProductLabelBody.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                    }
                    if (labelElement.classInfo.SKINCONTACT.isNotEmpty()) {
                        Text(text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("SKIN CONTACT: ") }
                            append(labelElement.classInfo.SKINCONTACT)
                        }, style = ProductLabelBody.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                    }
                    if (labelElement.classInfo.EYECONTACT.isNotEmpty()) {
                        Text(text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("EYE CONTACT: ") }
                            append(labelElement.classInfo.EYECONTACT)
                        }, style = ProductLabelBody.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                    }
                    Spacer(modifier = Modifier.height(mmToDp(labelElement.labelLayout.SPACEING)))
                }
                if (labelElement.classInfo.SAFESTORAGE.isNotEmpty()) {
                    Text(text = "SAFE STORAGE:", style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.SUBHEADFS)))
                    Text(text = labelElement.classInfo.SAFESTORAGE, style = ProductLabelBody.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                    Spacer(modifier = Modifier.height(mmToDp(labelElement.labelLayout.SPACEING)))
                }
                Spacer(modifier = Modifier.height(mmToDp(labelElement.labelLayout.SPACEING)))
                Text(text = DGLine(labelElement.dgInfo), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.SUBHEADFS)))
                Spacer(modifier = Modifier.height(mmToDp(labelElement.labelLayout.SPACEING)))
                if (labelElement.itemInfo.ITEMBARCODE.isNotEmpty()) {
                    var codeText: String
                    if(labelElement.itemInfo.LABELSTYLE.ProductIsTin()){
                        codeText = labelElement.itemInfo.ITEMBARCODE
                    }else{
                        codeText ="(01)${labelElement.itemInfo.ITEMBARCODE}(10)${labelElement.sharedViewModel.currentOrderNumber.value}"
                    }
                    val qrBitmap = try {
                        generateEAN13Barcode(
                            text = codeText, width = 600, height = 120
                        )
                    } catch (_: Exception) {
                        generateCODE128Barcode(
                            text = codeText, width = 600, height = 120
                        )
                    }
                    Column(
                        modifier = Modifier
                            .width(mmToDp(((labelElement.labelLayout.PAGEWIDTH - labelElement.labelLayout.MIDWIDTH) / 2) * 0.60))
                            .align(Alignment.CenterHorizontally), horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(mmToDp(labelElement.labelLayout.SPACEING)))
                        Spacer(modifier = Modifier.height(mmToDp(labelElement.labelLayout.SPACEING)))
                        Image(
                            bitmap = qrBitmap.asImageBitmap(),
                            contentDescription = "Barcode",
                        )
                        Text(text = labelElement.itemInfo.ITEMBARCODE, textAlign = TextAlign.Center, style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                    }
                } else {
                    Spacer(modifier = Modifier.height(mmToDp(labelElement.labelLayout.SPACEING)))
                }

            }
        }
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(all = mmToDp(labelElement.labelLayout.PAGEPAD)),contentAlignment = Alignment.TopEnd) {
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "Batch: #${labelElement.sharedViewModel.currentOrderNumber.value}", style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                val currentDate = LocalDate.now()
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                if (labelElement.itemInfo.BESTBEFORE <= 0) {
                    Text(text = "Packed: ${currentDate.format(formatter)}", style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                }else{
                    Text(text = "Best Before: ${currentDate.plusDays(labelElement.itemInfo.BESTBEFORE.toLong()).format(formatter)}", style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))

                }
            }
        }
        Box(modifier = Modifier.padding(all = mmToDp(labelElement.labelLayout.PAGEPAD))) {
            if (labelElement.itemInfo.QRCODE.isNotEmpty()) {
                val qrBitmap = generateQRCode(
                    text = labelElement.itemInfo.QRCODE, size = mmToPixels(labelElement.labelLayout.QRCODESIZE)
                )
                Column(modifier = Modifier.width(mmToDp(labelElement.labelLayout.QRCODESIZE)), horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        bitmap = qrBitmap.asImageBitmap(),
                        contentDescription = "QR Code"
                    )
                    Text(text = "Scan for\nmore Info", textAlign = TextAlign.Center, style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                }
            }
        }
    }
}

@Composable
fun TuffStickTwoSectionLabel(labelElement: LabelElements) {
    Box(modifier = Modifier
        .width(mmToDp(labelElement.labelLayout.PAGEWIDTH))
        .height(mmToDp(labelElement.labelLayout.PAGEHEIGHT))
        .background(color = White)) {
        Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.Bottom) {
            Column(modifier = Modifier.width(mmToDp(labelElement.labelLayout.MIDWIDTH)), horizontalAlignment = Alignment.CenterHorizontally) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(mmToDp(labelElement.labelLayout.COLORPOS)),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.tuff_logo),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth(),
                        alignment = Alignment.BottomCenter
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1F)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = "FIRE RETARDANT ADHESIVE/SEALER",
                            modifier = Modifier
                                .padding(top = mmToDp(labelElement.labelLayout.TOPNAMEPOS))
                                .align(Alignment.TopCenter)
                                .padding(vertical = mmToDp(labelElement.labelLayout.VARIANTPADVER), horizontal = mmToDp(labelElement.labelLayout.VARIANTPADHOZ)),

                            style = ProductLabelHeader.copy(fontSize = ptToSp(labelElement.labelLayout.TOPNAMEFS)), color = Color(0xFF787878)
                        )
                        Text(
                            text = labelElement.itemInfo.VARIANT.uppercase(),
                            modifier = Modifier
                                .padding(top = mmToDp(labelElement.labelLayout.VARIANTPOS))
                                .align(Alignment.TopCenter)
                                .border(BorderStroke(1.dp, Black))
                                .padding(vertical = mmToDp(labelElement.labelLayout.VARIANTPADVER), horizontal = mmToDp(labelElement.labelLayout.VARIANTPADHOZ)),
                            style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.VARIANTFS))
                        )
                        Text(
                            text = labelElement.itemInfo.SIZE,
                            modifier = Modifier
                                .padding(top = mmToDp(labelElement.labelLayout.SIZEPOS))
                                .align(Alignment.TopCenter),
                            style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.SIZEFS))
                        )
                    }
                }
            }
            Column(modifier = Modifier.width(mmToDp(((labelElement.labelLayout.PAGEWIDTH - labelElement.labelLayout.MIDWIDTH)))).padding(start = mmToDp(labelElement.labelLayout.PAGEPAD), top = mmToDp(labelElement.labelLayout.PAGEPAD), bottom = mmToDp(labelElement.labelLayout.PAGEPAD), end = mmToDp(labelElement.labelLayout.PAGEPAD))) {
                if (labelElement.classInfo.PICTOGRAM1.isNotEmpty() || labelElement.classInfo.PICTOGRAM2.isNotEmpty() || labelElement.classInfo.WARNINGSIGN.isNotEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        val pictogramL = Hazards.fromString(labelElement.classInfo.PICTOGRAM1.lowercase())
                        val pictogramR = Hazards.fromString(labelElement.classInfo.PICTOGRAM2.lowercase())
                        Image(
                            painter = painterResource(id = pictogramL.hazardImage()),
                            contentDescription = null,
                            modifier = Modifier
                                .height(mmToDp(labelElement.labelLayout.PICTOSIZE))
                                .padding(end = mmToDp(labelElement.labelLayout.PICTOGAP)),
                            alignment = Alignment.BottomStart
                        )
                        Image(
                            painter = painterResource(id = pictogramR.hazardImage()),
                            contentDescription = null,
                            modifier = Modifier
                                .height(mmToDp(labelElement.labelLayout.PICTOSIZE))
                                .padding(start = mmToDp(labelElement.labelLayout.PICTOGAP)),
                            alignment = Alignment.BottomStart
                        )
                        Text(
                            text = labelElement.classInfo.WARNINGSIGN.uppercase(),
                            modifier = Modifier.padding(top = mmToDp(labelElement.labelLayout.WARNINGPOS)),
                            style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.HEADINGFS))
                        )
                    }
                    Spacer(modifier = Modifier.height(mmToDp(labelElement.labelLayout.SPACEING)))
                    Spacer(modifier = Modifier.height(mmToDp(labelElement.labelLayout.SPACEING)))
                }
                if (labelElement.classInfo.PRECAUTIONS.isNotEmpty()) {
                    Text(text = "PRECAUTIONS:", style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.SUBHEADFS)))
                    Text(text = labelElement.classInfo.PRECAUTIONS, style = ProductLabelBody.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                    Spacer(modifier = Modifier.height(mmToDp(labelElement.labelLayout.SPACEING)))
                }
                if (labelElement.classInfo.INGESTION.isNotEmpty() || labelElement.classInfo.SKINCONTACT.isNotEmpty() || labelElement.classInfo.EYECONTACT.isNotEmpty()) {
                    Text(text = "FIRST AID:", style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.SUBHEADFS)))
                    if (labelElement.classInfo.INGESTION.isNotEmpty()) {
                        Text(text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("INGESTION: ") }
                            append(labelElement.classInfo.INGESTION)
                        }, style = ProductLabelBody.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                    }
                    if (labelElement.classInfo.SKINCONTACT.isNotEmpty()) {
                        Text(text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("SKIN CONTACT: ") }
                            append(labelElement.classInfo.SKINCONTACT)
                        }, style = ProductLabelBody.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                    }
                    if (labelElement.classInfo.EYECONTACT.isNotEmpty()) {
                        Text(text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("EYE CONTACT: ") }
                            append(labelElement.classInfo.EYECONTACT)
                        }, style = ProductLabelBody.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                    }
                    Spacer(modifier = Modifier.height(mmToDp(labelElement.labelLayout.SPACEING)))
                }
                if (labelElement.classInfo.SAFESTORAGE.isNotEmpty()) {
                    Text(text = "SAFE STORAGE:", style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.SUBHEADFS)))
                    Text(text = labelElement.classInfo.SAFESTORAGE, style = ProductLabelBody.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                    Spacer(modifier = Modifier.height(mmToDp(labelElement.labelLayout.SPACEING)))
                }
                Spacer(modifier = Modifier.height(mmToDp(labelElement.labelLayout.SPACEING)))
                Spacer(modifier = Modifier.height(mmToDp(labelElement.labelLayout.SPACEING)))


                Text(text = labelElement.classInfo.HELPTIPPAIL, style = ProductLabelBody.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                Spacer(modifier = Modifier.height(mmToDp(labelElement.labelLayout.SPACEING)))
                Text(text = "DIRECTIONS:", style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.SUBHEADFS)))
                Text(text = labelElement.classInfo.DIRECTIONS, style = ProductLabelBody.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))

                Spacer(modifier = Modifier.height(mmToDp(labelElement.labelLayout.SPACEING)))
                Spacer(modifier = Modifier.height(mmToDp(labelElement.labelLayout.SPACEING)))


                Text(text = DGLine(labelElement.dgInfo), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.SUBHEADFS)))
                Spacer(modifier = Modifier.height(mmToDp(labelElement.labelLayout.SPACEING)))

                HorizontalDivider(modifier = Modifier.padding(top = mmToDp(labelElement.labelLayout.SPACEING/2)), color = scottBlue)
                Image(
                    painter = painterResource(id = R.drawable.company_logo),
                    contentDescription = null,
                    modifier = Modifier
                        .width(mmToDp(labelElement.labelLayout.LOGOSMALLSIZE))
                        .padding(top = mmToDp(labelElement.labelLayout.SPACEING / 2), bottom = mmToDp(1.0)),
                    alignment = Alignment.BottomStart
                )
                Text(
                    "Unit 1/28 Lee Holm Road St. Marys NSW 2760\nP: (02) 9623 6444   E: sales@scottech.com.au\nwww.scottech.com.au",
                    style = ProductLabelBody.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS))
                )
                if (labelElement.itemInfo.ITEMBARCODE.isNotEmpty()) {
                    var codeText: String
                    if(labelElement.itemInfo.LABELSTYLE.ProductIsTin()){
                        codeText = labelElement.itemInfo.ITEMBARCODE
                    }else{
                        codeText ="(01)${labelElement.itemInfo.ITEMBARCODE}(10)${labelElement.sharedViewModel.currentOrderNumber.value}"
                    }
                    val qrBitmap = try {
                        generateEAN13Barcode(
                            text = codeText, width = 600, height = 120
                        )
                    } catch (_: Exception) {
                        generateCODE128Barcode(
                            text = codeText, width = 600, height = 120
                        )
                    }
                    Column(
                        modifier = Modifier
                            .width(mmToDp(((labelElement.labelLayout.PAGEWIDTH - labelElement.labelLayout.MIDWIDTH)) * 0.60))
                            .align(Alignment.CenterHorizontally), horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(mmToDp(labelElement.labelLayout.SPACEING)))
                        Spacer(modifier = Modifier.height(mmToDp(labelElement.labelLayout.SPACEING)))
                        Image(
                            bitmap = qrBitmap.asImageBitmap(),
                            contentDescription = "Barcode",
                        )
                        Text(text = labelElement.itemInfo.ITEMBARCODE, textAlign = TextAlign.Center, style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                    }
                } else {
                    Spacer(modifier = Modifier.height(mmToDp(labelElement.labelLayout.SPACEING)))
                }

            }
        }

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(all = mmToDp(labelElement.labelLayout.PAGEPAD)),contentAlignment = Alignment.TopEnd) {
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "Batch: #${labelElement.sharedViewModel.currentOrderNumber.value}", style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                val currentDate = LocalDate.now()
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                if (labelElement.itemInfo.BESTBEFORE <= 0) {
                    Text(text = "Packed: ${currentDate.format(formatter)}", style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                }else{
                    Text(text = "Best Before: ${currentDate.plusDays(labelElement.itemInfo.BESTBEFORE.toLong()).format(formatter)}", style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))

                }
                /*Spacer(modifier = Modifier.height(mmToDp(labelElement.labelLayout.SPACEING)))
                Spacer(modifier = Modifier.height(mmToDp(labelElement.labelLayout.SPACEING)))
                if (labelElement.itemInfo.QRCODE.isNotEmpty()) {
                    val qrBitmap = generateQRCode(
                        text = labelElement.itemInfo.QRCODE, size = mmToPixels(labelElement.labelLayout.QRCODESIZE)
                    )
                    Column(modifier = Modifier.width(mmToDp(labelElement.labelLayout.QRCODESIZE)), horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            bitmap = qrBitmap.asImageBitmap(),
                            contentDescription = "QR Code"
                        )
                        Text(text = "Scan for\nmore Info", textAlign = TextAlign.Center, style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                    }
                }*/

            }
        }
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(start = mmToDp(labelElement.labelLayout.MIDWIDTH + labelElement.labelLayout.PAGEPAD), top =mmToDp(labelElement.labelLayout.PAGEPAD))) {
            if (labelElement.itemInfo.QRCODE.isNotEmpty()) {
                val qrBitmap = generateQRCode(
                    text = labelElement.itemInfo.QRCODE, size = mmToPixels(labelElement.labelLayout.QRCODESIZE)
                )
                Column(modifier = Modifier.width(mmToDp(labelElement.labelLayout.QRCODESIZE)), horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        bitmap = qrBitmap.asImageBitmap(),
                        contentDescription = "QR Code"
                    )
                    Text(text = "Scan for\nmore Info", textAlign = TextAlign.Center, style = ProductLabelSubHeader.copy(fontSize = ptToSp(labelElement.labelLayout.BODYFS)))
                }
            }
        }
    }
}

val ProductLabelHeader = TextStyle(
    fontFamily = Helvetica,
    fontWeight = FontWeight.Black
)
val ProductLabelSubHeader = TextStyle(
    fontFamily = Helvetica,
    fontWeight = FontWeight.Bold
)
val ProductLabelBody = TextStyle(
    fontFamily = Helvetica,
    fontWeight = FontWeight.Light
)
val ProductLabelNormal = TextStyle(
    fontFamily = Helvetica,
    fontWeight = FontWeight.Normal
)

fun DGLine(dgInfo: APICallTables.assemblyLabelDGInfo?): String {
    return if (dgInfo != null){
        "UN ${dgInfo.UNNUMBER}    Packing Group ${dgInfo.PACKINGGROUP}"
    } else {
        "Not Classified as Dangerous Goods according to ADGC Edition 7.9 - 2024\nClassified as Hazardous according to SWA Poison Schedule S5"
    }
}
fun SimpleLabelResize(value: Double, barcodePresent: Boolean): Double{
    return if (barcodePresent) value
    else value + 6
}



