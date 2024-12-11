package com.jakubmeysner.legitnik.data.sdcatcard.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [SDCATCardRawDataEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sdcatCardRawDao(): SDCATCardRawDao
}
