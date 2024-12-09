package com.jakubmeysner.legitnik.ui.parking.details

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.jakubmeysner.legitnik.R
import com.jakubmeysner.legitnik.data.parking.ParkingLot
import com.jakubmeysner.legitnik.data.parking.ParkingLotRepository
import com.jakubmeysner.legitnik.util.ClassSimpleNameLoggingTag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ParkingLotDetailsUiState(
    val parkingLotDetails: ParkingLot? = null,
    val loading: Boolean = false,
    val error: Boolean = false,
    val messageIds: List<Int> = emptyList(),
)

@HiltViewModel
class ParkingLotDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val parkingLotRepository: ParkingLotRepository,
) : ViewModel(), ClassSimpleNameLoggingTag {
    private val route = savedStateHandle.toRoute<ParkingLotDetailsRoute>()
    private val _uiState = MutableStateFlow(ParkingLotDetailsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadParkingLotDetails()
    }

    fun loadParkingLotDetails(reload: Boolean = false) {
        viewModelScope.launch {
            try {
                _uiState.update { currentUiState -> currentUiState.copy(loading = true) }
                val parkingLotDetails = parkingLotRepository.getParkingLot(route.id, reload)
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        loading = false,
                        parkingLotDetails = parkingLotDetails,
                        error = parkingLotDetails == null,
                    )
                }

                if (parkingLotDetails == null) {
                    showViewModelMessage(R.string.parking_lot_details_data_unavailable_message)
                } else if (parkingLotDetails.freePlacesHistory.isNullOrEmpty()) {
                    showViewModelMessage(R.string.parking_lot_details_chart_data_unavailable_message)
                }

            } catch (e: Exception) {
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        error = true,
                        loading = false
                    )
                }
                showViewModelMessage(R.string.parking_lot_details_error_message)
            }
        }
    }

    private fun showViewModelMessage(messageId: Int) {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                messageIds = currentUiState.messageIds + messageId
            )
        }
    }

    //function to set message in reaction of user interaction
    fun showUserMessage(@StringRes messageId: Int) {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                messageIds = currentUiState.messageIds + messageId
            )
        }
    }

    fun removeShownMessage(@StringRes messageId: Int) {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                messageIds = currentUiState.messageIds.filter { it != messageId }
            )
        }
    }
}
