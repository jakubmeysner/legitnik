package com.jakubmeysner.legitnik.ui.parking.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakubmeysner.legitnik.data.parking.ParkingLot
import com.jakubmeysner.legitnik.data.parking.ParkingLotRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ParkingLotListUiState(
    val parkingLots: List<ParkingLot>? = null,
    val loading: Boolean = true,
    val error: Boolean = false,
    val savedParkingIds: List<String> = emptyList(),
)

@HiltViewModel
class ParkingLotListViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val parkingLotRepository: ParkingLotRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ParkingLotListUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadParkingLots()
    }

    fun loadParkingLots(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            try {
                _uiState.update { currentUiState ->
                    currentUiState.copy(loading = true, error = false)
                }
                val parkingLots = parkingLotRepository.getParkingLots(forceRefresh)
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        loading = false,
                        parkingLots = parkingLots,
                        error = false
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
