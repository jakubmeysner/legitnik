package com.jakubmeysner.legitnik.ui.parking.map.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.ComposeMapColorScheme
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import com.jakubmeysner.legitnik.data.parking.ParkingLot

@Composable
fun ParkingLotMapMap(
    parkingLots: List<ParkingLot>,
    navigateToParkingLotDetails: (id: String) -> Unit,
) {
    val cameraPositionState = rememberCameraPositionState()
    var cameraMoved by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!cameraMoved) {
            cameraPositionState.move(
                update = CameraUpdateFactory.newLatLngBounds(
                    LatLngBounds.builder().apply {
                        for (parkingLot in parkingLots) {
                            include(LatLng(parkingLot.latitude, parkingLot.longitude))
                        }
                    }.build(),
                    400
                ),
            )

            cameraMoved = true
        }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        mapColorScheme = ComposeMapColorScheme.FOLLOW_SYSTEM,
    ) {
        for (parkingLot in parkingLots) {
            key(parkingLot.id) {
                ParkingLotMapMarker(
                    parkingLot = parkingLot,
                    navigateToParkingLotDetails = navigateToParkingLotDetails,
                )
            }
        }
    }
}
