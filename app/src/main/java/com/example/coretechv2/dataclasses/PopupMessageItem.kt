package com.example.coretechv2.dataclasses

import androidx.compose.ui.graphics.vector.ImageVector

data class PopupMessageItem(
var message: String = "",
var messageButton1Text: String = "",
var onClickAction1: () -> Unit = {},
var messageButton2Text: String = "",
var onClickAction2: () -> Unit = {},
)

