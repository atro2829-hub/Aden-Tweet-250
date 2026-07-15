package com.adentweets.app.domain.repository

import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.Conversation
import com.adentweets.app.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    suspend fun getConversations(): Resource<List<Conversation>>
    fun observeConversations(): Flow<List<Conversation>>
    fun observeMessages(conversationId: String): Flow<List<Message>>
    suspend fun sendMessage(conversationId: String, content: String, mediaBase64: String?, mediaType: String?): Resource<Message>
    suspend fun createConversation(participantId: String): Resource<Conversation>
    suspend fun markAsRead(conversationId: String): Resource<Unit>
    suspend fun deleteMessage(messageId: String, conversationId: String): Resource<Unit>
    fun getUnreadCount(): Flow<Int>
}