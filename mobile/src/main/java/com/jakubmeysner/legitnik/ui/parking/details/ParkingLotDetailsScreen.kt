package com.jakubmeysner.legitnik.ui.parking.details

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ParkingLotDetailsScreen(
    viewModel: ParkingLotDetailsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    //TO REFACTOR: make parkingLotDetails not nullable
    if (uiState.error) {
        Text(text = "Oops, something went wrong :(")
    } else {
        ParkingGeneralCard(
            uiState.parkingLotDetails?.symbol ?: "",
            uiState.parkingLotDetails?.name ?: "",
            uiState.parkingLotDetails?.address ?: "",
            uiState.parkingLotDetails?.freePlaces ?: 0,
            "https://iparking.pwr.edu.pl${uiState.parkingLotDetails?.photo}"
        )
    }

}


