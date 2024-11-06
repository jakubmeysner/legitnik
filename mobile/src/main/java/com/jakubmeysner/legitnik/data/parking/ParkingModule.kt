package com.jakubmeysner.legitnik.data.parking

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@Module
@InstallIn(SingletonComponent::class)
abstract class ParkingModule {
    @Binds
    abstract fun bindParkingDataSource(
        parkingLotRemoteDataSource: ParkingLotRemoteDataSource
    ): ParkingLotDataSource

    @Binds
    abstract fun bindParkingRepository(
        parkingRepositoryImpl: ParkingRepositoryImpl
    ): ParkingRepository
}

@Module
@InstallIn(SingletonComponent::class)//should it be app level? https://developer.android.com/training/dependency-injection/hilt-android#generated-components
object RetrofitModule {
    @Provides
    fun provideBaseUrl(): String = "https://iparking.pwr.edu.pl/"

    @Provides
    fun provideRetrofit(baseUrl: String): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    fun provideApiService(retrofit: Retrofit): ParkingLotApiService {
        return retrofit.create(ParkingLotApiService::class.java)
    }
}
