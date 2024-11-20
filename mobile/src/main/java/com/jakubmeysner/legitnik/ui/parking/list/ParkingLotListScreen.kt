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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import coil3.compose.AsyncImage
import com.jakubmeysner.legitnik.data.parking.ParkingLot

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkingLotListScreen(
    viewModel: ParkingLotListViewModel = hiltViewModel(),
    onNavigateToParkingLotDetails: (id: String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pullToRefreshState = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false) }

    val onRefresh: () -> Unit = {
        isRefreshing = true
        viewModel.loadParkingLots(forceRefresh = true)
    }

    LaunchedEffect(uiState.loading) {
        if (!uiState.loading) {
            isRefreshing = false
        }
    }

    @Composable
    fun ParkingItem(parkingLot: ParkingLot) {
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
                .clickable { onNavigateToParkingLotDetails(parkingLot.id) },
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
                    text = "${parkingLot.freePlaces} wolnych miejsc",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            AsyncImage(
                model = "https://iparking.pwr.edu.pl${parkingLot.photo}",
                contentDescription = "Parking Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(90.dp)
            )
        }
    }

    Scaffold { paddingValues ->
        PullToRefreshBox(
            modifier = Modifier.padding(paddingValues),
            state = pullToRefreshState,
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
        ) {
            when {
                uiState.loading -> LoadingScreen()
                uiState.error -> ErrorScreen()
                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    items(uiState.parkingLots?.size ?: 0) { index ->
                        ParkingItem(parkingLot = uiState.parkingLots!![index])
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}


@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "Loading...")
    }
}

@Composable
fun ErrorScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Wystąpił błąd. Sprawdź swoje połączenie i spróbuj ponownie później.",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}
