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
import retrofit2.http.POST
import retrofit2.http.PATCH
import retrofit2.http.DELETE
import retrofit2.http.Multipart
import retrofit2.http.Body
import retrofit2.http.Part
import retrofit2.http.Path
import okhttp3.MultipartBody
import okhttp3.RequestBody

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

    // --- Auth ---

    @POST("auth/register")
    suspend fun register(
        @Body body: RegisterRequest
    ): AuthResponse

    @POST("auth/login")
    suspend fun login(
        @Body body: LoginRequest
    ): AuthResponse

    @GET("auth/about")
    suspend fun getMe(): ProfileDto

    // --- Trails extras: photos ---
    @Multipart
    @POST("trails/{trailId}/photos")
    suspend fun uploadTrailPhoto(
        @Path("trailId") trailId: Int,
        @Part file: MultipartBody.Part,
        @Part("caption") caption: RequestBody? = null
    ): PhotoDto

    @GET("trails/{trailId}/photos")
    suspend fun getTrailPhotos(
        @Path("trailId") trailId: Int
    ): List<PhotoDto>

    // --- Notes ---
    @GET("trails/{trailId}/notes")
    suspend fun getTrailNotes(
        @Path("trailId") trailId: Int
    ): List<NoteDto>

    @POST("trails/{trailId}/notes")
    suspend fun createNote(
        @Path("trailId") trailId: Int,
        @Body body: NoteCreateRequest
    ): NoteDto

    @PATCH("notes/{noteId}")
    suspend fun updateNote(
        @Path("noteId") noteId: Int,
        @Body body: NoteUpdateRequest
    ): NoteDto

    @DELETE("notes/{noteId}")
    suspend fun deleteNote(
        @Path("noteId") noteId: Int
    ): Unit

    // --- Favorites ---
    @POST("trails/{trailId}/favorite")
    suspend fun toggleFavorite(
        @Path("trailId") trailId: Int
    ): FavoriteStatusDto

    @GET("me/favorites")
    suspend fun getFavorites(): List<TrailDto>

    // --- Offline ---
    @POST("offline/trails/{trailId}")
    suspend fun toggleOffline(
        @Path("trailId") trailId: Int
    ): OfflineStatusDto

    @GET("offline/trails")
    suspend fun getOfflineTrails(): List<TrailDto>

    // --- Activities ---
    @POST("trails/{trailId}/activities")
    suspend fun logActivity(
        @Path("trailId") trailId: Int,
        @Body body: ActivityCreateRequest
    ): ActivityDto

    @GET("activities/me")
    suspend fun getMyActivities(): List<ActivityDto>

    // --- Profiles ---
    @GET("profiles/me")
    suspend fun getMyProfile(): ProfileDto

    @PATCH("profiles/me")
    suspend fun updateMyProfile(
        @Body body: ProfileUpdateRequest
    ): ProfileDto

    @GET("profiles/{userId}")
    suspend fun getProfileById(
        @Path("userId") userId: Int
    ): ProfileDto

    // --- Posts ---
    @POST("posts")
    suspend fun createPost(
        @Body body: PostCreateRequest
    ): PostDto

    @GET("posts")
    suspend fun getPosts(
        @Query("trail_id") trailId: Int? = null,
        @Query("author_id") authorId: Int? = null,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): List<PostDto>

    @GET("posts/{postId}")
    suspend fun getPost(
        @Path("postId") postId: Int
    ): PostDto

    @PATCH("posts/{postId}")
    suspend fun updatePost(
        @Path("postId") postId: Int,
        @Body body: PostUpdateRequest
    ): PostDto

    @DELETE("posts/{postId}")
    suspend fun deletePost(
        @Path("postId") postId: Int
    ): Unit

    // --- Parks & admin NPS refresh ---
    @POST("admin/nps/refresh")
    suspend fun refreshParks(): AdminRefreshResponse
}

object ApiClient {
    private val json = Json { ignoreUnknownKeys = true }

    private val client by lazy {
        val log = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

        OkHttpClient.Builder()
            .addInterceptor(log)
            .addInterceptor { chain ->
                val original = chain.request()
                val token = AuthStore.token

                val req = if (token != null) {
                    original.newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                } else original

                chain.proceed(req)
            }
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

object AuthStore {
    @Volatile var token: String? = null
}