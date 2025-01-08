package com.jakubmeysner.legitnik.domain.apdu

import com.jakubmeysner.legitnik.util.b

interface ApduTransceiver {
    fun transceive(data: ByteArray): ByteArray

    fun selectFileDfNoResponseData(dfName: ByteArray): ByteArray {
        val command = createCommand(
            cla = CLA,
            ins = INS_SELECT_FILE,
            p1 = P1_SELECT_FILE_BY_DF_NAME,
            p2 = P2_SELECT_FILE_NO_RESPONSE_DATA,
            data = dfName,
        )

        return transceive(command)
    }

    fun selectFileEfNoResponseData(efIdentifier: ByteArray): ByteArray {
        val command = createCommand(
            cla = CLA,
            ins = INS_SELECT_FILE,
            p1 = P1_SELECT_FILE_BY_EF_IDENTIFIER,
            p2 = P2_SELECT_FILE_NO_RESPONSE_DATA,
            data = efIdentifier,
        )

        return transceive(command)
    }

    fun readBinary(offset: Int): ByteArray {
        val command = createCommand(
            cla = CLA,
            ins = INS_READ_BINARY,
            p1 = offset.shr(8).toByte(),
            p2 = offset.toByte(),
            data = null,
            le = LE_DEFAULT,
        )

        return transceive(command)
    }

    fun readBinary(): ByteArray {
        val data = mutableListOf<Byte>()
        var sw1: Byte
        var sw2: Byte

        while (true) {
            val response = readBinary(data.size)
            data.addAll(response.asList().subList(0, response.size - 2))
            sw1 = response[response.lastIndex - 1]
            sw2 = response.last()

            if (sw1 != okSw[0] || sw2 != okSw[1]) {
                break
            }
        }

        return if (data.isEmpty()) {
            byteArrayOf(sw1, sw2)
        } else {
            data.addAll(okSw)
            data.toByteArray()
        }
    }

    companion object {
        val CLA = 0x00.b

        val INS_SELECT_FILE = 0xA4.b
        val INS_READ_BINARY = 0xB0.b

        val P1_SELECT_FILE_BY_DF_NAME = 0x04.b
        val P1_SELECT_FILE_BY_EF_IDENTIFIER = 0x02.b
        val P2_SELECT_FILE_RESPONSE_FCI = 0x00.b
        val P2_SELECT_FILE_NO_RESPONSE_DATA = 0x0C.b

        val LE_DEFAULT = 0x00.b

        val okSw: List<Byte> = listOf(0x90.b, 0x00)
        val functionNotSupportedSw: List<Byte> = listOf(0x6a, 0x81.b)
        val fileNotFoundSw: List<Byte> = listOf(0x6a, 0x82.b)
        val classNotSupportedSw: List<Byte> = listOf(0x6E, 0x00)
        val commandNotAllowedNoCurrentEfSw: List<Byte> = listOf(0x69, 0x86.b)

        fun createCommand(
            cla: Byte,
            ins: Byte,
            p1: Byte,
            p2: Byte,
            data: ByteArray? = null,
            le: Byte? = null,
        ): ByteArray {
            val command = ByteArray(
                4
                    + (if (data != null) 1 + data.size else 0)
                    + if (le != null) 1 else 0
            )

            command[0] = cla
            command[1] = ins
            command[2] = p1
            command[3] = p2

            if (data != null) {
                command[4] = data.size.toByte()
                data.copyInto(command, 5)
            }

            if (le != null) {
                command[command.lastIndex] = le
            }

            return command
        }

        fun isApduResponseOk(apduResponse: ByteArray): Boolean {
            val sw1 = apduResponse[apduResponse.lastIndex - 1]
            val sw2 = apduResponse.last()
            return sw1 == okSw[0] && sw2 == okSw[1]
        }
    }
}
