package com.jakubmeysner.legitnik.services

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.jakubmeysner.legitnik.data.settings.SettingsRepository
import com.jakubmeysner.legitnik.domain.notifications.MessageData
import com.jakubmeysner.legitnik.domain.notifications.NotificationHelper
import com.jakubmeysner.legitnik.util.ClassSimpleNameLoggingTag
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


enum class EventType(val value: String) {
    NON_ZERO("PARKING_LOT_FREE_PLACES_BECAME_NON_ZERO"),
    CHANGED("PARKING_LOT_FREE_PLACES_CHANGED")
}

@AndroidEntryPoint
class MyFirebaseMessagingService() : FirebaseMessagingService(), ClassSimpleNameLoggingTag {

    @Inject
    lateinit var notificationHelper: NotificationHelper

    var coroutineContext: CoroutineContext = SupervisorJob()

    @Inject
    lateinit var settingsRepository: SettingsRepository

    private val scope = CoroutineScope(coroutineContext)

    override fun onNewToken(token: String) {
        Log.d(tag, "Refreshed token: $token")

        scope.launch {
            settingsRepository.subscribeToFcmTopicsOnTokenRefresh()
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        scope.launch {
            Log.d(tag, "Message received: ${message.messageId}")
            if (message.data.isNotEmpty()) {
                Log.d(tag, "Message data payload: ${message.data}")
                val event = message.data["event"]
                when (event) {
                    EventType.NON_ZERO.value -> {
                        notificationHelper.showNotification(
                            MessageData(
                                event,
                                message.data["id"]!!,
                                message.data["freePlaces"]!!.toInt(),
                                null
                            )
                        )
                    }

                    EventType.CHANGED.value -> {
                        notificationHelper.showNotification(
                            MessageData(
                                event,
                                message.data["id"]!!,
                                message.data["freePlaces"]!!.toInt(),
                                message.data["previousFreePlaces"]!!.toInt()
                            )
                        )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        Log.d(tag, "Destroying FCM service!")
        super.onDestroy()
        scope.cancel()
    }
}
