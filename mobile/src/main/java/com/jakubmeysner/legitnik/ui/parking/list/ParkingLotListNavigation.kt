package com.jakubmeysner.legitnik.ui.parking.list

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object ParkingLotList

fun NavGraphBuilder.parkingLotListDestination(onNavigateToParkingLotDetails: (id: String) -> Unit) {
    composable<ParkingLotList> {
        ParkingLotListScreen(onNavigateToParkingLotDetails = onNavigateToParkingLotDetails)
    }
}
