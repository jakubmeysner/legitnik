package com.jakubmeysner.legitnik.data.sdcatcard.database

import com.jakubmeysner.legitnik.data.sdcatcard.SDCATCardRawDataInterface
import java.util.UUID

interface SDCATCardRawDataEntityInterface : SDCATCardRawDataInterface {
    val uuid: UUID
}
