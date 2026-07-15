package com.adentweets.app.presentation.screens.messages.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adentweets.app.domain.usecase.message.GetConversationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class InboxViewModel @Inject constructor(
    private val getConversationsUseCase: GetConversationsUseCase
) : ViewModel() {
    private val _conversations = MutableStateFlow<List<com.adentweets.app.domain.model.Conversation>>(emptyList())
    val conversations: StateFlow<List<com.adentweets.app.domain.model.Conversation>> = _conversations

    init {
        getConversationsUseCase().catch {}.collectLatest { _conversations.value = it }
    }
}