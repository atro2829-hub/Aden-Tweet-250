package com.adentweets.app.presentation.screens.explore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.Post
import com.adentweets.app.domain.model.User
import com.adentweets.app.domain.usecase.search.SearchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchResultsViewModel @Inject constructor(
    private val searchUseCase: SearchUseCase
) : ViewModel() {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun search(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val usersResult = searchUseCase.searchUsers(query)
            val postsResult = searchUseCase.searchPosts(query)
            _users.value = if (usersResult is Resource.Success) usersResult.data else emptyList()
            _posts.value = if (postsResult is Resource.Success) postsResult.data else emptyList()
            _isLoading.value = false
        }
    }
}