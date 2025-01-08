package com.jakubmeysner.legitnik.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.WorkerThread
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import com.jakubmeysner.legitnik.EventType
import com.jakubmeysner.legitnik.R
import com.jakubmeysner.legitnik.ui.settings.SettingsViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

data class MessageData(
    val event: String,
    val id: String,
    val freePlaces: Int,
    val previousFreePlaces: Int?,
)

class NotificationHelper @Inject constructor(@ApplicationContext context: Context) {
    companion object {
        private const val CHANNEL_FREE_PLACES_NON_ZERO = "free_places_non_zero"
        private const val CHANNEL_FREE_PLACES_CHANGED = "free_places_changed"
    }

    private val applicationContext = context.applicationContext

    private val notificationManager: NotificationManager =
        context.getSystemService() ?: throw IllegalStateException()

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
                NotificationManager.IMPORTANCE_LOW,
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

    @WorkerThread
    fun showNotification(messageData: MessageData) {
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            Intent(applicationContext, SettingsViewModel::class.java),
            flagUpdateCurrent(mutable = true)
        )
        when (messageData.event) {
            EventType.NON_ZERO.value -> {
                //TODO use string resources for notifications
                val builder =
                    NotificationCompat.Builder(applicationContext, CHANNEL_FREE_PLACES_NON_ZERO)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Free places available on parking (id=${messageData.id})")
                        .setContentText("Free places on parking lot (id=${messageData.id}) became non zero: current free places = ${messageData.freePlaces}")
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                notificationManager.notify(messageData.id.toInt(), builder.build())
            }

            EventType.CHANGED.value -> {

            }
        }
    }
}
