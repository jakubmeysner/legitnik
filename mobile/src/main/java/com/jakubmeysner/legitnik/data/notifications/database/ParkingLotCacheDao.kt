package com.jakubmeysner.legitnik.data.notifications.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ParkingLotCacheDao {
    @Query("SELECT * FROM parking_lot_cache")
    fun getAllFlow(): Flow<List<ParkingLotCacheEntity>>

    @Query("SELECT * FROM parking_lot_cache WHERE id = :id")
    fun getOne(id: String): ParkingLotCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertParkingLots(parkingLotsCache: List<ParkingLotCacheEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(parkingLotCache: ParkingLotCacheEntity)

    @Update
    fun update(parkingLotCache: ParkingLotCacheEntity)

    @Update
    fun updateParkingLots(parkingLotsCache: List<ParkingLotCacheEntity>)

    @Query("DELETE FROM parking_lot_cache")
    fun deleteParkingLots()

    @Delete
    fun delete(parkingLotCache: ParkingLotCacheEntity)
}
