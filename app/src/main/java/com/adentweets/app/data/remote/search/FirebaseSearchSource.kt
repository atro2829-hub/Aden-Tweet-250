package com.adentweets.app.data.remote.search

import com.google.firebase.database.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseSearchSource @Inject constructor(
    private val database: FirebaseDatabase
) {
    suspend fun searchUsers(query: String): List<DataSnapshot> {
        return try {
            database.getReference("users").orderByChild("profile/username")
                .startAt(query.lowercase()).endAt(query.lowercase() + "\uf8ff")
                .limitToFirst(20).get().await().children.toList()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun searchPosts(query: String): List<DataSnapshot> {
        return try {
            database.getReference("posts").orderByChild("content")
                .startAt(query).endAt(query + "\uf8ff")
                .limitToFirst(30).get().await().children.reversed().toList()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun getTrendingHashtags(): List<DataSnapshot> {
        return try {
            database.getReference("trends").orderByChild("postsCount").limitToLast(10).get().await()
                .children.sortedByDescending { it.child("postsCount").getValue(Long::class.java) ?: 0 }
        } catch (e: Exception) { emptyList() }
    }

    suspend fun getPostsByHashtag(hashtag: String): List<DataSnapshot> {
        return try {
            database.getReference("posts").orderByChild("createdAt").limitToLast(30).get().await()
                .children.filter { post ->
                    val content = post.child("content").getValue(String::class.java) ?: ""
                    content.contains("#$hashtag", ignoreCase = true)
                }.reversed().toList()
        } catch (e: Exception) { emptyList() }
    }
}