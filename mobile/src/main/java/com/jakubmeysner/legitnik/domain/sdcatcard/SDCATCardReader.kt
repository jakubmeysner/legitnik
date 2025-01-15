package com.jakubmeysner.legitnik.domain.sdcatcard

import com.jakubmeysner.legitnik.data.sdcatcard.SDCATCardApdu
import com.jakubmeysner.legitnik.data.sdcatcard.SDCATCardRawData
import com.jakubmeysner.legitnik.data.sdcatcard.SDCATCardType
import com.jakubmeysner.legitnik.domain.apdu.ApduTransceiver

fun readSDCATCard(apduTransceiver: ApduTransceiver): SDCATCardRawData {
    var type: SDCATCardType? = null
    lateinit var selectDfResponse: ByteArray

    for ((dfType, dfName) in SDCATCardApdu.typeToDfName) {
        selectDfResponse = apduTransceiver.selectFileDfNoResponseData(dfName.toByteArray())
        val sw1 = selectDfResponse[selectDfResponse.lastIndex - 1]
        val sw2 = selectDfResponse.last()

        if (sw1 == ApduTransceiver.okSw[0] && sw2 == ApduTransceiver.okSw[1]) {
            type = dfType
            break
        }
    }

    if (type == null) {
        throw SDCATCardReadException("Couldn't select any SDCAT DF: $selectDfResponse")
    }

    val selectMessageEfResponse = apduTransceiver.selectFileEfNoResponseData(
        SDCATCardApdu.messageEfIdentifier.toByteArray()
    )

    if (!ApduTransceiver.isApduResponseOk(selectMessageEfResponse)) {
        throw SDCATCardReadException("Couldn't select message EF: $selectMessageEfResponse")
    }

    val readMessageEfResponse = apduTransceiver.readBinary()

    if (!ApduTransceiver.isApduResponseOk(readMessageEfResponse)) {
        throw SDCATCardReadException("Couldn't read message EF: $readMessageEfResponse")
    }

    val selectCertificateEfResponse = apduTransceiver.selectFileEfNoResponseData(
        SDCATCardApdu.certificateEfIdentifier.toByteArray()
    )

    if (!ApduTransceiver.isApduResponseOk(selectCertificateEfResponse)) {
        throw SDCATCardReadException("Couldn't select certificate EF: $selectCertificateEfResponse")
    }

    val readCertificateEfResponse = apduTransceiver.readBinary()

    if (!ApduTransceiver.isApduResponseOk(readCertificateEfResponse)) {
        throw SDCATCardReadException("Couldn't read certificate EF: $readCertificateEfResponse")
    }

    return SDCATCardRawData(
        type,
        readMessageEfResponse.asList().subList(0, readMessageEfResponse.size - 2),
        readCertificateEfResponse.asList().subList(0, readCertificateEfResponse.size - 2),
    )
}

class SDCATCardReadException(message: String) : Exception(message)
