package com.adentweets.app.presentation.screens.explore.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.Post
import com.adentweets.app.domain.usecase.search.SearchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrendingTopicViewModel @Inject constructor(
    private val searchUseCase: SearchUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadPostsByHashtag(hashtag: String) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = searchUseCase.getPostsByHashtag(hashtag)) {
                is Resource.Success -> _posts.value = result.data
                else -> _posts.value = emptyList()
            }
            _isLoading.value = false
        }
    }
}