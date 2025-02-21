package com.jakubmeysner.legitnik.ui.parking.map

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.jakubmeysner.legitnik.ui.Route
import kotlinx.serialization.Serializable

@Serializable
object ParkingLotMapRoute : Route

fun NavGraphBuilder.parkingLotMapDestination(
    navigateToParkingLotDetails: (id: String) -> Unit,
) {
    composable<ParkingLotMapRoute> {
        ParkingLotMapScreen(
            navigateToParkingLotDetails = navigateToParkingLotDetails,
        )
    }
}

fun NavController.navigateToParkingLotMap() {
    navigate(route = ParkingLotMapRoute)
}
