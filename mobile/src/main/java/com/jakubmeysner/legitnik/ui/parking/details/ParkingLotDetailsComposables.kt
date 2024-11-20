package com.jakubmeysner.legitnik.ui.parking.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.jakubmeysner.legitnik.R
import com.jakubmeysner.legitnik.ui.theme.Typography

@Composable
fun ParkingLotDetailsGeneralCard(
    symbol: String,
    name: String,
    address: String,
    freePlaces: Int,
    imageLink: String,
) {
    Card {
        Column(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(all = 16.dp),
                verticalArrangement = Arrangement.spacedBy(space = 4.dp),
            ) {
                Text(
                    text = symbol,
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            AsyncImage(
                model = imageLink,
                contentDescription = stringResource(R.string.parking_lot_details_image_description),
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.padding(all = 16.dp),
                verticalArrangement = Arrangement.spacedBy(space = 16.dp),
            ) {
                for ((key, value) in listOf(
                    Pair(R.string.parking_lot_details_address, address),
                    Pair(R.string.parking_lot_details_free_places, freePlaces.toString()),
                )) {
                    Column(verticalArrangement = Arrangement.spacedBy(space = 4.dp)) {
                        Text(
                            text = stringResource(key),
                            style = MaterialTheme.typography.labelLarge,
                        )

                        Text(
                            text = value.trim(),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ParkingLotDetailsHistoryCard() {
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)

        ) {
            Text(
                text = stringResource(R.string.parking_lot_details_history),
                style = Typography.labelLarge
            )
//            Image(
//                painter = painterResource(R.drawable.stonks),
//                contentDescription = null,
//                modifier = Modifier.fillMaxWidth()
//            )

            Text(
                text = stringResource(R.string.parking_lot_details_map),
                style = Typography.labelLarge
            )

//            Image(
//                painter = painterResource(R.drawable.google_maps),
//                contentDescription = null,
//                modifier = Modifier.fillMaxWidth()
//            )

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
fun ParkingLotDetailsDataUnavailable() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
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
}
