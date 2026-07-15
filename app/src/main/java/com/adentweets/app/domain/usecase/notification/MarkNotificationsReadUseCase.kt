package com.adentweets.app.domain.usecase.notification

import com.adentweets.app.domain.repository.NotificationRepository
import javax.inject.Inject

class MarkNotificationsReadUseCase @Inject constructor(
    private val repo: NotificationRepository
) {
    suspend operator fun invoke(uid: String) = repo.markAllAsRead(uid)
}