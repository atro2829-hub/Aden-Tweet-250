package com.adentweets.app.presentation.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adentweets.app.presentation.theme.WarningOrange

@Composable
fun OfflineBanner() {
    Row(modifier = Modifier.fillMaxWidth().background(WarningOrange).padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = Icons.Default.WifiOff, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "أنت غير متصل بالإنترنت", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimary)
    }
}