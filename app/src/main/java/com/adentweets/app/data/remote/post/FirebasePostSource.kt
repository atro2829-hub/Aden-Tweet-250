package com.adentweets.app.data.remote.post

import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebasePostSource @Inject constructor(
    private val database: FirebaseDatabase
) {
    private fun postsRef() = database.getReference("posts")
    private fun likesRef() = database.getReference("likes")
    private fun retweetsRef() = database.getReference("retweets")
    private fun bookmarksRef() = database.getReference("bookmarks")

    suspend fun createPost(postData: Map<String, Any?>): String {
        val ref = postsRef().push()
        val data = postData.toMutableMap().apply { put("postId", ref.key!!) }
        ref.setValue(data).await()
        return ref.key!!
    }

    suspend fun getPostById(postId: String): DataSnapshot? {
        return try { postsRef().child(postId).get().await() } catch (e: Exception) { null }
    }

    fun observePost(postId: String): Flow<DataSnapshot?> = callbackFlow {
        val ref = postsRef().child(postId)
        val listener = ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) { trySend(snapshot) }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        })
        awaitClose { ref.removeEventListener(listener) }
    }

    suspend fun getPostsByAuthor(authorId: String, limit: Int = 20): List<DataSnapshot> {
        return try {
            postsRef().orderByChild("authorId").equalTo(authorId).limitToLast(limit).get().await()
                .children.reversed().toList()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun getRecentPosts(limit: Int = 20): List<DataSnapshot> {
        return try {
            postsRef().orderByChild("createdAt").limitToLast(limit).get().await()
                .children.reversed().toList()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun deletePost(postId: String): Boolean {
        return try { postsRef().child(postId).removeValue().await(); true } catch (e: Exception) { false }
    }

    suspend fun updatePost(postId: String, data: Map<String, Any?>): Boolean {
        return try { postsRef().child(postId).updateChildren(data).await(); true } catch (e: Exception) { false }
    }

    suspend fun likePost(postId: String, uid: String): Boolean {
        return try {
            val data = mapOf("uid" to uid, "postId" to postId, "likedAt" to ServerValue.TIMESTAMP)
            likesRef().child(postId).child(uid).setValue(data).await()
            postsRef().child(postId).child("likesCount").runTransaction(incrementOperation).await()
            true
        } catch (e: Exception) { false }
    }

    suspend fun unlikePost(postId: String, uid: String): Boolean {
        return try {
            likesRef().child(postId).child(uid).removeValue().await()
            postsRef().child(postId).child("likesCount").runTransaction(decrementOperation).await()
            true
        } catch (e: Exception) { false }
    }

    suspend fun retweetPost(postId: String, uid: String): Boolean {
        return try {
            val ref = postsRef().push()
            val data = mapOf(
                "postId" to ref.key, "authorId" to uid, "isRetweet" to true,
                "originalPostId" to postId, "createdAt" to ServerValue.TIMESTAMP, "content" to ""
            )
            ref.setValue(data).await()
            retweetsRef().child(postId).child(uid).setValue(mapOf(
                "uid" to uid, "postId" to postId, "retweetedAt" to ServerValue.TIMESTAMP, "retweetPostId" to ref.key
            )).await()
            true
        } catch (e: Exception) { false }
    }

    suspend fun undoRetweet(postId: String, uid: String, retweetPostId: String?): Boolean {
        return try {
            retweetsRef().child(postId).child(uid).removeValue().await()
            if (retweetPostId != null) postsRef().child(retweetPostId).removeValue().await()
            true
        } catch (e: Exception) { false }
    }

    suspend fun bookmarkPost(postId: String, uid: String): Boolean {
        return try {
            bookmarksRef().child(uid).child(postId).setValue(mapOf("postId" to postId, "bookmarkedAt" to ServerValue.TIMESTAMP)).await()
            true
        } catch (e: Exception) { false }
    }

    suspend fun removeBookmark(postId: String, uid: String): Boolean {
        return try {
            bookmarksRef().child(uid).child(postId).removeValue().await()
            true
        } catch (e: Exception) { false }
    }

    suspend fun getBookmarks(uid: String): List<DataSnapshot> {
        return try {
            bookmarksRef().child(uid).orderByChild("bookmarkedAt").limitToLast(50).get().await()
                .children.reversed().toList()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun getReplies(postId: String, limit: Int = 20): List<DataSnapshot> {
        return try {
            database.getReference("comments").child(postId).orderByChild("createdAt").limitToLast(limit).get().await()
                .children.reversed().toList()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun createReply(postId: String, replyData: Map<String, Any?>): String {
        val ref = database.getReference("comments").child(postId).push()
        val data = replyData.toMutableMap().apply { put("commentId", ref.key!!); put("postId", postId) }
        ref.setValue(data).await()
        postsRef().child(postId).child("commentsCount").runTransaction(incrementOperation).await()
        return ref.key!!
    }

    suspend fun pinPost(uid: String, postId: String): Boolean {
        return try { database.getReference("pinnedPosts").child(uid).setValue(postId).await(); true } catch (e: Exception) { false }
    }

    suspend fun unpinPost(uid: String): Boolean {
        return try { database.getReference("pinnedPosts").child(uid).removeValue().await(); true } catch (e: Exception) { false }
    }

    private val incrementOperation = object : Transaction.Handler {
        override fun doTransaction(currentData: MutableData): Transaction.Result {
            val val_ = (currentData.getValue(Long::class.java) ?: 0L) + 1
            currentData.value = val_
            return Transaction.success(currentData)
        }
        override fun onComplete(error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?) {}
    }

    private val decrementOperation = object : Transaction.Handler {
        override fun doTransaction(currentData: MutableData): Transaction.Result {
            val val_ = ((currentData.getValue(Long::class.java) ?: 0L) - 1).coerceAtLeast(0)
            currentData.value = val_
            return Transaction.success(currentData)
        }
        override fun onComplete(error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?) {}
    }
}