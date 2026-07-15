package com.adentweets.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.adentweets.app.presentation.screens.auth.*
import com.adentweets.app.presentation.screens.bookmarks.BookmarksScreen
import com.adentweets.app.presentation.screens.explore.*
import com.adentweets.app.presentation.screens.home.*
import com.adentweets.app.presentation.screens.media.*
import com.adentweets.app.presentation.screens.messages.*
import com.adentweets.app.presentation.screens.notifications.NotificationsScreen
import com.adentweets.app.presentation.screens.profile.*
import com.adentweets.app.presentation.screens.settings.*
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AdenTweetNavGraph(navController: NavHostController = androidx.navigation.compose.rememberNavController()) {
    var startDestination by rememberSaveable { mutableStateOf(Screen.Splash.route) }

    LaunchedEffect(Unit) {
        if (FirebaseAuth.getInstance().currentUser != null) {
            startDestination = Screen.Home.route
        } else {
            startDestination = Screen.Welcome.route
        }
    }

    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        // Auth Screens
        composable(Screen.Splash.route) { SplashScreen(navController = navController) }
        composable(Screen.Welcome.route) { WelcomeScreen(navController = navController) }
        composable(Screen.Login.route) { LoginScreen(navController = navController) }
        composable(Screen.Register.route) { RegisterScreen(navController = navController) }
        composable(Screen.ForgotPassword.route) { ForgotPasswordScreen(navController = navController) }
        composable(Screen.PhoneVerify.route) { PhoneVerifyScreen(navController = navController) }

        // Main Screens (with bottom nav)
        composable(Screen.Home.route) { HomeScreen(navController = navController) }
        composable(Screen.Explore.route) { ExploreScreen(navController = navController) }
        composable(Screen.Notifications.route) { NotificationsScreen(navController = navController) }
        composable(Screen.Inbox.route) { InboxScreen(navController = navController) }

        // Post Screens
        composable(
            route = Screen.CreatePost.route + "?replyTo={replyTo}&quoteId={quoteId}",
            arguments = listOf(
                navArgument("replyTo") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("quoteId") { type = NavType.StringType; nullable = true; defaultValue = null }
            )
        ) { CreatePostScreen(navController = navController, replyToId = it.arguments?.getString("replyTo"), quoteId = it.arguments?.getString("quoteId")) }

        composable(
            route = Screen.PostDetail.route,
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) { PostDetailScreen(navController = navController, postId = it.arguments?.getString("postId") ?: "") }

        composable(
            route = Screen.ThreadView.route,
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) { ThreadViewScreen(navController = navController, postId = it.arguments?.getString("postId") ?: "") }

        composable(
            route = Screen.PostMetrics.route,
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) { PostMetricsScreen(navController = navController, postId = it.arguments?.getString("postId") ?: "") }

        // Profile Screens
        composable(
            route = Screen.Profile.route,
            arguments = listOf(navArgument("uid") { type = NavType.StringType })
        ) { ProfileScreen(navController = navController, userId = it.arguments?.getString("uid") ?: "") }

        composable(Screen.EditProfile.route) { EditProfileScreen(navController = navController) }

        composable(
            route = Screen.Followers.route,
            arguments = listOf(navArgument("uid") { type = NavType.StringType })
        ) { FollowersScreen(navController = navController, userId = it.arguments?.getString("uid") ?: "") }

        composable(
            route = Screen.Following.route,
            arguments = listOf(navArgument("uid") { type = NavType.StringType })
        ) { FollowingScreen(navController = navController, userId = it.arguments?.getString("uid") ?: "") }

        // Explore / Search
        composable(
            route = Screen.SearchResults.route,
            arguments = listOf(navArgument("query") { type = NavType.StringType })
        ) { SearchResultsScreen(navController = navController, query = it.arguments?.getString("query") ?: "") }

        composable(
            route = Screen.TrendingTopic.route,
            arguments = listOf(navArgument("hashtag") { type = NavType.StringType })
        ) { TrendingTopicScreen(navController = navController, hashtag = it.arguments?.getString("hashtag") ?: "") }

        // Messages
        composable(Screen.NewMessage.route) { NewMessageScreen(navController = navController) }

        composable(
            route = Screen.Conversation.route,
            arguments = listOf(navArgument("conversationId") { type = NavType.StringType })
        ) { ConversationScreen(navController = navController, conversationId = it.arguments?.getString("conversationId") ?: "") }

        // Bookmarks
        composable(Screen.Bookmarks.route) { BookmarksScreen(navController = navController) }

        // Settings
        composable(Screen.Settings.route) { SettingsScreen(navController = navController) }
        composable(Screen.AccountSettings.route) { AccountSettingsScreen(navController = navController) }
        composable(Screen.PrivacySettings.route) { PrivacySettingsScreen(navController = navController) }
        composable(Screen.NotificationSettings.route) { NotificationSettingsScreen(navController = navController) }
        composable(Screen.AppearanceSettings.route) { AppearanceSettingsScreen(navController = navController) }
        composable(Screen.SecuritySettings.route) { SecuritySettingsScreen(navController = navController) }
        composable(Screen.BlockedAccounts.route) { BlockedAccountsScreen(navController = navController) }
        composable(Screen.MutedAccounts.route) { MutedAccountsScreen(navController = navController) }
        composable(Screen.Report.route) { ReportScreen(navController = navController) }

        // Media
        composable(Screen.ImageViewer.route) { FullImageViewer(navController = navController) }
        composable(Screen.VideoPlayer.route) { VideoPlayerScreen(navController = navController) }
    }
}