package com.adentweets.app.data.repository

import com.adentweets.app.core.util.Resource
import com.adentweets.app.data.remote.auth.FirebaseAuthSource
import com.adentweets.app.data.remote.post.FirebasePostSource
import com.adentweets.app.domain.model.Post
import com.adentweets.app.domain.repository.PostRepository
import com.google.firebase.database.DataSnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepositoryImpl @Inject constructor(
    private val postSource: FirebasePostSource,
    private val authSource: FirebaseAuthSource
) : PostRepository {

    override suspend fun createPost(post: Post): Resource<Post> {
        return try {
            val uid = authSource.currentUserId() ?: return Resource.Error("Not authenticated")
            val postData = mapOf(
                "authorId" to uid,
                "content" to post.content,
                "createdAt" to System.currentTimeMillis(),
                "likesCount" to 0,
                "retweetsCount" to 0,
                "commentsCount" to 0,
                "quotesCount" to 0,
                "viewsCount" to 0,
                "isRetweet" to false,
                "isQuote" to post.isQuote,
                "quotedPostId" to post.quotedPostId,
                "replyToPostId" to post.replyToPostId,
                "isPinned" to false,
                "isSensitive" to post.isSensitive,
                "isDeleted" to false
            )
            val postId = postSource.createPost(postData)
            Resource.Success(post.copy(postId = postId, authorId = uid))
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to create post")
        }
    }

    override suspend fun deletePost(postId: String): Resource<Unit> {
        return try {
            val success = postSource.deletePost(postId)
            if (success) Resource.Success(Unit) else Resource.Error("Failed to delete post")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete post")
        }
    }

    override suspend fun getPostById(postId: String): Resource<Post> {
        return try {
            val snapshot = postSource.getPostById(postId)
                ?: return Resource.Error("Post not found")
            Resource.Success(snapshot.toDomainPost())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get post")
        }
    }

    override suspend fun getPostsByAuthor(authorId: String, limit: Int): Resource<List<Post>> {
        return try {
            val snapshots = postSource.getPostsByAuthor(authorId, limit)
            Resource.Success(snapshots.map { it.toDomainPost() })
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get posts")
        }
    }

    override suspend fun likePost(postId: String): Resource<Unit> {
        return try {
            val uid = authSource.currentUserId() ?: return Resource.Error("Not authenticated")
            val success = postSource.likePost(postId, uid)
            if (success) Resource.Success(Unit) else Resource.Error("Failed to like post")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to like post")
        }
    }

    override suspend fun unlikePost(postId: String): Resource<Unit> {
        return try {
            val uid = authSource.currentUserId() ?: return Resource.Error("Not authenticated")
            val success = postSource.unlikePost(postId, uid)
            if (success) Resource.Success(Unit) else Resource.Error("Failed to unlike post")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to unlike post")
        }
    }

    override suspend fun retweetPost(postId: String): Resource<Unit> {
        return try {
            val uid = authSource.currentUserId() ?: return Resource.Error("Not authenticated")
            val success = postSource.retweetPost(postId, uid)
            if (success) Resource.Success(Unit) else Resource.Error("Failed to retweet")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to retweet")
        }
    }

    override suspend fun undoRetweet(postId: String): Resource<Unit> {
        return try {
            val uid = authSource.currentUserId() ?: return Resource.Error("Not authenticated")
            val success = postSource.undoRetweet(postId, uid, null)
            if (success) Resource.Success(Unit) else Resource.Error("Failed to undo retweet")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to undo retweet")
        }
    }

    override suspend fun quotePost(postId: String, quoteContent: String): Resource<Post> {
        return try {
            val uid = authSource.currentUserId() ?: return Resource.Error("Not authenticated")
            val postData = mapOf(
                "authorId" to uid,
                "content" to quoteContent,
                "createdAt" to System.currentTimeMillis(),
                "likesCount" to 0,
                "retweetsCount" to 0,
                "commentsCount" to 0,
                "quotesCount" to 0,
                "viewsCount" to 0,
                "isRetweet" to false,
                "isQuote" to true,
                "quotedPostId" to postId,
                "replyToPostId" to null,
                "isPinned" to false,
                "isSensitive" to false,
                "isDeleted" to false
            )
            val newPostId = postSource.createPost(postData)
            Resource.Success(Post(postId = newPostId, authorId = uid, content = quoteContent, isQuote = true, quotedPostId = postId))
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to quote post")
        }
    }

    override suspend fun bookmarkPost(postId: String): Resource<Unit> {
        return try {
            val uid = authSource.currentUserId() ?: return Resource.Error("Not authenticated")
            val success = postSource.bookmarkPost(postId, uid)
            if (success) Resource.Success(Unit) else Resource.Error("Failed to bookmark")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to bookmark")
        }
    }

    override suspend fun removeBookmark(postId: String): Resource<Unit> {
        return try {
            val uid = authSource.currentUserId() ?: return Resource.Error("Not authenticated")
            val success = postSource.removeBookmark(postId, uid)
            if (success) Resource.Success(Unit) else Resource.Error("Failed to remove bookmark")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to remove bookmark")
        }
    }

    override suspend fun getBookmarks(): Resource<List<Post>> {
        return try {
            val uid = authSource.currentUserId() ?: return Resource.Error("Not authenticated")
            val snapshots = postSource.getBookmarks(uid)
            Resource.Success(snapshots.map { it.toDomainPost() })
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get bookmarks")
        }
    }

    override suspend fun getReplies(postId: String, limit: Int): Resource<List<Post>> {
        return try {
            val snapshots = postSource.getReplies(postId, limit)
            Resource.Success(snapshots.map { it.toDomainPost() })
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get replies")
        }
    }

    override suspend fun pinPost(postId: String): Resource<Unit> {
        return try {
            val uid = authSource.currentUserId() ?: return Resource.Error("Not authenticated")
            val success = postSource.pinPost(uid, postId)
            if (success) Resource.Success(Unit) else Resource.Error("Failed to pin post")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to pin post")
        }
    }

    override fun unpinPost(postId: String): Resource<Unit> {
        return try {
            val uid = authSource.currentUserId() ?: return Resource.Error("Not authenticated")
            val success = postSource.unpinPost(uid)
            if (success) Resource.Success(Unit) else Resource.Error("Failed to unpin post")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to unpin post")
        }
    }

    override fun observePost(postId: String): Flow<Post?> {
        return postSource.observePost(postId).map { snapshot ->
            snapshot?.toDomainPost()
        }
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