package com.adentweets.app.presentation.components.post

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.adentweets.app.domain.model.Post
import com.adentweets.app.domain.model.User
import com.adentweets.app.presentation.components.common.Base64ImageView
import com.adentweets.app.presentation.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PostCard(
    post: Post,
    onLike: () -> Unit = {},
    onRetweet: () -> Unit = {},
    onReply: () -> Unit = {},
    onPostClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onBookmark: () -> Unit = {},
    onShare: () -> Unit = {},
    showActions: Boolean = true
) {
    val author = post.author

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onPostClick,
                onDoubleClick = { if (!post.isLikedByCurrentUser) onLike() }
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        if (post.isRetweet && post.originalPost == null) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 4.dp, start = 28.dp)) {
                Icon(Icons.Default.Repeat, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "أعد النشر", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            // Avatar
            Base64ImageView(
                base64 = author?.avatarBase64 ?: "",
                modifier = Modifier.size(44.dp),
                isCircle = true,
                size = 44.dp,
                contentScale = ContentScale.Crop,
                placeholder = {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Content column
            Column(modifier = Modifier.weight(1f)) {
                // Header row
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text(text = author?.displayName ?: "", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f, fill = false))
                    if (author?.isVerified == true) {
                        Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(16.dp).padding(start = 4.dp), tint = VerifiedBlue)
                    }
                    Text(text = "@${author?.username ?: ""}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(horizontal = 6.dp))
                    Text(text = "·", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.width(6.dp))
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Post content
                Text(text = post.content, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface, maxLines = 5, overflow = TextOverflow.Ellipsis, lineHeight = 20.sp)

                // Media preview
                if (post.mediaItems.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    MediaPreview(mediaItems = post.mediaItems)
                }

                // Quoted post
                if (post.isQuote && post.quotedPost != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    QuotedPostView(post = post.quotedPost!!)
                }

                // Action bar
                if (showActions) {
                    Spacer(modifier = Modifier.height(8.dp))
                    PostActionBar(
                        post = post,
                        onReply = onReply,
                        onRetweet = onRetweet,
                        onLike = onLike,
                        onBookmark = onBookmark,
                        onShare = onShare
                    )
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(top = 12.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    }
}

@Composable
private fun MediaPreview(mediaItems: List<com.adentweets.app.domain.model.MediaItem>) {
    val shape = RoundedCornerShape(16.dp)
    if (mediaItems.size == 1) {
        Base64ImageView(base64 = mediaItems[0].base64Data, modifier = Modifier.fillMaxWidth().height(200.dp).clip(shape))
    } else if (mediaItems.size <= 4) {
        val columns = if (mediaItems.size >= 3) 2 else mediaItems.size
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            mediaItems.chunked(columns).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
                    row.forEach { media ->
                        Base64ImageView(base64 = media.base64Data, modifier = Modifier.weight(1f).height(150.dp).clip(shape))
                    }
                    if (row.size < columns) { repeat(columns - row.size) { Spacer(modifier = Modifier.weight(1f)) } }
                }
            }
        }
    }
}

@Composable
private fun QuotedPostView(post: Post) {
    Card(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Base64ImageView(base64 = post.author?.avatarBase64 ?: "", isCircle = true, size = 20.dp, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = post.author?.displayName ?: "", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "@${post.author?.username ?: ""}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = post.content, style = MaterialTheme.typography.bodySmall, maxLines = 3, overflow = TextOverflow.Ellipsis)
        }
    }
}