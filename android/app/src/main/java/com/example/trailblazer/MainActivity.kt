// FILE: android/app/src/main/java/com/example/trailblazer/MainActivity.kt
package com.example.trailblazer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.trailblazer.ui.screens.CommunityScreen
import com.example.trailblazer.ui.screens.HomeMapScreen
import com.example.trailblazer.ui.screens.LoginScreen
import com.example.trailblazer.ui.screens.OfflineScreen
import com.example.trailblazer.ui.screens.ProfileScreen
import com.example.trailblazer.ui.screens.ProgressScreen
import com.example.trailblazer.ui.screens.RegisterScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MaterialTheme {
                Surface {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    // Simple string-based navigation for this project
    var currentScreen by remember { mutableStateOf("Login") }

    when (currentScreen) {
        "Login" -> LoginScreen(
            onLoginSuccess = {
                // User logged in successfully – go to main map
                currentScreen = "Map"
            },
            onNavigateToRegister = {
                currentScreen = "Register"
            }
        )

        "Register" -> RegisterScreen(
            onRegisterClick = { name, email, pass ->
                // RegisterScreen already calls register + login + sets token.
                // Once it reports success, go to the main map.
                println("Registered user: $name ($email)")
                currentScreen = "Map"
            },
            onSignInClick = {
                // From "Already have an account? Sign In"
                currentScreen = "Login"
            },
            onGoogleClick = {
                println("Google sign in clicked (not implemented)")
            },
            onAppleClick = {
                println("Apple sign in clicked (not implemented)")
            }
        )

        "Map" -> HomeMapScreen(
            onTrailClick = { trailId ->
                println("Trail clicked: $trailId")
                // In a fuller app, you could navigate to a TrailDetail screen here.
            },
            onNavigateToScreen = { screen ->
                // Bottom nav uses these names: "Map", "Community", "Profile", "Progress", "Offline"
                currentScreen = screen
            }
        )

        "Community" -> CommunityScreen(
            onNavigate = { screen ->
                currentScreen = screen
            }
        )

        "Profile" -> ProfileScreen(
            onNavigate = { screen ->
                currentScreen = screen
            },
            onEditProfile = {
                println("Edit profile clicked (not implemented)")
            }
        )

        "Progress" -> ProgressScreen(
            onNavigate = { screen ->
                currentScreen = screen
            }
        )

        "Offline" -> OfflineScreen(
            onNavigate = { screen ->
                currentScreen = screen
            }
        )
    }
}
