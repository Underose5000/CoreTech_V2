package com.example.coretechv2.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun OutlinedStyleButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent // matches TextField background
        ),
        shape = RoundedCornerShape(4.dp), // match OutlinedTextField corners
        border = BorderStroke(1.dp, Color.Gray),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .padding(0.dp)
            .width(90.dp)
            .height(45.dp)
    ) {
        Text(
            text = text,
            color = Color.Black // match TextField text color
        )
    }
}