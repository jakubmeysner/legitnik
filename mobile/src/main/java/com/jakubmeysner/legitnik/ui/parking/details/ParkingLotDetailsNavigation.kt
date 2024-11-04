package com.jakubmeysner.legitnik.ui.parking.details

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
data class ParkingLotDetails(val id: String)

fun NavGraphBuilder.parkingLotDetailsDestination() {
    composable<ParkingLotDetails> { backStackEntry ->
        val parkingLotDetails: ParkingLotDetails = backStackEntry.toRoute()
        ParkingLotDetailsScreen(id = parkingLotDetails.id)
    }
}
