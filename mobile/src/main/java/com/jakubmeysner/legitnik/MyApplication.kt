package com.jakubmeysner.legitnik

import android.app.Application
import com.jakubmeysner.legitnik.util.MessageData
import com.jakubmeysner.legitnik.util.NotificationHelper
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application() {
    @Inject
    lateinit var notificationHelper: NotificationHelper

    override fun onCreate() {
        super.onCreate()
        notificationHelper.setUpNotificationChannels()
        notificationHelper.showNotification(
            messageData = MessageData(
                EventType.NON_ZERO.value,
                "3",
                5,
                null
            )
        )
    }
}

