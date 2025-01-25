package com.jakubmeysner.legitnik.data.sdcatcard

import com.jakubmeysner.legitnik.data.sdcatcard.database.SDCATCardRawDao
import com.jakubmeysner.legitnik.data.sdcatcard.database.SDCATCardRawDataEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.UUID

class SDCATCardRepositoryUnitTest {
    private lateinit var daoMock: SDCATCardRawDao
    private lateinit var repository: SDCATCardRepository

    @Before
    fun setUp() {
        mockkStatic(UUID::class)
        daoMock = mockk()
        repository = SDCATCardRepository(daoMock)
    }

    @After
    fun tearDown() {
        unmockkStatic(UUID::class)
    }

    @Test
    fun `addCard should insert a card`() = runTest {
        coEvery { UUID.randomUUID() } returns testUuid
        coEvery { daoMock.insert(any()) } returns Unit

        repository.addCard(testRawData, null)

        coVerify {
            daoMock.insert(
                SDCATCardRawDataEntity(
                    id = testUuid,
                    hash = testRawData.getHash().toList(),
                    type = testRawData.type,
                    rawMessage = testRawData.rawMessage,
                    rawCertificate = testRawData.rawCertificate,
                    default = null,
                )
            )
        }
    }

    companion object {
        private val testUuid = UUID.randomUUID()

        private val testRawData = SDCATCardRawData(
            type = SDCATCardType.DOCTORAL_CANDIDATE,
            rawMessage = listOf(0x01, 0x02, 0x03),
            rawCertificate = listOf(0x04, 0x05, 0x06),
        )
    }
}
