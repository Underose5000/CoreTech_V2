package com.example.coretechv2.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.coretechv2.dataclasses.PopupItems
import com.example.coretechv2.ui.theme.borderColor

/**
 * A reusable full-screen popup container that displays custom composable content.
 *
 * This component renders a centered modal window over the entire screen
 * and displays content provided via [PopupItems].
 *
 * Features:
 * - Full-screen overlay container
 * - Centered popup window
 * - Configurable width and height
 * - Fully custom composable content slot
 *
 * Typical usage:
 * - Forms (test entry screens)
 * - Dialog-style workflows
 * - Custom UI popups (menus, editors, confirmations)
 *
 * @param item Configuration object containing:
 * - popup dimensions (width/height)
 * - composable content to render inside the popup
 */
@Composable
fun PopupWindow(item: PopupItems) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(item.width.dp)
                .height(item.height.dp)
                .border(1.dp, borderColor)
                .background(Color.White),
        ) {
            item.content()
        }
    }
}