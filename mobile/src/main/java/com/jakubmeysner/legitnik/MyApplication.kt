package com.jakubmeysner.legitnik

import android.app.Application
import com.jakubmeysner.legitnik.util.NotificationHelper
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @Inject
    lateinit var notificationHelper: NotificationHelper

    override fun onCreate() {
        super.onCreate()

        scope.launch {
            notificationHelper.setUpNotificationChannels()
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        scope.cancel()
    }
}

