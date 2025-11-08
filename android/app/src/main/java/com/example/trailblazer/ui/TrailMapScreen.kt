package com.example.trailblazer.ui

import android.annotation.SuppressLint
import androidx.compose.runtime.*
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.coroutines.*
import kotlin.math.*

@Composable
fun TrailMapScreen(vm: TrailMapViewModel = viewModel()) {
    val state by vm.ui.collectAsState()
    val mapView = rememberMapViewWithLifecycle()

    var googleMap by remember { mutableStateOf<GoogleMap?>(null) }
    var userMoved by remember { mutableStateOf(false) }
    var didInitialCamera by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    var debounceJob by remember { mutableStateOf<Job?>(null) }

    AndroidView(
        factory = {
            mapView.apply {
                getMapAsync { gMap ->
                    googleMap = gMap
                    gMap.uiSettings.isZoomControlsEnabled = true
                    gMap.uiSettings.isMapToolbarEnabled = true
                    try { gMap.isMyLocationEnabled = true } catch (_: SecurityException) {}

                    gMap.setOnCameraMoveStartedListener { reason ->
                        if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                            userMoved = true
                        }
                    }

                    gMap.setOnCameraIdleListener {
                        val center = gMap.cameraPosition.target
                        val bounds = gMap.projection.visibleRegion.latLngBounds
                        val radiusKm = visibleBoundsRadiusKm(bounds)
                            .coerceIn(1.0, 100.0) // don’t DDOS yourself; cap it

                        // Debounce rapid drags/zooms
                        debounceJob?.cancel()
                        debounceJob = scope.launch {
                            delay(250)
                            vm.refreshAt(center.latitude, center.longitude, radiusKm)
                        }
                    }
                }
            }
        },
        update = {
            val gMap = googleMap ?: return@AndroidView

            when (val ui = state) {
                is TrailUiState.Ready -> {
                    gMap.clear()
                    val pins = ui.trails
                    pins.forEach { pin ->
                        gMap.addMarker(
                            MarkerOptions()
                                .position(LatLng(pin.lat, pin.lng))
                                .title(pin.name)
                        )
                    }

                    // Recenter once on first data load if the user hasn't moved
                    if (pins.isNotEmpty() && !didInitialCamera && !userMoved) {
                        val first = pins.first()
                        gMap.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(LatLng(first.lat, first.lng), 11f)
                        )
                        didInitialCamera = true
                    }
                }
                else -> Unit
            }
        }
    )
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

        val target = pins.firstOrNull()
            ?.let { LatLng(it.lat, it.lng) }
            ?: LatLng(40.7128, -74.0060) // default
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(target, 11f))
    }
}

/** Approximate radius (km) from the center to the farthest visible corner. */
private fun visibleBoundsRadiusKm(bounds: LatLngBounds): Double {
    val c = bounds.center
    val candidates = listOf(bounds.northeast, bounds.southwest,
        LatLng(bounds.northeast.latitude, bounds.southwest.longitude),
        LatLng(bounds.southwest.latitude, bounds.northeast.longitude))
    val maxMeters = candidates.maxOf { haversineMeters(c.latitude, c.longitude, it.latitude, it.longitude) }
    return maxMeters / 1000.0
}

private fun haversineMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val R = 6371000.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2).pow(2.0) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2.0)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return R * c
}
