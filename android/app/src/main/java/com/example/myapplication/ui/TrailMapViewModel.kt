package com.example.trailblazer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _ui.value = TrailUiState.Loading
            try {
                // TODO: wire real API later
                val items = apiGetTrailsFallback()
                _ui.value = TrailUiState.Ready(items)
            } catch (t: Throwable) {
                _ui.value = TrailUiState.Error(t.message ?: "Unknown error")
            }
        }
    }

    // Fallback/demo
    private suspend fun apiGetTrailsFallback(): List<TrailPin> {
        return listOf(
            TrailPin("1", "Sample Trailhead", 40.7128, -74.0060),
            TrailPin("2", "Ridge Lookout", 40.7306, -73.9866),
            TrailPin("3", "Creek Crossing", 40.7580, -73.9855)
        )
    }
}
