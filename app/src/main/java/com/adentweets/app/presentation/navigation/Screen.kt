package com.adentweets.app.presentation.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Welcome : Screen("welcome")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object ForgotPassword : Screen("forgot_password")
    data object PhoneVerify : Screen("phone_verify")
    data object Home : Screen("home")
    data object CreatePost : Screen("post/create") {
        fun createRoute(replyToId: String? = null, quoteId: String? = null) = if (replyToId != null) "post/create?replyTo=$replyToId" else if (quoteId != null) "post/create?quoteId=$quoteId" else "post/create"
    }
    data object PostDetail : Screen("post/{postId}") {
        fun createRoute(postId: String) = "post/$postId"
    }
    data object ThreadView : Screen("post/{postId}/thread") {
        fun createRoute(postId: String) = "post/$postId/thread"
    }
    data object PostMetrics : Screen("post/{postId}/metrics") {
        fun createRoute(postId: String) = "post/$postId/metrics"
    }
    data object Profile : Screen("profile/{uid}") {
        fun createRoute(uid: String) = "profile/$uid"
    }
    data object EditProfile : Screen("profile/edit")
    data object Followers : Screen("profile/{uid}/followers") {
        fun createRoute(uid: String) = "profile/$uid/followers"
    }
    data object Following : Screen("profile/{uid}/following") {
        fun createRoute(uid: String) = "profile/$uid/following"
    }
    data object Explore : Screen("explore")
    data object SearchResults : Screen("search?q={query}") {
        fun createRoute(query: String) = "search?q=$query"
    }
    data object TrendingTopic : Screen("trend/{hashtag}") {
        fun createRoute(hashtag: String) = "trend/$hashtag"
    }
    data object Notifications : Screen("notifications")
    data object Inbox : Screen("messages")
    data object Conversation : Screen("messages/{conversationId}") {
        fun createRoute(conversationId: String) = "messages/$conversationId"
    }
    data object NewMessage : Screen("messages/new")
    data object Bookmarks : Screen("bookmarks")
    data object Settings : Screen("settings")
    data object AccountSettings : Screen("settings/account")
    data object PrivacySettings : Screen("settings/privacy")
    data object NotificationSettings : Screen("settings/notifications")
    data object AppearanceSettings : Screen("settings/appearance")
    data object SecuritySettings : Screen("settings/security")
    data object ImageViewer : Screen("media/image")
    data object VideoPlayer : Screen("media/video")
    data object Report : Screen("report")
    data object BlockedAccounts : Screen("settings/blocked")
    data object MutedAccounts : Screen("settings/muted")
}