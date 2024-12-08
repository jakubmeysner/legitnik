package com.jakubmeysner.legitnik.data.sdcatcard

import com.google.gson.Gson
import java.security.MessageDigest

interface SDCATCardRawDataInterface {
    val type: SDCATCardType
    val rawMessage: List<Byte>
    val rawCertificate: List<Byte>
}

fun SDCATCardRawDataInterface.getHash(): ByteArray {
    val gson = Gson()
    val gsonString = gson.toJson(this)
    val digest = MessageDigest.getInstance("SHA-256")

    return digest.digest(gsonString.toByteArray())
}
