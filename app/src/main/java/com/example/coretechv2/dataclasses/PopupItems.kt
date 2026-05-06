package com.example.coretechv2.dataclasses

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable


data class PopupItems(
    var width: Int = 500,
    var height: Int = 500,
    var content: @Composable () -> Unit = {},
)

data class SnackBarItems(
    var message: String = "",
    )

data class MessageItems(
    var width: Int = 300,
    var height: Int = 150,
    var message: String = "",
    var messageButton1Text: String = "",
    var onClickAction1: () -> Unit = {},
    var messageButton2Text: String = "",
    var onClickAction2: () -> Unit = {},
)

