package com.adentweets.app.data.remote.user

import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseUserSource @Inject constructor(
    private val database: FirebaseDatabase
) {
    private fun usersRef() = database.getReference("users")

    suspend fun getUserById(uid: String): DataSnapshot? {
        return try {
            usersRef().child(uid).child("profile").get().await()
        } catch (e: Exception) { null }
    }

    fun observeUser(uid: String): Flow<DataSnapshot?> = callbackFlow {
        val ref = usersRef().child(uid).child("profile")
        val listener = ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) { trySend(snapshot) }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        })
        awaitClose { ref.removeEventListener(listener) }
    }

    suspend fun createUser(uid: String, profileData: Map<String, Any?>): Boolean {
        return try {
            usersRef().child(uid).child("profile").setValue(profileData).await()
            usersRef().child(uid).child("settings").setValue(mapOf(
                "isPrivate" to false,
                "language" to "ar",
                "theme" to "system"
            )).await()
            true
        } catch (e: Exception) { false }
    }

    suspend fun updateProfile(uid: String, data: Map<String, Any?>): Boolean {
        return try {
            usersRef().child(uid).child("profile").updateChildren(data).await()
            true
        } catch (e: Exception) { false }
    }

    suspend fun getFollowers(uid: String): DataSnapshot {
        return usersRef().child(uid).child("follows").child("followers").get().await()
    }

    suspend fun getFollowing(uid: String): DataSnapshot {
        return usersRef().child(uid).child("follows").child("following").get().await()
    }

    suspend fun followUser(uid: String, targetUid: String): Boolean {
        return try {
            val updates = mapOf(
                "follows/$uid/following/$targetUid" to mapOf("uid" to targetUid, "followedAt" to ServerValue.TIMESTAMP),
                "follows/$targetUid/followers/$uid" to mapOf("uid" to uid, "followedAt" to ServerValue.TIMESTAMP)
            )
            database.reference.updateChildren(updates).await()
            true
        } catch (e: Exception) { false }
    }

    suspend fun unfollowUser(uid: String, targetUid: String): Boolean {
        return try {
            val updates = mapOf(
                "follows/$uid/following/$targetUid" to null,
                "follows/$targetUid/followers/$uid" to null
            )
            database.reference.updateChildren(updates).await()
            true
        } catch (e: Exception) { false }
    }

    suspend fun blockUser(uid: String, targetUid: String): Boolean {
        return try {
            database.reference.child("blocks").child(uid).child(targetUid).setValue(mapOf("blockedAt" to ServerValue.TIMESTAMP)).await()
            true
        } catch (e: Exception) { false }
    }

    suspend fun unblockUser(uid: String, targetUid: String): Boolean {
        return try {
            database.reference.child("blocks").child(uid).child(targetUid).removeValue().await()
            true
        } catch (e: Exception) { false }
    }

    suspend fun muteUser(uid: String, targetUid: String): Boolean {
        return try {
            database.reference.child("mutes").child(uid).child(targetUid).setValue(mapOf("mutedAt" to ServerValue.TIMESTAMP)).await()
            true
        } catch (e: Exception) { false }
    }

    suspend fun unmuteUser(uid: String, targetUid: String): Boolean {
        return try {
            database.reference.child("mutes").child(uid).child(targetUid).removeValue().await()
            true
        } catch (e: Exception) { false }
    }
}