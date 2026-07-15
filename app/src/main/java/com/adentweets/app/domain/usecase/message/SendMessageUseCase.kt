package com.adentweets.app.domain.usecase.message

import com.adentweets.app.domain.model.Message
import com.adentweets.app.domain.repository.MessageRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val repo: MessageRepository
) {
    suspend operator fun invoke(
        conversationId: String,
        content: String,
        mediaBase64: String? = null,
        mediaType: String? = null
    ): Message {
        val result = repo.sendMessage(conversationId, content, mediaBase64, mediaType)
        return result.data ?: throw Exception(result.message ?: "Failed to send message")
    }
}