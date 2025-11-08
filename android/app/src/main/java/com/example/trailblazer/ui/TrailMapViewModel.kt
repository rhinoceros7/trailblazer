package com.example.trailblazer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.trailblazer.net.ApiClient

data class TrailPin(
    val id: String,
    val name: String,
    val lat: Double,
    val lng: Double
)

sealed interface TrailUiState {
    data object Loading : TrailUiState
    data class Ready(val trails: List<TrailPin>) : TrailUiState
    data class Error(val message: String) : TrailUiState
}

class TrailMapViewModel : ViewModel() {

    private val _ui = MutableStateFlow<TrailUiState>(TrailUiState.Loading)
    val ui: StateFlow<TrailUiState> = _ui

    private var inFlight: Job? = null

    init {
        // Initial fetch (NYC, 50km)
        refreshAt(40.7128, -74.0060, 50.0)
    }

    fun refreshAt(centerLat: Double, centerLon: Double, radiusKm: Double) {
        inFlight?.cancel()
        inFlight = viewModelScope.launch {
            try {
                _ui.value = TrailUiState.Loading
                val near = "$centerLat,$centerLon"

                val trails = ApiClient.service.getTrailsNearby(near, radiusKm)
                val parks  = ApiClient.service.getParksNearby(near, radiusKm)

                val trailPins = trails.mapNotNull { t ->
                    val lat = t.lat ?: return@mapNotNull null
                    val lng = t.lng ?: return@mapNotNull null
                    TrailPin(t.id.toString(), t.name, lat, lng)
                }

                if (trailPins.isNotEmpty()) {
                    _ui.value = TrailUiState.Ready(trailPins)
                } else {
                    val parkPins = parks.map { p -> TrailPin(p.id.toString(), p.name, p.lat, p.lng) }
                    _ui.value = TrailUiState.Ready(parkPins)
                }
            } catch (t: Throwable) {
                _ui.value = TrailUiState.Error(t.message ?: "Network error")
            }
        }
    }
}
