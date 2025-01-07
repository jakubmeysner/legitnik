package com.jakubmeysner.legitnik.data.sdcatcard.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface SDCATCardRawDao {
    @Query("SELECT * FROM sdcat_card_raw_data")
    suspend fun getAll(): List<SDCATCardRawDataEntity>

    @Query("SELECT * FROM sdcat_card_raw_data")
    fun getAllFlow(): Flow<List<SDCATCardRawDataEntity>>

    @Query("SELECT * FROM sdcat_card_raw_data WHERE uuid = :uuid")
    suspend fun getOne(uuid: UUID): SDCATCardRawDataEntity?

    @Query("SELECT * FROM sdcat_card_raw_data WHERE uuid = :uuid")
    fun getOneFlow(uuid: UUID): Flow<SDCATCardRawDataEntity?>

    @Query("SELECT * FROM sdcat_card_raw_data WHERE hash = :hash")
    suspend fun getOneByHash(hash: List<Byte>): SDCATCardRawDataEntity?

    @Query("SELECT * FROM sdcat_card_raw_data WHERE `default` = 1")
    suspend fun getDefault(): SDCATCardRawDataEntity?

    @Query("SELECT * FROM sdcat_card_raw_data WHERE active = 1 OR `default` = 1 ORDER BY active DESC LIMIT 1")
    suspend fun getActiveOrDefault(): SDCATCardRawDataEntity?

    @Query("SELECT * FROM sdcat_card_raw_data WHERE hash = :hash")
    fun getOneByHashFlow(hash: List<Byte>): Flow<SDCATCardRawDataEntity?>

    @Insert
    suspend fun insert(card: SDCATCardRawDataEntity)

    @Query("UPDATE sdcat_card_raw_data SET `default` = 0 WHERE `default` = 1")
    suspend fun unsetDefault()

    @Query("UPDATE sdcat_card_raw_data SET `default` = 1 WHERE uuid = :id")
    suspend fun setDefault(id: UUID)

    @Transaction
    suspend fun replaceDefault(id: UUID) {
        unsetDefault()
        setDefault(id)
    }

    @Query("UPDATE sdcat_card_raw_data SET active = 0 WHERE active = 1")
    suspend fun unsetActive()

    @Query("UPDATE sdcat_card_raw_data SET active = 1 WHERE uuid = :id")
    suspend fun setActive(id: UUID)

    @Transaction
    suspend fun replaceActive(id: UUID) {
        unsetActive()
        setActive(id)
    }

    @Delete
    suspend fun delete(card: SDCATCardRawDataEntity)
}
