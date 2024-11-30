package com.jakubmeysner.legitnik.domain.apdu

import com.acs.smartcard.Reader
import com.jakubmeysner.legitnik.util.b

class UsbReaderTransceiver(
    private val usbReader: Reader,
    private val slotNum: Int,
) : ApduTransceiver {
    override fun transceive(data: ByteArray): ByteArray {
        return transceive(data, false)
    }

    private fun transceive(data: ByteArray, skipLe: Boolean): ByteArray {
        try {
            val responseBuffer = ByteArray(300)
            val actualData = if (skipLe) data.sliceArray(0..<data.lastIndex) else data

            var responseBytes = usbReader.transmit(
                slotNum, actualData, actualData.size, responseBuffer, responseBuffer.size
            )

            var (sw1, sw2) = Pair(
                responseBuffer[responseBytes - 2],
                responseBuffer[responseBytes - 1]
            )

            if (sw1 == SW1_RESPONSE_BYTES_STILL_AVAILABLE) {
                val fullResponse = mutableListOf<Byte>()
                val getResponseCommand = GET_RESPONSE_COMMAND_WITHOUT_LE.plus(sw2).toByteArray()

                while (true) {
                    responseBytes = usbReader.transmit(
                        slotNum,
                        getResponseCommand,
                        getResponseCommand.size,
                        responseBuffer,
                        responseBuffer.size
                    )

                    fullResponse.addAll(responseBuffer.asList().subList(0, responseBytes - 2))
                    sw1 = responseBuffer[responseBytes - 2]
                    sw2 = responseBuffer[responseBytes - 1]

                    if (sw1 != SW1_RESPONSE_BYTES_STILL_AVAILABLE) {
                        return fullResponse.apply {
                            add(sw1)
                            add(sw2)
                        }.toByteArray()
                    }
                }
            }

            if (sw1 == SW1_WRONG_LENGTH_LE) {
                return transceive(
                    data.slice(0..<data.lastIndex).plus(sw2).toByteArray(), false
                )
            }

            return responseBuffer.sliceArray(0..<responseBytes)
        } catch (exception: IllegalArgumentException) {
            if (exception.message == null && !skipLe) {
                return transceive(data, true)
            }

            throw exception
        }
    }

    companion object {
        private val SW1_RESPONSE_BYTES_STILL_AVAILABLE = 0x61.b
        private val SW1_WRONG_LENGTH_LE = 0x6C.b
        private val GET_RESPONSE_COMMAND_WITHOUT_LE = listOf(0x00, 0xC0.b, 0x00, 0x00)
    }
}
