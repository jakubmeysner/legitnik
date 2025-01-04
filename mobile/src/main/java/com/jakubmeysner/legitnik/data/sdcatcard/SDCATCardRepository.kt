package com.jakubmeysner.legitnik.data.sdcatcard

import com.jakubmeysner.legitnik.data.sdcatcard.database.SDCATCardRawDao
import com.jakubmeysner.legitnik.data.sdcatcard.database.SDCATCardRawDataEntity
import com.jakubmeysner.legitnik.data.sdcatcard.database.SDCATCardRawDataEntityInterface
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject


class SDCATCardRepository @Inject constructor(private val sdcatCardRawDao: SDCATCardRawDao) {
    suspend fun getAllCards(): List<SDCATCardRawDataEntityInterface> {
        return sdcatCardRawDao.getAll()
    }

    fun getAllCardsFlow(): Flow<List<SDCATCardRawDataEntityInterface>> {
        return sdcatCardRawDao.getAllFlow()
    }

    suspend fun getCard(uuid: UUID): SDCATCardRawDataEntityInterface? {
        return sdcatCardRawDao.getOne(uuid)
    }

    suspend fun getCardByHash(hash: ByteArray): SDCATCardRawDataEntityInterface? {
        return sdcatCardRawDao.getOneByHash(hash.toList())
    }

    suspend fun getDefaultCard(): SDCATCardRawDataEntityInterface? {
        return sdcatCardRawDao.getDefault()
    }

    suspend fun addCard(sdcatCardRawData: SDCATCardRawDataInterface, default: Boolean? = null) {
        sdcatCardRawDao.insert(
            SDCATCardRawDataEntity(
                id = UUID.randomUUID(),
                hash = sdcatCardRawData.getHash().toList(),
                type = sdcatCardRawData.type,
                rawMessage = sdcatCardRawData.rawMessage,
                rawCertificate = sdcatCardRawData.rawCertificate,
                default = default,
            )
        )
    }

    suspend fun removeCard(uuid: UUID) {
        sdcatCardRawDao.getOne(uuid)?.let {
            sdcatCardRawDao.delete(
                it
            )
        }
    }
}
