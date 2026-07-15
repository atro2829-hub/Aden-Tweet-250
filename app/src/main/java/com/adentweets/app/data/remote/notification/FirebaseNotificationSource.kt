package com.adentweets.app.data.remote.notification

import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseNotificationSource @Inject constructor(
    private val database: FirebaseDatabase
) {
    fun observeNotifications(uid: String): Flow<List<DataSnapshot>> = callbackFlow {
        val ref = database.getReference("notifications/$uid").orderByChild("createdAt").limitToLast(50)
        val listener = ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val notifs = snapshot.children.sortedByDescending { it.child("createdAt").getValue(Long::class.java) ?: 0 }
                trySend(notifs)
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        })
        awaitClose { ref.removeEventListener(listener) }
    }

    suspend fun markAsRead(notificationId: String, uid: String) {
        database.getReference("notifications/$uid/$notificationId/isRead").setValue(true).await()
    }

    suspend fun markAllAsRead(uid: String) {
        val snapshot = database.getReference("notifications/$uid").get().await()
        val updates = mutableMapOf<String, Any>()
        for (child in snapshot.children) {
            if (child.child("isRead").getValue(Boolean::class.java) == false) {
                updates["notifications/$uid/${child.key}/isRead"] = true
            }
        }
        if (updates.isNotEmpty()) database.reference.updateChildren(updates).await()
    }

    suspend fun deleteNotification(notificationId: String, uid: String) {
        database.getReference("notifications/$uid/$notificationId").removeValue().await()
    }
}