package com.jakubmeysner.legitnik.data.sdcatcard

import com.jakubmeysner.legitnik.data.sdcatcard.database.SDCATCardRawDao
import com.jakubmeysner.legitnik.data.sdcatcard.database.SDCATCardRawDataEntity
import com.jakubmeysner.legitnik.data.sdcatcard.database.SDCATCardRawDataEntityInterface
import com.jakubmeysner.legitnik.util.ClassSimpleNameLoggingTag
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject


class SDCATCardRepository @Inject constructor(
    private val sdcatCardRawDao: SDCATCardRawDao,
) : ClassSimpleNameLoggingTag {
    suspend fun getAllCards(): List<SDCATCardRawDataEntityInterface> {
        return sdcatCardRawDao.getAll()
    }

    fun getAllCardsFlow(): Flow<List<SDCATCardRawDataEntityInterface>> {
        return sdcatCardRawDao.getAllFlow()
    }

    suspend fun getCard(uuid: UUID): SDCATCardRawDataEntityInterface? {
        return sdcatCardRawDao.getOne(uuid)
    }

    fun getCardFlow(id: UUID): Flow<SDCATCardRawDataEntityInterface?> {
        return sdcatCardRawDao.getOneFlow(id)
    }

    suspend fun getCardByHash(hash: ByteArray): SDCATCardRawDataEntityInterface? {
        return sdcatCardRawDao.getOneByHash(hash.toList())
    }

    fun getCardByHashFlow(hash: List<Byte>): Flow<SDCATCardRawDataEntityInterface?> {
        return sdcatCardRawDao.getOneByHashFlow(hash)
    }

    suspend fun getDefaultCard(): SDCATCardRawDataEntityInterface? {
        return sdcatCardRawDao.getDefault()
    }

    suspend fun getActiveOrDefaultCard(): SDCATCardRawDataEntityInterface? {
        return sdcatCardRawDao.getActiveOrDefault()
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

    suspend fun unsetDefaultCard() {
        sdcatCardRawDao.unsetDefault()
    }

    suspend fun replaceDefaultCard(id: UUID) {
        sdcatCardRawDao.replaceDefault(id)
    }

    suspend fun unsetActiveCard() {
        sdcatCardRawDao.unsetActive()
    }

    suspend fun replaceActiveCard(id: UUID) {
        sdcatCardRawDao.replaceActive(id)
    }

    suspend fun removeCard(id: UUID) {
        sdcatCardRawDao.getOne(id)?.let {
            sdcatCardRawDao.delete(
                it
            )
        }
    }
}
