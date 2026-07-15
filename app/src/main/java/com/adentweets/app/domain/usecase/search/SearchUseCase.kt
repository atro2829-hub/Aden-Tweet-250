package com.adentweets.app.domain.usecase.search

import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.Post
import com.adentweets.app.domain.model.TrendingTopic
import com.adentweets.app.domain.model.User
import com.adentweets.app.domain.repository.SearchRepository
import javax.inject.Inject

class SearchUseCase @Inject constructor(
    private val repo: SearchRepository
) {
    suspend fun searchUsers(query: String): Resource<List<User>> = repo.searchUsers(query)
    suspend fun searchPosts(query: String): Resource<List<Post>> = repo.searchPosts(query)
    suspend fun getTrendingTopics(): Resource<List<TrendingTopic>> = repo.getTrendingTopics()
    suspend fun getPostsByHashtag(hashtag: String): Resource<List<Post>> = repo.getPostsByHashtag(hashtag)
}