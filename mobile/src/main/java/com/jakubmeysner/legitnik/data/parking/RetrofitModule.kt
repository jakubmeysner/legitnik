package com.jakubmeysner.legitnik.data.parking

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)//should it be app level? https://developer.android.com/training/dependency-injection/hilt-android#generated-components
object RetrofitModule {
    @Provides
    fun provideApiService(): ParkingLotApi {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://iparking.pwr.edu.pl")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(ParkingLotApi::class.java)
    }
}
