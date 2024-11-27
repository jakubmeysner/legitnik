package com.jakubmeysner.legitnik.ui.parking.list

import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object ParkingLotList

fun NavGraphBuilder.parkingLotListDestination(
    onNavigateToParkingLotDetails: (id: String) -> Unit,
    onShowSnackbar: suspend (visuals: SnackbarVisuals) -> SnackbarResult
) {
    composable<ParkingLotList> {
        ParkingLotListScreen(onNavigateToParkingLotDetails = onNavigateToParkingLotDetails, onShowSnackbar = onShowSnackbar)
    }
}
