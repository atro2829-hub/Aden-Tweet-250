package com.adentweets.app.domain.repository

import com.adentweets.app.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface FeedRepository {
    fun observeHomeFeed(uid: String): Flow<List<Post>>
    suspend fun refreshFeed(uid: String)
    suspend fun loadMoreFeed(uid: String, count: Int)
}