package com.jakubmeysner.legitnik.ui.parking.details.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.jakubmeysner.legitnik.ui.theme.LegitnikTheme

class ParkingLotDetailsGeneralCardScreenshotComponentTest {
    @Preview(showBackground = true)
    @Composable
    fun Default() {
        LegitnikTheme {
            ParkingLotDetailsGeneralCard(
                symbol = "PARA",
                name = "Parking A",
                address = "Street 1",
                freePlaces = 100,
                imageLink = "",
            )
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultDarkTheme() {
        LegitnikTheme(darkTheme = true) {
            ParkingLotDetailsGeneralCard(
                symbol = "PARA",
                name = "Parking A",
                address = "Street 1",
                freePlaces = 100,
                imageLink = "",
            )
        }
    }
}
