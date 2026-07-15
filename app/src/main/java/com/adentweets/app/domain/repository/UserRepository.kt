package com.adentweets.app.domain.repository

import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUserProfile(uid: String): Resource<User>
    suspend fun updateUserProfile(user: User): Resource<Unit>
    suspend fun updateAvatar(uid: String, avatarBase64: String): Resource<Unit>
    suspend fun updateBanner(uid: String, bannerBase64: String): Resource<Unit>
    suspend fun followUser(targetUid: String): Resource<Unit>
    suspend fun unfollowUser(targetUid: String): Resource<Unit>
    suspend fun getFollowers(uid: String, limit: Int): Resource<List<User>>
    suspend fun getFollowing(uid: String, limit: Int): Resource<List<User>>
    suspend fun blockUser(targetUid: String): Resource<Unit>
    suspend fun unblockUser(targetUid: String): Resource<Unit>
    suspend fun muteUser(targetUid: String): Resource<Unit>
    suspend fun unmuteUser(targetUid: String): Resource<Unit>
    fun observeUserProfile(uid: String): Flow<User?>
    fun observeOnlineStatus(uid: String): Flow<Boolean>
}