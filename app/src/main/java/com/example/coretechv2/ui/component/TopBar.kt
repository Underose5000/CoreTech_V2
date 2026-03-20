package com.example.coretechv2.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.navigation.NavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavController,
           title: String,
           backshow: Boolean = false,
           settingshow: Boolean = false,
           addshow: Boolean = false,
           pastshow: Boolean = false,
           menushow: Boolean = false,
           menuaction: () -> Unit = {},
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
                    if (pastshow){
                        IconButton(onClick = {}){
                            Icon(
                                imageVector = Icons.Filled.History,
                                contentDescription = "Add"
                            )
                        }
                    }
                    if (addshow){
                        IconButton(onClick = {}){
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Add"
                            )
                        }
                    }
                    if (menushow){
                        IconButton(onClick = {}){
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "Settings"
                            )
                        }
                    }
                    if (settingshow){
                        IconButton(onClick = {}){
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "Settings"
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