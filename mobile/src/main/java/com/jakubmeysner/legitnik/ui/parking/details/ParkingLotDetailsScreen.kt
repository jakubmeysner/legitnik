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
import com.jakubmeysner.legitnik.util.SnackbarVisualsData

@Composable
fun ParkingLotDetailsScreen(
    viewModel: ParkingLotDetailsViewModel = hiltViewModel(),
    onShowSnackbar: suspend (visuals: SnackbarVisuals) -> SnackbarResult,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.parkingLotDetails == null) {
        ParkingLotDetailsDataUnavailable()
    } else {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ParkingLotDetailsGeneralCard(
                uiState.parkingLotDetails!!.symbol,
                uiState.parkingLotDetails!!.name,
                uiState.parkingLotDetails!!.address,
                uiState.parkingLotDetails!!.freePlaces,
                "https://iparking.pwr.edu.pl${uiState.parkingLotDetails!!.photo}"
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




