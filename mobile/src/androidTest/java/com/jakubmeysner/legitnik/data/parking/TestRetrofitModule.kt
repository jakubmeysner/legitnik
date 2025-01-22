package com.jakubmeysner.legitnik.data.parking

import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TestResponses {
    val PARKING_LOT_DATA_RESPONSE = """
        {
    "success": 0,
    "places": [
        {
            "id": "4",
            "parking_id": "4",
            "czas_pomiaru": "2024-11-20 01:36:02",
            "liczba_miejsc": "145",
            "symbol": "WRO",
            "type": "O",
            "nazwa": "Parking Wrońskiego",
            "open_hour": "06:00:00",
            "close_hour": "22:00:00",
            "places": "207",
            "geo_lan": "17.055564",
            "geo_lat": "51.108964",
            "photo": "/images/photos/wro.jpg",
            "aktywny": "1",
            "show_park": "1",
            "lp": "2",
            "address": "Hoene-Wrońskiego 10, 50-376 Wrocław",
            "trend": "0"
        },
        {
            "id": "2",
            "parking_id": "2",
            "czas_pomiaru": "2024-11-20 01:36:02",
            "liczba_miejsc": "24",
            "symbol": "C13",
            "type": "O",
            "nazwa": "Polinka",
            "open_hour": null,
            "close_hour": null,
            "places": "54",
            "geo_lan": "17.058468",
            "geo_lat": "51.107393",
            "photo": "/images/photos/c13.jpg\r\n",
            "aktywny": "1",
            "show_park": "1",
            "lp": "3",
            "address": "wybrzeże Stanisława Wyspiańskiego 25, 50-370 Wrocław\r\n",
            "trend": "0"
        },
        {
            "id": "5",
            "parking_id": "5",
            "czas_pomiaru": "2024-11-20 01:36:02",
            "liczba_miejsc": "54",
            "symbol": "D20",
            "type": "O",
            "nazwa": "D20 - D21",
            "open_hour": "06:00:00",
            "close_hour": "22:30:00",
            "places": "76",
            "geo_lan": "17.0596779",
            "geo_lat": "51.1100504",
            "photo": "/images/photos/biblotech.jpg",
            "aktywny": "1",
            "show_park": "1",
            "lp": "5",
            "address": "Janiszewskiego 8, 50-372 Wrocław\r\n",
            "trend": "0"
        },
        {
            "id": "6",
            "parking_id": "6",
            "czas_pomiaru": "2024-11-20 01:36:02",
            "liczba_miejsc": "245",
            "symbol": "GEO-L",
            "type": "O",
            "nazwa": "GEO LO1 Geocentrum",
            "open_hour": "06:00:00",
            "close_hour": "22:30:00",
            "places": "301",
            "geo_lan": "17.0553342",
            "geo_lat": "51.1041626",
            "photo": "/images/photos/geo-l01.jpg",
            "aktywny": "1",
            "show_park": "1",
            "lp": "6",
            "address": "Na Grobli 15, 50-421 Wrocław\r\n",
            "trend": "0"
        },
        {
            "id": "7",
            "parking_id": "7",
            "czas_pomiaru": "2024-11-20 01:36:02",
            "liczba_miejsc": "83",
            "symbol": "E01",
            "type": "O",
            "nazwa": "Architektura",
            "open_hour": "06:00:00",
            "close_hour": "22:30:00",
            "places": "75",
            "geo_lan": "17.0541671",
            "geo_lat": "51.1187359",
            "photo": "/images/photos/eo1.jpg",
            "aktywny": "1",
            "show_park": "1",
            "lp": "7",
            "address": "Bolesława Prusa 53/55, 50-317 Wrocław\r\n",
            "trend": "0"
        }
    ]
}
    """.trimIndent()
    val PARKING_LOT_FREE_PLACES_HISTORY_RESPONSE = """{
    "success": 0,
    "slots": {
        "labels": [
            "05:00",
            "05:10",
            "05:20",
            "05:30",
            "05:40",
            "05:50",
            "06:10",
            "06:20",
            "06:30",
            "06:40",
            "06:50",
            "07:00",
            "07:10",
            "07:20",
            "07:30",
            "07:50",
            "08:10",
            "08:20",
            "08:30",
            "08:40",
            "08:50",
            "09:00",
            "09:10",
            "09:20",
            "09:30",
            "09:40",
            "09:50",
            "10:00",
            "10:10",
            "10:40",
            "10:50",
            "11:00",
            "11:10",
            "11:20",
            "11:30",
            "11:50",
            "12:00",
            "12:10",
            "12:30",
            "12:40",
            "13:00",
            "13:10",
            "13:20",
            "13:30",
            "13:40",
            "13:50",
            "14:00",
            "14:20",
            "14:30",
            "14:40",
            "14:50",
            "15:10",
            "15:20",
            "15:50",
            "16:00",
            "16:10",
            "16:30",
            "16:40",
            "16:50",
            "17:00",
            "17:10",
            "17:20",
            "17:30",
            "17:40",
            "17:50",
            "18:00",
            "18:10",
            "18:20",
            "18:30",
            "18:40",
            "18:50",
            "19:10",
            "19:20",
            "19:30",
            "19:40",
            "19:50",
            "20:10",
            "20:20",
            "20:30",
            "20:40",
            "20:50"
        ],
        "data": [
            "202",
            "202",
            "202",
            "202",
            "198",
            "197",
            "195",
            "192",
            "187",
            "178",
            "171",
            "149",
            "114",
            "79",
            "39",
            "2",
            "0",
            "0",
            "0",
            "0",
            "0",
            "0",
            "3",
            "0",
            "0",
            "0",
            "1",
            "3",
            "1",
            "0",
            "0",
            "0",
            "0",
            "0",
            "0",
            "0",
            "4",
            "2",
            "0",
            "4",
            "5",
            "0",
            "0",
            "2",
            "6",
            "6",
            "12",
            "20",
            "26",
            "26",
            "45",
            "66",
            "72",
            "104",
            "107",
            "110",
            "114",
            "115",
            "118",
            "124",
            "126",
            "126",
            "131",
            "134",
            "136",
            "143",
            "148",
            "148",
            "149",
            "149",
            "148",
            "152",
            "153",
            "153",
            "155",
            "156",
            "157",
            "157",
            "157",
            "158",
            "160"
        ]
    }
}""".trimIndent()
}


@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [RetrofitModule::class])
object TestRetrofitModule {
    @Provides
    fun provideFakeApiService(): ParkingLotApi {
        val mockInterceptor = Interceptor { chain ->
            val request = chain.request()

            val buffer = Buffer()
            request.body?.writeTo(buffer)
            val bodyString = buffer.readUtf8()

            val responseString = when {
                bodyString.contains("\"o\":\"get_parks\"") -> TestResponses.PARKING_LOT_DATA_RESPONSE

                bodyString.contains("\"o\":\"get_today_chart\"") -> TestResponses.PARKING_LOT_FREE_PLACES_HISTORY_RESPONSE

                else -> """{"status": "error", "message": "Unknown operation"}"""
            }

            Response.Builder()
                .code(200)
                .message("OK")
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .body(
                    responseString
                        .toResponseBody("application/json".toMediaTypeOrNull())
                )
                .addHeader("content-type", "application/json")
                .build()
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(mockInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://iparking.pwr.edu.pl")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ParkingLotApi::class.java)
    }
}
