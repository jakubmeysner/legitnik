package com.jakubmeysner.legitnik

import android.content.Context
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertAll
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import com.jakubmeysner.legitnik.data.parking.ParkingLotApi
import com.jakubmeysner.legitnik.data.parking.ParkingLotRepositoryImpl
import com.jakubmeysner.legitnik.data.parking.RetrofitModule
import com.jakubmeysner.legitnik.data.parking.TestResponses
import com.jakubmeysner.legitnik.util.ClassSimpleNameLoggingTag
import com.jakubmeysner.legitnik.util.TestTags
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.test.runTest
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(RetrofitModule::class)
class ParkingLotsApplicationTest : ClassSimpleNameLoggingTag {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Inject
    lateinit var parkingLotRepositoryImpl: ParkingLotRepositoryImpl

    private val context = ApplicationProvider.getApplicationContext<Context>()


    @Test
    fun openMap() {
        val fab =
            composeRule.onNodeWithContentDescription(context.getString(R.string.parking_lot_list_open_map))
        fab.assertExists()
        fab.performClick()
        val showMyLocationButton =
            composeRule.onNodeWithContentDescription(context.getString(R.string.parking_lot_map_show_my_location))
        showMyLocationButton.assertExists()
        showMyLocationButton.performClick()
    }

    @Test
    fun navigateBetweenParkingLotsDetails() {
        val parkingLotItems = composeRule.onAllNodesWithTag(TestTags.PARKING_ITEM)
        assert(parkingLotItems.fetchSemanticsNodes().isNotEmpty())
        parkingLotItems.assertAll(hasClickAction())

        for (i in 0..<parkingLotItems.fetchSemanticsNodes().size) {
            parkingLotItems[i].performClick()
            val parkingImage =
                composeRule.onNodeWithContentDescription(context.getString(R.string.parking_lot_details_image_description))
            parkingImage.assertExists()

            val chart = composeRule.onNodeWithTag(TestTags.FREE_PLACES_CHART)
            chart.assertExists()

            val map = composeRule.onNodeWithTag(TestTags.GOOGLE_MAP)
            map.assertExists()

            val navigateButton =
                composeRule.onNodeWithText(context.getString(R.string.parking_lot_details_navigate))
            navigateButton.assert(hasClickAction())
            Espresso.pressBack()
        }
    }

    @Test
    fun navigateBetweenParkingLotsDetails_CheckData() = runTest {
        val parkingLots = parkingLotRepositoryImpl.getParkingLots()
        parkingLots.forEach {
            val parkingLotItem = composeRule.onNodeWithText(it.symbol)
            parkingLotItem.assertExists()
            parkingLotItem.performClick()

            composeRule.onNodeWithText(it.address.trim()).assertExists()
            composeRule.onNodeWithText(it.freePlaces.toString()).assertExists()

            val chart = composeRule.onNodeWithTag(TestTags.FREE_PLACES_CHART)
            chart.assertExists()

            val map = composeRule.onNodeWithTag(TestTags.GOOGLE_MAP)
            map.assertExists()

            val navigateButton =
                composeRule.onNodeWithText(context.getString(R.string.parking_lot_details_navigate))
            navigateButton.assert(hasClickAction())
            Espresso.pressBack()
        }
    }

    @Module
    @InstallIn(SingletonComponent::class)
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
}
