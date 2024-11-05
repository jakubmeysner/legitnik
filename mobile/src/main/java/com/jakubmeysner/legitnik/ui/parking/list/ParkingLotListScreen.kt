package com.jakubmeysner.legitnik.ui.parking.list

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ParkingLotListScreen(
    viewModel: ParkingLotListViewModel = hiltViewModel(),
) {
    Text("Parking Lot List")
}
