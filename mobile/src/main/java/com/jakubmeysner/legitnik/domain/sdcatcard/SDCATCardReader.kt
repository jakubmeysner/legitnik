package com.jakubmeysner.legitnik.domain.sdcatcard

import com.jakubmeysner.legitnik.data.sdcatcard.SDCATCardRawData
import com.jakubmeysner.legitnik.data.sdcatcard.SDCATCardType
import com.jakubmeysner.legitnik.domain.apdu.ApduTransceiver
import com.jakubmeysner.legitnik.util.b


private val rid = byteArrayOf(0xD6.b, 0x16, 0x00, 0x00, 0x30)
private val studentDfName = byteArrayOf(*rid, 0x01, 0x01)
private val doctoralCandidateDfName = byteArrayOf(*rid, 0x01, 0x02)
private val academicTeacherDfName = byteArrayOf(*rid, 0x01, 0x03)

private val typeToDfName = mapOf(
    SDCATCardType.STUDENT to studentDfName,
    SDCATCardType.DOCTORAL_CANDIDATE to doctoralCandidateDfName,
    SDCATCardType.ACADEMIC_TEACHER to academicTeacherDfName,
)

private val messageEfIdentifier = byteArrayOf(0x00, 0x02)
private val certificateEfIdentifier = byteArrayOf(0x00, 0x01)

fun readSDCATCard(apduTransceiver: ApduTransceiver): SDCATCardRawData {
    var type: SDCATCardType? = null
    lateinit var selectDfResponse: ByteArray

    for ((dfType, dfName) in typeToDfName) {
        selectDfResponse = apduTransceiver.selectFileDf(dfName)
        val sw1 = selectDfResponse[selectDfResponse.lastIndex - 1]
        val sw2 = selectDfResponse.last()

        if (sw1 == ApduTransceiver.SW1_OK && sw2 == ApduTransceiver.SW2_OK) {
            type = dfType
            break
        }
    }

    if (type == null) {
        throw SDCATCardReadException("Couldn't select any SDCAT DF: $selectDfResponse")
    }

    val selectMessageEfResponse = apduTransceiver.selectFileEf(messageEfIdentifier)

    if (!ApduTransceiver.isApduResponseOk(selectMessageEfResponse)) {
        throw SDCATCardReadException("Couldn't select message EF: $selectMessageEfResponse")
    }

    val readMessageEfResponse = apduTransceiver.readBinary()

    if (!ApduTransceiver.isApduResponseOk(readMessageEfResponse)) {
        throw SDCATCardReadException("Couldn't read message EF: $readMessageEfResponse")
    }

    val selectCertificateEfResponse = apduTransceiver.selectFileEf(certificateEfIdentifier)

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
