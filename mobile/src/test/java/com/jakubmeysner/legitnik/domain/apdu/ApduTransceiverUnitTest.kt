package com.jakubmeysner.legitnik.domain.apdu

import com.jakubmeysner.legitnik.util.b
import io.mockk.every
import io.mockk.spyk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

class ApduTransceiverUnitTest {
    private lateinit var transceiver: ApduTransceiver

    @Before
    fun setUp() {
        transceiver = spyk(TestApduTransceiver())
    }

    @Test
    fun `createCommand should return correct command when data and le are provided`() {
        assertEquals(
            byteArrayOf(0x01, 0x02, 0x03, 0x04, 0x02, 0x10, 0x11, 0xFF.b).toList(),
            ApduTransceiver.createCommand(
                0x01, 0x02, 0x03, 0x04, byteArrayOf(0x10, 0x11), 0xFF.b
            ).toList()
        )
    }

    @Test
    fun `createCommand should return correct command when data is not provided`() {
        assertEquals(
            byteArrayOf(0x01, 0x02, 0x03, 0x04, 0xFF.b).toList(),
            ApduTransceiver.createCommand(
                0x01, 0x02, 0x03, 0x04, le = 0xFF.b
            ).toList()
        )
    }

    @Test
    fun `createCommand should return correct command when le is not provided`() {
        assertEquals(
            byteArrayOf(0x01, 0x02, 0x03, 0x04, 0x02, 0x10, 0x11).toList(),
            ApduTransceiver.createCommand(
                0x01, 0x02, 0x03, 0x04, byteArrayOf(0x10, 0x11)
            ).toList()
        )
    }

    @Test
    fun `createCommand should return correct command when data and le are not provided`() {
        assertEquals(
            byteArrayOf(0x01, 0x02, 0x03, 0x04).toList(),
            ApduTransceiver.createCommand(0x01, 0x02, 0x03, 0x04).toList()
        )
    }

    @Test
    fun `isApduResponseOk should return true when ok`() {
        assertTrue(ApduTransceiver.isApduResponseOk(byteArrayOf(0x90.b, 0x00)))
    }

    @Test
    fun `isApduResponseOk should return true when ok and extra data present`() {
        assertTrue(ApduTransceiver.isApduResponseOk(byteArrayOf(0x12, 0x34, 0x56, 0x90.b, 0x00)))
    }

    @Test
    fun `isApduResponseOk should return false when not ok`() {
        assertFalse(ApduTransceiver.isApduResponseOk(byteArrayOf(0x6a, 0x82.b)))
    }

    @Test
    fun `isApduResponseOk should return false when not ok and extra data present`() {
        assertFalse(ApduTransceiver.isApduResponseOk(byteArrayOf(0x12, 0x34, 0x56, 0x6a, 0x82.b)))
    }

    @Test
    fun `selectFileDfNoResponseData should send the appropriate command`() {
        val dfName = byteArrayOf(0x01, 0x02, 0x03)

        every {
            transceiver.transceive(
                byteArrayOf(
                    ApduTransceiver.CLA,
                    ApduTransceiver.INS_SELECT_FILE,
                    ApduTransceiver.P1_SELECT_FILE_BY_DF_NAME,
                    ApduTransceiver.P2_SELECT_FILE_NO_RESPONSE_DATA,
                    dfName.size.b,
                ) + dfName
            )
        } returns ApduTransceiver.okSw.toByteArray()

        assertEquals(ApduTransceiver.okSw, transceiver.selectFileDfNoResponseData(dfName).toList())
    }

    @Test
    fun `selectFileEfNoResponseData should send the appropriate command`() {
        val efIdentifier = byteArrayOf(0x02, 0x03)

        every {
            transceiver.transceive(
                byteArrayOf(
                    ApduTransceiver.CLA,
                    ApduTransceiver.INS_SELECT_FILE,
                    ApduTransceiver.P1_SELECT_FILE_BY_EF_IDENTIFIER,
                    ApduTransceiver.P2_SELECT_FILE_NO_RESPONSE_DATA,
                    efIdentifier.size.b,
                ) + efIdentifier
            )
        } returns ApduTransceiver.okSw.toByteArray()

        assertEquals(
            ApduTransceiver.okSw,
            transceiver.selectFileEfNoResponseData(efIdentifier).toList()
        )
    }

    @Test
    fun `readBinary(offset) should send the appropriate command`() {
        val data = byteArrayOf(0x01, 0x02, 0x03)

        every {
            transceiver.transceive(
                byteArrayOf(
                    ApduTransceiver.CLA,
                    ApduTransceiver.INS_READ_BINARY,
                    0x03,
                    0x04,
                    ApduTransceiver.LE_DEFAULT,
                )
            )
        } returns data + ApduTransceiver.okSw.toByteArray()

        assertEquals(
            (data + ApduTransceiver.okSw).toList(),
            transceiver.readBinary(0x0304).toList()
        )
    }

    @Test
    fun `readBinary() should read all data`() {
        val data = Random.nextBytes(1500)

        for (offset in 0..data.size step 256) {
            every { transceiver.readBinary(offset) } returns (
                if (offset + 256 < data.size) data.sliceArray(offset..<offset + 256) + ApduTransceiver.okSw
                else data.sliceArray(offset..data.lastIndex) + 0x62 + 0x82.b
                )
        }

        assertEquals(data.toList() + ApduTransceiver.okSw, transceiver.readBinary().toList())
    }

    private class TestApduTransceiver : ApduTransceiver {
        override fun transceive(data: ByteArray): ByteArray {
            throw NotImplementedError()
        }
    }
}
