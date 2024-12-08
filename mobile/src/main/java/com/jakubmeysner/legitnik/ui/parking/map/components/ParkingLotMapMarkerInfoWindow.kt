package com.jakubmeysner.legitnik.ui.parking.map.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.rememberMarkerState
import com.jakubmeysner.legitnik.R
import com.jakubmeysner.legitnik.data.parking.ParkingLot

@Composable
fun ParkingLotMapMarkerInfoWindow(
    parkingLot: ParkingLot,
    navigateToParkingLotDetails: (id: String) -> Unit,
) {
    val state = rememberMarkerState(
        position = LatLng(
            parkingLot.latitude,
            parkingLot.longitude
        ),
    )

    val icon = remember(parkingLot.freePlaces) {
        BitmapDescriptorFactory.defaultMarker(
            when {
                parkingLot.freePlaces >= 100 -> BitmapDescriptorFactory.HUE_CYAN
                parkingLot.freePlaces >= 10 -> BitmapDescriptorFactory.HUE_GREEN
                parkingLot.freePlaces > 0 -> BitmapDescriptorFactory.HUE_YELLOW
                else -> BitmapDescriptorFactory.HUE_RED
            }
        )
    }

    MarkerInfoWindow(
        state = state,
        icon = icon,
        title = parkingLot.symbol,
        onInfoWindowClick = {
            navigateToParkingLotDetails(parkingLot.id)
        },
    ) {
        OutlinedCard {
            Column(
                modifier = Modifier.padding(all = 12.dp),
                verticalArrangement = Arrangement.spacedBy(space = 4.dp),
            ) {
                Text(
                    text = parkingLot.symbol,
                    style = MaterialTheme.typography.titleLarge,
                )

                Text(
                    text = pluralStringResource(
                        id = R.plurals.parking_lot_list_free_places,
                        count = parkingLot.freePlaces,
                        parkingLot.freePlaces
                    ),
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}
