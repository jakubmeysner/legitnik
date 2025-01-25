package com.jakubmeysner.legitnik.data.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jakubmeysner.legitnik.data.notifications.database.ParkingLotCacheDao
import com.jakubmeysner.legitnik.data.notifications.database.ParkingLotCacheEntity
import com.jakubmeysner.legitnik.data.sdcatcard.database.SDCATCardRawDao
import com.jakubmeysner.legitnik.data.sdcatcard.database.SDCATCardRawDataEntity

@Database(
    version = 4,
    entities = [SDCATCardRawDataEntity::class, ParkingLotCacheEntity::class],
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4)
    ],
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sdcatCardRawDao(): SDCATCardRawDao
    abstract fun parkingLotCacheDao(): ParkingLotCacheDao
}
