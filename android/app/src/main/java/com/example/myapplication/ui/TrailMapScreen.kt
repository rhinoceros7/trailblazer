package com.example.trailblazer.ui

import android.Manifest
import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

@Composable
fun TrailMapScreen(vm: TrailMapViewModel = viewModel()) {
    val state by vm.ui.collectAsState()

    val mapView = rememberMapViewWithLifecycle()

    // Runtime permission launcher
    val permLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { /* no-op; we’ll re-evaluate on recompose */ }
    )

    LaunchedEffect(Unit) {
        permLauncher.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))
    }

    Box(Modifier.fillMaxSize()) {
        AndroidView(factory = { mapView }) { mv ->
            mv.getMapAsync { googleMap ->
                renderMap(googleMap, state)
            }
        }

        when (state) {
            is TrailUiState.Loading -> {
                CircularProgressIndicator()
            }
            is TrailUiState.Error -> {
                Snackbar { Text((state as TrailUiState.Error).message) }
            }
            else -> Unit
        }
    }
}

@SuppressLint("MissingPermission")
private fun renderMap(googleMap: GoogleMap, ui: TrailUiState) {
    // Enable gestures, UI, etc.
    googleMap.uiSettings.isZoomControlsEnabled = true
    googleMap.uiSettings.isMapToolbarEnabled = true

    // Try to enable My Location if permission was granted (safe no-op otherwise)
    try { googleMap.isMyLocationEnabled = true } catch (_: SecurityException) {}

    if (ui is TrailUiState.Ready) {
        googleMap.clear()
        val pins = ui.trails

        // Drop markers
        pins.forEach { pin ->
            googleMap.addMarker(
                MarkerOptions()
                    .position(LatLng(pin.lat, pin.lng))
                    .title(pin.name)
            )
        }

        // Camera: center on first pin or a sensible default
        val target = pins.firstOrNull()?.let { LatLng(it.lat, it.lng) }
            ?: LatLng(40.7128, -74.0060) // NYC default
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(target, 11f))
    }
}
