package com.example.coretechv2.dataclasses

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Represents an item displayed in a UI menu.
 *
 * A menu item can display a title and optional icon, perform an action when
 * selected, and optionally contain a [SwitchItem] for settings or filter
 * controls.
 *
 * The item can also be configured to disable its click action while still
 * displaying its contents. This can be useful when the menu item contains
 * a separate interactive component, such as a switch.
 *
 * @property title Function that returns the text displayed for the menu item.
 * @property onClick Callback executed when the menu item is selected.
 * @property clickEnabled Determines whether the menu item's main click action
 * is enabled.
 * @property icon Function that returns the optional icon displayed alongside
 * the menu item's title.
 * @property switch Optional [SwitchItem] displayed alongside the menu item.
 * When present, the switch can provide its own interaction independently of
 * the menu item's main click action.
 */
data class MenuItem(
    val title: () -> String = {""},
    val onClick: () -> Unit = {},
    val clickEnabled: Boolean = true,
    val icon: () -> ImageVector? = {null},
    val switch: SwitchItem? = null,
)