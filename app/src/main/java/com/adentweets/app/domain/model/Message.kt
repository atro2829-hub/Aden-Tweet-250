package com.adentweets.app.domain.model

data class Message(
    val messageId: String = "",
    val conversationId: String = "",
    val senderId: String = "",
    val content: String? = null,
    val mediaBase64: String? = null,
    val mediaType: MediaType? = null,
    val mediaThumbnailBase64: String? = null,
    val sentAt: Long = System.currentTimeMillis(),
    val readBy: Map<String, Long> = emptyMap(),
    val isDeleted: Boolean = false,
    val replyToMessageId: String? = null
)