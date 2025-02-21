package com.jakubmeysner.legitnik.ui.parking.list

import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.jakubmeysner.legitnik.data.parking.ParkingLot
import com.jakubmeysner.legitnik.ui.ScreenPreviewScaffold
import com.jakubmeysner.legitnik.ui.parking.ParkingRoute

@Composable
@Preview(showBackground = true)
fun ParkingLotListScreenPreview() {
    ScreenPreviewScaffold(
        selectedTopLevelRoute = ParkingRoute,
    ) {
        ParkingLotListScreen(
            uiState = UI_STATE,
            loadParkingLots = {},
            navigateToParkingLotMap = {},
            onNavigateToParkingLotDetails = {},
            onShowSnackbar = {
                SnackbarResult.Dismissed
            },
        )
    }
}

private val UI_STATE = ParkingLotListUiState(
    parkingLots = listOf(
        ParkingLot(
            id = "4",
            freePlaces = 176,
            name = "Parking Wrońskiego",
            symbol = "WRO",
            photo = "/images/photos/wro.jpg",
            address = "Hoene-Wrońskiego 10, 50-376 Wroclaw",
            latitude = 51.108964,
            longitude = 17.055564,
            freePlacesHistory = null,
        ),
        ParkingLot(
            id = "2",
            freePlaces = 47,
            name = "Polinka",
            symbol = "C13",
            photo = "/images/photos/c13.jpg",
            address = "wybrzeże Stanisława Wyspiańskiego 25, 50-370 Wrocław",
            latitude = 51.107393,
            longitude = 17.058468,
            freePlacesHistory = null,
        )
    ),
    loading = false,
)
