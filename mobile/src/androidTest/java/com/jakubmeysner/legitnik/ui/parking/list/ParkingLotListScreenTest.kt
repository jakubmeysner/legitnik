package com.jakubmeysner.legitnik.ui.parking.list

import android.content.Context
import androidx.compose.material3.SnackbarResult
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jakubmeysner.legitnik.R
import com.jakubmeysner.legitnik.data.parking.ParkingLot
import com.jakubmeysner.legitnik.data.parking.ParkingLotRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.lifecycle.SavedStateHandle

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class ParkingLotListScreenTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var context: Context

    private class FakeParkingLotRepository : ParkingLotRepository {
        private val testParkingLots = listOf(
            ParkingLot(
                id = "1",
                symbol = "P1",
                freePlaces = 10,
                photo = "/photo1.jpg",
                name = "Parking 1",
                address = "Address 1",
                latitude = 51.0,
                longitude = 17.0,
                freePlacesHistory = listOf(
                    Pair("8:00", 15),
                    Pair("9:00", 10),
                    Pair("10:00", 5)
                )
            ),
            ParkingLot(
                id = "2",
                symbol = "P2",
                freePlaces = 0,
                photo = "/photo2.jpg",
                name = "Parking 2",
                address = "Address 2",
                latitude = 51.1,
                longitude = 17.1,
                freePlacesHistory = emptyList()
            )
        )

        override suspend fun getParkingLots(refresh: Boolean): List<ParkingLot> {
            return testParkingLots
        }

        override suspend fun getParkingLot(id: String, refresh: Boolean): ParkingLot? {
            return testParkingLots.find { it.id == id }
        }
    }

    private fun createTestViewModel(): ParkingLotListViewModel {
        return ParkingLotListViewModel(
            savedStateHandle = SavedStateHandle(),
            parkingLotRepository = FakeParkingLotRepository()
        )
    }

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun displayParkingLots_whenDataLoaded() {
        val testViewModel = createTestViewModel()

        composeTestRule.setContent {
            context = LocalContext.current
            ParkingLotListScreen(
                viewModel = testViewModel,
                navigateToParkingLotMap = {},
                onNavigateToParkingLotDetails = {},
                onShowSnackbar = { SnackbarResult.Dismissed }
            )
        }

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("P1")
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("P1").assertIsDisplayed()
        composeTestRule.onNodeWithText("P2").assertIsDisplayed()
    }

    @Test
    fun displayErrorScreen_whenErrorOccurs() {
        val errorViewModel = ParkingLotListViewModel(
            savedStateHandle = SavedStateHandle(),
            parkingLotRepository = object : ParkingLotRepository {
                override suspend fun getParkingLots(refresh: Boolean): List<ParkingLot> {
                    throw Exception("Test error")
                }

                override suspend fun getParkingLot(id: String, refresh: Boolean): ParkingLot? {
                    throw Exception("Test error")
                }
            }
        )

        composeTestRule.setContent {
            context = LocalContext.current
            ParkingLotListScreen(
                viewModel = errorViewModel,
                navigateToParkingLotMap = {},
                onNavigateToParkingLotDetails = {},
                onShowSnackbar = { SnackbarResult.Dismissed }
            )
        }

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText(context.getString(R.string.parking_lot_list_error))
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText(context.getString(R.string.parking_lot_list_error))
            .assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.parking_lot_list_refresh))
            .assertIsDisplayed()
    }

    @Test
    fun showMapButton_whenPlayServicesAvailable() {
        val testViewModel = createTestViewModel()

        composeTestRule.setContent {
            context = LocalContext.current
            ParkingLotListScreen(
                viewModel = testViewModel,
                navigateToParkingLotMap = {},
                onNavigateToParkingLotDetails = {},
                onShowSnackbar = { SnackbarResult.Dismissed }
            )
        }

        composeTestRule.onNodeWithContentDescription(
            context.getString(R.string.parking_lot_list_open_map)
        ).assertExists()
    }

    @Test
    fun completeUserFlow_navigateToParkingDetails() {
        var navigatedToId: String? = null
        val testViewModel = ParkingLotListViewModel(
            savedStateHandle = SavedStateHandle(),
            parkingLotRepository = FakeParkingLotRepository()
        )

        composeTestRule.setContent {
            context = LocalContext.current
            ParkingLotListScreen(
                viewModel = testViewModel,
                navigateToParkingLotMap = {},
                onNavigateToParkingLotDetails = { id -> navigatedToId = id },
                onShowSnackbar = { SnackbarResult.Dismissed }
            )
        }

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("P1")
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("P1").assertIsDisplayed()
        composeTestRule.onNodeWithText("P2").assertIsDisplayed()

        composeTestRule.onNodeWithText(
            context.resources.getQuantityString(R.plurals.parking_lot_list_free_places, 10, 10)
        ).assertIsDisplayed()
        composeTestRule.onNodeWithText(
            context.resources.getQuantityString(R.plurals.parking_lot_list_free_places, 0, 0)
        ).assertIsDisplayed()

        composeTestRule.onNodeWithText("P2").performClick()

        assertEquals("2", navigatedToId)
    }

    @Test
    fun errorHandling_showErrorAndRetry() {
        var retryCount = 0
        val errorRepository = object : ParkingLotRepository {
            override suspend fun getParkingLots(refresh: Boolean): List<ParkingLot> {
                if (retryCount == 0) {
                    retryCount++
                    throw Exception("Network error")
                }
                return listOf(
                    ParkingLot(
                        id = "1",
                        symbol = "P1",
                        freePlaces = 10,
                        photo = "/photo1.jpg",
                        name = "Parking 1",
                        address = "Address 1",
                        latitude = 51.0,
                        longitude = 17.0,
                        freePlacesHistory = emptyList()
                    )
                )
            }

            override suspend fun getParkingLot(id: String, refresh: Boolean): ParkingLot? {
                throw Exception("Network error")
            }
        }

        val viewModel = ParkingLotListViewModel(
            savedStateHandle = SavedStateHandle(),
            parkingLotRepository = errorRepository
        )

        composeTestRule.setContent {
            context = LocalContext.current
            ParkingLotListScreen(
                viewModel = viewModel,
                navigateToParkingLotMap = {},
                onNavigateToParkingLotDetails = {},
                onShowSnackbar = { SnackbarResult.Dismissed }
            )
        }

        composeTestRule.onNodeWithText(context.getString(R.string.parking_lot_list_error))
            .assertIsDisplayed()

        val retryButton = composeTestRule.onNodeWithText(context.getString(R.string.parking_lot_list_refresh))
        retryButton.assertIsDisplayed()
        retryButton.assertHasClickAction()

        retryButton.performClick()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("P1")
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("P1").assertIsDisplayed()
    }
}
