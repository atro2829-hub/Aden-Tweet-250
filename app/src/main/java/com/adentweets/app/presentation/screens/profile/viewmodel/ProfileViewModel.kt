package com.adentweets.app.presentation.screens.profile.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.Post
import com.adentweets.app.domain.model.User
import com.adentweets.app.domain.usecase.post.LikePostUseCase
import com.adentweets.app.domain.usecase.post.RetweetPostUseCase
import com.adentweets.app.domain.usecase.post.BookmarkPostUseCase
import com.adentweets.app.domain.usecase.user.*
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val followUserUseCase: FollowUserUseCase,
    private val getFollowersUseCase: GetFollowersUseCase,
    private val getFollowingUseCase: GetFollowingUseCase,
    private val likePostUseCase: LikePostUseCase,
    private val retweetPostUseCase: RetweetPostUseCase,
    private val bookmarkPostUseCase: BookmarkPostUseCase
) : ViewModel() {

    private val targetUserId: String = savedStateHandle["userId"]
        ?: FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val isOwnProfile: Boolean = targetUserId == currentUid

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isFollowing = MutableStateFlow(false)
    val isFollowing: StateFlow<Boolean> = _isFollowing

    private val _followersCount = MutableStateFlow(0)
    val followersCount: StateFlow<Int> = _followersCount

    private val _followingCount = MutableStateFlow(0)
    val followingCount: StateFlow<Int> = _followingCount

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts

    private val _replies = MutableStateFlow<List<Post>>(emptyList())
    val replies: StateFlow<List<Post>> = _replies

    private val _likes = MutableStateFlow<List<Post>>(emptyList())
    val likes: StateFlow<List<Post>> = _likes

    private val _mediaPosts = MutableStateFlow<List<Post>>(emptyList())
    val mediaPosts: StateFlow<List<Post>> = _mediaPosts

    init {
        loadUserProfile()
        loadFollowers()
        loadFollowing()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                when (val result = getUserProfileUseCase(targetUserId)) {
                    is Resource.Success -> {
                        _user.value = result.data
                        _followersCount.value = result.data.followersCount
                        _followingCount.value = result.data.followingCount
                        // TODO: Load posts, replies, likes, media from their respective use cases / repos
                    }
                    else -> {}
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadFollowers() {
        viewModelScope.launch {
            when (val result = getFollowersUseCase(targetUserId, 100)) {
                is Resource.Success -> {
                    _followersCount.value = result.data.size
                    _isFollowing.value = result.data.any { it.uid == currentUid }
                }
                else -> {}
            }
        }
    }

    private fun loadFollowing() {
        viewModelScope.launch {
            when (val result = getFollowingUseCase(targetUserId, 100)) {
                is Resource.Success -> {
                    _followingCount.value = result.data.size
                }
                else -> {}
            }
        }
    }

    fun selectTab(index: Int) { _selectedTab.value = index }

    fun toggleFollow() {
        viewModelScope.launch {
            val newFollowingState = !_isFollowing.value
            _isFollowing.value = newFollowingState
            _followersCount.value = if (newFollowingState)
                _followersCount.value + 1
            else
                _followersCount.value - 1
            try {
                followUserUseCase(targetUserId, newFollowingState)
            } catch (_: Exception) {
                _isFollowing.value = !newFollowingState
                _followersCount.value = if (newFollowingState)
                    _followersCount.value - 1
                else
                    _followersCount.value + 1
            }
        }
    }

    fun likePost(postId: String) {
        viewModelScope.launch {
            val currentPosts = _posts.value.toMutableList()
            val index = currentPosts.indexOfFirst { it.postId == postId }
            if (index == -1) return@launch
            val post = currentPosts[index]
            val isLiked = post.isLikedByCurrentUser
            currentPosts[index] = post.copy(
                isLikedByCurrentUser = !isLiked,
                likesCount = if (isLiked) post.likesCount - 1 else post.likesCount + 1
            )
            _posts.value = currentPosts
            likePostUseCase(postId, !isLiked)
        }
    }

    fun retweetPost(postId: String) {
        viewModelScope.launch {
            val currentPosts = _posts.value.toMutableList()
            val index = currentPosts.indexOfFirst { it.postId == postId }
            if (index == -1) return@launch
            val post = currentPosts[index]
            val isRT = post.isRetweetedByCurrentUser
            currentPosts[index] = post.copy(
                isRetweetedByCurrentUser = !isRT,
                retweetsCount = if (isRT) post.retweetsCount - 1 else post.retweetsCount + 1
            )
            _posts.value = currentPosts
            retweetPostUseCase(postId, !isRT)
        }
    }

    fun bookmarkPost(postId: String) {
        viewModelScope.launch {
            val currentPosts = _posts.value.toMutableList()
            val index = currentPosts.indexOfFirst { it.postId == postId }
            if (index == -1) return@launch
            val post = currentPosts[index]
            val isBm = post.isBookmarkedByCurrentUser
            currentPosts[index] = post.copy(isBookmarkedByCurrentUser = !isBm)
            _posts.value = currentPosts
            bookmarkPostUseCase(postId, !isBm)
        }
    }
}