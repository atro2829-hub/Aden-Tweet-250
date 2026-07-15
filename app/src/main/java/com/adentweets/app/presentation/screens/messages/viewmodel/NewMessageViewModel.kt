package com.adentweets.app.presentation.screens.messages.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adentweets.app.domain.model.User
import com.adentweets.app.domain.usecase.message.CreateConversationUseCase
import com.adentweets.app.domain.usecase.user.SearchUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewMessageViewModel @Inject constructor(
    private val searchUsersUseCase: SearchUsersUseCase,
    private val createConversationUseCase: CreateConversationUseCase
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _searchResults = MutableStateFlow<List<User>>(emptyList())
    val searchResults: StateFlow<List<User>> = _searchResults

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _createdConversationId = MutableStateFlow<String?>(null)
    val createdConversationId: StateFlow<String?> = _createdConversationId

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.length >= 2) {
            searchUsers(query)
        } else {
            _searchResults.value = emptyList()
        }
    }

    private fun searchUsers(query: String) {
        viewModelScope.launch {
            _isSearching.value = true
            searchUsersUseCase(query).catch { }.collectLatest { users ->
                _searchResults.value = users
                _isSearching.value = false
            }
        }
    }

    fun createConversation(otherUserId: String) {
        viewModelScope.launch {
            _isSearching.value = true
            val result = createConversationUseCase(otherUserId)
            _createdConversationId.value = result
            _isSearching.value = false
        }
    }

    fun onConversationNavigated() {
        _createdConversationId.value = null
    }
}