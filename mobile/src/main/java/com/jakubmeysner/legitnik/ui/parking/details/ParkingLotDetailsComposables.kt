package com.jakubmeysner.legitnik.ui.parking.details

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.jakubmeysner.legitnik.R
import com.jakubmeysner.legitnik.ui.theme.LegitnikTheme

@Composable
fun ParkingGeneralCard(
    shortName: String,
    name: String,
    address: String,
    freePlaces: Int,
    imageLink: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = shortName)
        Text(text = name)
        AsyncImage(
            model = imageLink,
            contentDescription = stringResource(R.string.parking_details_image_description)
        )
        Text(text = stringResource(R.string.parking_details_address))
        Text(text = address)
        Text(text = stringResource(R.string.parking_details_free_places))
        Text(text = freePlaces.toString())
    }
}

@Preview
@Composable
fun ParkingGeneralCardPreview() {
    LegitnikTheme {
        ParkingGeneralCard(
            "WRO",
            "Parking Wrońskiego",
            "Hoene-Wrońskiego 10, 50-376 Wrocław",
            420,
            "https://iparking.pwr.edu.pl/images/photos/wro_4x.jpg"
        )
    }
}
