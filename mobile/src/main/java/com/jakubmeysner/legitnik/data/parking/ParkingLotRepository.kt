package com.jakubmeysner.legitnik.data.parking

import javax.inject.Inject


interface ParkingRepository {
    suspend fun getParkingLots():
        List<ParkingLot>
}


class ParkingRepositoryImpl @Inject constructor(private val parkingLotRemoteDataSource: ParkingLotDataSource) :
    ParkingRepository {
    override suspend fun getParkingLots(): List<ParkingLot> {
        return parkingLotRemoteDataSource.getParkingLots()
    }

}
