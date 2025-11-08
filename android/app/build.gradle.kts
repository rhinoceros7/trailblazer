import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

android.buildFeatures.buildConfig = true

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.example.trailblazer"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.trailblazer"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        // Read local.properties (Kotlin DSL needs the 'providers' arg)
        val props = gradleLocalProperties(rootDir, providers)

        // API base -> BuildConfig.BASE_URL
        val apiBase = props.getProperty("API_BASE_URL") ?: "http://10.0.2.2:8000"
        buildConfigField("String", "BASE_URL", "\"$apiBase\"")

        // Maps key -> AndroidManifest placeholder
        manifestPlaceholders["MAPS_API_KEY"] = props.getProperty("MAPS_API_KEY") ?: ""
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures { compose = true }
    buildTypes {
        release { isMinifyEnabled = false }
        debug { isMinifyEnabled = false }
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // Compose BOM & core UI
    implementation(platform("androidx.compose:compose-bom:2024.10.01"))
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation(libs.core.ktx)
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")

    // Material Components (View system) for manifest theme
    implementation("com.google.android.material:material:1.12.0")

    // Google Maps
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.maps.android:maps-compose:6.4.0")

    // Retrofit + OkHttp + JSON
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // KotlinX serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("com.squareup.retrofit2:converter-kotlinx-serialization:2.11.0")

    // Lifecycle / coroutines
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
}
