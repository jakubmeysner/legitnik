package com.jakubmeysner.legitnik.data.parking

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class ParkingLotModule {
    @Binds
    abstract fun bindParkingDataSource(
        parkingLotRemoteDataSource: ParkingLotRemoteDataSource,
    ): ParkingLotDataSource

    @Singleton
    @Binds
    abstract fun bindParkingRepository(
        parkingRepositoryImpl: ParkingLotRepositoryImpl,
    ): ParkingLotRepository
}
