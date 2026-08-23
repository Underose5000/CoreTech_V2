package com.example.coretechv2.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

/**
 * A reusable top-level app scaffold that provides:
 * - Top app bar with optional back navigation
 * - Up to four configurable action icons
 * - Snackbar host support
 * - Content container with proper padding handling
 *
 * This component wraps [Scaffold] and is used as the main layout
 * structure for most screens in the application.
 *
 * Features:
 * - Dynamic title display
 * - Optional back button navigation
 * - Up to 4 action icons (conditionally shown)
 * - Integrated [SnackbarHost] support
 * - Proper insets handling via [PaddingValues]
 *
 * Icon behavior:
 * - Icons default to [Icons.Filled.Warning]
 * - Only displayed if overridden from default
 * - Each icon has its own click action and description
 *
 * @param navController Navigation controller used for back navigation.
 * @param title Title displayed in the top app bar.
 * @param snackbarHostState Snackbar host state for showing messages.
 * @param backshow Enables back navigation button when true.
 *
 * @param icon1 First action icon (default hidden unless overridden).
 * @param icon1Description Content description for accessibility.
 * @param icon1action Click action for icon1.
 *
 * @param icon2 Second action icon.
 * @param icon2Description Content description for accessibility.
 * @param icon2action Click action for icon2.
 *
 * @param icon3 Third action icon.
 * @param icon3Description Content description for accessibility.
 * @param icon3action Click action for icon3.
 *
 * @param icon4 Fourth action icon.
 * @param icon4Description Content description for accessibility.
 * @param icon4action Click action for icon4.
 *
 * @param content Main screen content displayed below the top bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavController,
           title: String,
           snackbarHostState: SnackbarHostState,
           backshow: Boolean = false,

           icon1: ImageVector = Icons.Filled.Warning,
           icon1Description: String? = null,
           icon1action: () -> Unit = {},

           icon2: ImageVector = Icons.Filled.Warning,
           icon2Description: String? = null,
           icon2action: () -> Unit = {},

           icon3: ImageVector = Icons.Filled.Warning,
           icon3Description: String? = null,
           icon3action: () -> Unit = {},

           icon4: ImageVector = Icons.Filled.Warning,
           icon4Description: String? = null,
           icon4action: () -> Unit = {},

           content: @Composable (PaddingValues) -> Unit) {
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer

                ),
                title = {
                    Text(title)
                },
                navigationIcon = {
                    if (backshow){
                        IconButton(onClick = {navController.popBackStack()}){
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                },
                actions = {
                    if (icon4 != Icons.Filled.Warning){
                        IconButton(onClick = {icon4action()}){
                            Icon(
                                imageVector = icon4,
                                contentDescription = icon4Description
                            )
                        }
                    }
                    if (icon3 != Icons.Filled.Warning){
                        IconButton(onClick = {icon3action()}){
                            Icon(
                                imageVector = icon3,
                                contentDescription = icon3Description
                            )
                        }
                    }
                    if (icon2 != Icons.Filled.Warning){
                        IconButton(onClick = {icon2action()}){
                            Icon(
                                imageVector = icon2,
                                contentDescription = icon2Description
                            )
                        }
                    }
                    if (icon1 != Icons.Filled.Warning){
                        IconButton(onClick = {icon1action()}){
                            Icon(
                                imageVector = icon1,
                                contentDescription = icon1Description
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}