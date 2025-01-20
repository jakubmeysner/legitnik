package com.jakubmeysner.legitnik.data.database

import android.content.Context
import androidx.room.Room
import com.jakubmeysner.legitnik.data.notifications.database.ParkingLotCacheDao
import com.jakubmeysner.legitnik.data.sdcatcard.database.SDCATCardRawDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "database").build()
    }

    @Provides
    @Singleton
    fun provideSDCATCardRawDao(database: AppDatabase): SDCATCardRawDao {
        return database.sdcatCardRawDao()
    }

    @Provides
    @Singleton
    fun provideParkingLotCacheDao(database: AppDatabase): ParkingLotCacheDao {
        return database.parkingLotCacheDao()
    }
}
