package com.jakubmeysner.legitnik.domain.notifications

import android.content.Context
import com.jakubmeysner.legitnik.data.notifications.database.ParkingLotCacheDao
import com.jakubmeysner.legitnik.data.parking.ParkingLotRepository
import com.jakubmeysner.legitnik.data.settings.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationHelperModule {
    @Provides
    @Singleton
    fun providesNotificationHelper(
        @ApplicationContext context: Context,
        externalScope: CoroutineScope,
        parkingLotRepository: ParkingLotRepository,
        settingsRepository: SettingsRepository,
        parkingLotCacheDao: ParkingLotCacheDao,
    ): NotificationHelper {
        return NotificationHelper(
            context,
            externalScope,
            parkingLotRepository,
            settingsRepository,
            parkingLotCacheDao
        )
    }
}
