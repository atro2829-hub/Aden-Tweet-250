package com.adentweets.app.data.repository

import com.adentweets.app.data.remote.auth.FirebaseAuthSource
import com.adentweets.app.data.remote.feed.FirebaseFeedSource
import com.adentweets.app.data.remote.post.FirebasePostSource
import com.adentweets.app.domain.model.Post
import com.adentweets.app.domain.repository.FeedRepository
import com.google.firebase.database.DataSnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedRepositoryImpl @Inject constructor(
    private val feedSource: FirebaseFeedSource,
    private val postSource: FirebasePostSource,
    private val authSource: FirebaseAuthSource
) : FeedRepository {

    override fun observeHomeFeed(uid: String): Flow<List<Post>> {
        return feedSource.observeFeed(uid).map { postIds ->
            postIds.mapNotNull { postId ->
                try {
                    val snapshot = postSource.getPostById(postId)
                    snapshot?.toDomainPost()
                } catch (e: Exception) { null }
            }
        }
    }

    override suspend fun refreshFeed(uid: String) {
        // Feed is automatically updated via Firebase real-time listener.
        // No additional action needed.
    }

    override suspend fun loadMoreFeed(uid: String, count: Int) {
        // Feed is automatically updated via Firebase real-time listener.
        // No additional action needed.
    }

    private fun DataSnapshot.toDomainPost(): Post {
        return Post(
            postId = child("postId").getValue(String::class.java) ?: "",
            authorId = child("authorId").getValue(String::class.java) ?: "",
            content = child("content").getValue(String::class.java) ?: "",
            createdAt = child("createdAt").getValue(Long::class.java) ?: 0L,
            likesCount = child("likesCount").getValue(Int::class.java) ?: 0,
            retweetsCount = child("retweetsCount").getValue(Int::class.java) ?: 0,
            commentsCount = child("commentsCount").getValue(Int::class.java) ?: 0,
            quotesCount = child("quotesCount").getValue(Int::class.java) ?: 0,
            viewsCount = child("viewsCount").getValue(Int::class.java) ?: 0,
            isRetweet = child("isRetweet").getValue(Boolean::class.java) ?: false,
            originalPostId = child("originalPostId").getValue(String::class.java),
            isQuote = child("isQuote").getValue(Boolean::class.java) ?: false,
            quotedPostId = child("quotedPostId").getValue(String::class.java),
            replyToPostId = child("replyToPostId").getValue(String::class.java),
            isPinned = child("isPinned").getValue(Boolean::class.java) ?: false,
            isSensitive = child("isSensitive").getValue(Boolean::class.java) ?: false,
            isDeleted = child("isDeleted").getValue(Boolean::class.java) ?: false
        )
    }
}