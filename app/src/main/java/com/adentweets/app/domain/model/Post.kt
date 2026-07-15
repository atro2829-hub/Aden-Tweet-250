package com.adentweets.app.domain.model

data class Post(
    val postId: String = "",
    val authorId: String = "",
    val author: User? = null,
    val content: String = "",
    val mediaItems: List<MediaItem> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isRetweet: Boolean = false,
    val originalPostId: String? = null,
    val originalPost: Post? = null,
    val isQuote: Boolean = false,
    val quotedPostId: String? = null,
    val quotedPost: Post? = null,
    val replyToPostId: String? = null,
    val mentionedUserIds: List<String> = emptyList(),
    val hashtags: List<String> = emptyList(),
    val likesCount: Int = 0,
    val retweetsCount: Int = 0,
    val commentsCount: Int = 0,
    val quotesCount: Int = 0,
    val viewsCount: Int = 0,
    val bookmarksCount: Int = 0,
    val isLikedByCurrentUser: Boolean = false,
    val isRetweetedByCurrentUser: Boolean = false,
    val isBookmarkedByCurrentUser: Boolean = false,
    val isPinned: Boolean = false,
    val isSensitive: Boolean = false,
    val visibility: PostVisibility = PostVisibility.PUBLIC,
    val isDeleted: Boolean = false
)

enum class PostVisibility { PUBLIC, FOLLOWERS, PRIVATE }