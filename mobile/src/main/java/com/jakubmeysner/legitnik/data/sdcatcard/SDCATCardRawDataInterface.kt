package com.jakubmeysner.legitnik.data.sdcatcard

import java.security.MessageDigest

interface SDCATCardRawDataInterface {
    val type: SDCATCardType
    val rawMessage: List<Byte>
    val rawCertificate: List<Byte>
}

fun SDCATCardRawDataInterface.getHash(): ByteArray {
    val digest = MessageDigest.getInstance("SHA-256")
    return digest.digest(type.name.toByteArray().plus(rawMessage).plus(rawCertificate))
}
