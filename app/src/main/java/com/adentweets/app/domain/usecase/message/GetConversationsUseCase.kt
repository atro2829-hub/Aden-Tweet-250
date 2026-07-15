package com.adentweets.app.domain.usecase.message

import com.adentweets.app.domain.model.Conversation
import kotlinx.coroutines.flow.Flow
import com.adentweets.app.domain.repository.MessageRepository
import javax.inject.Inject

class GetConversationsUseCase @Inject constructor(
    private val repo: MessageRepository
) {
    operator fun invoke(): Flow<List<Conversation>> = repo.observeConversations()
}