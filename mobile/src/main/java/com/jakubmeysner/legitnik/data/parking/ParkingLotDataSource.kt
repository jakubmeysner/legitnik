package com.jakubmeysner.legitnik.data.parking

import com.google.gson.annotations.SerializedName
import javax.inject.Inject


data class ParkingLotsResponseApiModel(
    val success: Int,
    @SerializedName("places")
    val parkingLots: List<ParkingLotApiModel>
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
    @SerializedName("geo_lan") val geoLan: String,
    @SerializedName("geo_lat") val geoLat: String,
    @SerializedName("photo") val photo: String,
    @SerializedName("aktywny") val active: String,
    @SerializedName("show_park") val showPark: String,
    @SerializedName("lp") val lp: String,
    @SerializedName("address") val address: String,
    @SerializedName("trend") val trend: String
)


data class ParkingLot(
    val id: String,
    val freePlaces: Int,
    val symbol: String,
    val photo: String,
    val address: String,
)


interface ParkingLotDataSource {
    suspend fun getParkingLots():
        List<ParkingLot>
}

class ParkingLotRemoteDataSource @Inject constructor(private val parkingLotApi: ParkingLotApiService) :
    ParkingLotDataSource {
    override suspend fun getParkingLots(): List<ParkingLot> {
        val parkingList = parkingLotApi.getParkingLots().parkingLots
        return parkingList.map {
            ParkingLot(
                it.id,
                it.freePlaces,
                it.symbol,
                it.photo,
                it.address
            )
        }
    }
}
