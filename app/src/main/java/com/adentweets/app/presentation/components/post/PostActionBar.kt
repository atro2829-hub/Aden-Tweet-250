package com.adentweets.app.presentation.components.post

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adentweets.app.domain.model.Post
import com.adentweets.app.presentation.theme.*

@Composable
fun PostActionBar(
    post: Post,
    onReply: () -> Unit = {},
    onRetweet: () -> Unit = {},
    onLike: () -> Unit = {},
    onBookmark: () -> Unit = {},
    onShare: () -> Unit = {}
) {
    Row(modifier = Modifier.fillMaxWidth().padding(end = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        // Reply
        ActionButton(icon = if (true) Icons.Outlined.ChatBubbleOutline else Icons.Outlined.ChatBubbleOutline, count = post.commentsCount, color = MaterialTheme.colorScheme.onSurfaceVariant, onClick = onReply, size = 18.dp)

        // Retweet
        val rtColor by animateColorAsState(if (post.isRetweetedByCurrentUser) RetweetGreen else MaterialTheme.colorScheme.onSurfaceVariant, label = "rt")
        ActionButton(icon = Icons.Outlined.Repeat, count = post.retweetsCount, color = rtColor, onClick = onRetweet, activeColor = RetweetGreen, isActive = post.isRetweetedByCurrentUser, size = 18.dp)

        // Like
        val likeColor by animateColorAsState(if (post.isLikedByCurrentUser) LikeRed else MaterialTheme.colorScheme.onSurfaceVariant, label = "like")
        ActionButton(icon = if (post.isLikedByCurrentUser) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder, count = post.likesCount, color = likeColor, onClick = onLike, activeColor = LikeRed, isActive = post.isLikedByCurrentUser, size = 18.dp)

        // Views
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 12.dp)) {
            Icon(Icons.Outlined.BarChart, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            if (post.viewsCount > 0) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = formatCount(post.viewsCount), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        // Share & Bookmark
        Row {
            IconButton(onClick = onShare, modifier = Modifier.size(36.dp)) { Icon(Icons.Outlined.Share, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant) }
            val bmColor by animateColorAsState(if (post.isBookmarkedByCurrentUser) BookmarkBlue else MaterialTheme.colorScheme.onSurfaceVariant, label = "bm")
            IconButton(onClick = onBookmark, modifier = Modifier.size(36.dp)) { Icon(if (post.isBookmarkedByCurrentUser) Icons.Default.BookmarkBorder else Icons.Outlined.BookmarkBorder, contentDescription = null, modifier = Modifier.size(18.dp), tint = bmColor) }
        }
    }
}

@Composable
private fun ActionButton(icon: androidx.compose.ui.graphics.vector.ImageVector, count: Int, color: Color, onClick: () -> Unit, activeColor: Color = Color.Unspecified, isActive: Boolean = false, size: androidx.compose.ui.unit.Dp = 18.dp) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable(onClick = onClick).padding(horizontal = 12.dp, vertical = 4.dp)) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(size), tint = color)
        if (count > 0) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = formatCount(count), style = MaterialTheme.typography.labelSmall, color = color, fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal)
        }
    }
}

fun formatCount(count: Int): String {
    return when {
        count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
        count >= 1_000 -> String.format("%.1fK", count / 1_000.0)
        else -> count.toString()
    }
}