package com.adentweets.app.presentation.screens.messages.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adentweets.app.domain.model.Message
import com.adentweets.app.domain.usecase.message.SendMessageUseCase
import com.adentweets.app.domain.repository.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
    private val sendMessageUseCase: SendMessageUseCase
) : ViewModel() {
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages
    private val _messageText = MutableStateFlow("")
    val messageText: StateFlow<String> = _messageText
    private val _otherUserId = MutableStateFlow("")
    val otherUserId: StateFlow<String> = _otherUserId

    fun loadMessages(conversationId: String) {
        messageRepository.observeMessages(conversationId).catch {}.collectLatest { _messages.value = it }
    }

    fun updateMessageText(text: String) { _messageText.value = text }

    fun sendMessage(conversationId: String) {
        val text = _messageText.value.trim()
        if (text.isEmpty()) return
        viewModelScope.launch {
            sendMessageUseCase(conversationId, text, null, null)
            _messageText.value = ""
        }
    }
}