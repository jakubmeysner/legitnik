package com.jakubmeysner.legitnik.ui.parking.list

import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.jakubmeysner.legitnik.ui.Route
import kotlinx.serialization.Serializable

@Serializable
object ParkingLotListRoute : Route

fun NavGraphBuilder.parkingLotListDestination(
    navigateToParkingLotMap: () -> Unit,
    onNavigateToParkingLotDetails: (id: String) -> Unit,
    onShowSnackbar: suspend (visuals: SnackbarVisuals) -> SnackbarResult,
) {
    composable<ParkingLotListRoute> {
        ParkingLotListScreen(
            navigateToParkingLotMap = navigateToParkingLotMap,
            onNavigateToParkingLotDetails = onNavigateToParkingLotDetails,
            onShowSnackbar = onShowSnackbar
        )
    }
}
