package com.example.trailblazer.net

import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import okhttp3.MediaType.Companion.toMediaType
import com.example.trailblazer.BuildConfig

interface ApiService {
    // GET /trails?near=lat,lon&radius=50
    @GET("trails/")
    suspend fun getTrailsNearby(
        @Query("near") near: String,
        @Query("radius") radiusKm: Double = 50.0
    ): List<TrailDto>

    // GET /parks?near=lat,lon&radius=50
    @GET("parks/")
    suspend fun getParksNearby(
        @Query("near") near: String,
        @Query("radius") radius: Double = 50.0
    ): List<ParkDto>
}

object ApiClient {
    private val json = Json { ignoreUnknownKeys = true }

    private val client by lazy {
        val log = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        OkHttpClient.Builder()
            .addInterceptor(log)
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL.ensureTrailingSlash())
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .client(client)
            .build()
    }

    val service: ApiService by lazy { retrofit.create(ApiService::class.java) }
}

// Small helper so a missing slash never hurts us in dev
private fun String.ensureTrailingSlash(): String =
    if (endsWith("/")) this else "$this/"