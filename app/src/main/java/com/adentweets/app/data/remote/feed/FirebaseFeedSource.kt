package com.adentweets.app.data.remote.feed

import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseFeedSource @Inject constructor(
    private val database: FirebaseDatabase
) {
    private fun feedsRef() = database.getReference("feeds")

    suspend fun fanOutPost(postId: String, authorId: String) {
        val followersSnapshot = database.getReference("follows/$authorId/followers").get().await()
        val updates = mutableMapOf<String, Any>()
        updates["feeds/$authorId/$postId"] = mapOf("postId" to postId, "authorId" to authorId, "score" to ServerValue.TIMESTAMP, "addedAt" to ServerValue.TIMESTAMP)
        for (child in followersSnapshot.children) {
            val followerId = child.key ?: continue
            updates["feeds/$followerId/$postId"] = mapOf("postId" to postId, "authorId" to authorId, "score" to ServerValue.TIMESTAMP, "addedAt" to ServerValue.TIMESTAMP)
        }
        if (updates.isNotEmpty()) database.reference.updateChildren(updates).await()
    }

    fun observeFeed(uid: String): Flow<List<String>> = callbackFlow {
        val ref = feedsRef().child(uid).orderByChild("addedAt").limitToLast(30)
        val listener = ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val postIds = snapshot.children.mapNotNull { it.child("postId").getValue(String::class.java) }.reversed()
                trySend(postIds)
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        })
        awaitClose { ref.removeEventListener(listener) }
    }
}