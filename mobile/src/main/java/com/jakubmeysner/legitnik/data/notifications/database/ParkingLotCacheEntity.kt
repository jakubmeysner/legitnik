package com.jakubmeysner.legitnik.data.notifications.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "parking_lot_cache"
)


data class ParkingLotCacheEntity(
    @PrimaryKey override val id: String,
    override val symbol: String,
    @ColumnInfo(name = "free_places") override val freePlaces: Int,
    @ColumnInfo(name = "previous_free_places") override val previousFreePlaces: Int,
    @ColumnInfo(name = "is_followed") val isFollowed: Boolean,
) : ParkingLotCacheEntityInterface
