package com.jakubmeysner.legitnik.data.sdcatcard

import com.jakubmeysner.legitnik.data.sdcatcard.database.SDCATCardRawDataEntityInterface

data class SDCATCardDataEntity(
    override val rawData: SDCATCardRawDataEntityInterface,
    override val parsedData: SDCATCardParsedData,
) : SDCATCardDataInterface
