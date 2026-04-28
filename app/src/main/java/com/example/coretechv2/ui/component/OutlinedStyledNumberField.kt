package com.example.coretechv2.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.coretechv2.dataclasses.VisField



@Composable
fun OutlinedStyleDoubleNumberField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit
) {
    BasicTextField(
        value = value,
        onValueChange = {
            if (it.matches(Regex("""\d*\.?\d*"""))) {
                onValueChange(it)
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        ),
        textStyle = TextStyle(
            textAlign = TextAlign.Center,
            color = Color.Black
        ),
        singleLine = true,
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .width(90.dp)
            .height(45.dp)
            .then(modifier),
        decorationBox = { innerTextField ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                    .padding(vertical = 4.dp, horizontal = 8.dp)
            ) {
                innerTextField()
            }
        }
    )
}

@Composable
fun OutlinedStyleIntNumberField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit
) {
    BasicTextField(
        value = value,
        onValueChange = {
            if (it.matches(Regex(("""\d*""")))) {
                onValueChange(it)
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        ),
        textStyle = TextStyle(
            textAlign = TextAlign.Center,
            color = Color.Black
        ),
        singleLine = true,
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .width(90.dp)
            .height(45.dp)
            .then(modifier),
        decorationBox = { innerTextField ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                    .padding(vertical = 4.dp, horizontal = 8.dp)
            ) {
                innerTextField()
            }
        }
    )
}

