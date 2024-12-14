package com.jakubmeysner.legitnik.data.sdcatcard

data class SDCATCardRawData(
    override val type: SDCATCardType,
    override val rawMessage: List<Byte>,
    override val rawCertificate: List<Byte>,
) : SDCATCardRawDataInterface
