package com.jakubmeysner.legitnik.ui.parking.details

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class ParkingLotDetails(val id: String)

fun NavGraphBuilder.parkingLotDetailsDestination() {
    composable<ParkingLotDetails> {
        ParkingLotDetailsScreen()
    }
}
