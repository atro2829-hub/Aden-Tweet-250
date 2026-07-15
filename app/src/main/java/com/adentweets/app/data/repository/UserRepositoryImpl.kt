package com.adentweets.app.data.repository

import com.adentweets.app.core.util.Resource
import com.adentweets.app.data.remote.auth.FirebaseAuthSource
import com.adentweets.app.data.remote.user.FirebaseUserSource
import com.adentweets.app.domain.model.User
import com.adentweets.app.domain.repository.UserRepository
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userSource: FirebaseUserSource,
    private val authSource: FirebaseAuthSource,
    private val database: FirebaseDatabase
) : UserRepository {

    override suspend fun getUserProfile(uid: String): Resource<User> {
        return try {
            val snapshot = userSource.getUserById(uid)
                ?: return Resource.Error("User not found")
            Resource.Success(snapshot.toDomainUser())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get user profile")
        }
    }

    override suspend fun updateUserProfile(user: User): Resource<Unit> {
        return try {
            val uid = authSource.currentUserId() ?: return Resource.Error("Not authenticated")
            val data = mapOf(
                "username" to user.username,
                "displayName" to user.displayName,
                "bio" to user.bio,
                "location" to user.location,
                "website" to user.website
            )
            val success = userSource.updateProfile(uid, data)
            if (success) Resource.Success(Unit) else Resource.Error("Failed to update profile")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update profile")
        }
    }

    override suspend fun updateAvatar(uid: String, avatarBase64: String): Resource<Unit> {
        return try {
            val success = userSource.updateProfile(uid, mapOf("avatarBase64" to avatarBase64))
            if (success) Resource.Success(Unit) else Resource.Error("Failed to update avatar")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update avatar")
        }
    }

    override suspend fun updateBanner(uid: String, bannerBase64: String): Resource<Unit> {
        return try {
            val success = userSource.updateProfile(uid, mapOf("bannerBase64" to bannerBase64))
            if (success) Resource.Success(Unit) else Resource.Error("Failed to update banner")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update banner")
        }
    }

    override suspend fun followUser(targetUid: String): Resource<Unit> {
        return try {
            val uid = authSource.currentUserId() ?: return Resource.Error("Not authenticated")
            val success = userSource.followUser(uid, targetUid)
            if (success) Resource.Success(Unit) else Resource.Error("Failed to follow user")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to follow user")
        }
    }

    override suspend fun unfollowUser(targetUid: String): Resource<Unit> {
        return try {
            val uid = authSource.currentUserId() ?: return Resource.Error("Not authenticated")
            val success = userSource.unfollowUser(uid, targetUid)
            if (success) Resource.Success(Unit) else Resource.Error("Failed to unfollow user")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to unfollow user")
        }
    }

    override suspend fun getFollowers(uid: String, limit: Int): Resource<List<User>> {
        return try {
            val snapshot = userSource.getFollowers(uid)
            val users = snapshot.children.take(limit).mapNotNull { child ->
                val followerUid = child.child("uid").getValue(String::class.java) ?: child.key ?: return@mapNotNull null
                try {
                    userSource.getUserById(followerUid)?.toDomainUser()
                } catch (e: Exception) { null }
            }
            Resource.Success(users)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get followers")
        }
    }

    override suspend fun getFollowing(uid: String, limit: Int): Resource<List<User>> {
        return try {
            val snapshot = userSource.getFollowing(uid)
            val users = snapshot.children.take(limit).mapNotNull { child ->
                val followingUid = child.child("uid").getValue(String::class.java) ?: child.key ?: return@mapNotNull null
                try {
                    userSource.getUserById(followingUid)?.toDomainUser()
                } catch (e: Exception) { null }
            }
            Resource.Success(users)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get following")
        }
    }

    override suspend fun blockUser(targetUid: String): Resource<Unit> {
        return try {
            val uid = authSource.currentUserId() ?: return Resource.Error("Not authenticated")
            val success = userSource.blockUser(uid, targetUid)
            if (success) Resource.Success(Unit) else Resource.Error("Failed to block user")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to block user")
        }
    }

    override suspend fun unblockUser(targetUid: String): Resource<Unit> {
        return try {
            val uid = authSource.currentUserId() ?: return Resource.Error("Not authenticated")
            val success = userSource.unblockUser(uid, targetUid)
            if (success) Resource.Success(Unit) else Resource.Error("Failed to unblock user")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to unblock user")
        }
    }

    override suspend fun muteUser(targetUid: String): Resource<Unit> {
        return try {
            val uid = authSource.currentUserId() ?: return Resource.Error("Not authenticated")
            val success = userSource.muteUser(uid, targetUid)
            if (success) Resource.Success(Unit) else Resource.Error("Failed to mute user")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to mute user")
        }
    }

    override suspend fun unmuteUser(targetUid: String): Resource<Unit> {
        return try {
            val uid = authSource.currentUserId() ?: return Resource.Error("Not authenticated")
            val success = userSource.unmuteUser(uid, targetUid)
            if (success) Resource.Success(Unit) else Resource.Error("Failed to unmute user")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to unmute user")
        }
    }

    override fun observeUserProfile(uid: String): Flow<User?> {
        return userSource.observeUser(uid).map { snapshot ->
            snapshot?.toDomainUser()
        }
    }

    override fun observeOnlineStatus(uid: String): Flow<Boolean> = callbackFlow {
        val ref = database.getReference("users/$uid/online")
        val listener = ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.getValue(Boolean::class.java) ?: false)
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        })
        awaitClose { ref.removeEventListener(listener) }
    }

    private fun DataSnapshot.toDomainUser(): User {
        return User(
            uid = key ?: "",
            username = child("username").getValue(String::class.java) ?: "",
            displayName = child("displayName").getValue(String::class.java) ?: "",
            bio = child("bio").getValue(String::class.java) ?: "",
            location = child("location").getValue(String::class.java) ?: "",
            website = child("website").getValue(String::class.java) ?: "",
            avatarBase64 = child("avatarBase64").getValue(String::class.java) ?: "",
            bannerBase64 = child("bannerBase64").getValue(String::class.java) ?: "",
            isVerified = child("isVerified").getValue(Boolean::class.java) ?: false,
            isPremium = child("isPremium").getValue(Boolean::class.java) ?: false,
            isPrivate = child("isPrivate").getValue(Boolean::class.java) ?: false,
            joinedAt = child("joinedAt").getValue(Long::class.java) ?: 0L,
            followersCount = child("followersCount").getValue(Int::class.java) ?: 0,
            followingCount = child("followingCount").getValue(Int::class.java) ?: 0,
            postsCount = child("postsCount").getValue(Int::class.java) ?: 0
        )
    }
}