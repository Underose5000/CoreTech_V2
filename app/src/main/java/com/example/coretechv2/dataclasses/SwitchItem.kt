package com.example.coretechv2.dataclasses

import androidx.compose.ui.graphics.Color

/**
 * Represents the configuration and state behaviour of a switch displayed
 * within a [MenuItem].
 *
 * The switch state is provided through a function rather than a stored
 * Boolean value. This allows the current state to be read whenever the
 * menu is recomposed, ensuring that changes to the underlying state are
 * reflected by the switch.
 *
 * The switch can also define custom colours for its thumb and track.
 * When a colour is not provided, the UI component can use its own default
 * or theme colours.
 *
 * @property checked Function that returns the current checked state of
 * the switch.
 * @property onCheckedChange Callback executed when the switch's checked
 * state should be changed.
 * @property thumbColor Optional colour used for the switch thumb.
 * A null value allows the UI component to use its default colour.
 * @property trackColor Optional colour used for the switch track.
 * A null value allows the UI component to use its default colour.
 */
data class SwitchItem(
    val checked : () -> Boolean,
    val onCheckedChange: () -> Unit,
    val thumbColor: Color? = null,
    val trackColor: Color? = null,
    )