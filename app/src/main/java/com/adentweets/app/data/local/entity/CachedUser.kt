package com.adentweets.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_users")
data class CachedUser(
    @PrimaryKey val uid: String,
    val username: String = "",
    val displayName: String = "",
    val bio: String = "",
    val avatarBase64: String = "",
    val bannerBase64: String = "",
    val isVerified: Boolean = false,
    val isPremium: Boolean = false,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val postsCount: Int = 0,
    val cachedAt: Long = System.currentTimeMillis()
)