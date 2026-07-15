package com.adentweets.app.data.local.dao

import androidx.room.*
import com.adentweets.app.data.local.entity.CachedPost
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Query("SELECT * FROM cached_posts ORDER BY createdAt DESC")
    fun getAllPosts(): Flow<List<CachedPost>>

    @Query("SELECT * FROM cached_posts WHERE postId = :postId")
    suspend fun getPostById(postId: String): CachedPost?

    @Query("SELECT * FROM cached_posts WHERE authorId = :authorId ORDER BY createdAt DESC LIMIT :limit")
    suspend fun getPostsByAuthor(authorId: String, limit: Int = 20): List<CachedPost>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<CachedPost>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: CachedPost)

    @Delete
    suspend fun deletePost(post: CachedPost)

    @Query("DELETE FROM cached_posts")
    suspend fun clearAll()
}