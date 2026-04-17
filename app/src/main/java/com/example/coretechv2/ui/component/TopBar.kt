package com.example.coretechv2.ui.component

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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavController,
           title: String,
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

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
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