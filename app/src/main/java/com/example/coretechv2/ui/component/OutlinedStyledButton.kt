package com.example.coretechv2.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter.Companion.tint
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Search

/**
 * A custom outlined button styled to visually match an [OutlinedTextField].
 *
 * This composable creates a fixed-size button with:
 * - Transparent background
 * - Gray outline border
 * - Rounded corners
 * - No elevation/shadow
 *
 * The button is intended for compact UI layouts where a consistent
 * appearance with input fields is desired.
 *
 * @param text The text displayed inside the button.
 * @param onClick Callback triggered when the button is pressed.
 */
@Composable
fun OutlinedStyleButton(modifier: Modifier = Modifier, text: String, onClick: () -> Unit, icon: ImageVector? = null) {
    Button(
        onClick = onClick,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, Color.Gray),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .padding(0.dp)
            .width(90.dp)
            .height(45.dp)
            .then(modifier)
    ) {
        if(icon != null) {
        Icon(
            modifier = Modifier.size(12.dp),
            imageVector = icon,
            contentDescription = null)
        }
        if(text.isNotBlank()) {
            Text(
                text = text,
                color = Color.Black
            )
        }
    }
}

