package com.example.coretechv2.dataclasses

import androidx.compose.runtime.Composable

/**
 * Represents a reusable popup dialog configuration.
 *
 * Used to define popup window dimensions and composable content
 * displayed within the popup.
 *
 * Commonly used for:
 * - Forms
 * - Test entry screens
 * - Custom modal content
 * - Reusable dialog layouts
 *
 * @property width Width of the popup in dp/pixels depending on usage context.
 * @property height Height of the popup in dp/pixels depending on usage context.
 * @property content Composable UI content displayed inside the popup.
 */
data class PopupItems(
    var width: Int = 500,
    var height: Int = 500,
    var content: @Composable () -> Unit = {},
)

/**
 * Represents a configurable message dialog with up to two actions.
 *
 * Used for confirmation dialogs, warnings, errors, and general
 * user interaction prompts.
 *
 * Supports:
 * - Custom message text
 * - Two configurable buttons
 * - Independent click actions
 * - Adjustable dialog size
 *
 * Common examples:
 * - Save confirmation
 * - Delete confirmation
 * - Exit without saving warning
 *
 * @property width Width of the message dialog.
 * @property height Height of the message dialog.
 * @property message Message text displayed to the user.
 * @property messageButton1Text Text displayed on the first button.
 * @property onClickAction1 Action executed when the first button is pressed.
 * @property messageButton2Text Text displayed on the second button.
 * @property onClickAction2 Action executed when the second button is pressed.
 */
data class MessageItems(
    var width: Int = 300,
    var height: Int = 150,
    var message: String = "",
    var messageButton1Text: String = "",
    var onClickAction1: () -> Unit = {},
    var messageButton2Text: String = "",
    var onClickAction2: () -> Unit = {},
)

