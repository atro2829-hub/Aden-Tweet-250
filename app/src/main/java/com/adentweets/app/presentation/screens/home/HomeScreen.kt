package com.adentweets.app.presentation.screens.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.graphicsLayer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.adentweets.app.domain.model.Post
import com.adentweets.app.presentation.screens.home.viewmodel.HomeViewModel
import com.adentweets.app.presentation.theme.*

private data class BottomNavItem(
    val label: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val posts by viewModel.posts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()

    val bottomNavItems = listOf(
        BottomNavItem("الرئيسية", "home_route", Icons.Filled.Home, Icons.Filled.Home),
        BottomNavItem("اكتشف", "explore_route", Icons.Filled.Search, Icons.Filled.Search),
        BottomNavItem("الإشعارات", "notifications_route", Icons.Filled.Notifications, Icons.Filled.Notifications),
        BottomNavItem("الرسائل", "messages_route", Icons.Filled.MailOutline, Icons.Filled.MailOutline)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "عدن تويت",
                        color = AdenBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.background,
                tonalElevation = 0.dp
            ) {
                bottomNavItems.forEach { item ->
                    val isSelected = currentRoute == item.route
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label,
                                tint = if (isSelected) AdenBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                color = if (isSelected) AdenBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        selected = isSelected,
                        onClick = {
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    popUpTo("home_route") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            val fabScale by animateFloatAsState(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "fab_scale"
            )
            FloatingActionButton(
                onClick = { navController.navigate("create_post") },
                containerColor = AdenBlue,
                contentColor = Color.White,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(4.dp, 4.dp),
                modifier = Modifier
                    .size(56.dp)
                    .graphicsLayer {
                        scaleX = fabScale
                        scaleY = fabScale
                    }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Create,
                    contentDescription = "نشر تغريدة جديدة",
                    modifier = Modifier.size(26.dp)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tabs: لك / المتابَعين
            HomeTabs(
                selectedTab = selectedTab,
                onTabSelected = viewModel::selectTab
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant,
                thickness = 0.5.dp
            )

            // Feed content
            PullToRefreshBox(
                isRefreshing = false,
                onRefresh = { /* refresh handled by flow */ },
                modifier = Modifier.fillMaxSize()
            ) {
                when {
                    isLoading && posts.isEmpty() -> {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(5) {
                                ShimmerPostCardPlaceholder()
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outlineVariant,
                                    thickness = 0.5.dp
                                )
                            }
                        }
                    }
                    posts.isEmpty() -> {
                        EmptyStateView(
                            message = "لا توجد تغريدات بعد",
                            subtitle = "عند نشر التغريدات ستظهر هنا"
                        )
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 8.dp)
                        ) {
                            items(posts, key = { it.postId }) { post ->
                                PostCard(
                                    post = post,
                                    onLike = { viewModel.likePost(post.postId) },
                                    onRetweet = { viewModel.retweetPost(post.postId) },
                                    onBookmark = { viewModel.bookmarkPost(post.postId) },
                                    onPostClick = {
                                        navController.navigate("post_detail/${post.postId}")
                                    },
                                    onProfileClick = {
                                        navController.navigate("profile/${post.authorId}")
                                    },
                                    onReply = {
                                        navController.navigate("create_post?replyTo=${post.postId}")
                                    }
                                )
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outlineVariant,
                                    thickness = 0.5.dp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeTabs(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf("لك", "المتابَعين")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        tabs.forEachIndexed { index, title ->
            val isSelected = selectedTab == index
            val textColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.onSurfaceVariant,
                animationSpec = tween(200),
                label = "tab_color"
            )
            val indicatorWidth by animateDpAsState(
                targetValue = if (isSelected) 48.dp else 0.dp,
                animationSpec = tween(250),
                label = "indicator_width"
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onTabSelected(index) }
                    .padding(vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    color = textColor,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .height(3.dp)
                        .width(indicatorWidth)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .background(if (isSelected) AdenBlue else Color.Transparent)
                )
            }
        }
    }
}

// ─── PostCard ────────────────────────────────────────────────────────

@Composable
fun PostCard(
    post: Post,
    onLike: () -> Unit,
    onRetweet: () -> Unit,
    onBookmark: () -> Unit,
    onPostClick: () -> Unit,
    onProfileClick: () -> Unit,
    onReply: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPostClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        if (post.isRetweet && post.originalPost != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Canvas(modifier = Modifier.size(14.dp)) {
                    drawArc(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        startAngle = -90f,
                        sweepAngle = 270f,
                        useCenter = false,
                        style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
                    )
                    drawLine(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        start = Offset(size.width * 0.5f, size.height * 0.15f),
                        end = Offset(size.width, 0f),
                        strokeWidth = 1.5.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "أعد نشره ${post.author?.displayName ?: ""}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { onProfileClick() },
                contentAlignment = Alignment.Center
            ) {
                val avatarBase64 = post.author?.avatarBase64
                if (!avatarBase64.isNullOrBlank()) {
                    Base64ImageView(
                        base64 = avatarBase64,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Text(
                        text = (post.author?.displayName ?: "م").take(1),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Name row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = post.author?.displayName ?: "مستخدم",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (post.author?.isVerified == true) {
                        Spacer(modifier = Modifier.width(4.dp))
                        VerifiedBadge()
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "@${post.author?.username ?: "user"}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "·",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatTimestamp(post.createdAt),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Content
                Text(
                    text = post.content,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )

                // Media
                if (post.mediaItems.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        val firstMedia = post.mediaItems.first()
                        if (firstMedia.base64Data.isNotEmpty()) {
                            Base64ImageView(
                                base64 = firstMedia.base64Data,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text("📷", fontSize = 32.sp)
                            }
                        }
                    }
                }

                // Action buttons
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    PostActionButton(
                        count = post.commentsCount,
                        iconContent = "💬",
                        activeColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        onClick = onReply
                    )
                    PostActionButton(
                        count = post.retweetsCount,
                        iconContent = "🔁",
                        isActive = post.isRetweetedByCurrentUser,
                        activeColor = RetweetGreen,
                        activeBgColor = RetweetGreenBg,
                        onClick = onRetweet
                    )
                    PostActionButton(
                        count = post.likesCount,
                        iconContent = "❤",
                        isActive = post.isLikedByCurrentUser,
                        activeColor = LikeRed,
                        activeBgColor = LikeRedBg,
                        onClick = onLike
                    )
                    PostActionButton(
                        count = post.viewsCount,
                        iconContent = "👁",
                        activeColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        onClick = {}
                    )
                    Row {
                        PostActionButton(
                            count = 0,
                            iconContent = "🔖",
                            isActive = post.isBookmarkedByCurrentUser,
                            activeColor = BookmarkBlue,
                            activeBgColor = BookmarkBlueBg,
                            onClick = onBookmark,
                            showCount = false
                        )
                        PostActionButton(
                            count = 0,
                            iconContent = "↗",
                            activeColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            onClick = {},
                            showCount = false
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VerifiedBadge() {
    Canvas(modifier = Modifier.size(18.dp)) {
        drawCircle(
            color = VerifiedBlue,
            radius = size.minDimension / 2f
        )
    }
}

@Composable
private fun PostActionButton(
    count: Int,
    iconContent: String,
    onClick: () -> Unit,
    isActive: Boolean = false,
    activeColor: Color = AdenBlue,
    activeBgColor: Color = Color.Transparent,
    showCount: Boolean = true
) {
    val bgColor by animateColorAsState(
        targetValue = if (isActive) activeBgColor else Color.Transparent,
        animationSpec = tween(150),
        label = "action_bg"
    )
    val iconColor by animateColorAsState(
        targetValue = if (isActive) activeColor else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(150),
        label = "action_color"
    )

    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(bgColor)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = iconContent,
            fontSize = 16.sp,
            color = iconColor
        )
        if (showCount && count > 0) {
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = formatCount(count),
                color = iconColor,
                fontSize = 13.sp
            )
        }
    }
}

// ─── Base64ImageView placeholder ────────────────────────────────────

@Composable
fun Base64ImageView(
    base64: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        // Placeholder: In production, decode base64 → Bitmap → ImageBitmap
        Text(
            text = "🖼",
            fontSize = 24.sp,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

// ─── Shimmer placeholder ────────────────────────────────────────────

@Composable
fun ShimmerPostCardPlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = CircleShape
                    )
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                // Name shimmer
                Box(
                    modifier = Modifier
                        .height(16.dp)
                        .width(140.dp)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
                Spacer(modifier = Modifier.height(10.dp))
                // Content shimmer lines
                repeat(3) { i ->
                    Box(
                        modifier = Modifier
                            .height(14.dp)
                            .fillMaxWidth(if (i == 2) 0.55f else 1f)
                            .clip(MaterialTheme.shapes.extraSmall)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )
                    if (i < 2) Spacer(modifier = Modifier.height(8.dp))
                }
                Spacer(modifier = Modifier.height(10.dp))
                // Image shimmer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
            }
        }
    }
}

// ─── Empty state ────────────────────────────────────────────────────

@Composable
fun EmptyStateView(
    message: String,
    subtitle: String? = null
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = subtitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// ─── Utility ────────────────────────────────────────────────────────

fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val minutes = (diff / 60_000).toInt()
    return when {
        minutes < 1 -> "الآن"
        minutes < 60 -> "${minutes}د"
        minutes < 1440 -> "${minutes / 60}س"
        minutes < 43200 -> "${minutes / 1440}أ"
        else -> "${minutes / 43200}ش"
    }
}

fun formatCount(count: Int): String = when {
    count >= 1_000_000 -> String.format("%.1fم", count / 1_000_000.0)
    count >= 1_000 -> String.format("%.1fأ", count / 1_000.0)
    else -> count.toString()
}