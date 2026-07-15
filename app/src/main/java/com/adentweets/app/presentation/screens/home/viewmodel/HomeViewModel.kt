package com.adentweets.app.presentation.screens.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.Post
import com.adentweets.app.domain.usecase.feed.GetFeedUseCase
import com.adentweets.app.domain.usecase.post.*
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getFeedUseCase: GetFeedUseCase,
    private val likePostUseCase: LikePostUseCase,
    private val retweetPostUseCase: RetweetPostUseCase,
    private val bookmarkPostUseCase: BookmarkPostUseCase,
    private val deletePostUseCase: DeletePostUseCase
) : ViewModel() {

    private val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab

    init {
        if (currentUid.isNotEmpty()) {
            getFeedUseCase(currentUid).catch { }.collectLatest { _posts.value = it }
        }
    }

    fun selectTab(index: Int) { _selectedTab.value = index }

    fun likePost(postId: String) {
        viewModelScope.launch {
            val post = _posts.value.find { it.postId == postId } ?: return@launch
            val isLiked = post.isLikedByCurrentUser
            val updated = _posts.value.map { if (it.postId == postId) it.copy(isLikedByCurrentUser = !isLiked, likesCount = if (isLiked) it.likesCount - 1 else it.likesCount + 1) else it }
            _posts.value = updated
            if (isLiked) likePostUseCase(postId, false) else likePostUseCase(postId, true)
        }
    }

    fun retweetPost(postId: String) {
        viewModelScope.launch {
            val post = _posts.value.find { it.postId == postId } ?: return@launch
            val isRT = post.isRetweetedByCurrentUser
            val updated = _posts.value.map { if (it.postId == postId) it.copy(isRetweetedByCurrentUser = !isRT, retweetsCount = if (isRT) it.retweetsCount - 1 else it.retweetsCount + 1) else it }
            _posts.value = updated
            if (isRT) retweetPostUseCase(postId, false) else retweetPostUseCase(postId, true)
        }
    }

    fun bookmarkPost(postId: String) {
        viewModelScope.launch {
            val post = _posts.value.find { it.postId == postId } ?: return@launch
            val isBm = post.isBookmarkedByCurrentUser
            val updated = _posts.value.map { if (it.postId == postId) it.copy(isBookmarkedByCurrentUser = !isBm) else it }
            _posts.value = updated
            if (isBm) bookmarkPostUseCase(postId, false) else bookmarkPostUseCase(postId, true)
        }
    }
}