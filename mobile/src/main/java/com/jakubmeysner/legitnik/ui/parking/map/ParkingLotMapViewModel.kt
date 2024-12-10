package com.jakubmeysner.legitnik.ui.parking.map

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakubmeysner.legitnik.data.parking.ParkingLot
import com.jakubmeysner.legitnik.data.parking.ParkingLotRepository
import com.jakubmeysner.legitnik.util.ClassSimpleNameLoggingTag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ParkingLotMapUiState(
    val parkingLots: List<ParkingLot>? = null,
    val loading: Boolean = false,
)

@HiltViewModel
class ParkingLotMapViewModel @Inject constructor(
    private val parkingLotRepository: ParkingLotRepository,
) : ViewModel(), ClassSimpleNameLoggingTag {
    private val _uiState = MutableStateFlow(ParkingLotMapUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            loadParkingLots()
        }
    }

    private suspend fun loadParkingLots() {
        try {
            _uiState.update {
                it.copy(
                    loading = true,
                )
            }

            val parkingLots = parkingLotRepository.getParkingLots()

            _uiState.update {
                it.copy(
                    parkingLots = parkingLots,
                    loading = false,
                )
            }
        } catch (e: Exception) {
            Log.e(tag, "An exception occurred while loading parking lots", e)
        }
    }
}
