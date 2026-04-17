package com.example.coretechv2.dataclasses

import androidx.compose.ui.graphics.vector.ImageVector

data class MenuItem(
    val title: String,
    val onClick: () -> Unit,
    val icon: ImageVector? = null,
)