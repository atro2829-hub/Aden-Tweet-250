package com.adentweets.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_posts")
data class CachedPost(
    @PrimaryKey val postId: String,
    val authorId: String,
    val content: String,
    val createdAt: Long,
    val likesCount: Int,
    val retweetsCount: Int,
    val commentsCount: Int,
    val viewsCount: Int,
    val jsonMediaItems: String = "[]",
    val jsonAuthor: String = "{}",
    val isRetweet: Boolean = false,
    val originalPostId: String? = null,
    val isQuote: Boolean = false,
    val quotedPostId: String? = null,
    val replyToPostId: String? = null,
    val isPinned: Boolean = false,
    val isSensitive: Boolean = false,
    val isDeleted: Boolean = false,
    val cachedAt: Long = System.currentTimeMillis()
)