package com.jakubmeysner.legitnik.domain.sdcatcard

import com.jakubmeysner.legitnik.data.sdcatcard.SDCATCardRawData

data class SDCATCardData(
    val rawData: SDCATCardRawData,
    val parsedData: SDCATCardParsedData,
)
