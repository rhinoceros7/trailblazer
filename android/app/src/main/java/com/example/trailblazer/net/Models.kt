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
    @SerialName("lon") val lng: Double? = null, // <-- map 'lon' from API -> 'lng' for UI
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