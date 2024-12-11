package com.jakubmeysner.legitnik.ui.parking.map

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jakubmeysner.legitnik.ui.parking.map.components.ParkingLotMapMap

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ParkingLotMapScreen(
    viewModel: ParkingLotMapViewModel = hiltViewModel(),
    navigateToParkingLotDetails: (id: String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val parkingLots = uiState.parkingLots

    if (parkingLots != null) {
        ParkingLotMapMap(
            parkingLots = parkingLots,
            navigateToParkingLotDetails = navigateToParkingLotDetails,
        )
    } else if (uiState.loading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(size = 64.dp),
            )
        }
    }
}
