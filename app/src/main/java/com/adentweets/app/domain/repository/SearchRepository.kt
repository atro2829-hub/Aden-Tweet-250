package com.adentweets.app.domain.repository

import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.TrendingTopic
import com.adentweets.app.domain.model.Post
import com.adentweets.app.domain.model.User

interface SearchRepository {
    suspend fun searchUsers(query: String): Resource<List<User>>
    suspend fun searchPosts(query: String): Resource<List<Post>>
    suspend fun getTrendingTopics(): Resource<List<TrendingTopic>>
    suspend fun getPostsByHashtag(hashtag: String): Resource<List<Post>>
}