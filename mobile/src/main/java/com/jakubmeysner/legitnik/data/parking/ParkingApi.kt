package com.jakubmeysner.legitnik.data.parking

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

data class ParkingLotDetailsFreePlacesHistoryBody(
    val o: String,
    val i: String,
)

interface ParkingLotApi {
    @Headers("Referer: https://iparking.pwr.edu.pl/", "X-Requested-With: XMLHttpRequest")
    @POST("/modules/iparking/scripts/ipk_operations.php")
    suspend fun getParkingLots(@Body body: Map<String, String> = mapOf("o" to "get_parks")):
        ParkingLotsResponseApiModel

    @Headers("Referer: https://iparking.pwr.edu.pl/", "X-Requested-With: XMLHttpRequest")
    @POST("/modules/iparking/scripts/ipk_operations.php")
    suspend fun getParkingLotDetails(
        @Body body: ParkingLotDetailsFreePlacesHistoryBody,
    ): ParkingLotFreePlacesHistoryResponseApiModel
}
