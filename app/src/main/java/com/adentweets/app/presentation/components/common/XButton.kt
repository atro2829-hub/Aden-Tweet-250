package com.adentweets.app.presentation.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.adentweets.app.presentation.theme.AdenBlue

@Composable
fun XButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    isOutlined: Boolean = false,
    isSmall: Boolean = false,
    containerColor: Color = AdenBlue,
    contentColor: Color = Color.White
) {
    val height = if (isSmall) 36.dp else 48.dp
    val shape = RoundedCornerShape(if (isSmall) 18.dp else 24.dp)

    if (isOutlined) {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier.height(height),
            enabled = enabled,
            shape = shape,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = containerColor),
            border = androidx.compose.foundation.BorderStroke(1.dp, containerColor)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = containerColor)
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(text = text, style = if (isSmall) MaterialTheme.typography.labelLarge else MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
        }
    } else {
        Button(
            onClick = onClick,
            modifier = modifier.height(height),
            enabled = enabled && !isLoading,
            shape = shape,
            colors = ButtonDefaults.buttonColors(containerColor = containerColor, contentColor = contentColor, disabledContainerColor = containerColor.copy(alpha = 0.5f), disabledContentColor = contentColor.copy(alpha = 0.5f))
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = contentColor)
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(text = text, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
        }
    }
}