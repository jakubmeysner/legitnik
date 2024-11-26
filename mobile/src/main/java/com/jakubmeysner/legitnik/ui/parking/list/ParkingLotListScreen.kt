package com.jakubmeysner.legitnik.ui.parking.list

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import coil3.compose.AsyncImage
import com.jakubmeysner.legitnik.data.parking.ParkingLot
import com.jakubmeysner.legitnik.R
import com.jakubmeysner.legitnik.util.SnackbarVisualsData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkingLotListScreen(
    viewModel: ParkingLotListViewModel = hiltViewModel(),
    onNavigateToParkingLotDetails: (id: String) -> Unit,
    onShowSnackbar: suspend (visuals: SnackbarVisuals) -> SnackbarResult
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pullToRefreshState = rememberPullToRefreshState()
    val errorMessage = stringResource(R.string.parking_lot_list_snack_error)

    val onRefresh: () -> Unit = {
        viewModel.loadParkingLots(forceRefresh = true)
    }

    LaunchedEffect(uiState.error) {
        if (uiState.error && !uiState.parkingLots.isNullOrEmpty()) {
            onShowSnackbar(
                SnackbarVisualsData(
                    message = errorMessage,
                    duration = SnackbarDuration.Long,
                    withDismissAction = true
                )
            )
        }
    }

    if (uiState.parkingLots.isNullOrEmpty() && uiState.loading) {
        LoadingIndicator()
    } else {
        PullToRefreshBox(
            modifier = Modifier,
            state = pullToRefreshState,
            isRefreshing = uiState.loading,
            onRefresh = onRefresh,
        ) {
            when {
                uiState.parkingLots.isNullOrEmpty() && uiState.error -> ErrorIndicator(onRetry = onRefresh)
                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val parkingLots = uiState.parkingLots

                    if (parkingLots != null) {
                        items(parkingLots.size) { index ->
                            ParkingItem(
                                parkingLot = parkingLots[index],
                                onClick = onNavigateToParkingLotDetails
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ParkingItem(
    parkingLot: ParkingLot,
    onClick: (String) -> Unit
) {
    val backgroundColor = if (parkingLot.freePlaces == 0) {
        MaterialTheme.colorScheme.surfaceContainerHighest
    } else {
        MaterialTheme.colorScheme.surfaceContainerLow
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .background(backgroundColor, shape = RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, MaterialTheme.colorScheme.surfaceContainerHighest, shape = RoundedCornerShape(16.dp))
            .clickable { onClick(parkingLot.id) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = parkingLot.symbol,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.parking_lot_list_free_places, parkingLot.freePlaces),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        AsyncImage(
            model = "https://iparking.pwr.edu.pl${parkingLot.photo}",
            contentDescription = "Parking Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .aspectRatio(1f)
        )
    }
}


@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.width(64.dp)
        )
    }
}

@Composable
fun ErrorIndicator(
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.parking_lot_list_error),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            androidx.compose.material3.FilledTonalButton(
                onClick = onRetry,
            ) {
                Text(text = stringResource(R.string.parking_lot_list_refresh))
            }
        }
    }
}

