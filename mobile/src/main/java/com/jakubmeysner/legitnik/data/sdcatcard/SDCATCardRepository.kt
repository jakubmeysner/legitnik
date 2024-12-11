package com.jakubmeysner.legitnik.data.sdcatcard

import com.jakubmeysner.legitnik.data.sdcatcard.database.SDCATCardRawDao
import com.jakubmeysner.legitnik.data.sdcatcard.database.SDCATCardRawDataEntity
import com.jakubmeysner.legitnik.data.sdcatcard.database.SDCATCardRawDataEntityInterface
import java.util.UUID
import javax.inject.Inject


class SDCATCardRepository @Inject constructor(private val sdcatCardRawDao: SDCATCardRawDao) {
    suspend fun getAllCards(): List<SDCATCardRawDataEntityInterface> {
        return sdcatCardRawDao.getAll()
    }

    suspend fun getCard(uuid: UUID): SDCATCardRawDataEntityInterface {
        return sdcatCardRawDao.getOne(uuid)
    }

    suspend fun getCardByHash(hash: ByteArray): SDCATCardRawDataEntityInterface? {
        return sdcatCardRawDao.getOneByHash(hash.toList())
    }

    suspend fun addCard(sdcatCardRawData: SDCATCardRawDataInterface) {
        sdcatCardRawDao.insert(
            SDCATCardRawDataEntity(
                UUID.randomUUID(),
                sdcatCardRawData.getHash().toList(),
                sdcatCardRawData.type,
                sdcatCardRawData.rawMessage,
                sdcatCardRawData.rawCertificate
            )
        )
    }

    suspend fun removeCard(uuid: UUID) {
        sdcatCardRawDao.delete(
            sdcatCardRawDao.getOne(uuid)
        )
    }
}
