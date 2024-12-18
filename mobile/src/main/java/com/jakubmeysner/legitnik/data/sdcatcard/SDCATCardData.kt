package com.jakubmeysner.legitnik.data.sdcatcard

data class SDCATCardData(
    override val rawData: SDCATCardRawDataInterface,
    override val parsedData: SDCATCardParsedData,
) : SDCATCardDataInterface
