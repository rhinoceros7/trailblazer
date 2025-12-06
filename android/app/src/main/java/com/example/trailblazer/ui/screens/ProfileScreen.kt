// FILE: app/src/main/java/com/example/trailblazer/ui/screens/ProfileScreen.kt
package com.example.trailblazer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Hiking
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trailblazer.net.ApiClient
import com.example.trailblazer.net.ProfileDto
import com.example.trailblazer.net.TrailDto
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

data class UserTrail(
    val id: Int,
    val name: String,
    val distance: String,
    val difficulty: String
)

@Composable
fun ProfileScreen(
    onNavigate: (String) -> Unit,
    onEditProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Profile", "Achievements", "Settings")

    var profile by remember { mutableStateOf<ProfileDto?>(null) }
    var favoriteTrails by remember { mutableStateOf<List<TrailDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    fun mapTrailToUserTrail(trail: TrailDto): UserTrail {
        val miles = (trail.lengthKm ?: 0.0) * 0.621371
        val milesText = "${(miles * 10.0).roundToInt() / 10.0} mi"
        return UserTrail(
            id = trail.id,
            name = trail.name,
            distance = milesText,
            difficulty = trail.difficulty ?: "Unknown"
        )
    }

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val api = ApiClient.service
            profile = api.getMyProfile()
            favoriteTrails = api.getFavorites()
        } catch (e: Exception) {
            errorMessage = e.message ?: "Failed to load profile."
        } finally {
            isLoading = false
        }
    }

    val userTrails: List<UserTrail> = favoriteTrails.map(::mapTrailToUserTrail)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header with Tabs
        Surface(
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Column {
                Text(
                    text = "Profile",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121),
                    modifier = Modifier.padding(16.dp)
                )

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

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Header
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val displayName = profile?.displayName ?: "TrailBlazer User"
                        val avatarInitial = displayName.firstOrNull()?.uppercase() ?: "?"

                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4CAF50)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = avatarInitial,
                                color = Color.White,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        // Name and Username
                        Text(
                            text = displayName,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121)
                        )
                        Text(
                            text = profile?.homeState?.let { "@${it.lowercase()}" } ?: "@hiker",
                            fontSize = 14.sp,
                            color = Color(0xFF757575)
                        )

                        Spacer(Modifier.height(16.dp))

                        // Stats Row
                        val trailsCount = profile?.totalTrailsCompleted ?: 0
                        val reviewsCount = favoriteTrails.size
                        val photosCount = 0 // You can wire this later with photo endpoint

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatItem(icon = Icons.Default.Hiking, value = trailsCount.toString(), label = "Trails")
                            StatItem(icon = Icons.Default.Star, value = reviewsCount.toString(), label = "Favorites")
                            StatItem(icon = Icons.Default.Image, value = photosCount.toString(), label = "Photos")
                        }

                        Spacer(Modifier.height(16.dp))

                        // Edit Button
                        OutlinedButton(
                            onClick = onEditProfile,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF4CAF50)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Edit")
                        }
                    }
                }
            }

            // Trail List Section
            item {
                Column {
                    Text(
                        text = "My Trails",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF212121)
                    )
                    Spacer(Modifier.height(12.dp))

                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF4CAF50))
                        }
                    } else if (userTrails.isEmpty()) {
                        Text(
                            text = "No trails yet. Mark some favorites to see them here!",
                            fontSize = 13.sp,
                            color = Color(0xFF757575)
                        )
                    } else {
                        userTrails.forEach { trail ->
                            TrailListItem(trail)
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        }

        // Bottom Nav
        BottomNavigationBar(
            currentScreen = "Profile",
            onNavigate = onNavigate
        )
    }
}

@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF4CAF50),
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF212121)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF757575)
        )
    }
}

@Composable
private fun TrailListItem(trail: UserTrail) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Trail Image
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE8F5E9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoLibrary,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = trail.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF212121)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color(0xFF2196F3),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = trail.distance,
                            fontSize = 11.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Surface(
                        color = Color(0xFF4CAF50).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = trail.difficulty,
                            fontSize = 11.sp,
                            color = Color(0xFF4CAF50),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}
