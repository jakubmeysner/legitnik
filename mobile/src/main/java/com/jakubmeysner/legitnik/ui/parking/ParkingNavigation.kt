package com.jakubmeysner.legitnik.ui.parking

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.jakubmeysner.legitnik.ui.parking.details.parkingLotDetailsDestination
import com.jakubmeysner.legitnik.ui.parking.list.ParkingLotList
import com.jakubmeysner.legitnik.ui.parking.list.parkingLotListDestination
import kotlinx.serialization.Serializable

@Serializable
object Parking

fun NavGraphBuilder.parkingDestination() {
    navigation<Parking>(startDestination = ParkingLotList) {
        parkingLotListDestination()
        parkingLotDetailsDestination()
    }
}
