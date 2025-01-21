package com.jakubmeysner.legitnik.ui.sdcatcardsaved.list

import com.jakubmeysner.legitnik.data.sdcatcard.SDCATCardDataEntity
import com.jakubmeysner.legitnik.data.sdcatcard.SDCATCardParsedData
import com.jakubmeysner.legitnik.data.sdcatcard.SDCATCardRawDataInterface
import com.jakubmeysner.legitnik.data.sdcatcard.SDCATCardRepository
import com.jakubmeysner.legitnik.data.sdcatcard.database.SDCATCardRawDataEntity
import com.jakubmeysner.legitnik.data.sdcatcard.database.SDCATCardRawDataEntityInterface
import com.jakubmeysner.legitnik.domain.sdcatcard.toParsed
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.seconds

class SDCATCardSavedListViewModelUnitTest {
    private lateinit var repositoryMock: SDCATCardRepository
    private lateinit var viewModel: SDCATCardSavedListViewModel

    @Before
    fun setUp() {
        repositoryMock = mockk()
        mockkStatic(SDCATCardRawDataInterface::toParsed)
        every { repositoryMock.getAllCardsFlow() } returns flowOf(testData.keys.toList())

        for ((raw, parsed) in testData) {
            every { raw.toParsed() } returns parsed
        }

        viewModel = SDCATCardSavedListViewModel(repositoryMock)
    }

    @After
    fun tearDown() {
        unmockkStatic(SDCATCardRawDataEntityInterface::toParsed)
    }

    @Test
    fun `uiState should contain the cards provided by the repository`() = runTest {
        val expectedUiState = SDCATCardSavedListUiState(
            cards = testData.map { (raw, parsed) ->
                SDCATCardDataEntity(
                    rawData = raw,
                    parsedData = parsed,
                )
            }
        )

        assertEquals(
            expectedUiState,
            withTimeoutOrNull(1.seconds) {
                viewModel.uiState.first { it == expectedUiState }
            } ?: viewModel.uiState.value,
        )
    }

    companion object {
        private val testData = mapOf<SDCATCardRawDataEntity, SDCATCardParsedData>(
            mockk<SDCATCardRawDataEntity>(relaxed = true) to mockk(relaxed = true),
            mockk<SDCATCardRawDataEntity>(relaxed = true) to mockk(relaxed = true),
        )
    }
}
