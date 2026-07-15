package com.adentweets.app.presentation.screens.bookmarks.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.Post
import com.adentweets.app.domain.usecase.post.BookmarkPostUseCase
import com.adentweets.app.domain.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init { loadBookmarks() }

    fun refresh() { loadBookmarks() }

    private fun loadBookmarks() {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = postRepository.getBookmarks()) {
                is Resource.Success -> _posts.value = result.data
                else -> {}
            }
            _isLoading.value = false
        }
    }

    fun removeBookmark(postId: String) {
        viewModelScope.launch {
            postRepository.removeBookmark(postId)
            _posts.value = _posts.value.filter { it.postId != postId }
        }
    }
}