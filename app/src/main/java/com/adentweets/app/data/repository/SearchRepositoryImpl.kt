package com.adentweets.app.data.repository

import com.adentweets.app.core.util.Resource
import com.adentweets.app.data.remote.search.FirebaseSearchSource
import com.adentweets.app.domain.model.Post
import com.adentweets.app.domain.model.TrendingTopic
import com.adentweets.app.domain.model.User
import com.adentweets.app.domain.repository.SearchRepository
import com.google.firebase.database.DataSnapshot
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepositoryImpl @Inject constructor(
    private val searchSource: FirebaseSearchSource
) : SearchRepository {

    override suspend fun searchUsers(query: String): Resource<List<User>> {
        return try {
            val snapshots = searchSource.searchUsers(query)
            // searchUsers queries users/profile/username, so snapshots are at user level
            val users = snapshots.map { it.toDomainUser() }
            Resource.Success(users)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to search users")
        }
    }

    override suspend fun searchPosts(query: String): Resource<List<Post>> {
        return try {
            val snapshots = searchSource.searchPosts(query)
            Resource.Success(snapshots.map { it.toDomainPost() })
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to search posts")
        }
    }

    override suspend fun getTrendingTopics(): Resource<List<TrendingTopic>> {
        return try {
            val snapshots = searchSource.getTrendingHashtags()
            val topics = snapshots.mapIndexed { index, snap ->
                TrendingTopic(
                    trendId = snap.key ?: "",
                    hashtag = snap.child("hashtag").getValue(String::class.java) ?: "",
                    category = snap.child("category").getValue(String::class.java) ?: "",
                    postsCount = (snap.child("postsCount").getValue(Long::class.java) ?: 0L).toInt(),
                    rank = index + 1,
                    updatedAt = snap.child("updatedAt").getValue(Long::class.java) ?: System.currentTimeMillis()
                )
            }
            Resource.Success(topics)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get trending topics")
        }
    }

    override suspend fun getPostsByHashtag(hashtag: String): Resource<List<Post>> {
        return try {
            val snapshots = searchSource.getPostsByHashtag(hashtag)
            Resource.Success(snapshots.map { it.toDomainPost() })
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get posts by hashtag")
        }
    }

    private fun DataSnapshot.toDomainUser(): User {
        // searchUsers queries at the user level, profile data is nested
        val profileSnap = child("profile")
        return User(
            uid = key ?: "",
            username = profileSnap.child("username").getValue(String::class.java) ?: "",
            displayName = profileSnap.child("displayName").getValue(String::class.java) ?: "",
            bio = profileSnap.child("bio").getValue(String::class.java) ?: "",
            location = profileSnap.child("location").getValue(String::class.java) ?: "",
            website = profileSnap.child("website").getValue(String::class.java) ?: "",
            avatarBase64 = profileSnap.child("avatarBase64").getValue(String::class.java) ?: "",
            bannerBase64 = profileSnap.child("bannerBase64").getValue(String::class.java) ?: "",
            isVerified = profileSnap.child("isVerified").getValue(Boolean::class.java) ?: false,
            isPremium = profileSnap.child("isPremium").getValue(Boolean::class.java) ?: false,
            isPrivate = profileSnap.child("isPrivate").getValue(Boolean::class.java) ?: false,
            joinedAt = profileSnap.child("joinedAt").getValue(Long::class.java) ?: 0L,
            followersCount = profileSnap.child("followersCount").getValue(Int::class.java) ?: 0,
            followingCount = profileSnap.child("followingCount").getValue(Int::class.java) ?: 0,
            postsCount = profileSnap.child("postsCount").getValue(Int::class.java) ?: 0
        )
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