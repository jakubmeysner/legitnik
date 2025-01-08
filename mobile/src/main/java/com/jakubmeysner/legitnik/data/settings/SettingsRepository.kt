package com.jakubmeysner.legitnik.data.settings

import android.content.Context
import android.util.Log
import com.jakubmeysner.legitnik.data.settings.SettingsProto.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import dagger.hilt.android.qualifiers.ApplicationContext
import com.google.firebase.messaging.FirebaseMessaging
import com.jakubmeysner.legitnik.data.parking.ParkingLot
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

enum class CategoryType {
    NOTIFICATION,
    ONGOING
}

class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firebaseMessaging: FirebaseMessaging
) {
    private val settingsFlow: Flow<Settings> = context.settingsDataStore.data

    fun getCategoryState(category: CategoryType): Flow<Boolean> = settingsFlow.map { settings ->
        when (category) {
            CategoryType.NOTIFICATION -> settings.notificationCategoryEnabled
            CategoryType.ONGOING -> settings.ongoingCategoryEnabled
        }
    }

    suspend fun toggleCategory(category: CategoryType, isEnabled: Boolean) {
        context.settingsDataStore.updateData { currentSettings ->
            val updatedSettings = when (category) {
                CategoryType.NOTIFICATION -> currentSettings.toBuilder()
                    .setNotificationCategoryEnabled(isEnabled)
                    .build()
                CategoryType.ONGOING -> currentSettings.toBuilder()
                    .setOngoingCategoryEnabled(isEnabled)
                    .build()
            }
            updatedSettings
        }

        val categoryIds = getSavedIdsForCategory(category).firstOrNull() ?: emptyList()

        if(!isEnabled) {
            categoryIds.forEach { id ->
                isSettingEnabled(id, category).firstOrNull()?.let { isEnabled ->
                    if (isEnabled)
                        manageFcmSubscriptionForSetting(id, category, false)
                }
            }
        }
        else {
            categoryIds.forEach { id ->
                isSettingEnabled(id, category).firstOrNull()?.let { isEnabled ->
                    if (isEnabled)
                        toggleSetting(id, category, true)
                }
            }
        }
    }

    fun isSettingEnabled(id: String, category: CategoryType): Flow<Boolean> =
        settingsFlow.map { settings ->
            when (category) {
                CategoryType.NOTIFICATION -> settings.notificationSettingsMap[id] ?: false
                CategoryType.ONGOING -> settings.ongoingSettingsMap[id] ?: false
            }
        }

    suspend fun toggleSetting(id: String, category: CategoryType, isEnabled: Boolean) {
        context.settingsDataStore.updateData { currentSettings ->
            val builder = currentSettings.toBuilder()
            when (category) {
                CategoryType.NOTIFICATION -> builder.putNotificationSettings(id, isEnabled)
                CategoryType.ONGOING -> builder.putOngoingSettings(id, isEnabled)
            }
            manageFcmSubscriptionForSetting(id, category, isEnabled)
            builder.build()
        }
    }

    fun getSavedIdsForCategory(category: CategoryType): Flow<List<String>> {
        return settingsFlow.map { settings ->
            when (category) {
                CategoryType.NOTIFICATION -> settings.notificationSettingsMap.keys.toList()
                CategoryType.ONGOING -> settings.ongoingSettingsMap.keys.toList()
            }
        }
    }

    private fun manageFcmSubscriptionForSetting(id: String, category: CategoryType, isEnabled: Boolean) {
        val topic = when (category) {
            CategoryType.NOTIFICATION -> "parking-lots-non-zero-$id"
            CategoryType.ONGOING -> "parking-lots-free-places-changed-$id"
        }
        if (isEnabled) {
            firebaseMessaging.subscribeToTopic(topic)
            Log.d("FCM", "Subscribed to: $topic")
        } else {
            firebaseMessaging.unsubscribeFromTopic(topic)
            Log.d("FCM", "Unsubscribed from: $topic")
        }
    }

    suspend fun subscribeToFcmTopicsOnTokenRefresh() {
        coroutineScope {
            launch {
                getCategoryState(CategoryType.NOTIFICATION).firstOrNull()?.let { isEnabled ->
                    toggleCategory(CategoryType.NOTIFICATION, isEnabled)
                }
            }

            launch {
                getCategoryState(CategoryType.ONGOING).firstOrNull()?.let { isEnabled ->
                    toggleCategory(CategoryType.ONGOING, isEnabled)
                }
            }
        }

        combine(
            getSavedIdsForCategory(CategoryType.NOTIFICATION),
            getSavedIdsForCategory(CategoryType.ONGOING)
        ) { notificationIds, ongoingIds ->
            notificationIds.forEach { id ->
                isSettingEnabled(id, CategoryType.NOTIFICATION).firstOrNull()?.let { isEnabled ->
                    toggleSetting(id, CategoryType.NOTIFICATION, isEnabled)
                }
            }
            ongoingIds.forEach { id ->
                isSettingEnabled(id, CategoryType.ONGOING).firstOrNull()?.let { isEnabled ->
                    toggleSetting(id, CategoryType.ONGOING, isEnabled)
                }
            }
        }.firstOrNull()
    }

    suspend fun cacheParkingLotSymbols(parkingLots: List<ParkingLot>) {
        context.settingsDataStore.updateData { currentSettings ->
            val builder = currentSettings.toBuilder()
            parkingLots.forEach { parkingLot ->
                builder.putParkingLotSymbols(parkingLot.id, parkingLot.symbol)
            }
            builder.build()
        }
    }

    fun getCachedParkingLotSymbol(id: String): Flow<String?> {
        return settingsFlow.map { settings ->
            settings.parkingLotSymbols.getOrDefault(id, null)
        }
    }
}
