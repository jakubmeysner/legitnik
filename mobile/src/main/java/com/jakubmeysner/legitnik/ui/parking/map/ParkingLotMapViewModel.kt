package com.jakubmeysner.legitnik.ui.parking.map

import androidx.lifecycle.ViewModel
import com.jakubmeysner.legitnik.data.parking.ParkingLotRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ParkingLotMapViewModel @Inject constructor(
    private val parkingLotRepository: ParkingLotRepository,
) : ViewModel()
