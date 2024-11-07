package com.jakubmeysner.legitnik.ui.parking.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.jakubmeysner.legitnik.data.parking.ParkingLot
import com.jakubmeysner.legitnik.data.parking.ParkingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


data class ParkingLotDetailsUiState(
    val parkingLotDetails: ParkingLot? = null,
    val loading: Boolean = false
)

@HiltViewModel
class ParkingLotDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle, parkingLotRepository: ParkingRepository
) : ViewModel() {
    private val route = savedStateHandle.toRoute<ParkingLotDetails>()
    private val _uiState = MutableStateFlow(ParkingLotDetailsUiState())
    val uiState = _uiState.asStateFlow()



}
