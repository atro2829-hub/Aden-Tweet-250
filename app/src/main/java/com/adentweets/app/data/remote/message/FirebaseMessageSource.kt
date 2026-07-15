package com.adentweets.app.data.remote.message

import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseMessageSource @Inject constructor(
    private val database: FirebaseDatabase
) {
    fun observeMessages(conversationId: String): Flow<List<DataSnapshot>> = callbackFlow {
        val ref = database.getReference("messages/$conversationId").orderByChild("sentAt").limitToLast(50)
        val messages = mutableMapOf<String, DataSnapshot>()
        val listener = ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snap: DataSnapshot, prev: String?) { messages[snap.key!!] = snap; trySend(messages.values.sortedBy { it.child("sentAt").getValue(Long::class.java) ?: 0 }) }
            override fun onChildChanged(snap: DataSnapshot, prev: String?) { messages[snap.key!!] = snap; trySend(messages.values.sortedBy { it.child("sentAt").getValue(Long::class.java) ?: 0 }) }
            override fun onChildRemoved(snap: DataSnapshot) { messages.remove(snap.key); trySend(messages.values.sortedBy { it.child("sentAt").getValue(Long::class.java) ?: 0 }) }
            override fun onChildMoved(snap: DataSnapshot, prev: String?) {}
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        })
        awaitClose { ref.removeEventListener(listener) }
    }

    suspend fun sendMessage(conversationId: String, messageData: Map<String, Any?>): String {
        val ref = database.getReference("messages/$conversationId").push()
        val data = messageData.toMutableMap().apply { put("messageId", ref.key!!); put("conversationId", conversationId) }
        ref.setValue(data).await()
        val lastMessage = mapOf("content" to (data["content"] ?: ""), "senderId" to data["senderId"], "sentAt" to ServerValue.TIMESTAMP, "type" to (data.getOrDefault("type", "text") ?: "text"))
        database.getReference("conversations/$conversationId/lastMessage").setValue(lastMessage).await()
        return ref.key!!
    }

    suspend fun createConversation(uid: String, participantId: String): String {
        val ref = database.getReference("conversations").push()
        val convId = ref.key!!
        val participants = listOf(uid, participantId).sorted()
        val data = mapOf(
            "conversationId" to convId, "participantIds" to participants,
            "participantsMap" to mapOf(uid to true, participantId to true),
            "createdAt" to ServerValue.TIMESTAMP,
            "lastMessage" to mapOf("content" to "", "senderId" to "", "sentAt" to ServerValue.TIMESTAMP, "type" to "text")
        )
        ref.setValue(data).await()
        return convId
    }

    fun observeConversations(uid: String): Flow<List<DataSnapshot>> = callbackFlow {
        val ref = database.getReference("conversations").orderByChild("createdAt").limitToLast(50)
        val listener = ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val convs = snapshot.children.filter { conv ->
                    conv.child("participantsMap").child(uid).exists()
                }.sortedByDescending { it.child("createdAt").getValue(Long::class.java) ?: 0 }
                trySend(convs)
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        })
        awaitClose { ref.removeEventListener(listener) }
    }

    suspend fun markAsRead(conversationId: String, uid: String) {
        database.getReference("messages/$conversationId").orderByChild("sentAt").limitToLast(1).get().await().children.firstOrNull()?.let {
            database.getReference("messages/$conversationId/${it.key}/readBy/$uid").setValue(ServerValue.TIMESTAMP).await()
        }
    }

    suspend fun deleteMessage(messageId: String, conversationId: String) {
        database.getReference("messages/$conversationId/$messageId/isDeleted").setValue(true).await()
    }
}