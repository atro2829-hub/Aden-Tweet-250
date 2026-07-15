package com.adentweets.app.presentation.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.adentweets.app.presentation.theme.*

@Composable
fun XTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    isPassword: Boolean = false,
    isError: Boolean = false,
    errorMessage: String = "",
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    val containerColor = if (isError) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant
    val borderColor = if (isError) ErrorRed else MaterialTheme.colorScheme.outlineVariant

    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(containerColor, RoundedCornerShape(28.dp))
                .border(1.dp, borderColor, RoundedCornerShape(28.dp)),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AdenBlue,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = AdenBlue,
                focusedContainerColor = containerColor,
                unfocusedContainerColor = containerColor
            ),
            shape = RoundedCornerShape(28.dp),
            singleLine = singleLine,
            visualTransformation = if (isPassword) VisualTransformation.PasswordMask() else visualTransformation,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            textStyle = MaterialTheme.typography.bodyMedium
        )
        if (isError && errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = ErrorRed, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 16.dp, top = 4.dp))
        }
    }
}