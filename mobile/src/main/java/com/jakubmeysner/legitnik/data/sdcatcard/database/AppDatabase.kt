package com.jakubmeysner.legitnik.data.sdcatcard.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    version = 3,
    entities = [SDCATCardRawDataEntity::class],
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
    ],
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sdcatCardRawDao(): SDCATCardRawDao
}
