package com.example.coretechv2.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * A styled numeric input field that allows decimal numbers.
 *
 * This composable wraps a [BasicTextField] with a custom outlined style
 * and input validation for floating-point values.
 *
 * Features:
 * - Allows digits and a single decimal point
 * - Center-aligned text
 * - Fixed width and height styling
 * - Rounded rectangle border
 * - Numeric keyboard input
 *
 * Validation rule:
 * - Accepts input matching: `\d*\.?\d*`
 *   (e.g. "12", "12.3", "0.5")
 *
 * @param value Current text value of the field.
 * @param onValueChange Callback triggered when input changes and is valid.
 * @param modifier Optional modifier for external styling overrides.
 */
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

/**
 * A styled numeric input field that only allows integer values.
 *
 * This composable wraps a [BasicTextField] with:
 * - Integer-only input validation
 * - Center-aligned text
 * - Fixed size styling
 * - Outlined border appearance
 *
 * Validation rule:
 * - Accepts digits only (`\d*`)
 *
 * @param value Current text value of the field.
 * @param onValueChange Callback triggered when valid input is entered.
 * @param modifier Optional modifier for styling adjustments.
 */
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

@Composable
fun OutlinedStyleTextAndButtonField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onClick: () -> Unit,
    icon: ImageVector
) {
    BasicTextField(
        value = value,
        onValueChange = { onValueChange(it) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        ),
        textStyle = TextStyle(
            textAlign = TextAlign.Start,
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
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .fillMaxSize()
                    .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                    .padding(vertical = 4.dp, horizontal = 8.dp)
            ) {
                innerTextField()
            }
            Box(
                contentAlignment = Alignment.CenterEnd,
                modifier = Modifier
                    .fillMaxSize()
                    .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                    .padding(vertical = 4.dp, horizontal = 8.dp)
            ){IconButton(
                onClick = onClick,
                modifier = Modifier.size(32.dp)
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.Black
                )
            }}
        }
    )
}

