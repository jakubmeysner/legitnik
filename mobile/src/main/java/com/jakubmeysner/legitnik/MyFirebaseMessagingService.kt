package com.jakubmeysner.legitnik

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.jakubmeysner.legitnik.util.ClassSimpleNameLoggingTag
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.jakubmeysner.legitnik.data.settings.SettingsRepository
import com.jakubmeysner.legitnik.util.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.jakubmeysner.legitnik.util.MessageData


enum class EventType(val value: String) {
    NON_ZERO("PARKING_LOT_FREE_PLACES_BECAME_NON_ZERO"),
    CHANGED("PARKING_LOT_FREE_PLACES_CHANGED")
}

@AndroidEntryPoint
class MyFirebaseMessagingService @Inject constructor(private val notificationHelper: NotificationHelper): FirebaseMessagingService(), ClassSimpleNameLoggingTag {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(tag, "Refreshed token: $token")

        CoroutineScope(Dispatchers.IO).launch {
            settingsRepository.subscribeToFcmTopicsOnTokenRefresh()
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
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
