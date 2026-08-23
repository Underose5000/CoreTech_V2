package com.example.coretechv2.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.coretechv2.dataclasses.MenuItem

/**
 * A simple vertical menu component built using a LazyColumn.
 *
 * This composable renders a list of clickable menu items provided
 * by [MenuItem].
 *
 * Features:
 * - Fixed width menu container
 * - Optional leading icon for each item
 * - Clickable rows triggering item actions
 * - Dividers between items (except after the last item)
 *
 * Common usage:
 * - Side navigation menus
 * - Popup action menus
 * - Drawer-style option lists
 *
 * @param menuList List of [MenuItem] objects defining:
 * - title text
 * - optional icon
 * - click behaviour
 */
@Composable
fun Menu(
    menuList: List<MenuItem>,
) {
    Column(
        modifier = Modifier
            .width(200.dp)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .border(1.dp, MaterialTheme.colorScheme.onPrimaryContainer)
            .padding(horizontal = 8.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            itemsIndexed(menuList) {index, item ->
                Row(
                    modifier = Modifier
                        .clickable {item.onClick()}
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                ) {
                    if (item.icon != null){Icon(imageVector = item.icon, contentDescription = null)}
                    Text(text = item.title())
                }
                if (index < menuList.lastIndex)
                HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

            }
        }

    }

}

