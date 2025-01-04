package com.jakubmeysner.legitnik.domain.apdu

import com.jakubmeysner.legitnik.util.b

interface ApduTransceiver {
    fun transceive(data: ByteArray): ByteArray

    fun selectFileDf(dfName: ByteArray): ByteArray {
        val command = createCommand(
            cla = CLA,
            ins = INS_SELECT_FILE,
            p1 = P1_SELECT_FILE_BY_DF_NAME,
            p2 = P2_SELECT_FILE_BY_DF_NAME,
            data = dfName,
            le = LE_DEFAULT,
        )

        return transceive(command)
    }

    fun selectFileEf(efIdentifier: ByteArray): ByteArray {
        val command = createCommand(
            cla = CLA,
            ins = INS_SELECT_FILE,
            p1 = P1_SELECT_FILE_BY_EF_IDENTIFIER,
            p2 = P2_SELECT_FILE_BY_EF_IDENTIFIER,
            data = efIdentifier,
            le = LE_DEFAULT,
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
        val P2_SELECT_FILE_BY_DF_NAME = 0x00.b

        val P1_SELECT_FILE_BY_EF_IDENTIFIER = 0x02.b
        val P2_SELECT_FILE_BY_EF_IDENTIFIER = 0x00.b

        val LE_DEFAULT = 0x00.b

        val selectFileByDfNameCommandPrefix = listOf(
            CLA, INS_SELECT_FILE, P1_SELECT_FILE_BY_DF_NAME, P2_SELECT_FILE_BY_DF_NAME
        )

        val selectFileByEfIdentifierCommandPrefix = listOf(
            CLA, INS_SELECT_FILE, P1_SELECT_FILE_BY_EF_IDENTIFIER, P2_SELECT_FILE_BY_EF_IDENTIFIER
        )

        val readBinaryCommandPrefix = listOf(CLA, INS_READ_BINARY)

        val okSw: List<Byte> = listOf(0x90.b, 0x00)
        val functionNotSupportedSw: List<Byte> = listOf(0x6a, 0x81.b)
        val fileNotFoundSw: List<Byte> = listOf(0x6a, 0x82.b)

        fun createCommand(
            cla: Byte,
            ins: Byte,
            p1: Byte,
            p2: Byte,
            data: ByteArray?,
            le: Byte,
        ): ByteArray {
            val command = ByteArray(5 + if (data != null) 1 + data.size else 0)
            command[0] = cla
            command[1] = ins
            command[2] = p1
            command[3] = p2
            command[command.lastIndex] = le

            if (data != null) {
                command[4] = data.size.toByte()
                data.copyInto(command, 5)
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
