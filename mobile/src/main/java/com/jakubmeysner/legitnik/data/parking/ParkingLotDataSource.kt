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


data class ParkingLot(
    val id: String,
    val freePlaces: Int,
    val name: String,
    val symbol: String,
    val photo: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    /*it should be moved to different dataclass (ParkingLotDetails or smth)
     to avoid unnecessary data passing (copying)
     hardcoded for development time*/
    val chartData: Map<String, Int> = mapOf(
        //opening/closing hours differs in parking lots
        "6:00" to (0..300).random(),
        "7:00" to (0..300).random(),
        "8:00" to (0..300).random(),
        "9:00" to (0..300).random(),
        "10:00" to (0..300).random(),
        "11:00" to (0..300).random(),
        "12:00" to (0..300).random(),
        "13:00" to (0..300).random(),
        "14:00" to (0..300).random(),
        "15:00" to (0..300).random(),
        "16:00" to (0..300).random(),
        "17:00" to (0..300).random(),
        "18:00" to (0..300).random(),
        "19:00" to (0..300).random(),
        "20:00" to (0..300).random(),
        "21:00" to (0..300).random(),
        "22:00" to (0..300).random()
    ),
)


interface ParkingLotDataSource {
    suspend fun getParkingLots():
        List<ParkingLot>
}

class ParkingLotRemoteDataSource @Inject constructor(
    private val parkingLotApi: ParkingLotApi,
    private val ioDispatcher: CoroutineDispatcher,
) :
    ParkingLotDataSource, ClassSimpleNameLoggingTag {
    override suspend fun getParkingLots(): List<ParkingLot> {
        return withContext(ioDispatcher) {
            Log.d(tag, "Trying to fetch data")
            val parkingList = parkingLotApi.getParkingLots().parkingLots
            Log.d(tag, "Parking data fetched")
            val result = parkingList.map {
                ParkingLot(
                    it.id,
                    it.freePlaces,
                    it.name,
                    it.symbol,
                    it.photo,
                    it.address,
                    it.geoLat,
                    it.geoLan
                )
            }
            Log.d(tag, "result = $result")
            result
        }

    }
}
