package com.example.coretechv2.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.coretechv2.dataclasses.MenuItem

/**
 * Displays a vertical menu containing a list of [MenuItem] objects.
 *
 * Each menu item can contain a title, an optional icon, and an optional
 * switch. The main menu item click action can be enabled or disabled
 * independently of any switch contained within the item.
 *
 * The menu is displayed inside a fixed-width container with a themed
 * background and border. Menu items are displayed using a [LazyColumn]
 * and are separated by horizontal dividers, except for the final item.
 *
 * Menu items containing a switch can use the switch to change their state
 * without relying on the menu item's main click action.
 *
 * @param menuList List of [MenuItem] objects to display in the menu.
 * @param width Width of the menu container in density-independent pixels.
 */
@Composable
fun Menu(
    menuList: List<MenuItem>,
    width: Int = 200,
) {
    Column(
        modifier = Modifier
            .width(width.dp)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .border(1.dp, MaterialTheme.colorScheme.onPrimaryContainer)
            .padding(horizontal = 8.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            itemsIndexed(menuList) { index, item ->
                Row(
                    modifier = Modifier
                        .clickable(
                            enabled = item.clickEnabled,
                            onClick = { item.onClick() }
                        )
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (item.switch != null) {
                    Switch(
                        checked = item.switch.checked(),
                        onCheckedChange = { item.switch.onCheckedChange() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = (item.switch.thumbColor ?: MaterialTheme.colorScheme.onPrimary),
                            checkedTrackColor = (item.switch.trackColor ?: MaterialTheme.colorScheme.primary),
                        )
                    )
                        Spacer(modifier = Modifier.width(5.dp))
                    }
                    if (item.icon() != null) {
                        item.icon()?.let { Icon(imageVector = it, contentDescription = null) }
                        Spacer(modifier = Modifier.width(5.dp))
                    }
                    Text(text = item.title())

                }
                if (index < menuList.lastIndex)
                    HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

            }
        }

    }

}

