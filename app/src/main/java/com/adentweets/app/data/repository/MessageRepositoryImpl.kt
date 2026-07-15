package com.adentweets.app.data.repository

import com.adentweets.app.core.util.Resource
import com.adentweets.app.data.remote.auth.FirebaseAuthSource
import com.adentweets.app.data.remote.message.FirebaseMessageSource
import com.adentweets.app.domain.model.Conversation
import com.adentweets.app.domain.model.LastMessageInfo
import com.adentweets.app.domain.model.MediaType
import com.adentweets.app.domain.model.Message
import com.adentweets.app.domain.repository.MessageRepository
import com.google.firebase.database.DataSnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageRepositoryImpl @Inject constructor(
    private val messageSource: FirebaseMessageSource,
    private val authSource: FirebaseAuthSource
) : MessageRepository {

    override suspend fun getConversations(): Resource<List<Conversation>> {
        return try {
            val uid = authSource.currentUserId() ?: return Resource.Error("Not authenticated")
            val snapshots = mutableListOf<DataSnapshot>()
            val collector = messageSource.observeConversations(uid)
            collector.collect { snapshots.addAll(it) }
            Resource.Success(snapshots.map { it.toDomainConversation() })
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get conversations")
        }
    }

    override fun observeConversations(): Flow<List<Conversation>> {
        val uid = authSource.currentUserId() ?: return flow { emit(emptyList()) }
        return messageSource.observeConversations(uid).map { snapshots ->
            snapshots.map { it.toDomainConversation() }
        }
    }

    override fun observeMessages(conversationId: String): Flow<List<Message>> {
        return messageSource.observeMessages(conversationId).map { snapshots ->
            snapshots.map { it.toDomainMessage() }
        }
    }

    override suspend fun sendMessage(
        conversationId: String,
        content: String,
        mediaBase64: String?,
        mediaType: String?
    ): Resource<Message> {
        return try {
            val uid = authSource.currentUserId() ?: return Resource.Error("Not authenticated")
            val data = mutableMapOf<String, Any?>(
                "senderId" to uid,
                "content" to content,
                "sentAt" to System.currentTimeMillis(),
                "isDeleted" to false,
                "type" to (mediaType ?: "text")
            )
            if (mediaBase64 != null) {
                data["mediaBase64"] = mediaBase64
            }
            val messageId = messageSource.sendMessage(conversationId, data)
            Resource.Success(
                Message(
                    messageId = messageId,
                    conversationId = conversationId,
                    senderId = uid,
                    content = content,
                    mediaBase64 = mediaBase64,
                    mediaType = mediaType?.let { MediaType.valueOf(it.uppercase()) },
                    sentAt = System.currentTimeMillis()
                )
            )
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to send message")
        }
    }

    override suspend fun createConversation(participantId: String): Resource<Conversation> {
        return try {
            val uid = authSource.currentUserId() ?: return Resource.Error("Not authenticated")
            val convId = messageSource.createConversation(uid, participantId)
            Resource.Success(
                Conversation(
                    conversationId = convId,
                    participantIds = listOf(uid, participantId).sorted()
                )
            )
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to create conversation")
        }
    }

    override suspend fun markAsRead(conversationId: String): Resource<Unit> {
        return try {
            val uid = authSource.currentUserId() ?: return Resource.Error("Not authenticated")
            messageSource.markAsRead(conversationId, uid)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to mark as read")
        }
    }

    override suspend fun deleteMessage(messageId: String, conversationId: String): Resource<Unit> {
        return try {
            messageSource.deleteMessage(messageId, conversationId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete message")
        }
    }

    override fun getUnreadCount(): Flow<Int> {
        val uid = authSource.currentUserId() ?: return flow { emit(0) }
        return callbackFlow {
            val ref = com.google.firebase.database.FirebaseDatabase.getInstance()
                .getReference("conversations").orderByChild("createdAt").limitToLast(50)
            val listener = ref.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
                override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                    var totalUnread = 0
                    for (conv in snapshot.children) {
                        val unread = conv.child("unreadCounts").child(uid).getValue(Int::class.java) ?: 0
                        totalUnread += unread
                    }
                    trySend(totalUnread)
                }
                override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                    close(error.toException())
                }
            })
            awaitClose { ref.removeEventListener(listener) }
        }
    }

    private fun DataSnapshot.toDomainConversation(): Conversation {
        val participantIds = child("participantIds").children.mapNotNull { it.getValue(String::class.java) }
        val participantsMap = mutableMapOf<String, Boolean>()
        child("participantsMap").children.forEach { participantsMap[it.key ?: ""] = it.getValue(Boolean::class.java) ?: false }

        val lastMessageSnap = child("lastMessage")
        val lastMessage = if (lastMessageSnap.exists()) {
            LastMessageInfo(
                content = lastMessageSnap.child("content").getValue(String::class.java) ?: "",
                senderId = lastMessageSnap.child("senderId").getValue(String::class.java) ?: "",
                sentAt = lastMessageSnap.child("sentAt").getValue(Long::class.java) ?: 0,
                type = lastMessageSnap.child("type").getValue(String::class.java) ?: "text"
            )
        } else null

        val unreadCounts = mutableMapOf<String, Int>()
        child("unreadCounts").children.forEach { unreadCounts[it.key ?: ""] = it.getValue(Int::class.java) ?: 0 }

        return Conversation(
            conversationId = child("conversationId").getValue(String::class.java) ?: key ?: "",
            participantIds = participantIds,
            participantsMap = participantsMap,
            createdAt = child("createdAt").getValue(Long::class.java) ?: 0L,
            lastMessage = lastMessage,
            unreadCounts = unreadCounts
        )
    }

    private fun DataSnapshot.toDomainMessage(): Message {
        val mediaBase64 = child("mediaBase64").getValue(String::class.java)
        val mediaTypeStr = child("type").getValue(String::class.java)
        val readBy = mutableMapOf<String, Long>()
        child("readBy").children.forEach { readBy[it.key ?: ""] = it.getValue(Long::class.java) ?: 0 }

        return Message(
            messageId = child("messageId").getValue(String::class.java) ?: key ?: "",
            conversationId = child("conversationId").getValue(String::class.java) ?: "",
            senderId = child("senderId").getValue(String::class.java) ?: "",
            content = child("content").getValue(String::class.java),
            mediaBase64 = mediaBase64,
            mediaType = mediaTypeStr?.let {
                try { MediaType.valueOf(it.uppercase()) } catch (e: Exception) { null }
            },
            sentAt = child("sentAt").getValue(Long::class.java) ?: 0L,
            readBy = readBy,
            isDeleted = child("isDeleted").getValue(Boolean::class.java) ?: false,
            replyToMessageId = child("replyToMessageId").getValue(String::class.java)
        )
    }
}