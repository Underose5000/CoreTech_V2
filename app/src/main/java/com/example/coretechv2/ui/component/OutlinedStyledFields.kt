package com.example.coretechv2.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
 * Displays a styled numeric input field that accepts decimal values.
 *
 * The field uses a [BasicTextField] with a custom outlined appearance and
 * restricts input to digits with an optional decimal point. The text is
 * centre-aligned and the field uses a numeric keyboard when focused.
 *
 * Valid examples include `"12"`, `"12.3"`, and `"0.5"`.
 *
 * @param modifier Optional [Modifier] used to customise the field's layout
 * or appearance.
 * @param value The current text value displayed by the field.
 * @param onValueChange Callback invoked when the entered value matches the
 * accepted numeric format.
 * @param textStyle The [TextStyle] used to display the input text.
 */
@Composable
fun OutlinedStyleDoubleNumberField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    textStyle: TextStyle = TextStyle(
        textAlign = TextAlign.Center,
        color = Color.Black
    )
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
        textStyle = textStyle,
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
 * Displays a styled numeric input field that accepts integer values only.
 *
 * The field uses a [BasicTextField] with a custom outlined appearance and
 * restricts input to digits. The text is centre-aligned and the field uses
 * a numeric keyboard when focused.
 *
 * Empty input is also permitted so that the user can clear the field while
 * editing its value.
 *
 * @param modifier Optional [Modifier] used to customise the field's layout
 * or appearance.
 * @param value The current text value displayed by the field.
 * @param onValueChange Callback invoked when the entered value contains only
 * numeric digits.
 * @param textStyle The [TextStyle] used to display the input text.
 */
@Composable
fun OutlinedStyleIntNumberField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    textStyle: TextStyle = TextStyle(
        textAlign = TextAlign.Center,
        color = Color.Black
    )
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
        textStyle = textStyle,
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
 * Displays a styled single-line text field with an action button.
 *
 * The text field can be used for editable or read-only text. An icon button
 * is displayed alongside the text and invokes the supplied [onClick]
 * callback when pressed.
 *
 * The field uses a text keyboard when editing is enabled and provides a
 * bordered, rounded appearance consistent with the other styled input
 * components in this package.
 *
 * @param modifier Optional [Modifier] used to customise the field's layout
 * or appearance.
 * @param value The current text value displayed by the field.
 * @param onValueChange Callback invoked when the text value changes.
 * @param onClick Callback invoked when the action icon is pressed.
 * @param icon The [ImageVector] displayed inside the action button.
 * @param readOnly Whether the text field should prevent user editing while
 * still allowing the action button to be used. Defaults to `false`.
 */
@Composable
fun OutlinedStyleTextAndButtonField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onClick: () -> Unit,
    icon: ImageVector,
    readOnly: Boolean = false
) {
    BasicTextField(
        value = value,
        onValueChange = { onValueChange(it) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text
        ),
        textStyle = TextStyle(
            textAlign = TextAlign.Start,
            color = Color.Black
        ),
        singleLine = true,
        readOnly = readOnly,
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
            ) {
                IconButton(
                    onClick = onClick,
                    modifier = Modifier.size(32.dp)
                ) {

                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.Black
                    )
                }
            }
        }
    )
}

/**
 * Displays a styled multi-line text input field.
 *
 * The field provides a bordered, rounded container suitable for entering
 * longer text. Input is left-aligned and begins at the top-left of the
 * field. The field has a fixed height of 200dp and supports multiple lines.
 *
 * @param modifier Optional [Modifier] used to customise the field's layout
 * or appearance.
 * @param value The current text value displayed by the field.
 * @param onValueChange Callback invoked whenever the text value changes.
 */
@Composable
fun OutlinedStyleTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit
) {
    BasicTextField(
        value = value,
        onValueChange = { onValueChange(it) },
        textStyle = TextStyle(
            textAlign = TextAlign.Left,
            color = Color.Black
        ),
        singleLine = false,
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .height(200.dp)
            .then(modifier),
        maxLines = Int.MAX_VALUE,
        decorationBox = { innerTextField ->
            Box(
                contentAlignment = Alignment.TopStart,
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
 * Displays a styled single-line text input field.
 *
 * The field provides a bordered, rounded container with left-aligned text.
 * It is intended for short, single-line text values and uses a fixed height
 * of 45dp.
 *
 * @param modifier Optional [Modifier] used to customise the field's layout
 * or appearance.
 * @param value The current text value displayed by the field.
 * @param onValueChange Callback invoked whenever the text value changes.
 */
@Composable
fun OutlinedStyleTextLine(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit
) {
    BasicTextField(
        value = value,
        onValueChange = { onValueChange(it) },
        textStyle = TextStyle(
            textAlign = TextAlign.Left,
            color = Color.Black
        ),
        singleLine = true,
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .height(45.dp)
            .then(modifier),
        maxLines = Int.MAX_VALUE,
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
        }
    )
}

/**
 * Displays a styled outlined button with optional icon content.
 *
 * The button uses a transparent background, grey border, and rounded
 * corners to provide a consistent outlined appearance. An optional icon
 * can be displayed alongside the button text.
 *
 * If [text] is blank, only the icon is displayed when an icon is supplied.
 * If [icon] is `null`, no icon is displayed.
 *
 * @param modifier Optional [Modifier] used to customise the button's layout
 * or appearance.
 * @param text The text displayed inside the button.
 * @param onClick Callback invoked when the button is pressed.
 * @param icon Optional [ImageVector] displayed before the button text.
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
        if (icon != null) {
            Icon(
                modifier = Modifier.size(12.dp),
                imageVector = icon,
                contentDescription = null
            )
        }
        if (text.isNotBlank()) {
            Text(
                text = text,
                color = Color.Black
            )
        }
    }
}

