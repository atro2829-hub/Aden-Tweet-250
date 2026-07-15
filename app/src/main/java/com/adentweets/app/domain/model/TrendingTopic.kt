package com.adentweets.app.domain.model

data class TrendingTopic(
    val trendId: String = "",
    val hashtag: String = "",
    val category: String = "",
    val postsCount: Int = 0,
    val rank: Int = 0,
    val updatedAt: Long = System.currentTimeMillis()
)