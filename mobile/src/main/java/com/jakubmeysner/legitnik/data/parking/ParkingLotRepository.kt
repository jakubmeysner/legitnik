package com.jakubmeysner.legitnik.data.parking


import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject


interface ParkingLotRepository {
    suspend fun getParkingLots(refresh: Boolean = false):
        List<ParkingLot>

    suspend fun getParkingLot(id: String, refresh: Boolean = false): ParkingLot?
}


class ParkingLotRepositoryImpl @Inject constructor(
    private val parkingLotRemoteDataSource: ParkingLotDataSource,
    private val externalScope: CoroutineScope
) :
    ParkingLotRepository, LoggingInterface {
    private val cachedParkingLotsMutex = Mutex()
    private var cachedParkingLots: List<ParkingLot>? = null

    override suspend fun getParkingLots(refresh: Boolean): List<ParkingLot> {
        val parkingLots = cachedParkingLotsMutex.withLock { this.cachedParkingLots }
        if (parkingLots != null && !refresh) {
            return parkingLots
        }
        return externalScope.async {
            parkingLotRemoteDataSource.getParkingLots().also { networkResult ->
                // Thread-safe write to latestParkingLots.
                cachedParkingLotsMutex.withLock {
                    cachedParkingLots = networkResult
                    Log.d(TAG, "Inside mutex: $networkResult")
                }
            }
        }.await()
    }

    override suspend fun getParkingLot(id: String, refresh: Boolean): ParkingLot? {
        return getParkingLots(refresh = refresh).find { it.id == id }
    }


}
