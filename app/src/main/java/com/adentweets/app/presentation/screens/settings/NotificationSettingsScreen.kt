package com.adentweets.app.presentation.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.adentweets.app.presentation.theme.AdenBlue

@Composable
fun NotificationSettingsScreen(navController: NavController) {
    var likesEnabled by remember { mutableStateOf(true) }
    var retweetsEnabled by remember { mutableStateOf(true) }
    var newFollowersEnabled by remember { mutableStateOf(true) }
    var repliesEnabled by remember { mutableStateOf(true) }
    var mentionsEnabled by remember { mutableStateOf(true) }
    var directMessagesEnabled by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "تفضيلات الإشعارات",
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
            NotificationToggleItem(
                icon = Icons.Default.Favorite,
                label = "الإعجابات",
                description = "إشعار عندما يعجب شخص ما بمنشورك",
                checked = likesEnabled,
                onCheckedChange = { likesEnabled = it }
            )

            HorizontalDivider(modifier = Modifier.padding(start = 56.dp))

            NotificationToggleItem(
                icon = Icons.Default.Repeat,
                label = "إعادات النشر",
                description = "إشعار عندما يعيد نشر شخص ما منشورك",
                checked = retweetsEnabled,
                onCheckedChange = { retweetsEnabled = it }
            )

            HorizontalDivider(modifier = Modifier.padding(start = 56.dp))

            NotificationToggleItem(
                icon = Icons.Default.GroupAdd,
                label = "المتابَعين الجدد",
                description = "إشعار عندما يتابعك شخص جديد",
                checked = newFollowersEnabled,
                onCheckedChange = { newFollowersEnabled = it }
            )

            HorizontalDivider(modifier = Modifier.padding(start = 56.dp))

            NotificationToggleItem(
                icon = Icons.Default.Reply,
                label = "الردود",
                description = "إشعار عندما يرد شخص على منشورك",
                checked = repliesEnabled,
                onCheckedChange = { repliesEnabled = it }
            )

            HorizontalDivider(modifier = Modifier.padding(start = 56.dp))

            NotificationToggleItem(
                icon = Icons.Default.AlternateEmail,
                label = "الإشارات",
                description = "إشعار عندما يشير شخص إليك",
                checked = mentionsEnabled,
                onCheckedChange = { mentionsEnabled = it }
            )

            HorizontalDivider(modifier = Modifier.padding(start = 56.dp))

            NotificationToggleItem(
                icon = Icons.Default.ChatBubble,
                label = "الرسائل المباشرة",
                description = "إشعار عند استلام رسالة جديدة",
                checked = directMessagesEnabled,
                onCheckedChange = { directMessagesEnabled = it }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun NotificationToggleItem(
    icon: ImageVector,
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier
                .padding(start = 8.dp, end = 16.dp)
                .size(24.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedTrackColor = AdenBlue)
        )
    }
}