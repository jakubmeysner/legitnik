package com.jakubmeysner.legitnik.data.sdcatcard.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import java.util.UUID

@Dao
interface SDCATCardRawDao {
    @Query("SELECT * FROM SDCAT_card_raw_data")
    suspend fun getAll(): List<SDCATCardRawDataEntity>

    @Query("SELECT * FROM SDCAT_card_raw_data WHERE uuid = :uuid")
    suspend fun getOne(uuid: UUID): SDCATCardRawDataEntity?

    @Query("SELECT * FROM SDCAT_card_raw_data WHERE hash = :hash")
    suspend fun getOneByHash(hash: List<Byte>): SDCATCardRawDataEntity?

    @Insert
    suspend fun insert(card: SDCATCardRawDataEntity)

    @Delete
    suspend fun delete(card: SDCATCardRawDataEntity)
}
