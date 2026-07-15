package com.adentweets.app.presentation.screens.explore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.TrendingTopic
import com.adentweets.app.domain.model.User
import com.adentweets.app.domain.usecase.search.SearchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val searchUseCase: SearchUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _trendingTopics = MutableStateFlow<List<TrendingTopic>>(emptyList())
    val trendingTopics: StateFlow<List<TrendingTopic>> = _trendingTopics

    private val _suggestedUsers = MutableStateFlow<List<User>>(emptyList())
    val suggestedUsers: StateFlow<List<User>> = _suggestedUsers

    private val _searchResults = MutableStateFlow<SearchResults>(SearchResults())
    val searchResults: StateFlow<SearchResults> = _searchResults

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _selectedCategory = MutableStateFlow("الكل")
    val selectedCategory: StateFlow<String> = _selectedCategory

    val categories = listOf("الكل", "أخبار", "رياضة", "ترفيه", "علوم", "تقنية", "أعمال")

    init {
        loadTrendingTopics()
        loadSuggestedUsers()
    }

    private fun loadTrendingTopics() {
        viewModelScope.launch {
            when (val result = searchUseCase.getTrendingTopics()) {
                is Resource.Success -> _trendingTopics.value = result.data
                else -> {}
            }
        }
    }

    private fun loadSuggestedUsers() {
        viewModelScope.launch {
            when (val result = searchUseCase.searchUsers("")) {
                is Resource.Success -> _suggestedUsers.value = result.data.take(5)
                else -> {}
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.length >= 2) search(query) else _searchResults.value = SearchResults()
    }

    private fun search(query: String) {
        viewModelScope.launch {
            _isSearching.value = true
            val users = when (val r = searchUseCase.searchUsers(query)) {
                is Resource.Success -> r.data
                else -> emptyList()
            }
            val posts = when (val r = searchUseCase.searchPosts(query)) {
                is Resource.Success -> r.data
                else -> emptyList()
            }
            _searchResults.value = SearchResults(users = users, posts = posts)
            _isSearching.value = false
        }
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    data class SearchResults(
        val users: List<User> = emptyList(),
        val posts: List<com.adentweets.app.domain.model.Post> = emptyList()
    )
}