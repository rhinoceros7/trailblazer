// FILE: app/src/main/java/com/example/trailblazer/ui/screens/ProgressScreen.kt
package com.example.trailblazer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Hiking
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trailblazer.net.ActivityDto
import com.example.trailblazer.net.ApiClient
import com.example.trailblazer.net.ProfileDto
import kotlinx.coroutines.launch
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun ProgressScreen(
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var profile by remember { mutableStateOf<ProfileDto?>(null) }
    var activities by remember { mutableStateOf<List<ActivityDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val api = ApiClient.service
            profile = api.getMyProfile()
            activities = api.getMyActivities()
        } catch (e: Exception) {
            errorMessage = e.message ?: "Failed to load progress."
        } finally {
            isLoading = false
        }
    }

    // Compute simple stats
    val totalDistanceKm = activities.mapNotNull { it.distanceKm }.sum()
    val totalDistanceMi = totalDistanceKm * 0.621371
    val totalHours = activities.mapNotNull { it.durationMinutes }.sum() / 60.0
    val trailsCompleted = profile?.totalTrailsCompleted ?: activities.mapNotNull { it.trailId }.distinct().size

    // Weekly goal: 10 miles, use total distance as a simple proxy
    val weeklyGoalMi = 10.0
    val weeklyProgress = if (weeklyGoalMi > 0) {
        min((totalDistanceMi / weeklyGoalMi).toFloat(), 1f)
    } else 0f

    val weeklyDistanceText = "${(totalDistanceMi * 10.0).roundToInt() / 10.0} / $weeklyGoalMi"

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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "Your Hiking Progress",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
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
            // Weekly Goal Card
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE8F5E9)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Progress Circle
                        Box(
                            modifier = Modifier.size(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                progress = { weeklyProgress },
                                modifier = Modifier.size(120.dp),
                                color = Color(0xFF4CAF50),
                                strokeWidth = 12.dp,
                                trackColor = Color.White
                            )
                            Text(
                                text = "${(weeklyProgress * 100).roundToInt()}%",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4CAF50)
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = "Weekly Goal Progress",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF212121)
                        )

                        Text(
                            text = weeklyDistanceText,
                            fontSize = 16.sp,
                            color = Color(0xFF757575)
                        )
                        Text(
                            text = "miles",
                            fontSize = 14.sp,
                            color = Color(0xFF757575)
                        )

                        Spacer(Modifier.height(12.dp))

                        LinearProgressIndicator(
                            progress = { weeklyProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = Color(0xFF4CAF50),
                            trackColor = Color.White
                        )

                        Spacer(Modifier.height(8.dp))

                        val remaining = (weeklyGoalMi - totalDistanceMi).coerceAtLeast(0.0)
                        Text(
                            text = "${(remaining * 10.0).roundToInt() / 10.0} miles to go",
                            fontSize = 13.sp,
                            color = Color(0xFF757575)
                        )
                    }
                }
            }

            // Stats Grid
            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.height(320.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        StatCard(
                            icon = Icons.Default.LocationOn,
                            value = "${(totalDistanceMi * 10.0).roundToInt() / 10.0}",
                            label = "Total Distance (mi)"
                        )
                    }
                    item {
                        StatCard(
                            icon = Icons.Default.Hiking,
                            value = profile?.totalDistanceKm?.let {
                                val ft = it * 3280.84
                                (ft.roundToInt()).toString()
                            } ?: "—",
                            label = "Total Elevation (ft)"
                        )
                    }
                    item {
                        StatCard(
                            icon = Icons.Default.Hiking,
                            value = trailsCompleted.toString(),
                            label = "Trails Completed"
                        )
                    }
                    item {
                        StatCard(
                            icon = Icons.Default.Timer,
                            value = "${(totalHours * 10.0).roundToInt() / 10.0}",
                            label = "Total Hours"
                        )
                    }
                }
            }

            // Achievements Section (still static, which is fine)
            item {
                Column {
                    Text(
                        text = "Achievements",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF212121)
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AchievementCard(
                            icon = "🥾",
                            title = "First Steps",
                            modifier = Modifier.weight(1f)
                        )
                        AchievementCard(
                            icon = "⛰️",
                            title = "Peak Seeker",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Bottom Nav
        BottomNavigationBar(
            currentScreen = "Progress",
            onNavigate = onNavigate
        )
    }
}

@Composable
private fun StatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color(0xFF757575),
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

@Composable
private fun AchievementCard(
    icon: String,
    title: String,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE8F5E9)
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = icon,
                fontSize = 32.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF212121)
            )
        }
    }
}
