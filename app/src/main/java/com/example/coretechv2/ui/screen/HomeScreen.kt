package com.example.coretechv2.ui.screen

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.coretechv2.R
import com.example.coretechv2.ui.component.TopBar
import java.util.Locale
import java.util.Locale.getDefault

@Composable
fun HomeScreen(
    navController: NavController,
    currentUser: String
) {
    val configuration = LocalConfiguration.current
    val isLandscape =
        configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val screenWidthDp = configuration.screenWidthDp

    val isTablet = screenWidthDp >= 600


    if (isLandscape and isTablet) {
        LandscapeTabletLayout(currentUser)
    } else if (!isLandscape and isTablet){
        PortraitLayout(currentUser)
    } else if (isLandscape and !isTablet){
        PortraitLayout(currentUser)
    } else {
        PortraitLayout(currentUser)
    }
}


@Composable
fun LandscapeTabletLayout(
    currentUser: String
) {
    TopBar(
        title = "Home"
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(all = 30.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Welcome ${currentUser.replaceFirstChar { it.uppercase() }}",
                    fontWeight = FontWeight(800),
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center,
                )
            }

            Row(
                modifier = Modifier
                    .weight(3f)
                    .padding(vertical = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 50.dp)
                ) {
                    Text(
                        "Assembly",
                        fontWeight = FontWeight(800),
                        fontSize = 30.sp,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Row(modifier = Modifier.weight(5f)) {
                        Button(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(5f)
                                .padding(horizontal = 10.dp),
                            shape = RoundedCornerShape(17.dp),
                            contentPadding = PaddingValues(start = 8.dp, top = 4.dp, end = 4.dp, bottom = 4.dp),
                            onClick = {}
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.assembly_orders_icon),
                                contentDescription = null,
                                modifier = Modifier.scale(1.2f)
                                    .weight(30f),
                                )
                            Spacer(modifier = Modifier.weight(2f))
                            Text(
                                "Assembly Orders",
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(69f)
                            )
                        }
                        Button(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(5f)
                                .padding(horizontal = 10.dp),
                            shape = RoundedCornerShape(17.dp),
                            contentPadding = PaddingValues(start = 8.dp, top = 4.dp, end = 4.dp, bottom = 4.dp),
                            onClick = {}
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.print_product_label_icon),
                                contentDescription = null,
                                modifier = Modifier.scale(1.2f)
                                    .weight(30f),

                                )
                            Spacer(modifier = Modifier.weight(2f))
                            Text(
                                "Print Product Labels",
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(69f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Row(modifier = Modifier.weight(5f)) {
                        Button(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(5f)
                                .padding(horizontal = 10.dp),
                            shape = RoundedCornerShape(17.dp),
                            contentPadding = PaddingValues(start = 8.dp, top = 4.dp, end = 4.dp, bottom = 4.dp),
                            onClick = {}
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.print_box_label_icon),
                                contentDescription = null,
                                modifier = Modifier.scale(1.2f)
                                    .weight(30f)

                                )
                            Spacer(modifier = Modifier.weight(8f))
                            Text(
                                "Print Box Labels",
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(60f)
                            )
                        }
                        Spacer(modifier = Modifier.weight(5f))
                    }

                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 50.dp)
                ) {
                    Text(
                        "Sales",
                        fontWeight = FontWeight(800),
                        fontSize = 30.sp,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Row(modifier = Modifier.weight(5f)) {
                        Button(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(5f)
                                .padding(horizontal = 10.dp),
                            shape = RoundedCornerShape(17.dp),
                            onClick = {}
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.pack_order_icon),
                                contentDescription = null,

                                )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                "Pack Orders",
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center,
                            )
                        }
                        Button(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(5f)
                                .padding(horizontal = 10.dp),
                            shape = RoundedCornerShape(17.dp),
                            onClick = {}
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.dispatch_order_icon),
                                contentDescription = null,

                                )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                "Dispatch Orders",
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Row(modifier = Modifier.weight(5f)) {
                        Button(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(5f)
                                .padding(horizontal = 10.dp),
                            shape = RoundedCornerShape(17.dp),
                            onClick = {}
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.print_shipping_doc_icon),
                                contentDescription = null,

                                )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                "Print Shipping Documents",
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center,
                            )
                        }
                        Spacer(modifier = Modifier.weight(5f))
                    }
                }
            }
            Row(
                modifier = Modifier
                    .weight(3f)
                    .padding(vertical = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 50.dp)
                ) {
                    Text(
                        "Purchases",
                        fontWeight = FontWeight(800),
                        fontSize = 30.sp,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Row(modifier = Modifier.weight(5f)) {
                        Button(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(5f)
                                .padding(horizontal = 10.dp),
                            shape = RoundedCornerShape(17.dp),
                            onClick = {}
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.purchase_orders_icon),
                                contentDescription = null,

                                )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                "Purchase Orders",
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center,
                            )
                        }
                        Button(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(5f)
                                .padding(horizontal = 10.dp),
                            shape = RoundedCornerShape(17.dp),
                            onClick = {}
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.receive_goods_icon),
                                contentDescription = null,

                                )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                "Receive Goods",
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Row(modifier = Modifier.weight(5f)) {
                        Spacer(modifier = Modifier.weight(5f))
                        Spacer(modifier = Modifier.weight(5f))
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 50.dp)
                ) {
                    Text(
                        "Inventory",
                        fontWeight = FontWeight(800),
                        fontSize = 30.sp,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Row(modifier = Modifier.weight(5f)) {
                        Button(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(5f)
                                .padding(horizontal = 10.dp),
                            shape = RoundedCornerShape(17.dp),
                            onClick = {}
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.pack_order_icon),
                                contentDescription = null,

                                )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                "Items List",
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center,
                            )
                        }
                        Button(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(5f)
                                .padding(horizontal = 10.dp),
                            shape = RoundedCornerShape(17.dp),
                            onClick = {}
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.stocktake_icon),
                                contentDescription = null,

                                )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                "Stocktake",
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Row(modifier = Modifier.weight(5f)) {
                        Button(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(5f)
                                .padding(horizontal = 10.dp),
                            shape = RoundedCornerShape(17.dp),
                            onClick = {}
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.request_stock_icon),
                                contentDescription = null,

                                )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                "Request Stock",
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center,
                            )
                        }
                        Spacer(modifier = Modifier.weight(5f))
                    }
                }
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.End,
        ) {
            Row(modifier = Modifier.weight(10f)) {
                Spacer(modifier = Modifier.weight(2f))
                Image(
                    painter = painterResource(id = R.drawable.homepage_graphic),
                    contentDescription = null,
                    modifier = Modifier.weight(3f)
                )
            }
            Spacer(modifier = Modifier.weight(15f))
        }
    }

}

@Composable
fun PortraitLayout(
    currentUser: String
) {
    TopBar(
        title = "Home"
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(all = 30.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Welcome ${currentUser.replaceFirstChar { it.uppercase() }}",
                    fontWeight = FontWeight(800),
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center,
                )
            }

            Row(
                modifier = Modifier
                    .weight(3f)
                    .padding(vertical = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 50.dp)
                ) {
                    Text(
                        "Assembly",
                        fontWeight = FontWeight(800),
                        fontSize = 30.sp,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(5f),
                        shape = RoundedCornerShape(12.dp),
                        onClick = {}
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.assembly_orders_icon),
                            contentDescription = null,
                            //modifier = Modifier.weight(1f)
                        )
                        Text(
                            "Assembly Orders",
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(5f),
                        shape = RoundedCornerShape(12.dp),
                        onClick = {}
                    ) {
                        Text(
                            "Print Product Labels",
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(5f),
                        shape = RoundedCornerShape(12.dp),
                        onClick = {}
                    ) {
                        Text(
                            "Print Box Labels",
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 50.dp)
                ) {
                    Text(
                        "Sales",
                        fontWeight = FontWeight(800),
                        fontSize = 30.sp,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(5f),
                        shape = RoundedCornerShape(12.dp),
                        onClick = {}
                    ) {
                        Text(
                            "Pack Order",
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(5f),
                        shape = RoundedCornerShape(12.dp),
                        onClick = {}
                    ) {
                        Text(
                            "Dispatch Order",
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(5f),
                        shape = RoundedCornerShape(12.dp),
                        onClick = {}
                    ) {
                        Text(
                            "Print Shipping Documents",
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .weight(3f)
                    .padding(vertical = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 50.dp)
                ) {
                    Text(
                        "Purchases",
                        fontWeight = FontWeight(800),
                        fontSize = 30.sp,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(5f),
                        shape = RoundedCornerShape(12.dp),
                        onClick = {}
                    ) {
                        Text(
                            "Purchase Orders",
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(5f),
                        shape = RoundedCornerShape(12.dp),
                        onClick = {}
                    ) {
                        Text(
                            "Receive Goods",
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.weight(5f))
                    /*Button(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(5f),
                            shape = RoundedCornerShape(12.dp),
                        onClick = {}
                    ) { Text("TBD",
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,) }*/
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 50.dp)
                ) {
                    Text(
                        "Inventory",
                        fontWeight = FontWeight(800),
                        fontSize = 30.sp,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(5f),
                        shape = RoundedCornerShape(12.dp),
                        onClick = {}
                    ) {
                        Text(
                            "Items List",
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(5f),
                        shape = RoundedCornerShape(12.dp),
                        onClick = {}
                    ) {
                        Text(
                            "Stocktake",
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(5f),
                        shape = RoundedCornerShape(12.dp),
                        onClick = {}
                    ) {
                        Text(
                            "Request Stock",
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.End,
        ) {
            Row(modifier = Modifier.weight(10f)) {
                Spacer(modifier = Modifier.weight(2f))
                Image(
                    painter = painterResource(id = R.drawable.homepage_graphic),
                    contentDescription = null,
                    modifier = Modifier.weight(3f)
                )
            }
            Spacer(modifier = Modifier.weight(15f))
        }
    }

}
