package com.jakubmeysner.legitnik.ui.parking.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jakubmeysner.legitnik.ui.parking.details.components.ParkingLotDetailsChartCard
import com.jakubmeysner.legitnik.ui.parking.details.components.ParkingLotDetailsDataUnavailable
import com.jakubmeysner.legitnik.ui.parking.details.components.ParkingLotDetailsGeneralCard
import com.jakubmeysner.legitnik.ui.parking.details.components.ParkingLotDetailsMapCard
import com.jakubmeysner.legitnik.util.SnackbarVisualsData

@Composable
fun ParkingLotDetailsScreen(
    viewModel: ParkingLotDetailsViewModel = hiltViewModel(),
    onShowSnackbar: suspend (visuals: SnackbarVisuals) -> SnackbarResult,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val parkingLotDetails = uiState.parkingLotDetails

    val onReload: () -> Unit = {
        viewModel.loadParkingLotDetails(reload = true)
    }

    if (parkingLotDetails == null) {
        ParkingLotDetailsDataUnavailable(onReload)
    } else {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ParkingLotDetailsGeneralCard(
                parkingLotDetails.symbol,
                parkingLotDetails.name,
                parkingLotDetails.address,
                parkingLotDetails.freePlaces,
                "https://iparking.pwr.edu.pl${parkingLotDetails.photo}"
            )

            ParkingLotDetailsChartCard(parkingLotDetails.freePlacesHistory, onReload)

            ParkingLotDetailsMapCard(
                parkingLotDetails.latitude,
                parkingLotDetails.longitude,
                parkingLotDetails.name
            )
        }
    }

    if (uiState.messageIds.isNotEmpty()) {
        val messageId = uiState.messageIds.first()
        val message = stringResource(id = messageId)

        LaunchedEffect(key1 = messageId) {
            onShowSnackbar(
                SnackbarVisualsData(
                    message = message,
                    duration = SnackbarDuration.Long,
                    withDismissAction = true
                )
            )
            viewModel.removeShownMessage(messageId)
        }
    }

}




