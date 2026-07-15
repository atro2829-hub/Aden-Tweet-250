package com.adentweets.app.presentation.screens.notifications

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.adentweets.app.domain.model.AppNotification
import com.adentweets.app.domain.model.NotificationType
import com.adentweets.app.presentation.components.common.Base64ImageView
import com.adentweets.app.presentation.components.common.EmptyStateView
import com.adentweets.app.presentation.screens.notifications.viewmodel.NotificationsViewModel
import com.adentweets.app.presentation.theme.AdenBlue
import com.adentweets.app.presentation.theme.LikeRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    navController: NavController,
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    val notifications by viewModel.notifications.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()

    val filteredNotifications = remember(notifications, selectedTab) {
        if (selectedTab == 0) notifications
        else notifications.filter { it.type == NotificationType.MENTION }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "الإشعارات",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                    }
                },
                actions = {
                    if (notifications.any { !it.isRead }) {
                        TextButton(onClick = { viewModel.markAllRead() }) {
                            Text(
                                text = "تحديد الكل كمقروء",
                                color = AdenBlue,
                                fontSize = 14.sp
                            )
                        }
                    }
                    IconButton(onClick = { /* Filter */ }) {
                        Icon(Icons.Default.FilterList, contentDescription = "تصفية")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onSurface,
                divider = { HorizontalDivider() }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { viewModel.selectTab(0) },
                    text = { Text("الكل", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { viewModel.selectTab(1) },
                    text = { Text("الإشعارات المذكورة", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) }
                )
            }

            if (filteredNotifications.isEmpty()) {
                EmptyStateView(
                    message = "لا توجد إشعارات",
                    icon = Icons.Default.Notifications
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(filteredNotifications, key = { it.notificationId }) { notification ->
                        NotificationItem(
                            notification = notification,
                            onClick = {
                                notification.postId?.let { postId ->
                                    navController.navigate("post_detail/$postId")
                                } ?: notification.fromUserId.let { userId ->
                                    if (userId.isNotEmpty()) {
                                        navController.navigate("profile/$userId")
                                    }
                                }
                            }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 72.dp),
                            thickness = 0.5.dp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationItem(
    notification: AppNotification,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (!notification.isRead) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
        else Color.Transparent,
        label = "notif_bg"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Avatar
        notification.fromUser?.avatarBase64?.let { avatar ->
            Base64ImageView(
                base64 = avatar,
                isCircle = true,
                size = 40.dp
            )
        } ?: Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Content
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = buildNotificationText(notification),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = getTimeAgoArabic(notification.createdAt),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Unread indicator
        if (!notification.isRead) {
            Box(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(AdenBlue)
            )
        }
    }
}

@Composable
private fun buildNotificationText(notification: AppNotification): androidx.compose.ui.text.AnnotatedString {
    val username = "@${notification.fromUser?.username ?: ""}"
    val displayName = notification.fromUser?.displayName ?: ""

    return buildAnnotatedString {
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append(displayName)
        }
        append(" ")
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
            append(username)
        }
        append(" ")

        when (notification.type) {
            NotificationType.LIKE -> {
                withStyle(style = SpanStyle(color = LikeRed)) {
                    append("أعجب")
                }
                append(" بمنشورك")
            }
            NotificationType.RETWEET -> {
                withStyle(style = SpanStyle(color = com.adentweets.app.presentation.theme.RetweetGreen)) {
                    append("أعاد")
                }
                append(" نشر منشورك")
            }
            NotificationType.FOLLOW -> {
                append("بدأ بمتابعتك")
            }
            NotificationType.COMMENT -> {
                append("ردّ على منشورك")
            }
            NotificationType.MENTION -> {
                append("أشاركك في منشور")
            }
            NotificationType.QUOTE -> {
                append("اقتبس منشورك")
            }
            NotificationType.MESSAGE -> {
                append("أرسل لك رسالة")
            }
        }

        if (notification.message.isNotBlank()) {
            append("\n")
            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
                append(notification.message)
            }
        }
    }
}

private fun getTimeAgoArabic(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        seconds < 60 -> "الآن"
        minutes < 60 -> "منذ $minutes ${arabicMinutes(minutes)}"
        hours < 24 -> "منذ $hours ${arabicHours(hours)}"
        days < 7 -> "منذ $days ${arabicDays(days)}"
        days < 30 -> "منذ ${days / 7} ${if (days / 7 == 1) "أسبوع" else "أسابيع"}"
        else -> "منذ ${days / 30} ${if (days / 30 == 1) "شهر" else "أشهر"}"
    }
}

private fun arabicMinutes(m: Long): String = if (m == 1L) "دقيقة" else "دقائق"
private fun arabicHours(h: Long): String = if (h == 1L) "ساعة" else if (h == 2L) "ساعتين" else if (h in 3..10) "$h ساعات" else "ساعة"
private fun arabicDays(d: Long): String = if (d == 1L) "يوم" else if (d == 2L) "يومين" else if (d in 3..10) "$d أيام" else "يوم"