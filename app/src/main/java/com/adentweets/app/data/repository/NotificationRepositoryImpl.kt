package com.adentweets.app.data.repository

import com.adentweets.app.core.util.Resource
import com.adentweets.app.data.remote.notification.FirebaseNotificationSource
import com.adentweets.app.domain.model.AppNotification
import com.adentweets.app.domain.model.NotificationType
import com.adentweets.app.domain.repository.NotificationRepository
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepositoryImpl @Inject constructor(
    private val notificationSource: FirebaseNotificationSource,
    private val database: FirebaseDatabase
) : NotificationRepository {

    override fun observeNotifications(uid: String): Flow<List<AppNotification>> {
        return notificationSource.observeNotifications(uid).map { snapshots ->
            snapshots.map { it.toDomainNotification() }
        }
    }

    override suspend fun markAsRead(notificationId: String): Resource<Unit> {
        return try {
            notificationSource.markAsRead(notificationId, "")
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to mark notification as read")
        }
    }

    override suspend fun markAllAsRead(uid: String): Resource<Unit> {
        return try {
            notificationSource.markAllAsRead(uid)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to mark all notifications as read")
        }
    }

    override suspend fun deleteNotification(notificationId: String): Resource<Unit> {
        return try {
            notificationSource.deleteNotification(notificationId, "")
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete notification")
        }
    }

    override fun getUnreadCount(uid: String): Flow<Int> = callbackFlow {
        val ref = database.getReference("notifications/$uid")
        val listener = ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val count = snapshot.children.count { child ->
                    child.child("isRead").getValue(Boolean::class.java) == false
                }
                trySend(count)
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        })
        awaitClose { ref.removeEventListener(listener) }
    }

    private fun DataSnapshot.toDomainNotification(): AppNotification {
        val typeStr = child("type").getValue(String::class.java) ?: "LIKE"
        val type = try {
            NotificationType.valueOf(typeStr.uppercase())
        } catch (e: Exception) { NotificationType.LIKE }

        return AppNotification(
            notificationId = key ?: "",
            type = type,
            fromUserId = child("fromUserId").getValue(String::class.java) ?: "",
            postId = child("postId").getValue(String::class.java),
            commentId = child("commentId").getValue(String::class.java),
            message = child("message").getValue(String::class.java) ?: "",
            createdAt = child("createdAt").getValue(Long::class.java) ?: 0L,
            isRead = child("isRead").getValue(Boolean::class.java) ?: false
        )
    }
}