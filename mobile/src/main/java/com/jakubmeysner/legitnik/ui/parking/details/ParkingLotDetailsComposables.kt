package com.jakubmeysner.legitnik.ui.parking.details

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.jakubmeysner.legitnik.R
import com.jakubmeysner.legitnik.ui.theme.PurpleGrey40

@Composable
fun ParkingGeneralCard(
    symbol: String,
    name: String,
    address: String,
    freePlaces: Int,
    imageLink: String
) {
    Card(border = BorderStroke(1.dp, PurpleGrey40)) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .fillMaxWidth()
        ) {
            Text(text = symbol, fontWeight = FontWeight(500), fontSize = 20.sp)
            Text(text = name)
            AsyncImage(
                model = imageLink,
                contentDescription = stringResource(R.string.parking_lot_details_image_description),
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(16.dp))

            )
            Text(
                text = stringResource(R.string.parking_lot_details_address),
                fontWeight = FontWeight(500),
                fontSize = 20.sp
            )
            Text(text = address)
            Text(
                text = stringResource(R.string.parking_lot_details_free_places),
                fontWeight = FontWeight(500),
                fontSize = 20.sp
            )
            Text(text = freePlaces.toString())
        }

    }
}

@Composable
fun ParkingDetailsCard() {
    Card(border = BorderStroke(1.dp, PurpleGrey40)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)

        ) {
            Text(
                text = stringResource(R.string.parking_lot_details_history),
                fontWeight = FontWeight(500),
                fontSize = 20.sp
            )
            Image(
                painter = painterResource(R.drawable.stonks),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = stringResource(R.string.parking_lot_details_map),
                fontWeight = FontWeight(500),
                fontSize = 20.sp
            )

            Image(
                painter = painterResource(R.drawable.google_maps),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth()
            )

            Button(onClick = {}, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        ImageVector.vectorResource(R.drawable.navigation_variant_outline),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.parking_lot_details_navigate),

                        )
                }
            }
        }
    }
}


@Composable
fun SnackbarScreen(
    messageIds: List<Int>,
    uiState: ParkingLotDetailsUiState,
    showSnackbar: (Int) -> Unit,
    setSnackbarShown: (Int) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.error,
                    dismissActionContentColor = MaterialTheme.colorScheme.error,
                    snackbarData = data
                )
            }
        },
        modifier = modifier.fillMaxSize(),
    ) { scaffoldPadding ->

        if (uiState.parkingLotDetails == null) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding),
            ) {
                Button(
                    onClick = {/*TODO refresh action*/ },
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(72.dp),
                    shape = CircleShape
                ) {
                    Icon(
                        ImageVector.vectorResource(R.drawable.reload),
                        contentDescription = null,
                        modifier = Modifier.size(56.dp)
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .verticalScroll(rememberScrollState())
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ParkingGeneralCard(
                    uiState.parkingLotDetails.symbol,
                    uiState.parkingLotDetails.name,
                    uiState.parkingLotDetails.address,
                    uiState.parkingLotDetails.freePlaces,
                    "https://iparking.pwr.edu.pl${uiState.parkingLotDetails.photo}"
                )

                ParkingDetailsCard()

            }
        }
    }

    if (messageIds.isNotEmpty()) {
        val messageId = messageIds.first()
        val message = stringResource(id = messageId)

        LaunchedEffect(key1 = messageId) {
            snackbarHostState.showSnackbar(
                message = message,
                withDismissAction = true,
                duration = SnackbarDuration.Long
            )
            setSnackbarShown(messageId)
        }
    }
}


