package com.jakubmeysner.legitnik.data.sdcatcard

data class SDCATCardRawData(
    val type: SDCATCardType,
    val rawMessage: List<Byte>,
    val rawCertificate: List<Byte>,
)
