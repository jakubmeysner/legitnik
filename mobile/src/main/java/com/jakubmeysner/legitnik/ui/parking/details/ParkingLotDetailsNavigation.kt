package com.jakubmeysner.legitnik.ui.parking.details

import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class ParkingLotDetailsRoute(val id: String)

fun NavGraphBuilder.parkingLotDetailsDestination(onShowSnackbar: suspend (visuals: SnackbarVisuals) -> SnackbarResult) {
    composable<ParkingLotDetailsRoute> {
        ParkingLotDetailsScreen(onShowSnackbar = onShowSnackbar)
    }
}

fun NavController.navigateToParkingLotDetails(id: String) {
    navigate(route = ParkingLotDetailsRoute(id = id))
}
