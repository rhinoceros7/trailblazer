// FILE: app/src/main/java/com/example/trailblazer/ui/screens/OfflineScreen.kt
package com.example.trailblazer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Hiking
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trailblazer.net.ApiClient
import com.example.trailblazer.net.TrailDto
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

data class DownloadedTrail(
    val id: Int,
    val name: String,
    val distance: String,
    val size: String,
    val downloadedDate: String
)

@Composable
fun OfflineScreen(
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var downloadedTrails by remember { mutableStateOf<List<DownloadedTrail>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var trailIdInput by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    fun mapTrailToDownloaded(trail: TrailDto): DownloadedTrail {
        val miles = (trail.lengthKm ?: 0.0) * 0.621371
        val milesText = "${(miles * 10.0).roundToInt() / 10.0} mi"
        return DownloadedTrail(
            id = trail.id,
            name = trail.name,
            distance = milesText,
            size = "--",
            downloadedDate = "Saved for offline"
        )
    }

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val trails = ApiClient.service.getOfflineTrails()
            downloadedTrails = trails.map(::mapTrailToDownloaded)
        } catch (e: Exception) {
            errorMessage = e.message ?: "Failed to load offline trails."
        } finally {
            isLoading = false
        }
    }

    fun refreshOffline() {
        scope.launch {
            try {
                val trails = ApiClient.service.getOfflineTrails()
                downloadedTrails = trails.map(::mapTrailToDownloaded)
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to refresh offline trails."
            }
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Offline Downloads",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )

                Button(
                    onClick = { showAddDialog = true },
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
                    Text("Add for Offline", fontSize = 14.sp)
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
            // Storage Card (still dummy numbers, but fine for now)
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudDownload,
                                contentDescription = null,
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Storage Usage",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF212121)
                                )
                                Text(
                                    text = "96.6 MB used of 512 MB",
                                    fontSize = 13.sp,
                                    color = Color(0xFF757575)
                                )
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        LinearProgressIndicator(
                            progress = { 0.19f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = Color(0xFF4CAF50),
                            trackColor = Color(0xFFE0E0E0)
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = "415.2 MB available for offline trails",
                            fontSize = 12.sp,
                            color = Color(0xFF757575)
                        )
                    }
                }
            }

            // Downloaded Trails Section
            item {
                Text(
                    text = "Downloaded Trails (${downloadedTrails.size})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF212121)
                )
            }

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
                items(downloadedTrails) { trail ->
                    DownloadedTrailCard(
                        trail = trail,
                        onDelete = {
                            scope.launch {
                                try {
                                    ApiClient.service.toggleOffline(trail.id)
                                    refreshOffline()
                                } catch (e: Exception) {
                                    errorMessage = e.message ?: "Failed to remove offline trail."
                                }
                            }
                        }
                    )
                }
            }

            // Offline Tips Card
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE3F2FD)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF2196F3),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Offline Tips",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1976D2)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "• Download trails before heading to areas with poor reception\n" +
                                        "• Maps include trail routes, elevation profiles, and key waypoints\n" +
                                        "• Downloaded content automatically updates when connected\n" +
                                        "• GPS tracking works offline for safety",
                                fontSize = 13.sp,
                                color = Color(0xFF1565C0),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }

        // Bottom Nav
        BottomNavigationBar(
            currentScreen = "Offline",
            onNavigate = onNavigate
        )
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val id = trailIdInput.toIntOrNull()
                        if (id != null) {
                            scope.launch {
                                try {
                                    ApiClient.service.toggleOffline(id)
                                    refreshOffline()
                                    trailIdInput = ""
                                    showAddDialog = false
                                } catch (e: Exception) {
                                    errorMessage = e.message ?: "Failed to save offline trail."
                                }
                            }
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            },
            title = { Text("Save trail for offline") },
            text = {
                OutlinedTextField(
                    value = trailIdInput,
                    onValueChange = { trailIdInput = it },
                    label = { Text("Trail ID") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        )
    }
}

@Composable
private fun DownloadedTrailCard(
    trail: DownloadedTrail,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Hiking,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            // Trail Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = trail.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF212121)
                )
                Text(
                    text = trail.distance,
                    fontSize = 13.sp,
                    color = Color(0xFF757575)
                )
                Text(
                    text = trail.size,
                    fontSize = 12.sp,
                    color = Color(0xFF9E9E9E)
                )
                Text(
                    text = trail.downloadedDate,
                    fontSize = 11.sp,
                    color = Color(0xFF9E9E9E)
                )
            }

            // Delete Button
            IconButton(
                onClick = onDelete
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color(0xFFE91E63)
                )
            }
        }
    }
}
