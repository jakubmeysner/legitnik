package com.jakubmeysner.legitnik.ui.parking.details

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ParkingLotDetailsScreen(
    viewModel: ParkingLotDetailsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val messageIds by viewModel.messageIds.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    SnackbarScreen(
        messageIds = messageIds,
        uiState = uiState,
        showSnackbar = viewModel::showUserMessage,
        setSnackbarShown = viewModel::removeShownMessage,
        snackbarHostState = snackbarHostState
    )
}




