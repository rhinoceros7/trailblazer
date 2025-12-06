package com.example.trailblazer.net

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Trails api response model
@Serializable
data class TrailDto(
    val id: Int,
    val name: String,
    val difficulty: String?,
    @SerialName("length_km") val lengthKm: Double?,
    @SerialName("elevation_gain_m") val elevationGainM: Double?,
    val lat: Double? = null,
    @SerialName("lon") val lng: Double? = null, // map 'lon' from API -> 'lng' for UI
    val accessible: Boolean?,
    @SerialName("has_waterfall") val hasWaterfall: Boolean?,
    @SerialName("has_viewpoint") val hasViewpoint: Boolean?,
    @SerialName("avg_rating") val avgRating: Double?,
    @SerialName("ratings_count") val ratingsCount: Int?
)

// Parks api response model
@Serializable
data class ParkDto(
    val id: Int,
    val name: String,
    val state: String,
    val lat: Double,
    @SerialName("lon") val lng: Double
)

// --- Auth + user ---

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    @SerialName("display_name") val displayName: String
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class AuthResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("token_type") val tokenType: String = "bearer"
)

@Serializable
data class UserDto(
    val id: Int,
    val email: String,
    @SerialName("display_name") val displayName: String? = null
)

// --- Photos ---

@Serializable
data class PhotoDto(
    val id: Int,
    @SerialName("trail_id") val trailId: Int,
    @SerialName("user_id") val userId: Int,
    val caption: String? = null,
    @SerialName("created_at") val createdAt: String,
    val url: String
)

// --- Notes ---

@Serializable
data class NoteDto(
    val id: Int,
    @SerialName("trail_id") val trailId: Int,
    @SerialName("user_id") val userId: Int,
    @SerialName("text") val text: String,
    @SerialName("created_at") val createdAt: String
)

@Serializable
data class NoteCreateRequest(
    val body: String
)

@Serializable
data class NoteUpdateRequest(
    val body: String? = null
)

// --- Favorites ---

@Serializable
data class FavoriteStatusDto(
    val ok: Boolean,
    @SerialName("is_favorited") val isFavorited: Boolean,
    val message: String
)

// --- Offline ---

@Serializable
data class OfflineStatusDto(
    val ok: Boolean,
    @SerialName("is_offline") val isOffline: Boolean,
    val message: String
)

// --- Activities ---

@Serializable
data class ActivityDto(
    val id: Int,
    @SerialName("trail_id") val trailId: Int,
    @SerialName("user_id") val userId: Int,
    @SerialName("distance_km") val distanceKm: Double? = null,
    @SerialName("duration_min") val durationMinutes: Double? = null,
    @SerialName("elevation_gain_m") val elevationGainM: Double? = null,
    @SerialName("date") val date: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class ActivityCreateRequest(
    @SerialName("distance_km") val distanceKm: Double? = null,
    @SerialName("duration_min") val durationMinutes: Double? = null,
    @SerialName("elevation_gain_m") val elevationGainM: Double? = null,
    @SerialName("date") val date: String? = null
)

// --- Profiles ---

@Serializable
data class ProfileDto(
    @SerialName("user_id") val userId: Int,
    @SerialName("display_name") val displayName: String? = null,
    val bio: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("home_state") val homeState: String? = null,
    @SerialName("home_lat") val homeLat: Double? = null,
    @SerialName("home_lon") val homeLon: Double? = null,
    // These are not currently returned by ProfileOut, so they’ll be null – which is fine.
    @SerialName("total_trails_completed") val totalTrailsCompleted: Int? = null,
    @SerialName("total_distance_km") val totalDistanceKm: Double? = null,
    @SerialName("avg_difficulty") val avgDifficulty: String? = null
)

@Serializable
data class ProfileUpdateRequest(
    @SerialName("display_name") val displayName: String? = null,
    val bio: String? = null
)

// --- Posts ---

@Serializable
data class PostDto(
    val id: Int,
    @SerialName("trail_id") val trailId: Int? = null,
    @SerialName("user_id") val userId: Int,
    val body: String,
    @SerialName("created_at") val createdAt: String
)

@Serializable
data class PostCreateRequest(
    val body: String,
    @SerialName("trail_id") val trailId: Int? = null
)

@Serializable
data class PostUpdateRequest(
    val body: String? = null
)

// --- Admin NPS refresh ---

@Serializable
data class AdminRefreshResponse(
    val ok: Boolean,
    val imported: Int? = null,
    val skipped: Int? = null,
    val message: String? = null
)
