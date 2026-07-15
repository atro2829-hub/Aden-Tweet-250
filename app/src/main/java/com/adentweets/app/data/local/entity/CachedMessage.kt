package com.adentweets.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_messages")
data class CachedMessage(
    @PrimaryKey val messageId: String,
    val conversationId: String,
    val senderId: String,
    val content: String? = null,
    val sentAt: Long,
    val jsonReadBy: String = "{}",
    val isDeleted: Boolean = false,
    val cachedAt: Long = System.currentTimeMillis()
)