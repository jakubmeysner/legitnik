package com.jakubmeysner.legitnik.ui.parking.map.components

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.ComposeMapColorScheme
import com.google.maps.android.compose.DefaultMapProperties
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import com.jakubmeysner.legitnik.R
import com.jakubmeysner.legitnik.data.parking.ParkingLot

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ParkingLotMapMap(
    parkingLots: List<ParkingLot>,
    navigateToParkingLotDetails: (id: String) -> Unit,
) {
    val context = LocalContext.current

    val isPlayServicesAvailable = remember(context) {
        GoogleApiAvailability.getInstance()
            .isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS
    }

    if (!isPlayServicesAvailable) {
        return
    }

    val locationPermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
    )

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

    Scaffold(
        floatingActionButton = {
            if (locationPermissionsState.permissions.none { it.status.isGranted }) {
                Box(
                    modifier = Modifier.padding(bottom = 96.dp),
                ) {
                    FloatingActionButton(
                        onClick = {
                            locationPermissionsState.launchMultiplePermissionRequest()
                        }
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.mdi_crosshairs_gps),
                            contentDescription = stringResource(
                                R.string.parking_lot_map_show_my_location
                            ),
                        )
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = DefaultMapProperties.copy(
                isMyLocationEnabled = locationPermissionsState.permissions.any { it.status.isGranted }
            ),
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

}
