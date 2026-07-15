package com.adentweets.app.presentation.screens.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.adentweets.app.domain.model.TrendingTopic
import com.adentweets.app.domain.model.User
import com.adentweets.app.presentation.components.common.Base64ImageView
import com.adentweets.app.presentation.components.common.XButton
import com.adentweets.app.presentation.components.post.PostCard
import com.adentweets.app.presentation.screens.explore.viewmodel.ExploreViewModel
import com.adentweets.app.presentation.theme.AdenBlue

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExploreScreen(
    navController: NavController,
    viewModel: ExploreViewModel = hiltViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val trendingTopics by viewModel.trendingTopics.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val categories = viewModel.categories

    val isSearchActive = searchQuery.length >= 2

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ── Search Bar ──
        SearchBarSection(
            query = searchQuery,
            onQueryChange = { viewModel.updateSearchQuery(it) },
            isSearchActive = isSearchActive,
            onClearSearch = { viewModel.updateSearchQuery("") }
        )

        // ── Category Chips (only when not searching) ──
        if (!isSearchActive) {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    val isSelected = category == selectedCategory
                    val bgColor = if (isSelected) AdenBlue else MaterialTheme.colorScheme.surfaceVariant
                    val textColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                    Text(
                        text = category,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(bgColor)
                            .clickable { viewModel.selectCategory(category) }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        color = textColor,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
        }

        // ── Content ──
        if (isSearchActive) {
            SearchResultsContent(
                searchResults = searchResults,
                isSearching = isSearching,
                navController = navController
            )
        } else {
            TrendingContent(
                trendingTopics = trendingTopics,
                navController = navController
            )
        }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Search Bar
// ──────────────────────────────────────────────────────────────────────────────
@Composable
private fun SearchBarSection(
    query: String,
    onQueryChange: (String) -> Unit,
    isSearchActive: Boolean,
    onClearSearch: () -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                text = "ابحث في عدن تويت",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClearSearch) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "مسح البحث",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(52.dp),
        shape = RoundedCornerShape(26.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AdenBlue,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            cursorColor = AdenBlue
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        textStyle = MaterialTheme.typography.bodyLarge
    )
}

// ──────────────────────────────────────────────────────────────────────────────
// Trending Content (default view)
// ──────────────────────────────────────────────────────────────────────────────
@Composable
private fun TrendingContent(
    trendingTopics: List<TrendingTopic>,
    navController: NavController
) {
    if (trendingTopics.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "لا توجد موضوعات رائجة حالياً",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                text = "الأكثر رواجاً في عدن تويت",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }

        items(trendingTopics) { topic ->
            TrendingItem(
                topic = topic,
                onClick = {
                    navController.navigate("trending_topic/${topic.hashtag.removePrefix("#")}")
                }
            )
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun TrendingItem(
    topic: TrendingTopic,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "${topic.rank} · ${topic.category}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = topic.hashtag,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "${topic.postsCount} منشور",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    HorizontalDivider(
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
    )
}

// ──────────────────────────────────────────────────────────────────────────────
// Search Results Content
// ──────────────────────────────────────────────────────────────────────────────
@Composable
private fun SearchResultsContent(
    searchResults: ExploreViewModel.SearchResults,
    isSearching: Boolean,
    navController: NavController
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("الأشخاص", "المنشورات", "الوسوم")

    TabRow(
        selectedTabIndex = selectedTab,
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onSurface,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                color = AdenBlue
            )
        },
        divider = {
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
            )
        }
    ) {
        tabTitles.forEachIndexed { index, title ->
            Tab(
                selected = selectedTab == index,
                onClick = { selectedTab = index },
                text = {
                    Text(
                        text = title,
                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                    )
                },
                selectedContentColor = MaterialTheme.colorScheme.onSurface,
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    when (selectedTab) {
        0 -> PeopleTabContent(
            users = searchResults.users,
            isSearching = isSearching,
            navController = navController
        )
        1 -> PostsTabContent(
            posts = searchResults.posts,
            isSearching = isSearching,
            navController = navController
        )
        2 -> HashtagsTabContent(
            posts = searchResults.posts,
            isSearching = isSearching,
            navController = navController
        )
    }
}

// ─── People Tab ───
@Composable
private fun PeopleTabContent(
    users: List<User>,
    isSearching: Boolean,
    navController: NavController
) {
    if (isSearching) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            androidx.compose.material3.CircularProgressIndicator(color = AdenBlue)
        }
        return
    }

    if (users.isEmpty()) {
        EmptySearchResult(message = "لم يتم العثور على أشخاص")
        return
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(users, key = { it.uid }) { user ->
            UserSearchRow(
                user = user,
                onUserClick = { navController.navigate("profile/${user.uid}") },
                onFollowClick = { }
            )
        }
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun UserSearchRow(
    user: User,
    onUserClick: () -> Unit,
    onFollowClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onUserClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Base64ImageView(
            base64 = user.avatarBase64,
            modifier = Modifier.size(48.dp),
            isCircle = true,
            size = 48.dp,
            placeholder = {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = user.displayName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (user.isVerified) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = null,
                        modifier = Modifier
                            .size(16.dp)
                            .padding(start = 4.dp),
                        tint = AdenBlue
                    )
                }
            }
            Text(
                text = "@${user.username}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (user.bio.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = user.bio,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        XButton(
            text = "متابعة",
            onClick = onFollowClick,
            isSmall = true,
            isOutlined = true
        )
    }
    HorizontalDivider(
        modifier = Modifier.padding(start = 76.dp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
    )
}

// ─── Posts Tab ───
@Composable
private fun PostsTabContent(
    posts: List<com.adentweets.app.domain.model.Post>,
    isSearching: Boolean,
    navController: NavController
) {
    if (isSearching) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            androidx.compose.material3.CircularProgressIndicator(color = AdenBlue)
        }
        return
    }

    if (posts.isEmpty()) {
        EmptySearchResult(message = "لم يتم العثور على منشورات")
        return
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(posts, key = { it.postId }) { post ->
            PostCard(
                post = post,
                onPostClick = { navController.navigate("post_detail/${post.postId}") },
                onProfileClick = {
                    post.author?.let { navController.navigate("profile/${it.uid}") }
                }
            )
        }
    }
}

// ─── Hashtags Tab ───
@Composable
private fun HashtagsTabContent(
    posts: List<com.adentweets.app.domain.model.Post>,
    isSearching: Boolean,
    navController: NavController
) {
    if (isSearching) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            androidx.compose.material3.CircularProgressIndicator(color = AdenBlue)
        }
        return
    }

    val allHashtags = remember(posts) {
        posts.flatMap { it.hashtags }.distinct().sorted()
    }

    if (allHashtags.isEmpty()) {
        EmptySearchResult(message = "لم يتم العثور على وسم")
        return
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(allHashtags) { hashtag ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate("trending_topic/${hashtag.removePrefix("#")}")
                    }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Tag,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (hashtag.startsWith("#")) hashtag else "#$hashtag",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = AdenBlue
                )
            }
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
            )
        }
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

// ─── Empty State ───
@Composable
private fun EmptySearchResult(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}