package com.adentweets.app.domain.usecase.notification

import com.adentweets.app.domain.model.AppNotification
import kotlinx.coroutines.flow.Flow
import com.adentweets.app.domain.repository.NotificationRepository
import javax.inject.Inject

class GetNotificationsUseCase @Inject constructor(
    private val repo: NotificationRepository
) {
    operator fun invoke(uid: String): Flow<List<AppNotification>> = repo.observeNotifications(uid)
}