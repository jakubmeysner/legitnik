package com.jakubmeysner.legitnik.ui.parking.list

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle


@Composable
fun ParkingLotListScreen(
    viewModel: ParkingLotListViewModel = hiltViewModel(),
    onNavigateToParkingLotDetails: (id: String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column {
        uiState.parkingLots?.forEach {
            Button(onClick = { onNavigateToParkingLotDetails(it.id) }) {
                Text(
                    it.symbol
                )
            }
        }

    }
}
