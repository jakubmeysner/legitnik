package com.jakubmeysner.legitnik.data.sdcatcard.database

import android.content.Context
import androidx.room.Room
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
        return Room.databaseBuilder(context, AppDatabase::class.java, "database")
            .addTypeConverter(Converters()).build()
    }

    @Provides
    fun provideSDCATCardRawDao(database: AppDatabase): SDCATCardRawDao {
        return database.SDCATCardRawDao()
    }
}
