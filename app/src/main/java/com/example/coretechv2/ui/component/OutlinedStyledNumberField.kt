package com.example.coretechv2.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.coretechv2.dataclasses.VisField



@Composable
fun OutlinedStyleNumberField(modifier : Modifier = Modifier, value : String, onValueChange : (String) -> Unit) {
    OutlinedTextField(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .width(80.dp)
            .then(modifier),
        textStyle = TextStyle(textAlign = TextAlign.Center),
        value = value,
        onValueChange = {onValueChange(it)},
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        ),
    )
}

