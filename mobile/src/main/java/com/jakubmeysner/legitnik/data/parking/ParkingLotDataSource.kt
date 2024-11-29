package com.jakubmeysner.legitnik.data.parking

import android.util.Log
import com.google.gson.annotations.SerializedName
import com.jakubmeysner.legitnik.util.ClassSimpleNameLoggingTag
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject


data class ParkingLotsResponseApiModel(
    val success: Int,
    @SerializedName("places")
    val parkingLots: List<ParkingLotApiModel>,
)

data class ParkingLotApiModel(
    val id: String,
    @SerializedName("parking_id") val parkingId: String,
    @SerializedName("czas_pomiaru") val measurementTime: String,
    @SerializedName("liczba_miejsc") val freePlaces: Int,
    @SerializedName("symbol") val symbol: String,
    @SerializedName("type") val type: String,
    @SerializedName("nazwa") val name: String,
    @SerializedName("open_hour") val openHour: String,
    @SerializedName("close_hour") val closeHour: String,
    @SerializedName("places") val places: String,
    @SerializedName("geo_lan") val geoLan: Double,
    @SerializedName("geo_lat") val geoLat: Double,
    @SerializedName("photo") val photo: String,
    @SerializedName("aktywny") val active: String,
    @SerializedName("show_park") val showPark: String,
    @SerializedName("lp") val lp: String,
    @SerializedName("address") val address: String,
    @SerializedName("trend") val trend: String,
)

data class ParkingLotFreePlacesHistoryResponseApiModel(
    val success: Int,
    @SerializedName("slots")
    val parkingLotFreePlacesHistory: ParkingLotFreePlacesHistoryDataApiModel,
)

data class ParkingLotFreePlacesHistoryDataApiModel(
    @SerializedName("labels")
    val hours: List<String>,
    @SerializedName("data")
    val freePlaces: List<Int>,
)


data class ParkingLot(
    val id: String,
    val freePlaces: Int,
    val name: String,
    val symbol: String,
    val photo: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    var freePlacesHistory: List<Pair<String, Int>> = emptyList(),
)


interface ParkingLotDataSource {
    suspend fun getParkingLots():
        List<ParkingLot>

    suspend fun getParkingLotFreePlacesHistory(id: String): List<Pair<String, Int>>
}

class ParkingLotRemoteDataSource @Inject constructor(
    private val parkingLotApi: ParkingLotApi,
    private val ioDispatcher: CoroutineDispatcher,
) :
    ParkingLotDataSource, ClassSimpleNameLoggingTag {
    override suspend fun getParkingLots(): List<ParkingLot> {
        return withContext(ioDispatcher) {
            Log.d(tag, "Trying to fetch parking lots data")
            val parkingList = parkingLotApi.getParkingLots().parkingLots
            Log.d(tag, "Parking data fetched")

            val parkingLots = parkingList.map {
                ParkingLot(
                    it.id,
                    it.freePlaces,
                    it.name,
                    it.symbol,
                    it.photo,
                    it.address,
                    it.geoLat,
                    it.geoLan,
                    freePlacesHistory = getParkingLotFreePlacesHistory(it.id)
                )
            }

            Log.d(tag, "result = $parkingLots")
            parkingLots
        }

    }

    override suspend fun getParkingLotFreePlacesHistory(id: String): List<Pair<String, Int>> {
        return withContext(ioDispatcher) {
            var result = emptyList<Pair<String, Int>>()
            try {
                val body = ParkingLotDetailsFreePlacesHistoryBody(o = "get_today_chart", i = id)
                Log.d(tag, "Fetching free places history data from parking (id=$id)")
                val freePlacesHistory =
                    parkingLotApi.getParkingLotDetails(body).parkingLotFreePlacesHistory
                Log.d(tag, "Data fetched")
                result = freePlacesHistory.hours.zip(freePlacesHistory.freePlaces)
                Log.d(tag, "Data = $result")
            } catch (e: Exception) {
                Log.d(tag, "Exception = $e")
            }
            result
        }
    }
}
