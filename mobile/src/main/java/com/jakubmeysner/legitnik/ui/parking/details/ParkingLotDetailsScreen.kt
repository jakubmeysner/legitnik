package com.jakubmeysner.legitnik.ui.parking.details

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ParkingLotDetailsScreen(
    id: String,
    viewModel: ParkingLotDetailsViewModel = hiltViewModel(),
) {
    Text("Parking Lot Details")
}
