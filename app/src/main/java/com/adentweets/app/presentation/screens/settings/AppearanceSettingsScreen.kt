package com.adentweets.app.presentation.screens.settings

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.BrightnessLow
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.adentweets.app.presentation.theme.AdenBlue

@Composable
fun AppearanceSettingsScreen(navController: NavController) {
    var selectedTheme by remember { mutableStateOf(2) } // 0: light, 1: dark, 2: auto
    var selectedFontSize by remember { mutableStateOf(1) } // 0: small, 1: medium, 2: large

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "المظهر والسطوع",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // المظهر
            AppearanceGroupHeader(label = "المظهر")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ThemeOptionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.LightMode,
                    label = "فاتح",
                    isSelected = selectedTheme == 0,
                    onClick = { selectedTheme = 0 }
                )
                ThemeOptionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.DarkMode,
                    label = "داكن",
                    isSelected = selectedTheme == 1,
                    onClick = { selectedTheme = 1 }
                )
                ThemeOptionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.BrightnessAuto,
                    label = "تلقائي",
                    isSelected = selectedTheme == 2,
                    onClick = { selectedTheme = 2 }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            HorizontalDivider()

            // حجم الخط
            AppearanceGroupHeader(label = "حجم الخط")
            val fontSizes = listOf("صغير", "متوسط", "كبير")
            fontSizes.forEachIndexed { index, size ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clickable { selectedFontSize = index }
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedFontSize == index,
                        onClick = { selectedFontSize = index },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = AdenBlue
                        )
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    val textStyle = when (index) {
                        0 -> MaterialTheme.typography.bodySmall
                        1 -> MaterialTheme.typography.bodyLarge
                        2 -> MaterialTheme.typography.titleMedium
                        else -> MaterialTheme.typography.bodyLarge
                    }
                    Text(
                        text = size,
                        style = textStyle,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "نص تجريبي",
                        style = textStyle,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ThemeOptionCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 2.dp,
                color = if (isSelected) AdenBlue else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 20.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(32.dp),
            tint = if (isSelected) AdenBlue else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) AdenBlue else MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        if (isSelected) {
            Spacer(modifier = Modifier.height(4.dp))
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "محدد",
                modifier = Modifier.size(18.dp),
                tint = AdenBlue
            )
        }
    }
}

@Composable
private fun AppearanceGroupHeader(label: String) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        color = AdenBlue,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 12.dp)
    )
}