package com.adentweets.app.domain.repository

import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.AppNotification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun observeNotifications(uid: String): Flow<List<AppNotification>>
    suspend fun markAsRead(notificationId: String): Resource<Unit>
    suspend fun markAllAsRead(uid: String): Resource<Unit>
    suspend fun deleteNotification(notificationId: String): Resource<Unit>
    fun getUnreadCount(uid: String): Flow<Int>
}