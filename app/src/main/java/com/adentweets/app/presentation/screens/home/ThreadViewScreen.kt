package com.adentweets.app.presentation.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.Post
import com.adentweets.app.domain.usecase.post.GetPostUseCase
import com.adentweets.app.domain.usecase.post.LikePostUseCase
import com.adentweets.app.domain.usecase.post.RetweetPostUseCase
import com.adentweets.app.domain.usecase.post.BookmarkPostUseCase
import com.adentweets.app.presentation.theme.AdenBlue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThreadViewModel @Inject constructor(
    private val getPostUseCase: GetPostUseCase,
    private val likePostUseCase: LikePostUseCase,
    private val retweetPostUseCase: RetweetPostUseCase,
    private val bookmarkPostUseCase: BookmarkPostUseCase
) : ViewModel() {

    private val _threadPosts = MutableStateFlow<List<Post>>(emptyList())
    val threadPosts: StateFlow<List<Post>> = _threadPosts

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadThread(rootPostId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val thread = mutableListOf<Post>()
                var currentPostId = rootPostId
                val visited = mutableSetOf<String>()

                // Walk up the thread to the root
                val chain = mutableListOf<Post>()
                var postId = rootPostId
                while (postId.isNotEmpty() && postId !in visited) {
                    visited.add(postId)
                    when (val result = getPostUseCase(postId)) {
                        is Resource.Success -> {
                            val p = result.data
                            chain.add(0, p)
                            postId = p.replyToPostId ?: ""
                        }
                        else -> break
                    }
                }
                thread.addAll(chain)

                // Walk down to find replies (children)
                // In a full implementation, this would use GetRepliesUseCase
                // to recursively build the thread tree

                _threadPosts.value = thread
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun likePost(postId: String) {
        viewModelScope.launch {
            val updated = _threadPosts.value.map { post ->
                if (post.postId == postId) {
                    val isLiked = post.isLikedByCurrentUser
                    post.copy(
                        isLikedByCurrentUser = !isLiked,
                        likesCount = if (isLiked) post.likesCount - 1 else post.likesCount + 1
                    )
                } else post
            }
            _threadPosts.value = updated
            val post = _threadPosts.value.find { it.postId == postId }
            if (post != null) likePostUseCase(postId, post.isLikedByCurrentUser)
        }
    }

    fun retweetPost(postId: String) {
        viewModelScope.launch {
            val updated = _threadPosts.value.map { post ->
                if (post.postId == postId) {
                    val isRT = post.isRetweetedByCurrentUser
                    post.copy(
                        isRetweetedByCurrentUser = !isRT,
                        retweetsCount = if (isRT) post.retweetsCount - 1 else post.retweetsCount + 1
                    )
                } else post
            }
            _threadPosts.value = updated
            val post = _threadPosts.value.find { it.postId == postId }
            if (post != null) retweetPostUseCase(postId, post.isRetweetedByCurrentUser)
        }
    }

    fun bookmarkPost(postId: String) {
        viewModelScope.launch {
            val updated = _threadPosts.value.map { post ->
                if (post.postId == postId) {
                    val isBm = post.isBookmarkedByCurrentUser
                    post.copy(isBookmarkedByCurrentUser = !isBm)
                } else post
            }
            _threadPosts.value = updated
            val post = _threadPosts.value.find { it.postId == postId }
            if (post != null) bookmarkPostUseCase(postId, post.isBookmarkedByCurrentUser)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThreadViewScreen(
    navController: NavController,
    rootPostId: String,
    viewModel: ThreadViewModel = hiltViewModel()
) {
    val threadPosts by viewModel.threadPosts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(rootPostId) {
        viewModel.loadThread(rootPostId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                title = {
                    Text(
                        text = "المسلسلة",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AdenBlue)
                }
            }
            threadPosts.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyStateView(message = "لا توجد منشورات في هذه المسلسلة")
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(threadPosts, key = { it.postId }) { post ->
                        // Thread connector line
                        if (post != threadPosts.last()) {
                            Column {
                                PostCard(
                                    post = post,
                                    onLike = { viewModel.likePost(post.postId) },
                                    onRetweet = { viewModel.retweetPost(post.postId) },
                                    onBookmark = { viewModel.bookmarkPost(post.postId) },
                                    onPostClick = {
                                        navController.navigate("post_detail/${post.postId}")
                                    },
                                    onProfileClick = {
                                        navController.navigate("profile/${post.authorId}")
                                    },
                                    onReply = {
                                        navController.navigate("create_post?replyTo=${post.postId}")
                                    }
                                )
                                // Thread connector
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 38.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(start = 3.dp)
                                    ) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Box(
                                            modifier = Modifier
                                                .width(2.dp)
                                                .weight(1f)
                                                .padding(vertical = 2.dp)
                                        ) {
                                            androidx.compose.foundation.Canvas(
                                                modifier = Modifier.fillMaxSize()
                                            ) {
                                                drawRect(
                                                    color = MaterialTheme.colorScheme.outlineVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            PostCard(
                                post = post,
                                onLike = { viewModel.likePost(post.postId) },
                                onRetweet = { viewModel.retweetPost(post.postId) },
                                onBookmark = { viewModel.bookmarkPost(post.postId) },
                                onPostClick = {
                                    navController.navigate("post_detail/${post.postId}")
                                },
                                onProfileClick = {
                                    navController.navigate("profile/${post.authorId}")
                                },
                                onReply = {
                                    navController.navigate("create_post?replyTo=${post.postId}")
                                }
                            )
                        }
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant,
                            thickness = 0.5.dp
                        )
                    }
                }
            }
        }
    }
}