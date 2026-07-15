package com.adentweets.app.presentation.screens.bookmarks

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.adentweets.app.presentation.components.common.EmptyStateView
import com.adentweets.app.presentation.components.post.PostCard
import com.adentweets.app.presentation.screens.bookmarks.viewmodel.BookmarksViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreen(
    navController: NavController,
    viewModel: BookmarksViewModel = hiltViewModel()
) {
    val posts by viewModel.posts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "المرجعيات",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                    }
                },
                actions = {
                    IconButton(onClick = { /* More options */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "المزيد")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        if (isLoading && posts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = com.adentweets.app.presentation.theme.AdenBlue)
            }
        } else if (posts.isEmpty()) {
            Box(modifier = Modifier.padding(paddingValues)) {
                EmptyStateView(
                    message = "لا توجد منشورات محفوظة",
                    icon = Icons.Default.BookmarkBorder
                )
            }
        } else {
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    isRefreshing = true
                    viewModel.refresh()
                    isRefreshing = false
                },
                modifier = Modifier.padding(paddingValues)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(posts, key = { it.postId }) { post ->
                        BookmarkedPostCard(
                            post = post,
                            onPostClick = {
                                navController.navigate("post_detail/${post.postId}")
                            },
                            onProfileClick = {
                                post.authorId.let { uid ->
                                    if (uid.isNotEmpty()) navController.navigate("profile/$uid")
                                }
                            },
                            onRemoveBookmark = {
                                viewModel.removeBookmark(post.postId)
                            }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 72.dp),
                            thickness = 0.5.dp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BookmarkedPostCard(
    post: com.adentweets.app.domain.model.Post,
    onPostClick: () -> Unit,
    onProfileClick: () -> Unit,
    onRemoveBookmark: () -> Unit
) {
    var showRemoveDialog by remember { mutableStateOf(false) }

    PostCard(
        post = post,
        onPostClick = onPostClick,
        onProfileClick = onProfileClick,
        onBookmark = { showRemoveDialog = true }
    )

    if (showRemoveDialog) {
        AlertDialog(
            onDismissRequest = { showRemoveDialog = false },
            title = {
                Text(
                    text = "إزالة المرجعية",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(text = "هل تريد إزالة هذا المنشور من المرجعيات؟")
            },
            confirmButton = {
                TextButton(onClick = {
                    onRemoveBookmark()
                    showRemoveDialog = false
                }) {
                    Text("إزالة", color = com.adentweets.app.presentation.theme.ErrorRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRemoveDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}