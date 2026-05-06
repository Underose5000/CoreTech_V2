package com.example.coretechv2.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.coretechv2.dataclasses.MenuItem
import com.example.coretechv2.dataclasses.PopupItems

@Composable
fun PopupWindow(item : PopupItems) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(item.width.dp)
                .height(item.height.dp)
                .background(Color.White),
        ) {
            item.content()
        }
    }
}