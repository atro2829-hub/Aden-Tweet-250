package com.adentweets.app.domain.model

data class Conversation(
    val conversationId: String = "",
    val participantIds: List<String> = emptyList(),
    val participantsMap: Map<String, Boolean> = emptyMap(),
    val createdAt: Long = System.currentTimeMillis(),
    val lastMessage: LastMessageInfo? = null,
    val unreadCounts: Map<String, Int> = emptyMap()
)

data class LastMessageInfo(
    val content: String = "",
    val senderId: String = "",
    val sentAt: Long = 0,
    val type: String = "text"
)