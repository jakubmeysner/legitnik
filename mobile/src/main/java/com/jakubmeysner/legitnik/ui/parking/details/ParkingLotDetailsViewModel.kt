package com.jakubmeysner.legitnik.ui.parking.details

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.jakubmeysner.legitnik.data.parking.LoggingInterface
import com.jakubmeysner.legitnik.data.parking.ParkingLot
import com.jakubmeysner.legitnik.data.parking.ParkingLotRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ParkingLotDetailsUiState(
    val parkingLotDetails: ParkingLot? = null,
    val loading: Boolean = false,
    val error: Boolean = false
)

@HiltViewModel
class ParkingLotDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val parkingLotRepository: ParkingLotRepository
) : ViewModel(), LoggingInterface {
    private val route = savedStateHandle.toRoute<ParkingLotDetails>()

    private val _uiState = MutableStateFlow(ParkingLotDetailsUiState())
    val uiState = _uiState.asStateFlow()


    init {
        loadParkingLotDetails()
        Log.d(TAG, "id = ${route.id}")
    }

    private fun loadParkingLotDetails() {
        viewModelScope.launch {
            try {
                _uiState.update { currentUiState -> currentUiState.copy(loading = true) }
                val parkingLotDetails = parkingLotRepository.getParkingLot(route.id)
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        loading = false,
                        parkingLotDetails = parkingLotDetails,
                        error = parkingLotDetails == null,
                    )
                }

            } catch (e: Exception) {
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        error = true,
                        loading = false
                    )
                }
            }
        }
    }
}
