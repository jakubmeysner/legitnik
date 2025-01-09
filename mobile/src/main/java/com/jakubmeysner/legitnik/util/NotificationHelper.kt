package com.jakubmeysner.legitnik.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import com.jakubmeysner.legitnik.EventType
import com.jakubmeysner.legitnik.MainActivity
import com.jakubmeysner.legitnik.R
import com.jakubmeysner.legitnik.data.parking.ParkingLotRepository
import com.jakubmeysner.legitnik.data.settings.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject

data class MessageData(
    val event: String,
    val id: String,
    val freePlaces: Int,
    val previousFreePlaces: Int?,
)


class NotificationHelper @Inject constructor(
    @ApplicationContext context: Context,
    private val parkingLotRepository: ParkingLotRepository,
    private val settingsRepository: SettingsRepository,
) : ClassSimpleNameLoggingTag {
    companion object {
        private const val CHANNEL_FREE_PLACES_NON_ZERO = "free_places_non_zero"
        private const val CHANNEL_FREE_PLACES_CHANGED = "free_places_changed"
        private const val FREE_PLACES_CHANGED_NOTIFICATION_ID = 0
    }

    private val applicationContext = context.applicationContext

    private val notificationManager: NotificationManager =
        context.getSystemService() ?: throw IllegalStateException()


    fun removeFreePlacesChangedNotification() {
        notificationManager.cancel(FREE_PLACES_CHANGED_NOTIFICATION_ID)
    }
    fun setUpNotificationChannels() {
        if (notificationManager.getNotificationChannel(CHANNEL_FREE_PLACES_NON_ZERO) == null) {
            addChannel(
                CHANNEL_FREE_PLACES_NON_ZERO,
                applicationContext.getString(R.string.notification_helper_channel_free_places_non_zero_name),
                NotificationManager.IMPORTANCE_DEFAULT,
                applicationContext.getString(R.string.notification_helper_channel_free_places_non_zero_description)
            )
        }
        if (notificationManager.getNotificationChannel(CHANNEL_FREE_PLACES_CHANGED) == null) {
            addChannel(
                CHANNEL_FREE_PLACES_CHANGED,
                applicationContext.getString(R.string.notification_helper_channel_free_places_changed_name),
                NotificationManager.IMPORTANCE_MIN,
                applicationContext.getString(R.string.notification_helper_channel_free_places_changed_description)
            )
        }
    }

    private fun addChannel(
        channel: String,
        name: String,
        importance: Int,
        channelDescription: String,
    ) {
        notificationManager.createNotificationChannel(
            NotificationChannel(
                channel,
                name,
                importance,
            ).apply {
                description = channelDescription
            },
        )
    }

    private fun flagUpdateCurrent(mutable: Boolean): Int {
        return if (mutable) {
            if (Build.VERSION.SDK_INT >= 31) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        }
    }

    suspend fun showNotification(messageData: MessageData) {
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            Intent(applicationContext, MainActivity::class.java),
            flagUpdateCurrent(mutable = true)
        )
        when (messageData.event) {
            EventType.NON_ZERO.value -> {
                val parkingLot = parkingLotRepository.getParkingLot(messageData.id)
                //TODO use string resources for notifications
                val builder =
                    NotificationCompat.Builder(applicationContext, CHANNEL_FREE_PLACES_NON_ZERO)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle(
                            applicationContext.getString(
                                R.string.notification_helper_non_zero_notification_title,
                                parkingLot?.symbol
                            )
                        )
                        .setContentText(
                            "Free places = ${messageData.freePlaces}"
                        )
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                notificationManager.notify(133712331 + messageData.id.toInt(), builder.build())
            }

            EventType.CHANGED.value -> {
                val parkingLots = parkingLotRepository.getParkingLots()
                val settings = settingsRepository.settingsFlow.first()
                val followedParkingLots = parkingLots
                    .filter { settings.ongoingSettingsMap[it.id] ?: false }
                val builder =
                    NotificationCompat.Builder(applicationContext, CHANNEL_FREE_PLACES_NON_ZERO)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle(
                            applicationContext.getString(
                                R.string.notification_helper_changed_notification_title
                            )
                        )
                        .setContentText(
                            followedParkingLots
                                .joinToString(separator = ", ") { "${it.symbol} - ${it.freePlaces}" }
                        )
                        .setStyle(
                            NotificationCompat.BigTextStyle().bigText(
                                followedParkingLots
                                    .joinToString(separator = "\n")
                                    { "${it.symbol} - ${it.freePlaces}" }
                            )
                        )
                        .setContentIntent(pendingIntent)
                        .setSilent(true)
                        .setOngoing(true)
                notificationManager.notify(FREE_PLACES_CHANGED_NOTIFICATION_ID, builder.build())
            }
        }
    }
}
