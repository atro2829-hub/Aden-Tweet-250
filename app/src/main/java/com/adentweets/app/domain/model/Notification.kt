package com.adentweets.app.domain.model

data class AppNotification(
    val notificationId: String = "",
    val type: NotificationType = NotificationType.LIKE,
    val fromUserId: String = "",
    val fromUser: User? = null,
    val postId: String? = null,
    val commentId: String? = null,
    val message: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

enum class NotificationType {
    LIKE, RETWEET, FOLLOW, COMMENT, MENTION, QUOTE, MESSAGE
}