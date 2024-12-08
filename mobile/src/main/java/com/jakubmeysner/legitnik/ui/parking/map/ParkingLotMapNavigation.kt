package com.jakubmeysner.legitnik.ui.parking.map

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object ParkingLotMapRoute

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
