# TrailBlazer README
---
## **Contributors:** 

   Arti Hajdari: 
   - Expertise: Leadership, Backend (Python, SQL, APIs, etc.), Diagrams, Documentation
   - Role: Project Lead, ensured development stayed on track, created diagrams/documentation, assisted with backend development
   
   Nafis Uddin: 
   - Expertise: Backend (Python, SQL, APIs, etc.)
   - Role: Backend Developer, ensured full backend logic with APIs and database
   
   Rafi Hossain: 
   - Expertise: Frontend (Kotlin, JS, HTML/CSS, etc.), Diagrams
   - Role: Frontend Developer, created UI to match Figma Diagrams
   
   Miadul Haque: 
   - Expertise: Frontend (Kotlin, JS, HTML/CSS, etc.)
   - Role: Frontend Developer, styled the UI of the app, ensured functionality between frontend and backend
   
   Ryan King:
   - Expertise: Frontend (Kotlin, Google Maps, etc.), Diagrams
   - Role: Frontend Developer, created Map UI with Google Maps, connected frontend and backend for full functionality, assisted with diagrams

---
## **About:** 

   TrailBlazer is a mobile application designed for nature and outdoor enthusiasts to find, explore, share, and review nature and hiking trails, parks, and more. 
   
   Key Features: 
   
   Map:
   - Google Maps integration to display the Map UI that highlights all nearby nature spots within a desired radius from the user
   - GPS location tracking in real time when connected to the internet/cellular data
   - Smooth Map interaction and animations
   - Displays Trails with distance, difficulty, ratings, features, elevation, and more
   
   Community:
   - A Community tab where users can post about trails in the form of pictures and descriptions
   - 5-Star rating system, reviews can be sorted by Recent, Popular, and Friends
   - Interaction with other users' posts through liking and commenting
   - Following users allows for interaction specifically with friends and in desired groups
   
   Security:
   - JWT-based token authentication
   - Strong Password requirements
   - Rate-Limiting to prevent brute-force attacks
   - Password hashing
   
   Users:
   - The user can create a profile/login easily
   - User profile shows activity such as trails completed, reviews, and photos
   - The user can manage account settings, password management, etc.
   
   Progress: 
   - The user can create goals in the Progress Tab
   - Shows a weekly goal set by the user in miles, and the percentage completed
   - Displays information such as distance and elevation traveled, time spent, and trails completed
   - Has achievements, so the user can quantify their progress in a gameified way
   
   Offline:
   - The user can download select trails for future use when offline on the trail
   - Displays a map of the trail with all necessary details when offline
   - Downloads the trail onto the user's device

---
##  **User Guide:**

   Installation and Setup:
   
   Prerequisites:
   1. First, have either an Android device running Android 7.0 (API 24) or higher, OR Android Studio for emulation
   2. A Google Account (Gmail)
   3. An Internet connection
   
   Setting Up Environment:
   1. Install Android Studio from developer.android.com, with default settings
   2. Open it and run the setup wizard
   3. Install Android SDK Platform 34 (Android 14) through SDK Manager
   4. Configure API Keys:
      - Create a local.properties file in the android/ directory
      - Add your Google Maps API key:
        ```properties
        MAPS_API_KEY=your_google_maps_api_key_here
        API_BASE_URL=http://10.0.2.2:8000
        
      - To get a Google Maps API key: Go to [Google Cloud Console](https://console.cloud.google.com/)
        - Create a new project or select an existing one
        - Enable "Maps SDK for Android"
        - Create credentials → API Key
        - Restrict the key to Android apps for security
      
      - Set Up Backend:
        - Install Python 3.9 or higher
        - Create a virtual environment:
          ```bash
          python -m venv .venv
          source .venv/bin/activate  # IF USING WINDOWS: .venv\Scripts\activate
   
        - Install dependencies:
          ```bash
          pip install -r requirements.txt
   
        - Create an .env file and add the necessary keys (To get an NPS API key, visit https://www.nps.gov/subjects/developer/get-started.htm :
          ```env 
           JWT_SECRET=your-super-secret-jwt-key-change-this-in-production
           NPS_API_KEY=your_nps_api_key_here 
           DATABASE_URL=sqlite:///./trailblazer.db
   
   5. Initialize Database:
      ```bash
      python populate_database.py
      python nj_trails.py
      python metro_trails.py
   
   6. Start Backend Server:
      ```bash
      uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
   - Verify backend is running:
     - Open browser to http://localhost:8000
     - Should see: {"status":"ok","service":"trailblazer"}
     - API documentation: http://localhost:8000/docs
   
   
   Installing the App:
   1. Install the given APK file
   2. On your Android device, enable "Install from Unknown Sources" or "Allow from this source."
   3. In the Downloads or Files, click on the Trailblazer APK, and click install to begin installation, then open to launch the app
   
   Getting Started:
   After downloading and setting up the application, the user takes these steps:
   1. Click Sign-Up to create an account
   2. Enter a Username (Ex, JohnHikes23), Email Address, and Strong Password (Ex, Password123!)
   3. If the account already exists, sign in with Email and Password
   
   Exploring the App:
   1. View and interact with the Map UI to view nearby parks and trails, clicking on them to see their details.
   2. Post pictures and add a review for a desired trail in the Community Tab, and interact with other users' posts as well.
   3. Update your Progress in the progress tab, setting a weekly goal.
   4. Download any future trails you wish to hike for offline use in the Offline tab.
   5. Edit your profile and account settings in the Profile tab.
   
   Testing Strategies:
   
   Test Case 1: User Registration:
   Open the app, click "Sign Up", and enter the details "TestUser" for display name, "test@example.com" for email, and "TestPass123!" for the password, confirm the password, and click Sign Up. The expected results should be successful account creation, you should be redirected to the home screen, your JWT token should be saved, and you should be automatically logged in. You should be able to log in with this email and password correctly each time. Incorrect Email/Password entries will fail and prompt you to enter the correct credentials. A weak password will not be permitted in signup and will prompt you to create a strong one that fits the guidelines. Rate limiting limits the user to 5 incorrect login attempts per 15 minutes
   
   Test Case 2: Navigation:
   After logging in successfully, the user will grant location permissions, and when viewing the map UI, will see their location and trails within 50km marked with map markers. Ex. if the user's location is Times Square, NYC, 15 spots will load, with the closest being the High Line 0.8km away, and Breakneck Ridge being the farthest at 48.3km away. Tapping on a trail will display its name, difficulty, ratings, reviews, distance, elevation, and features (ie, waterfall, viewpoint, etc.)
   
   Test Case 3: Community:
   The user should be able to click on a trail's details, add a review, from 1-5 stars with a short description, and hit Submit.
   
   Test Case 4: Account Settings:
   The user should be able to go into their account settings in the Profile tab, hit Change Password to change their password to a new, strong password successfully. The user can easily log out to not stay logged in when closing and reopening the app.
   
---
## **Technology Stack:**
   
   Target Devices:
   - Android: Android SDK 33 (Android 14)
   
   Development Environment
   - Android Studio: Ladybug | 2024.1.1 or newer
   - Gradle: 8.13
   - JDK: 17 (Java Development Kit)
   - Kotlin: 2.0.21
   - Python: 3.9+
   - Git: Version control
   
   Required Software and Packages:
   - Stated in detail in the [User Guide](#-User-Guide)
   - Google Account, Google Maps API key, National Parks Service API key, necessary Python libraries

---
## **Features and Technical Implementation:**

   1. Sign In and Authentication:
      
      - Location: app/routers/auth.py
      - 
      - Technologies:
        - JWT (JSON Web Tokens) for stateless authentication
        - PBKDF2-SHA256 password hashing via Passlib
        - HTTP Bearer token scheme
         
      - Implementation:
         ```python
         # Password Hashing
         pwd_context = CryptContext(schemes=["pbkdf2_sha256"], deprecated="auto")
         
         def hash_password(plain: str) -> str:
             return pwd_context.hash(plain)
         
         # JWT Generation
         def create_access_token(*, user_id: int) -> str:
             now = datetime.now(tz=timezone.utc)
             exp = now + timedelta(minutes=settings.JWT_EXPIRE_MINUTES)
             payload = {
                 "sub": str(user_id),
                 "iat": int(now.timestamp()),
                 "exp": int(exp.timestamp()),
             }
             return jwt.encode(payload, settings.JWT_SECRET, algorithm=settings.JWT_ALG)

       - Security Features:
         - Passwords hashed with 29,000 iterations
         - JWT tokens expire after 7 days
         - Rate limiting: 5 failed login attempts per 15 minutes
         - Secure token storage in Android using EncryptedSharedPreferences
         
       - Endpoints:
         - POST /auth/register - Create new user account
         - POST /auth/login - Authenticate and receive JWT token
         - GET /auth/about - Get current user information
         - POST /auth/change-password - Update user password
         - POST /auth/refresh - Refresh JWT token
            
   2. Map UI and Trails:
      - Location:
        - Backend: app/routers/trails.py, app/routers/parks.py
        - Frontend: android/app/src/main/java/com/example/trailblazer/ui/screens/HomeMapScreen.kt
          
      - Technologies:
        - Google Maps SDK for Android
        - Maps Compose library for Jetpack Compose integration
        - Haversine formula for distance calculations
        - SQLAlchemy for database queries
       
      - Features: Real-time location tracking with GPS
        - Dynamic radius-based trail loading (0.1km - 200km)
        - Trail markers with clustering for dense areas
        - Info windows showing trail preview
        - Smooth camera animations
        - Custom marker styling
       
      - Implementation:
        - Nearby Search:
          ```python
          def haversine_km(lat1: float, lon1: float, lat2: float, lon2: float) -> float:
             R = 6371.0  # Earth's radius in km
             dlat = radians(lat2 - lat1)
             dlon = radians(lon2 - lon1)
             a = sin(dlat/2)**2 + cos(radians(lat1)) * cos(radians(lat2)) * sin(dlon/2)**2
             return 2 * R * asin(sqrt(a))
         Map Integration:
          ```kotlin
          GoogleMap(
             modifier = Modifier.fillMaxSize(),
             properties = MapProperties(isMyLocationEnabled = true),
             uiSettings = MapUiSettings(zoomControlsEnabled = true)
          ) {
             trails.forEach { trail ->
                 Marker(
                     state = MarkerState(position = LatLng(trail.lat, trail.lng)),
                     title = trail.name,
                     onClick = { onTrailClick(trail.id); true }
                 )
             }
          }

       - Endpoints:
         - GET /trails?near=lat,lon&radius=50 - Get nearby trails
         - GET /trails/{trail_id} - Get specific trail details
         - GET /trails/search?q=name - Search trails by name
         - GET /parks?near=lat,lon&radius=50 - Get nearby parks

     
   3. Community:
      - Location:
        - Backend: app/routers/posts.py
        - Frontend: android/app/src/main/java/com/example/trailblazer/ui/screens/CommunityScreen.kt
      
      - Technologies:
        - RESTful API design
        - Compose LazyColumn for efficient list rendering
        - Kotlin Coroutines for asynchronous operations
       
      - Features:
        - Create, read, update, delete posts
        - Link posts to specific trails
        - Like and bookmark posts
        - Comment system
        - Filter by Recent/Popular/Friends
        - User profiles with post history
       
      - Database Schema:
        ```python
        class Post(Base):
          __tablename__ = "posts"
          id: int
          user_id: int  # Author
          trail_id: int | None  # Optional trail link
          title: str | None
          body: str  # Post content
          created_at: datetime
          updated_at: datetime

      - Endpoints:
        - POST /posts - Create new post
        - GET /posts?trail_id=&author_id=&limit=50 - List posts with filters
        - GET /posts/{post_id} - Get single post
        - PATCH /posts/{post_id} - Update own post
        - DELETE /posts/{post_id} - Delete own post
     
   4. Profile:
      - Location:
        - Backend: app/routers/profiles.py
        - Frontend: android/app/src/main/java/com/example/trailblazer/ui/screens/ProfileScreen.kt
      
      - Technologies:
        - One-to-one relationship between User and Profile
        - Profile photos stored on file system
        - Aggregate queries for statistics
       
      - Profile Data:
        ```python
        class Profile(Base):
          user_id: int  # Primary key
          avatar_url: str | None
          bio: str | None
          home_state: str | None
          home_lat: float | None
          home_lon: float | None

      - Endpoints:
        - GET /profiles/me - Get current user's profile
        - PATCH /profiles/me - Update profile information
        - GET /profiles/{user_id} - View another user's profile
           
   5. Progress:
      - Location:
        - Backend: app/routers/activities.py
        - Frontend: android/app/src/main/java/com/example/trailblazer/ui/screens/ProgressScreen.kt
      
      - Technologies:
        - SQLAlchemy aggregate functions
        - Timezone-aware datetime handling
        - Compose Canvas for data visualization (future)
       
      - Features:
        - Automatic activity logging from trail completions
        - Manual activity entry with custom metrics
        - Weekly/monthly/yearly statistics
        - Achievement system with badges
        - Activity history with filtering
       
      - Activity Schema:
        ```python
        class Activity(Base):
          id: int
          user_id: int
          trail_id: int
          date: datetime
          distance_km: float | None
          duration_min: int | None
          elevation_gain_m: float | None
          created_at: datetime

      - Stats
        ```python
        q = db.query(
             func.sum(Activity.distance_km),
             func.count(Activity.id),
             func.count(func.distinct(Activity.trail_id)),
             func.avg(Activity.distance_km),
             func.avg(Activity.duration_min)
         ).filter(Activity.user_id == current_user.id)

      - Endpoints
        - POST /trails/{trail_id}/activities - Log activity
        - GET /activities/me?date_from=&date_to= - List activities with filters
        - GET /progress/me - Get aggregated statistics
     
   6. Offline:
      - Location:
        - Backend: app/routers/offline.py
        - Frontend: android/app/src/main/java/com/example/trailblazer/ui/screens/OfflineScreen.kt
          
      - Technologies:
        - Room Database for local caching (planned)
        - File system storage for trail data
        - Background WorkManager for downloads (planned)
       
      - Implementation:
        ```python
        class OfflineDownload(Base):
          id: int
          user_id: int
          trail_id: int
          created_at: datetime

      - Download Process:
        - User selects trail for offline access
        - Trail metadata stored in local database
        - Map tiles cached using Google Maps Prefetch API (planned)
        - Trail data accessible without network connection

      - Storage Management:
        - Track total storage used by offline content
        - Estimated 5MB per trail (map data + metadata)
        - User can remove trails to free space
      
      - Endpoints:
        - POST /offline/trails/{trail_id} - Toggle offline status
        - GET /offline/trails - List downloaded trails


---
## **Packages and APIs:**

   **Packages:**
   
   Backend Packages:
   
   - FastAPI (0.115.2) for a modern, high-performance web framework for building APIs with automatic API documentation.
     - Why: Automatic OpenAPI (Swagger) documentation, built-in data validation with Pydantic, async support for better performance, type hints for better code quality, easy dependency injection system
     - Used: Path operations (GET, POST, PATCH, DELETE), dependency injection for database sessions and authentication, request/response models with Pydantic, automatic JSON serialization
     - Sample Endpoint:
       ```python
       @router.post("/trails/{trail_id}/reviews", response_model=schemas.MsgOut)
        def add_review(
       trail_id: int,
       payload: schemas.ReviewCreate,
       db: Session = Depends(get_db),
       current_user: User = Depends(get_current_user), ):
       # Implementation
   
   - SQLAlchemy (2.0.36) for an SQL toolkit and Object-Relational Mapping (ORM) library.
     - Why: Type-safe ORM with Python 3.10+ syntax, Support for multiple database backends, Efficient query optimization, Relationship handling between tables, Migration support with Alembic
     - Sample:
       ```python
       class Trail(Base):
       __tablename__ = "trails"
       id: Mapped[int] = mapped_column(primary_key=True)
       name: Mapped[str] = mapped_column(String, index=True)
       reviews: Mapped[list["Review"]] = relationship(
           back_populates="trail",
           cascade="all, delete-orphan"
       )
   
   - Pydantic (2.9.2) for data validation using Python type annotations.
     - Why: Automatic validation of request/response data, Type coercion and conversion, Clear error messages for invalid data, JSON Schema generation, Fast performance with Rust backend
       - Sample:
         ```python
         class TrailOut(BaseModel):
         id: int
         name: str
         difficulty: str
         length_km: Optional[float] = None
         lat: Optional[float] = None
         lon: Optional[float] = None
         
         class Config:
           from_attributes = True
   
   
   - Passlib (1.7.4) for a password hashing library with support for multiple algorithms.
     - Why: Secure password hashing with PBKDF2-SHA256, Configurable iteration counts, Automatic salt generation, Future-proof with algorithm migration support
     - Implementation:
       ```python
       pwd_context = CryptContext(
          schemes=["pbkdf2_sha256"],
          deprecated="auto"
       )
      
       hashed = pwd_context.hash("password123")
       verified = pwd_context.verify("password123", hashed)
   
   
   - PyJWT (2.9.0) for JSON Web Token implementation for Python.
     - Why: Standard JWT format for stateless authentication, Cryptographic signing for token integrity, Expiration handling, Support for multiple algorithms (HS256, RS256)
     - Token Generation:
       ```python
       payload = {
          "sub": str(user_id),
          "iat": int(now.timestamp()),
          "exp": int(exp.timestamp()),
       }
       token = jwt.encode(payload, SECRET, algorithm="HS256")
   
   
   Frontend Packages:
   
   - Jetpack Compose (BOM 2024.10.01) for a modern Android UI toolkit using declarative programming
     - Why: Declarative UI reduces boilerplate code, Built-in Material Design 3 components, Reactive state management, Better performance than XML layouts, Easier testing and preview
     - Sample:
       ```kotlin
       @Composable
       fun TrailCard(trail: Trail) {
          Card(
              modifier = Modifier.fillMaxWidth(),
              elevation = CardDefaults.cardElevation(4.dp)
          ) {
              Column(modifier = Modifier.padding(16.dp)) {
                  Text(trail.name, style = MaterialTheme.typography.titleLarge)
                  Text("${trail.distance} mi", style = MaterialTheme.typography.bodyMedium)
              }
          }
       }
   
   - Retrofit (2.11.0) for a type-safe HTTP client for Android
     - Why: Clean API definition with annotations, Automatic JSON conversion, Built-in error handling, Support for synchronous and asynchronous calls, Easy integration with Coroutines
     - Sample:
       ```kotlin
       interface ApiService {
          @GET("trails/")
          suspend fun getTrailsNearby(
              @Query("near") near: String,
              @Query("radius") radiusKm: Double = 50.0
          ): List<TrailDto>
       
          @POST("auth/login")
          suspend fun login(@Body body: LoginRequest): AuthResponse
       }
   
   - OkHttp (4.12.0) for HTTP client for networking operations
     - Why: Connection pooling for efficiency, Transparent GZIP compression, Response caching, Interceptors for authentication, Robust error handling
     - Sample:
       ```kotlin
       OkHttpClient.Builder()
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
   
   - Google Maps SDK (19.0.0) for embedding Google Maps in Android applications
     - Why: Industry-standard mapping solution, Accurate location services, Rich features like markers, polylines, and custom styling, Offline map support, Regular updates and maintenance
     - Sample:
       ```xml
       <meta-data
          android:name="com.google.android.geo.API_KEY"
          android:value="${MAPS_API_KEY}" />
   
   - Kotlin Coroutines (1.8.1) for asynchronous programming in Kotlin
     - Why: Simpler than callbacks or RxJava, Built-in cancellation support, Structured concurrency, Exception handling, Integration with Android lifecycle
     - Sample:
       ```kotlin
       viewModelScope.launch {
          try {
              val trails = ApiClient.service.getTrailsNearby(near, radius)
              _uiState.value = UiState.Success(trails)
          } catch (e: Exception) {
              _uiState.value = UiState.Error(e.message)
          }
       }
   
   **APIs:** 
   - External APIs
     
   - National Parks Service (NPS) API: for importing official park and trail data from the US National Park Service.
     - Authentication: API key required
     - Why: Official park data, Comprehensive information (descriptions, activities, amenities), Regular updates from NPS, Free to use with attribution, Covers all US states and territories
     - Endpoints Used: Getting parks by state: GET /parks?stateCode=NY&limit=500&api_key=YOUR_KEY
     - Parameters: stateCode (string): Two-letter state code (e.g., "NY", "CA"), limit (integer): Max number of results (default: 50, max: 500), api_key (string): Your NPS API key
     - Response:
       ```json
          {
           "data": [
             {
               "id": "77E0D7F0-1942-494A-ACE2-9004D2BDC59E",
               "name": "African Burial Ground",
               "latitude": "40.71452681",
               "longitude": "-74.00447358",
               "states": "NY",
               "description": "...",
               "activities": [...],
               "topics": [...],
               "contacts": {...}
             }
           ],
           "total": "32"
         }
     - Sample:
       ```python
       # app/services/nps.py
         def import_parks_by_states(db: Session, state_code: str, limit: int = 500):
             params = {
              "stateCode": state_code.upper(),
              "limit": limit,
              "api_key": settings.NPS_API_KEY
             }
       
          response = client.get(
              "https://developer.nps.gov/api/v1/parks",
              params=params
          )
          data = response.json().get("data", [])
          
          for item in data:
              park = Park(
                  nps_id=item.get("id"),
                  name=item.get("name"),
                  state=state_code.upper(),
                  lat=float(item["latitude"]),
                  lon=float(item["longitude"])
              )
              db.add(park)
   
   
   
   - Google Maps API: for displaying interactive maps, geocoding, and location services.
     - Authentication: API key required
     - Services Used: Maps SDK for Android: Interactive map display, Custom markers and info windows, User location tracking, Camera animations, Map styling
     - Response:
       ```{
           "results": [{
             "formatted_address": "1600 Amphitheatre Parkway, Mountain View, CA",
             "geometry": {
               "location": {
                 "lat": 37.4224764,
                 "lng": -122.0842499
               }
             }
           }],
           "status": "OK"
         }

---

--------------------------------------------------------------------------------------------------------------------------
