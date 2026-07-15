package com.adentweets.app.core.util

object Constants {
    const val MAX_POST_LENGTH = 280
    const val MAX_IMAGES_PER_POST = 4
    const val MAX_VIDEO_DURATION_SECONDS = 60
    const val MAX_IMAGE_SIZE_KB = 800
    const val MAX_AVATAR_SIZE_KB = 200
    const val MAX_BANNER_SIZE_KB = 500
    const val MAX_VIDEO_SIZE_KB = 8000
    const val FEED_PAGE_SIZE = 20
    const val SEARCH_PAGE_SIZE = 20
    const val NOTIFICATIONS_PAGE_SIZE = 30
    const val MESSAGES_PAGE_SIZE = 30
    const val PROFILE_IMAGE_MAX_WIDTH = 400
    const val POST_IMAGE_MAX_WIDTH = 1080
    const val BANNER_IMAGE_MAX_WIDTH = 1500
    const val JPEG_QUALITY_HIGH = 85
    const val JPEG_QUALITY_MEDIUM = 70
    const val JPEG_QUALITY_LOW = 50

    object DatabasePaths {
        const val USERS = "users"
        const val POSTS = "posts"
        const val LIKES = "likes"
        const val RETWEETS = "retweets"
        const val BOOKMARKS = "bookmarks"
        const val COMMENTS = "comments"
        const val FOLLOWS = "follows"
        const val CONVERSATIONS = "conversations"
        const val MESSAGES = "messages"
        const val NOTIFICATIONS = "notifications"
        const val HASHTAGS = "hashtags"
        const val TRENDS = "trends"
        const val FEEDS = "feeds"
        const val BLOCKS = "blocks"
        const val MUTES = "mutes"
        const val REPORTS = "reports"
        const val PINNED_POSTS = "pinnedPosts"
        const val USER_ACTIVITY = "userActivity"
    }
}