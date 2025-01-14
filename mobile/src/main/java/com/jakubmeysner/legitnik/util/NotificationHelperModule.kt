package com.jakubmeysner.legitnik.util

import android.content.Context
import com.jakubmeysner.legitnik.data.parking.ParkingLotRepository
import com.jakubmeysner.legitnik.data.settings.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationHelperModule {
    @Provides
    @Singleton
    fun providesNotificationHelper(
        @ApplicationContext context: Context,
        parkingLotRepository: ParkingLotRepository,
        settingsRepository: SettingsRepository,
    ): NotificationHelper {
        return NotificationHelper(context, parkingLotRepository, settingsRepository)
    }
}
