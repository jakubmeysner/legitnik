package com.jakubmeysner.legitnik.ui.parking.list

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object ParkingLotList

fun NavGraphBuilder.parkingLotListDestination() {
    composable<ParkingLotList> {
        ParkingLotListScreen()
    }
}
