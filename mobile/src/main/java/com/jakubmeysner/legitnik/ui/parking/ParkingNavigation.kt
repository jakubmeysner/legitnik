package com.jakubmeysner.legitnik.ui.parking

import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.jakubmeysner.legitnik.ui.parking.details.parkingLotDetailsDestination
import com.jakubmeysner.legitnik.ui.parking.list.ParkingLotList
import com.jakubmeysner.legitnik.ui.parking.list.parkingLotListDestination
import kotlinx.serialization.Serializable

@Serializable
object Parking

fun NavGraphBuilder.parkingDestination(
    onNavigateToParkingLotDetails: (id: String) -> Unit,
    onShowSnackbar: suspend (visuals: SnackbarVisuals) -> SnackbarResult,
) {
    navigation<Parking>(startDestination = ParkingLotList) {
        parkingLotListDestination(
            onNavigateToParkingLotDetails = onNavigateToParkingLotDetails,
            onShowSnackbar = onShowSnackbar,
            )
        parkingLotDetailsDestination(onShowSnackbar = onShowSnackbar)
    }
}
