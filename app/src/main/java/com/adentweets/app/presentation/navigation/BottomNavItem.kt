package com.adentweets.app.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home.route, "الرئيسية", Icons.Default.Home),
    BottomNavItem(Screen.Explore.route, "استكشاف", Icons.Default.Search),
    BottomNavItem(Screen.Notifications.route, "إشعارات", Icons.Default.Notifications),
    BottomNavItem(Screen.Inbox.route, "رسائل", Icons.Default.MailOutline)
)