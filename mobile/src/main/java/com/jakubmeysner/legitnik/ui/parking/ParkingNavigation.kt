package com.jakubmeysner.legitnik.ui.parking

import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.jakubmeysner.legitnik.ui.Route
import com.jakubmeysner.legitnik.ui.parking.details.parkingLotDetailsDestination
import com.jakubmeysner.legitnik.ui.parking.list.ParkingLotListRoute
import com.jakubmeysner.legitnik.ui.parking.list.parkingLotListDestination
import com.jakubmeysner.legitnik.ui.parking.map.parkingLotMapDestination
import kotlinx.serialization.Serializable

@Serializable
object ParkingRoute : Route

fun NavGraphBuilder.parkingDestination(
    navigateToParkingLotMap: () -> Unit,
    onNavigateToParkingLotDetails: (id: String) -> Unit,
    onShowSnackbar: suspend (visuals: SnackbarVisuals) -> SnackbarResult,
) {
    navigation<ParkingRoute>(startDestination = ParkingLotListRoute) {
        parkingLotListDestination(
            navigateToParkingLotMap = navigateToParkingLotMap,
            onNavigateToParkingLotDetails = onNavigateToParkingLotDetails,
            onShowSnackbar = onShowSnackbar,
        )

        parkingLotMapDestination(
            navigateToParkingLotDetails = onNavigateToParkingLotDetails,
        )

        parkingLotDetailsDestination(onShowSnackbar = onShowSnackbar)
    }
}
