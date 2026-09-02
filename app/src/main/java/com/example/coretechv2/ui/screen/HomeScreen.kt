package com.example.coretechv2.ui.screen

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.coretechv2.R
import com.example.coretechv2.ui.component.TopBar
import com.example.coretechv2.viewmodel.SharedViewModel

/**
 * Displays the main home screen of the application.
 *
 * Determines the appropriate layout based on the device's screen dimensions,
 * density, and orientation. The home screen provides access to the application's
 * assembly, sales, purchasing, and inventory functionality.
 *
 * The screen also retrieves the user's assembly orders when it is first displayed.
 *
 * @param navController Navigation controller used to navigate between application screens.
 * @param sharedViewModel Shared view model containing application-wide state and
 * data, including the current user and assembly orders.
 */
@Composable
fun HomeScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    val configuration = LocalConfiguration.current
    val snackbarHostState = remember { SnackbarHostState() }
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val screenWidthDp = configuration.screenWidthDp
    val screenHeightDp = configuration.screenHeightDp
    val screenDensity = configuration.densityDpi
    var isFlipPhone = false
    var isTablet = false

    if (screenWidthDp >= 550 && screenDensity >= 500) {
        isFlipPhone = true
        Log.d("screen dp size", "is FLip")
    } else if (screenWidthDp >= 600) {
        isTablet = true
        Log.d("screen dp size", "is Tablet")
    } else {
        Log.d("screen dp size", "is Phone")
    }

    Log.d("screen dp size", "$screenWidthDp by $screenHeightDp @ $screenDensity & is landscape = $isLandscape")
    LaunchedEffect(Unit) {
        sharedViewModel.retrieveAssemblyOrders()
        sharedViewModel.retrieveAllAssemblyOrders()
        Log.d("LaunchedEffect", "Orders Retrieved. List size = ${sharedViewModel.AssemblyOrdersList.size}")
    }


    if (isLandscape and isTablet) {
        LandscapeTabletLayout(
            navController = navController,
            sharedViewModel = sharedViewModel,
            sharedViewModel.currentUser.value,
            snackbarHostState
        )
    } else if (!isLandscape and isTablet) {
        PortraitTabletLayout(
            navController = navController,
            sharedViewModel = sharedViewModel,
            sharedViewModel.currentUser.value,
            snackbarHostState
        )
    } else if (isFlipPhone) {
        FlipPhoneLayout(
            navController = navController,
            sharedViewModel = sharedViewModel,
            sharedViewModel.currentUser.value,
            snackbarHostState
        )
    } else {
        PortraitLayout(
            navController = navController,
            sharedViewModel = sharedViewModel,
            sharedViewModel.currentUser.value,
            snackbarHostState
        )
    }
}

/**
 * Displays the home screen layout for a tablet in landscape orientation.
 *
 * Organises the available application functions into Assembly, Sales, Purchases,
 * and Inventory sections. Buttons are arranged horizontally to make use of the
 * additional screen width available in landscape orientation.
 *
 * @param navController Navigation controller used to navigate between application screens.
 * @param sharedViewModel Shared view model containing application-wide state and data.
 * @param currentUser Name of the currently logged-in user displayed in the welcome message.
 * @param snackbarHostState State used by the top bar to display snackbar messages.
 */
@Composable
fun LandscapeTabletLayout(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    currentUser: String,
    snackbarHostState: SnackbarHostState
) {
    Log.d("screen dp size", "LandscapeTabletLayout being used")
    TopBar(
        navController = navController,
        title = "Home",
        snackbarHostState,

        ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(all = 30.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Welcome ${currentUser.lowercase().replaceFirstChar { it.uppercase() }}",
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
                        AssemblyOrdersButton(
                            navController = navController,
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(5f)
                                .padding(horizontal = 10.dp),
                            shape = RoundedCornerShape(17.dp),
                        )
                        PrintProductLabelsButton(
                            navController = navController,
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(5f)
                                .padding(horizontal = 10.dp),
                            shape = RoundedCornerShape(17.dp),
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Row(modifier = Modifier.weight(5f)) {
                        PrintBoxLabelsButton(
                            navController = navController,
                            sharedViewModel = sharedViewModel,
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(5f)
                                .padding(horizontal = 10.dp),
                            shape = RoundedCornerShape(17.dp),
                        )
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
                        PackOrdersButton(
                            navController = navController,
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(5f)
                                .padding(horizontal = 10.dp),
                            shape = RoundedCornerShape(17.dp),
                        )
                        DispatchOrdersButton(
                            navController = navController,
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(5f)
                                .padding(horizontal = 10.dp),
                            shape = RoundedCornerShape(17.dp),
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Row(modifier = Modifier.weight(5f)) {
                        PrintShippingDocumentsButton(
                            navController = navController,
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(5f)
                                .padding(horizontal = 10.dp),
                            shape = RoundedCornerShape(17.dp),
                        )
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
                        PurchaseOrdersButton(
                            navController = navController,
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(5f)
                                .padding(horizontal = 10.dp),
                            shape = RoundedCornerShape(17.dp),
                        )
                        ReceiveGoodsButton(
                            navController = navController,
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(5f)
                                .padding(horizontal = 10.dp),
                            shape = RoundedCornerShape(17.dp),
                        )
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
                        ItemsListButton(
                            navController = navController,
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(5f)
                                .padding(horizontal = 10.dp),
                            shape = RoundedCornerShape(17.dp),
                        )
                        StocktakeButton(
                            navController = navController,
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(5f)
                                .padding(horizontal = 10.dp),
                            shape = RoundedCornerShape(17.dp),
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Row(modifier = Modifier.weight(5f)) {
                        RequestStockButton(
                            navController = navController,
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(5f)
                                .padding(horizontal = 10.dp),
                            shape = RoundedCornerShape(17.dp),
                        )
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

/**
 * Displays the home screen layout for a tablet in portrait orientation.
 *
 * Organises the available application functions into Assembly, Sales, Purchases,
 * and Inventory sections. Buttons are displayed vertically within each section
 * to accommodate the reduced screen width of portrait orientation.
 *
 * @param navController Navigation controller used to navigate between application screens.
 * @param sharedViewModel Shared view model containing application-wide state and data.
 * @param currentUser Name of the currently logged-in user displayed in the welcome message.
 * @param snackbarHostState State used by the top bar to display snackbar messages.
 */
@Composable
fun PortraitTabletLayout(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    currentUser: String,
    snackbarHostState: SnackbarHostState
) {
    Log.d("screen dp size", "PortraitLayout being used")
    TopBar(
        navController = navController,
        title = "Home",
        snackbarHostState
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(all = 30.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Welcome ${currentUser.lowercase().replaceFirstChar { it.uppercase() }}",
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
                    AssemblyOrdersButton(
                        navController = navController,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(5f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    PrintProductLabelsButton(
                        navController = navController,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(5f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    PrintBoxLabelsButton(
                        navController = navController,
                        sharedViewModel = sharedViewModel,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(5f),
                        shape = RoundedCornerShape(12.dp)
                    )
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
                    PackOrdersButton(
                        navController = navController,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(5f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    DispatchOrdersButton(
                        navController = navController,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(5f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    PrintShippingDocumentsButton(
                        navController = navController,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(5f),
                        shape = RoundedCornerShape(12.dp)
                    )
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
                    PurchaseOrdersButton(
                        navController = navController,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(5f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    ReceiveGoodsButton(
                        navController = navController,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(5f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.weight(5f))
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
                    ItemsListButton(
                        navController = navController,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(5f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    StocktakeButton(
                        navController = navController,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(5f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    RequestStockButton(
                        navController = navController,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(5f),
                        shape = RoundedCornerShape(12.dp)
                    )
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

/**
 * Displays the home screen layout for a high-density foldable or flip phone.
 *
 * Uses a more compact layout and smaller text sizes than the standard phone
 * and tablet layouts to accommodate the device's available screen dimensions.
 *
 * @param navController Navigation controller used to navigate between application screens.
 * @param sharedViewModel Shared view model containing application-wide state and data.
 * @param currentUser Name of the currently logged-in user displayed in the welcome message.
 * @param snackbarHostState State used by the top bar to display snackbar messages.
 */
@Composable
fun FlipPhoneLayout(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    currentUser: String,
    snackbarHostState: SnackbarHostState
) {
    Log.d("screen dp size", "FlipPhoneLayout being used")
    TopBar(
        navController = navController,
        title = "Home",
        snackbarHostState
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(all = 15.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Welcome ${currentUser.lowercase().replaceFirstChar { it.uppercase() }}",
                    fontWeight = FontWeight(800),
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                )
            }

            Row(
                modifier = Modifier
                    .weight(4f)
                    .padding(vertical = 10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 30.dp)
                ) {
                    Text(
                        "Assembly",
                        fontWeight = FontWeight(800),
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    AssemblyOrdersButton(
                        navController = navController,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(5f),
                        shape = RoundedCornerShape(12.dp),
                        fontSize = 15
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    PrintProductLabelsButton(
                        navController = navController,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(5f),
                        shape = RoundedCornerShape(12.dp),
                        fontSize = 15
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    PrintBoxLabelsButton(
                        navController = navController,
                        sharedViewModel = sharedViewModel,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(5f),
                        shape = RoundedCornerShape(12.dp),
                        fontSize = 15
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 30.dp)
                ) {
                    Text(
                        "Sales",
                        fontWeight = FontWeight(800),
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    PackOrdersButton(
                        navController = navController,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(5f),
                        shape = RoundedCornerShape(12.dp),
                        fontSize = 15
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    DispatchOrdersButton(
                        navController = navController,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(5f),
                        shape = RoundedCornerShape(12.dp),
                        fontSize = 15
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    PrintShippingDocumentsButton(
                        navController = navController,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(5f),
                        shape = RoundedCornerShape(12.dp),
                        fontSize = 15
                    )
                }
            }
            Row(
                modifier = Modifier
                    .weight(4f)
                    .padding(vertical = 10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 30.dp)
                ) {
                    Text(
                        "Purchases",
                        fontWeight = FontWeight(800),
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    PurchaseOrdersButton(
                        navController = navController,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(5f),
                        shape = RoundedCornerShape(12.dp),
                        fontSize = 15
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    ReceiveGoodsButton(
                        navController = navController,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(5f),
                        shape = RoundedCornerShape(12.dp),
                        fontSize = 15
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.weight(5f))
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 30.dp)
                ) {
                    Text(
                        "Inventory",
                        fontWeight = FontWeight(800),
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    ItemsListButton(
                        navController = navController,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(5f),
                        shape = RoundedCornerShape(12.dp),
                        fontSize = 15
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    StocktakeButton(
                        navController = navController,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(5f),
                        shape = RoundedCornerShape(12.dp),
                        fontSize = 15
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    RequestStockButton(
                        navController = navController,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(5f),
                        shape = RoundedCornerShape(12.dp),
                        fontSize = 15
                    )
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
            Row(modifier = Modifier.weight(15f)) {
                Spacer(modifier = Modifier.weight(3f))
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

/**
 * Displays the standard portrait home screen layout.
 *
 * Organises the available application functions into Assembly, Sales, Purchases,
 * and Inventory sections using a two-column arrangement. This layout is used
 * for standard phones that do not meet the tablet or flip-phone screen criteria.
 *
 * @param navController Navigation controller used to navigate between application screens.
 * @param sharedViewModel Shared view model containing application-wide state and data.
 * @param currentUser Name of the currently logged-in user displayed in the welcome message.
 * @param snackbarHostState State used by the top bar to display snackbar messages.
 */
@Composable
fun PortraitLayout(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    currentUser: String,
    snackbarHostState: SnackbarHostState
) {
    Log.d("screen dp size", "PortraitLayout being used")
    TopBar(
        navController = navController,
        title = "Home",
        snackbarHostState
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(all = 30.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Welcome ${currentUser.lowercase().replaceFirstChar { it.uppercase() }}",
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
                    AssemblyOrdersButton(
                        navController = navController,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(5f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    PrintProductLabelsButton(
                        navController = navController,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(5f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    PrintBoxLabelsButton(
                        navController = navController,
                        sharedViewModel = sharedViewModel,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(5f),
                        shape = RoundedCornerShape(12.dp)
                    )
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
                    PackOrdersButton(
                        navController = navController,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(5f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    DispatchOrdersButton(
                        navController = navController,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(5f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    PrintShippingDocumentsButton(
                        navController = navController,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(5f),
                        shape = RoundedCornerShape(12.dp)
                    )
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
                    PurchaseOrdersButton(
                        navController = navController,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(5f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    ReceiveGoodsButton(
                        navController = navController,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(5f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.weight(5f))
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
                    ItemsListButton(
                        navController = navController,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(5f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    StocktakeButton(
                        navController = navController,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(5f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    RequestStockButton(
                        navController = navController,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(5f),
                        shape = RoundedCornerShape(12.dp)
                    )
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

/**
 * Displays a button for navigating to the Assembly Orders screen.
 *
 * @param navController Navigation controller used to navigate to the Assembly Orders screen.
 * @param modifier Modifier used to configure the button's layout and appearance.
 * @param shape Shape applied to the button's corners.
 * @param fontSize Font size used for the button label in scaled pixels.
 */
@Composable
fun AssemblyOrdersButton(navController: NavController, modifier: Modifier, shape: Shape, fontSize: Int = 20) {
    Button(
        modifier = modifier,
        shape = shape,
        contentPadding = PaddingValues(start = 10.dp, top = 4.dp, end = 4.dp, bottom = 4.dp),
        onClick = { navController.navigate("assemblyorders") }
    ) {
        Image(
            painter = painterResource(id = R.drawable.assembly_orders_icon),
            contentDescription = null,
            modifier = Modifier
                .scale(1.2f)
                .weight(1f)
        )
        Text(
            "Assembly Orders",
            fontSize = fontSize.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(3f)
        )
    }
}

/**
 * Displays the Print Product Labels button.
 *
 * This functionality is currently disabled and does not perform an action
 * when pressed.
 *
 * @param navController Navigation controller available for future navigation
 * to the product label printing screen.
 * @param modifier Modifier used to configure the button's layout and appearance.
 * @param shape Shape applied to the button's corners.
 * @param fontSize Font size used for the button label in scaled pixels.
 */
@Composable
fun PrintProductLabelsButton(navController: NavController, modifier: Modifier, shape: Shape, fontSize: Int = 20) {
    Button(
        modifier = modifier,
        shape = shape,
        contentPadding = PaddingValues(start = 10.dp, top = 4.dp, end = 4.dp, bottom = 4.dp),
        onClick = {},
        enabled = false
    ) {
        Image(
            painter = painterResource(id = R.drawable.print_product_label_icon),
            contentDescription = null,
            modifier = Modifier
                .scale(1.2f)
                .weight(1f)
        )
        Text(
            "Print Product Labels",
            fontSize = fontSize.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(3f)
        )
    }
}

/**
 * Displays a button for navigating to the Print Box Labels screen.
 *
 * @param navController Navigation controller used to navigate to the Print Box Labels screen.
 * @param sharedViewModel Shared view model containing application-wide state and data.
 * @param modifier Modifier used to configure the button's layout and appearance.
 * @param shape Shape applied to the button's corners.
 * @param fontSize Font size used for the button label in scaled pixels.
 */
@Composable
fun PrintBoxLabelsButton(navController: NavController, sharedViewModel: SharedViewModel, modifier: Modifier, shape: Shape, fontSize: Int = 20) {
    Button(
        modifier = modifier,
        shape = shape,
        contentPadding = PaddingValues(start = 10.dp, top = 4.dp, end = 4.dp, bottom = 4.dp),
        onClick = { navController.navigate("printboxlabels") },
        enabled = true
    ) {
        Image(
            painter = painterResource(id = R.drawable.print_box_label_icon),
            contentDescription = null,
            modifier = Modifier
                .scale(1.2f)
                .weight(1f)
        )
        Text(
            "Print Box Labels",
            fontSize = fontSize.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(3f)
        )
    }
}

/**
 * Displays the Pack Orders button.
 *
 * This functionality is currently disabled and does not perform an action
 * when pressed.
 *
 * @param navController Navigation controller available for future navigation
 * to the Pack Orders screen.
 * @param modifier Modifier used to configure the button's layout and appearance.
 * @param shape Shape applied to the button's corners.
 * @param fontSize Font size used for the button label in scaled pixels.
 */
@Composable
fun PackOrdersButton(navController: NavController, modifier: Modifier, shape: Shape, fontSize: Int = 20) {
    Button(
        modifier = modifier,
        shape = shape,
        contentPadding = PaddingValues(start = 10.dp, top = 4.dp, end = 4.dp, bottom = 4.dp),
        onClick = {},
        enabled = false
    ) {
        Image(
            painter = painterResource(id = R.drawable.pack_order_icon),
            contentDescription = null,
            modifier = Modifier
                .scale(1.2f)
                .weight(1f)
        )
        Text(
            "Pack Orders",
            fontSize = fontSize.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(3f)
        )
    }
}

/**
 * Displays the Dispatch Orders button.
 *
 * This functionality is currently disabled and does not perform an action
 * when pressed.
 *
 * @param navController Navigation controller available for future navigation
 * to the Dispatch Orders screen.
 * @param modifier Modifier used to configure the button's layout and appearance.
 * @param shape Shape applied to the button's corners.
 * @param fontSize Font size used for the button label in scaled pixels.
 */
@Composable
fun DispatchOrdersButton(navController: NavController, modifier: Modifier, shape: Shape, fontSize: Int = 20) {
    Button(
        modifier = modifier,
        shape = shape,
        contentPadding = PaddingValues(start = 10.dp, top = 4.dp, end = 4.dp, bottom = 4.dp),
        onClick = {},
        enabled = false
    ) {
        Image(
            painter = painterResource(id = R.drawable.dispatch_order_icon),
            contentDescription = null,
            modifier = Modifier
                .scale(1.2f)
                .weight(1f)
        )
        Text(
            "Dispatch Orders",
            fontSize = fontSize.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(3f)
        )
    }
}

/**
 * Displays the Print Shipping Documents button.
 *
 * This functionality is currently disabled and does not perform an action
 * when pressed.
 *
 * @param navController Navigation controller available for future navigation
 * to the shipping documents screen.
 * @param modifier Modifier used to configure the button's layout and appearance.
 * @param shape Shape applied to the button's corners.
 * @param fontSize Font size used for the button label in scaled pixels.
 */
@Composable
fun PrintShippingDocumentsButton(navController: NavController, modifier: Modifier, shape: Shape, fontSize: Int = 20) {
    Button(
        modifier = modifier,
        shape = shape,
        contentPadding = PaddingValues(start = 10.dp, top = 4.dp, end = 4.dp, bottom = 4.dp),
        onClick = {},
        enabled = false
    ) {
        Image(
            painter = painterResource(id = R.drawable.print_shipping_doc_icon),
            contentDescription = null,
            modifier = Modifier
                .scale(1.2f)
                .weight(1f)
        )
        Text(
            "Print Shipping Documents",
            fontSize = fontSize.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(3f)
        )
    }
}

/**
 * Displays the Purchase Orders button.
 *
 * This functionality is currently disabled and does not perform an action
 * when pressed.
 *
 * @param navController Navigation controller available for future navigation
 * to the Purchase Orders screen.
 * @param modifier Modifier used to configure the button's layout and appearance.
 * @param shape Shape applied to the button's corners.
 * @param fontSize Font size used for the button label in scaled pixels.
 */
@Composable
fun PurchaseOrdersButton(navController: NavController, modifier: Modifier, shape: Shape, fontSize: Int = 20) {
    Button(
        modifier = modifier,
        shape = shape,
        contentPadding = PaddingValues(start = 10.dp, top = 4.dp, end = 4.dp, bottom = 4.dp),
        onClick = {},
        enabled = false
    ) {
        Image(
            painter = painterResource(id = R.drawable.purchase_orders_icon),
            contentDescription = null,
            modifier = Modifier
                .scale(1.2f)
                .weight(1f)
        )
        Text(
            "Purchase Orders",
            fontSize = fontSize.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(3f)
        )
    }
}

/**
 * Displays the Receive Goods button.
 *
 * This functionality is currently disabled and does not perform an action
 * when pressed.
 *
 * @param navController Navigation controller available for future navigation
 * to the Receive Goods screen.
 * @param modifier Modifier used to configure the button's layout and appearance.
 * @param shape Shape applied to the button's corners.
 * @param fontSize Font size used for the button label in scaled pixels.
 */
@Composable
fun ReceiveGoodsButton(navController: NavController, modifier: Modifier, shape: Shape, fontSize: Int = 20) {
    Button(
        modifier = modifier,
        shape = shape,
        contentPadding = PaddingValues(start = 10.dp, top = 4.dp, end = 4.dp, bottom = 4.dp),
        onClick = {},
        enabled = false
    ) {
        Image(
            painter = painterResource(id = R.drawable.receive_goods_icon),
            contentDescription = null,
            modifier = Modifier
                .scale(1.2f)
                .weight(1f)
        )
        Text(
            "Receive Goods",
            fontSize = fontSize.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(3f)
        )
    }
}

/**
 * Displays the Items List button.
 *
 * This functionality is currently disabled and does not perform an action
 * when pressed.
 *
 * @param navController Navigation controller available for future navigation
 * to the Items List screen.
 * @param modifier Modifier used to configure the button's layout and appearance.
 * @param shape Shape applied to the button's corners.
 * @param fontSize Font size used for the button label in scaled pixels.
 */
@Composable
fun ItemsListButton(navController: NavController, modifier: Modifier, shape: Shape, fontSize: Int = 20) {
    Button(
        modifier = modifier,
        shape = shape,
        contentPadding = PaddingValues(start = 10.dp, top = 4.dp, end = 4.dp, bottom = 4.dp),
        onClick = {},
        enabled = false
    ) {
        Image(
            painter = painterResource(id = R.drawable.pack_order_icon),
            contentDescription = null,
            modifier = Modifier
                .scale(1.2f)
                .weight(1f)
        )
        Text(
            "Items List",
            fontSize = fontSize.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(3f)
        )
    }
}

/**
 * Displays the Stocktake button.
 *
 * This functionality is currently disabled and does not perform an action
 * when pressed.
 *
 * @param navController Navigation controller available for future navigation
 * to the Stocktake screen.
 * @param modifier Modifier used to configure the button's layout and appearance.
 * @param shape Shape applied to the button's corners.
 * @param fontSize Font size used for the button label in scaled pixels.
 */
@Composable
fun StocktakeButton(navController: NavController, modifier: Modifier, shape: Shape, fontSize: Int = 20) {
    Button(
        modifier = modifier,
        shape = shape,
        contentPadding = PaddingValues(start = 10.dp, top = 4.dp, end = 4.dp, bottom = 4.dp),
        onClick = {},
        enabled = false
    ) {
        Image(
            painter = painterResource(id = R.drawable.stocktake_icon),
            contentDescription = null,
            modifier = Modifier
                .scale(1.2f)
                .weight(1f)
        )
        Text(
            "Stocktake",
            fontSize = fontSize.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(3f)
        )
    }
}

/**
 * Displays the Request Stock button.
 *
 * This functionality is currently disabled and does not perform an action
 * when pressed.
 *
 * @param navController Navigation controller available for future navigation
 * to the Request Stock screen.
 * @param modifier Modifier used to configure the button's layout and appearance.
 * @param shape Shape applied to the button's corners.
 * @param fontSize Font size used for the button label in scaled pixels.
 */
@Composable
fun RequestStockButton(navController: NavController, modifier: Modifier, shape: Shape, fontSize: Int = 20) {
    Button(
        modifier = modifier,
        shape = shape,
        contentPadding = PaddingValues(start = 10.dp, top = 4.dp, end = 4.dp, bottom = 4.dp),
        onClick = {},
        enabled = false
    ) {
        Image(
            painter = painterResource(id = R.drawable.request_stock_icon),
            contentDescription = null,
            modifier = Modifier
                .scale(1.2f)
                .weight(1f)
        )
        Text(
            "Request Stock",
            fontSize = fontSize.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(3f)
        )
    }
}