package com.jakubmeysner.legitnik.ui.parking.details.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.jakubmeysner.legitnik.R

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
