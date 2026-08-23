package com.example.coretechv2.dataclasses

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Represents a menu item used in navigation drawers, dropdowns,
 * popup menus, or action lists within the UI.
 *
 * Each menu item contains:
 * - A display title
 * - A click action callback
 * - An optional icon
 *
 * @property title Text displayed for the menu item.
 * @property onClick Callback executed when the menu item is selected.
 * @property icon Optional icon displayed alongside the title.
 */
data class MenuItem(
    val title: () -> String,
    val onClick: () -> Unit,
    val icon: ImageVector? = null,
)