package com.adentweets.app.domain.model

data class User(
    val uid: String = "",
    val username: String = "",
    val displayName: String = "",
    val bio: String = "",
    val location: String = "",
    val website: String = "",
    val avatarBase64: String = "",
    val bannerBase64: String = "",
    val isVerified: Boolean = false,
    val isPremium: Boolean = false,
    val isPrivate: Boolean = false,
    val joinedAt: Long = System.currentTimeMillis(),
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val postsCount: Int = 0,
    val email: String = "",
    val phone: String = ""
)