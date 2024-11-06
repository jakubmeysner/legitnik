package com.jakubmeysner.legitnik.data.parking

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface ParkingLotApiService {
    @Headers("Referer: https://iparking.pwr.edu.pl/", "X-Requested-With: XMLHttpRequest")
    @POST("modules/iparking/scripts/ipk_operations.php")
    suspend fun getParkingLots(@Body body: Map<String, String> = mapOf("o" to "get_parks")):
        ParkingLotsResponseApiModel

    @GET("parking_lots/{id}")
    suspend fun getParkingLotDetails(@Path("id") id: Int): ParkingLotsResponseApiModel
}
