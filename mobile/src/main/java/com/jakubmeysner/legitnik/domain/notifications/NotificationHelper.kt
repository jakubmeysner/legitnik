package com.jakubmeysner.legitnik.domain.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import com.jakubmeysner.legitnik.MainActivity
import com.jakubmeysner.legitnik.R
import com.jakubmeysner.legitnik.data.parking.ParkingLot
import com.jakubmeysner.legitnik.data.parking.ParkingLotRepository
import com.jakubmeysner.legitnik.data.settings.SettingsRepository
import com.jakubmeysner.legitnik.services.EventType
import com.jakubmeysner.legitnik.util.ClassSimpleNameLoggingTag
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
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


    private val scope = CoroutineScope(Dispatchers.Main)

    private val parkingLotCache = mutableMapOf<String, ParkingLotCache>()

    private data class ParkingLotCache(
        val id: String,
        val symbol: String,
        var freePlaces: Int,
        var previousFreePlaces: Int,
    )

    init {
        setUpNotificationChannels()
        observeSettingsChanges()
    }

    private fun setUpNotificationChannels() {
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

    suspend fun showNotification(messageData: MessageData) {
        when (messageData.event) {
            EventType.NON_ZERO.value -> {
                showNonZeroNotification(messageData)
            }

            EventType.CHANGED.value -> {
                showOngoingNotification(messageData)
            }
        }
    }

    private fun showOngoingNotification(messageData: MessageData) {
        if (parkingLotCache.isEmpty()) {
            removeOngoingNotification()
            return
        }

        parkingLotCache[messageData.id]?.let { cached ->
            cached.freePlaces = messageData.freePlaces
            cached.previousFreePlaces = messageData.previousFreePlaces ?: messageData.freePlaces
        }

        buildOngoingNotification()
    }

    private suspend fun showNonZeroNotification(messageData: MessageData) {
        val parkingLot = parkingLotRepository.getParkingLot(messageData.id)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            Intent(applicationContext, MainActivity::class.java),
            flagUpdateCurrent(mutable = true)
        )

        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_FREE_PLACES_NON_ZERO)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(
                applicationContext.getString(
                    R.string.notification_helper_non_zero_notification_title,
                    parkingLot?.symbol
                )
            )
            .setContentText("Free places = ${messageData.freePlaces}")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(133712331 + messageData.id.toInt(), builder.build())
    }

    private fun buildOngoingNotification() {
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            Intent(applicationContext, MainActivity::class.java),
            flagUpdateCurrent(mutable = true)
        )

        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_FREE_PLACES_CHANGED)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(
                applicationContext.getString(
                    R.string.notification_helper_changed_notification_title
                )
            )
            .setContentText(
                parkingLotCache.values
                    .joinToString(separator = ", ") { "${it.symbol} - ${it.freePlaces}" }
            )
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    parkingLotCache.values
                        .joinToString(separator = "\n") { "${it.symbol} - ${it.freePlaces} (${it.freePlaces - it.previousFreePlaces})" }
                )
            )
            .setContentIntent(pendingIntent)
            .setSilent(true)
            .setOngoing(true)

        notificationManager.notify(FREE_PLACES_CHANGED_NOTIFICATION_ID, builder.build())
    }

    private suspend fun getFollowedParkingLots(): List<ParkingLot> {
        val parkingLots = parkingLotRepository.getParkingLots()
        val settings = settingsRepository.settingsFlow.first()
        val followedParkingLots = parkingLots
            .filter { settings.ongoingSettingsMap[it.id] ?: false }
        return followedParkingLots
    }

    private suspend fun initializeCache() {
        val followedParkingLots = getFollowedParkingLots()
        parkingLotCache.clear()
        followedParkingLots.forEach { parkingLot ->
            parkingLotCache[parkingLot.id] = ParkingLotCache(
                id = parkingLot.id,
                symbol = parkingLot.symbol,
                freePlaces = parkingLot.freePlaces,
                previousFreePlaces = parkingLot.freePlaces
            )
        }
    }

    private fun observeSettingsChanges() {
        scope.launch {
            settingsRepository.settingsFlow.collect { settings ->
                if (settings.ongoingCategoryEnabled) {
                    initializeCache()
                    buildOngoingNotification()
                } else {
                    parkingLotCache.clear()
                    removeOngoingNotification()
                }
            }
        }
    }

    private fun removeOngoingNotification() {
        notificationManager.cancel(FREE_PLACES_CHANGED_NOTIFICATION_ID)
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
}
