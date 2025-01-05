package com.jakubmeysner.legitnik.data.sdcatcard.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
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

    @Query("SELECT * FROM sdcat_card_raw_data WHERE hash = :hash")
    fun getOneByHashFlow(hash: List<Byte>): Flow<SDCATCardRawDataEntity?>

    @Insert
    suspend fun insert(card: SDCATCardRawDataEntity)

    @Delete
    suspend fun delete(card: SDCATCardRawDataEntity)
}
