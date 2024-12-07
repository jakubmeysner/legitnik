package com.jakubmeysner.legitnik.data.sdcatcard

import com.google.gson.Gson
import java.security.MessageDigest
import java.util.UUID

data class SDCATCardRawData(
    val type: SDCATCardType,
    val rawMessage: List<Byte>,
    val rawCertificate: List<Byte>,
)

fun SDCATCardRawData.toUUID(): UUID {
    val gson = Gson()
    val gsonString = gson.toJson(this)
    val digest = MessageDigest.getInstance("SHA-256")

    val hash = digest.digest(gsonString.toByteArray())

    val mostSignificantBits = hash.copyOfRange(0, 8).toLong()
    val leastSignificantBits = hash.copyOfRange(8, 16).toLong()
    return UUID(mostSignificantBits, leastSignificantBits)
}

//gepetto generated
private fun ByteArray.toLong(): Long {
    var result: Long = 0
    for (byte in this) {
        result = result shl 8 or (byte.toLong() and 0xFF)
    }
    return result
}
