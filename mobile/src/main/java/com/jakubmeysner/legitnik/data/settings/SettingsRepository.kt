package com.jakubmeysner.legitnik.data.settings

import android.content.Context
import android.util.Log
import com.jakubmeysner.legitnik.data.settings.SettingsProto.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import dagger.hilt.android.qualifiers.ApplicationContext
import com.google.firebase.messaging.FirebaseMessaging
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
            manageFcmSubscription(category, isEnabled)
            updatedSettings
        }
    }

    fun isSettingEnabled(label: String, category: CategoryType): Flow<Boolean> =
        settingsFlow.map { settings ->
            when (category) {
                CategoryType.NOTIFICATION -> settings.notificationSettingsMap[label] ?: false
                CategoryType.ONGOING -> settings.ongoingSettingsMap[label] ?: false
            }
        }

    suspend fun toggleSetting(label: String, category: CategoryType, isEnabled: Boolean) {
        context.settingsDataStore.updateData { currentSettings ->
            val builder = currentSettings.toBuilder()
            when (category) {
                CategoryType.NOTIFICATION -> builder.putNotificationSettings(label, isEnabled)
                CategoryType.ONGOING -> builder.putOngoingSettings(label, isEnabled)
            }
            manageFcmSubscriptionForSetting(label, category, isEnabled)
            builder.build()
        }
    }

    fun getSavedLabelsForCategory(category: CategoryType): Flow<List<String>> {
        return settingsFlow.map { settings ->
            when (category) {
                CategoryType.NOTIFICATION -> settings.notificationSettingsMap.keys.toList()
                CategoryType.ONGOING -> settings.ongoingSettingsMap.keys.toList()
            }
        }
    }

    private fun manageFcmSubscription(category: CategoryType, isEnabled: Boolean) {
        val topic = when (category) {
            CategoryType.NOTIFICATION -> "notifications"
            CategoryType.ONGOING -> "ongoing"
        }
        if (isEnabled) {
            firebaseMessaging.subscribeToTopic(topic)
            Log.d("FCM", "Subscribed to: $topic")
        } else {
            firebaseMessaging.unsubscribeFromTopic(topic)
            Log.d("FCM", "Unsubscribed from: $topic")
        }
    }

    private fun manageFcmSubscriptionForSetting(label: String, category: CategoryType, isEnabled: Boolean) {
        val topic = when (category) {
            CategoryType.NOTIFICATION -> "notification_$label"
            CategoryType.ONGOING -> "ongoing_$label"
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
            getSavedLabelsForCategory(CategoryType.NOTIFICATION),
            getSavedLabelsForCategory(CategoryType.ONGOING)
        ) { notificationLabels, ongoingLabels ->
            notificationLabels.forEach { label ->
                isSettingEnabled(label, CategoryType.NOTIFICATION).firstOrNull()?.let { isEnabled ->
                    toggleSetting(label, CategoryType.NOTIFICATION, isEnabled)
                }
            }
            ongoingLabels.forEach { label ->
                isSettingEnabled(label, CategoryType.ONGOING).firstOrNull()?.let { isEnabled ->
                    toggleSetting(label, CategoryType.ONGOING, isEnabled)
                }
            }
        }.firstOrNull()
    }
}
