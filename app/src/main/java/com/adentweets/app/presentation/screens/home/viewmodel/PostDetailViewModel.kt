package com.adentweets.app.presentation.screens.home.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adentweets.app.domain.model.Post
import com.adentweets.app.domain.usecase.post.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getPostUseCase: GetPostUseCase,
    private val getRepliesUseCase: GetRepliesUseCase,
    private val likePostUseCase: LikePostUseCase,
    private val retweetPostUseCase: RetweetPostUseCase,
    private val bookmarkPostUseCase: BookmarkPostUseCase
) : ViewModel() {

    private val postId: String = savedStateHandle["postId"] ?: ""

    private val _post = MutableStateFlow<Post?>(null)
    val post: StateFlow<Post?> = _post

    private val _replies = MutableStateFlow<List<Post>>(emptyList())
    val replies: StateFlow<List<Post>> = _replies

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _repliesLoading = MutableStateFlow(false)
    val repliesLoading: StateFlow<Boolean> = _repliesLoading

    init {
        loadPost()
        loadReplies()
    }

    private fun loadPost() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                when (val result = getPostUseCase(postId)) {
                    is com.adentweets.app.core.util.Resource.Success -> _post.value = result.data
                    else -> {}
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadReplies() {
        viewModelScope.launch {
            _repliesLoading.value = true
            try {
                when (val result = getRepliesUseCase(postId, 50)) {
                    is com.adentweets.app.core.util.Resource.Success -> _replies.value = result.data
                    else -> {}
                }
            } finally {
                _repliesLoading.value = false
            }
        }
    }

    fun likePost() {
        val post = _post.value ?: return
        viewModelScope.launch {
            val isLiked = post.isLikedByCurrentUser
            _post.value = post.copy(
                isLikedByCurrentUser = !isLiked,
                likesCount = if (isLiked) post.likesCount - 1 else post.likesCount + 1
            )
            likePostUseCase(postId, !isLiked)
        }
    }

    fun retweetPost() {
        val post = _post.value ?: return
        viewModelScope.launch {
            val isRT = post.isRetweetedByCurrentUser
            _post.value = post.copy(
                isRetweetedByCurrentUser = !isRT,
                retweetsCount = if (isRT) post.retweetsCount - 1 else post.retweetsCount + 1
            )
            retweetPostUseCase(postId, !isRT)
        }
    }

    fun bookmarkPost() {
        val post = _post.value ?: return
        viewModelScope.launch {
            val isBm = post.isBookmarkedByCurrentUser
            _post.value = post.copy(isBookmarkedByCurrentUser = !isBm)
            bookmarkPostUseCase(postId, !isBm)
        }
    }
}