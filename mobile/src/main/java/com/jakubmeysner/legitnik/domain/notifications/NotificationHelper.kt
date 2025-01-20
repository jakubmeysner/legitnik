package com.jakubmeysner.legitnik.domain.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import com.jakubmeysner.legitnik.MainActivity
import com.jakubmeysner.legitnik.R
import com.jakubmeysner.legitnik.data.notifications.ParkingLotCache
import com.jakubmeysner.legitnik.data.notifications.database.ParkingLotCacheDao
import com.jakubmeysner.legitnik.data.notifications.database.ParkingLotCacheEntity
import com.jakubmeysner.legitnik.data.parking.ParkingLotRepository
import com.jakubmeysner.legitnik.data.settings.SettingsRepository
import com.jakubmeysner.legitnik.services.EventType
import com.jakubmeysner.legitnik.util.ClassSimpleNameLoggingTag
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
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
    externalScope: CoroutineScope,
    private val parkingLotRepository: ParkingLotRepository,
    private val settingsRepository: SettingsRepository,
    private val parkingLotCacheDao: ParkingLotCacheDao,
) : ClassSimpleNameLoggingTag {
    companion object {
        private const val CHANNEL_FREE_PLACES_NON_ZERO = "free_places_non_zero"
        private const val CHANNEL_FREE_PLACES_CHANGED = "free_places_changed"
        private const val FREE_PLACES_CHANGED_NOTIFICATION_ID = 0
    }

    private val applicationContext = context.applicationContext

    private val notificationManager: NotificationManager =
        context.getSystemService() ?: throw IllegalStateException()

    private val scope = externalScope


    init {
        Log.d(tag, "Initializing NotificationHelper: init {} block")
        setUpNotificationChannels()
        externalScope.launch {

            if (parkingLotCacheDao.getAllFlow().first().isEmpty()) {
                //seed database
                initFollowedParkingLots()
            }
            observeSettingsChanges()
        }
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

    fun showNotification(messageData: MessageData) {
        scope.launch {
            Log.d(tag, "Received notification request: $messageData")
            when (messageData.event) {
                EventType.NON_ZERO.value -> {
                    showNonZeroNotification(messageData)
                }

                EventType.CHANGED.value -> {
                    Log.d(tag, "Processing CHANGED event")
                    try {
                        val parkingLotsBeforeUpdate = getFollowedParkingLots()
                        Log.d(tag, "Current followed parking lots: $parkingLotsBeforeUpdate")

                        showOngoingNotification(messageData)

                        val parkingLotsAfterUpdate = getFollowedParkingLots()
                        Log.d(tag, "Updated followed parking lots: $parkingLotsAfterUpdate")
                    } catch (e: Exception) {
                        Log.e(tag, "Error in showOngoingNotification: ${e.message}", e)
                        throw e
                    }
                }
            }
        }

    }

    private suspend fun showOngoingNotification(messageData: MessageData) {
        Log.d(tag, "Starting showOngoingNotification")
        val parkingLotsCache = getFollowedParkingLots()
        Log.d(tag, "Retrieved ${parkingLotsCache.size} followed parking lots")
        if (parkingLotsCache.isEmpty()) {
            Log.d(tag, "No followed parking lots found, removing ongoing notification")
            removeOngoingNotification()
            return
        }

        try {
            val parkingLotCache = parkingLotCacheDao.getOne(messageData.id)
                ?: throw Error("Parking lot cache not found with id = ${messageData.id}")
            Log.d(tag, "Found parking lot cache: $parkingLotCache")
            parkingLotCacheDao.update(
                ParkingLotCacheEntity(
                    messageData.id,
                    parkingLotCache.symbol,
                    messageData.freePlaces,
                    messageData.previousFreePlaces ?: messageData.freePlaces,
                    isFollowed = true
                )
            )
            Log.d(tag, "Updated parking lot cache in database")

            buildOngoingNotification()
            Log.d(tag, "Built and displayed ongoing notification")
        } catch (e: Exception) {
            Log.e(tag, "Error updating parking lot cache: ${e.message}", e)
            throw e
        }
    }

    private fun showNonZeroNotification(messageData: MessageData) {
        val parkingLot = parkingLotCacheDao.getOne(messageData.id)
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

    private suspend fun buildOngoingNotification() {
        Log.d(tag, "Starting buildOngoingNotification")
        val followedParkingLots = getFollowedParkingLots()
        Log.d(tag, "Retrieved ${followedParkingLots.size} parking lots for notification")
        try {
            val pendingIntent = PendingIntent.getActivity(
                applicationContext,
                0,
                Intent(applicationContext, MainActivity::class.java),
                flagUpdateCurrent(mutable = true)
            )
            val builder =
                NotificationCompat.Builder(applicationContext, CHANNEL_FREE_PLACES_CHANGED)
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
                                {
                                    "${it.symbol} - ${it.freePlaces} (${if (it.freePlaces - it.previousFreePlaces > 0) "+" else ""}" +
                                        "${(it.freePlaces - it.previousFreePlaces)})"
                                }
                        )
                    )
                    .setContentIntent(pendingIntent)
                    .setSilent(true)
                    .setOngoing(true)

            notificationManager.notify(FREE_PLACES_CHANGED_NOTIFICATION_ID, builder.build())
            Log.d(tag, "Notification updated!")
        } catch (e: Exception) {
            Log.e(tag, "Error building notification: ${e.message}", e)
            throw e
        }

    }

    private suspend fun getFollowedParkingLots(): List<ParkingLotCache> {
        return parkingLotCacheDao.getAllFlow().first().filter { it.isFollowed }
            .map { ParkingLotCache(it.id, it.symbol, it.freePlaces, it.previousFreePlaces) }
    }

    private suspend fun initFollowedParkingLots() {
        val parkingLots = parkingLotRepository.getParkingLots(false)
        val settings = settingsRepository.settingsFlow.first()
        val followedParkingLots = parkingLots
            .map { parkingLot ->
                ParkingLotCacheEntity(
                    parkingLot.id,
                    parkingLot.symbol,
                    parkingLot.freePlaces,
                    parkingLot.freePlaces,
                    settings.ongoingSettingsMap[parkingLot.id] ?: false
                )
            }
        parkingLotCacheDao.insertParkingLots(followedParkingLots)
    }

    private suspend fun updateCache() {
        val settings = settingsRepository.settingsFlow.first()
        Log.d(tag, "Updating cache")
        parkingLotCacheDao.updateParkingLots(parkingLotCacheDao.getAllFlow().first().map {
            Log.d(
                tag,
                "Updating parking ${it.symbol} | isFollowed = ${settings.ongoingSettingsMap[it.id] ?: false}"
            )
            ParkingLotCacheEntity(
                it.id,
                it.symbol,
                it.freePlaces,
                it.previousFreePlaces,
                settings.ongoingSettingsMap[it.id] ?: false
            )
        })
    }


    private suspend fun observeSettingsChanges() {
        settingsRepository.settingsFlow.collect { settings ->
            updateCache()
            if (settings.ongoingCategoryEnabled) {
                buildOngoingNotification()
            } else {
                removeOngoingNotification()
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
