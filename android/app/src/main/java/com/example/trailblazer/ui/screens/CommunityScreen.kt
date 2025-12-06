// FILE: app/src/main/java/com/example/trailblazer/ui/screens/CommunityScreen.kt
package com.example.trailblazer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trailblazer.net.ApiClient
import com.example.trailblazer.net.PostCreateRequest
import com.example.trailblazer.net.PostDto
import kotlinx.coroutines.launch

data class Post(
    val id: Int,
    val username: String,
    val challengeName: String?,
    val timeAgo: String,
    val content: String,
    val likes: Int,
    val comments: Int,
    val isLiked: Boolean = false,
    val isBookmarked: Boolean = false
)

@Composable
fun CommunityScreen(
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Recent", "Popular", "Friends")

    var posts by remember { mutableStateOf<List<Post>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showNewPostDialog by remember { mutableStateOf(false) }
    var newPostText by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    fun mapDtoToPost(dto: PostDto): Post {
        return Post(
            id = dto.id,
            username = "Hiker #${dto.userId}",
            challengeName = null,
            timeAgo = dto.createdAt,
            content = dto.body,
            likes = 0,
            comments = 0
        )
    }

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val apiPosts = ApiClient.service.getPosts(limit = 50)
            posts = apiPosts.map(::mapDtoToPost)
        } catch (e: Exception) {
            errorMessage = e.message ?: "Failed to load posts."
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header
        Surface(
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Community",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )

                    Button(
                        onClick = { showNewPostDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("New Post", fontSize = 14.sp)
                    }
                }

                // Tabs (purely visual for now)
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.White,
                    contentColor = Color(0xFF4CAF50)
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTab == index) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }
            }
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage ?: "",
                color = Color(0xFFD32F2F),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                fontSize = 13.sp
            )
        }

        // Posts List
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF4CAF50))
                    }
                }
            } else {
                items(posts) { post ->
                    PostCard(post = post)
                }
            }
        }

        // Bottom Nav
        BottomNavigationBar(
            currentScreen = "Community",
            onNavigate = onNavigate
        )
    }

    if (showNewPostDialog) {
        AlertDialog(
            onDismissRequest = { showNewPostDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newPostText.isNotBlank()) {
                            scope.launch {
                                try {
                                    val api = ApiClient.service
                                    api.createPost(
                                        PostCreateRequest(
                                            body = newPostText,
                                            trailId = null
                                        )
                                    )
                                    // Reload posts
                                    val apiPosts = api.getPosts(limit = 50)
                                    posts = apiPosts.map(::mapDtoToPost)
                                    newPostText = ""
                                    showNewPostDialog = false
                                } catch (e: Exception) {
                                    errorMessage = e.message ?: "Failed to create post."
                                }
                            }
                        }
                    }
                ) {
                    Text("Post")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewPostDialog = false }) {
                    Text("Cancel")
                }
            },
            title = { Text("New Post") },
            text = {
                OutlinedTextField(
                    value = newPostText,
                    onValueChange = { newPostText = it },
                    placeholder = { Text("Share something with the community") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        )
    }
}

@Composable
private fun PostCard(post: Post) {
    var isLiked by remember { mutableStateOf(post.isLiked) }
    var isBookmarked by remember { mutableStateOf(post.isBookmarked) }
    var likeCount by remember { mutableStateOf(post.likes) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // User Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF4CAF50)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = post.username.firstOrNull()?.uppercase() ?: "?",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = post.username,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF212121)
                    )

                    if (post.challengeName != null) {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(SpanStyle(color = Color(0xFF2196F3), fontWeight = FontWeight.SemiBold)) {
                                    append(post.challengeName)
                                }
                                withStyle(SpanStyle(color = Color(0xFF757575))) {
                                    append(" • ${post.timeAgo}")
                                }
                            },
                            fontSize = 13.sp
                        )
                    } else {
                        Text(
                            text = post.timeAgo,
                            fontSize = 13.sp,
                            color = Color(0xFF757575)
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Post Content
            Text(
                text = post.content,
                fontSize = 14.sp,
                color = Color(0xFF212121),
                lineHeight = 20.sp
            )

            Spacer(Modifier.height(16.dp))

            // Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Like
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        isLiked = !isLiked
                        likeCount += if (isLiked) 1 else -1
                    }
                ) {
                    Icon(
                        imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (isLiked) Color(0xFFE91E63) else Color(0xFF757575),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = likeCount.toString(),
                        fontSize = 14.sp,
                        color = Color(0xFF757575)
                    )
                }

                // Comment (still UI-only)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { }
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubble,
                        contentDescription = "Comment",
                        tint = Color(0xFF757575),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = post.comments.toString(),
                        fontSize = 14.sp,
                        color = Color(0xFF757575)
                    )
                }

                Spacer(Modifier.weight(1f))

                // Bookmark
                IconButton(
                    onClick = { isBookmarked = !isBookmarked },
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Favorite else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (isBookmarked) Color(0xFF4CAF50) else Color(0xFF757575)
                    )
                }
            }
        }
    }
}
