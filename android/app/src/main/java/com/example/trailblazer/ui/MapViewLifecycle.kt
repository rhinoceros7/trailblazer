package com.example.trailblazer.ui

import android.content.Context
import android.os.Bundle
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.MapView

@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }

    DisposableEffect(Unit) {
        mapView.onCreate(Bundle())
        mapView.onStart()
        mapView.onResume()
        onDispose {
            mapView.onPause()
            mapView.onStop()
            mapView.onDestroy()
        }
    }
    return mapView
}