package com.adentweets.app.domain.repository

import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    suspend fun createPost(post: Post): Resource<Post>
    suspend fun deletePost(postId: String): Resource<Unit>
    suspend fun getPostById(postId: String): Resource<Post>
    suspend fun getPostsByAuthor(authorId: String, limit: Int): Resource<List<Post>>
    suspend fun likePost(postId: String): Resource<Unit>
    suspend fun unlikePost(postId: String): Resource<Unit>
    suspend fun retweetPost(postId: String): Resource<Unit>
    suspend fun undoRetweet(postId: String): Resource<Unit>
    suspend fun quotePost(postId: String, quoteContent: String): Resource<Post>
    suspend fun bookmarkPost(postId: String): Resource<Unit>
    suspend fun removeBookmark(postId: String): Resource<Unit>
    suspend fun getBookmarks(): Resource<List<Post>>
    suspend fun getReplies(postId: String, limit: Int): Resource<List<Post>>
    suspend fun pinPost(postId: String): Resource<Unit>
    suspend fun unpinPost(postId: String): Resource<Unit>
    fun observePost(postId: String): Flow<Post?>
}