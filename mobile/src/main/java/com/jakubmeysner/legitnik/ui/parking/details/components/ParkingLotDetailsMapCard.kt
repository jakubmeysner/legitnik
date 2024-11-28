package com.jakubmeysner.legitnik.ui.parking.details.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.jakubmeysner.legitnik.R
import com.jakubmeysner.legitnik.util.showMap

@Composable
fun ParkingLotDetailsMapCard(latitude: Double, longitude: Double, name: String) {
    val context = LocalContext.current
    val parkingGeo = LatLng(latitude, longitude)
    val parkingMarkerState = rememberMarkerState(position = parkingGeo)
    val cameraPosition = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(parkingGeo, 12f)
    }

    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.parking_lot_details_map),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            GoogleMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                cameraPositionState = cameraPosition,
            ) {
                Marker(state = parkingMarkerState, title = name)
            }


            Button(onClick = {
                showMap(
                    "geo:0,0?q=$latitude,$longitude".toUri(), context
                )
            }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
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
