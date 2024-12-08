package com.jakubmeysner.legitnik.ui.parking.map

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ParkingLotMapScreen(
    viewModel: ParkingLotMapViewModel = hiltViewModel(),
) {
    Text("Map")
}
