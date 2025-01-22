package com.jakubmeysner.legitnik

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertAll
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import com.jakubmeysner.legitnik.data.parking.ParkingLotRepositoryImpl
import com.jakubmeysner.legitnik.data.parking.RetrofitModule
import com.jakubmeysner.legitnik.util.ClassSimpleNameLoggingTag
import com.jakubmeysner.legitnik.util.TestTags
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
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


    @Test
    fun openMap() {
        val fab =
            composeRule.onNodeWithContentDescription(composeRule.activity.getString(R.string.parking_lot_list_open_map))
        fab.assertExists()
        fab.performClick()
        val showMyLocationButton =
            composeRule.onNodeWithContentDescription(composeRule.activity.getString(R.string.parking_lot_map_show_my_location))
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
                composeRule.onNodeWithContentDescription(composeRule.activity.getString(R.string.parking_lot_details_image_description))
            parkingImage.assertExists()

            val chart = composeRule.onNodeWithTag(TestTags.FREE_PLACES_CHART)
            chart.assertExists()

            val map = composeRule.onNodeWithTag(TestTags.GOOGLE_MAP)
            map.assertExists()

            val navigateButton =
                composeRule.onNodeWithText(composeRule.activity.getString(R.string.parking_lot_details_navigate))
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
                composeRule.onNodeWithText(composeRule.activity.getString(R.string.parking_lot_details_navigate))
            navigateButton.assert(hasClickAction())
            Espresso.pressBack()
        }
    }
}
