package com.jakubmeysner.legitnik.ui.parking.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakubmeysner.legitnik.data.parking.ParkingLot
import com.jakubmeysner.legitnik.data.parking.ParkingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ParkingLotUiState(
    val parkingLots: List<ParkingLot>? = null,
    val loading: Boolean = true,
    val error: Boolean = false
)


@HiltViewModel
class ParkingLotListViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val parkingRepository: ParkingRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ParkingLotUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadParkingLots()
    }

    private fun loadParkingLots() {
        viewModelScope.launch {
            try {
                _uiState.update { currentUiState -> currentUiState.copy(loading = true) }
                val parkingLots = parkingRepository.getParkingLots()
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        loading = false,
                        parkingLots = parkingLots
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


