package com.jakubmeysner.legitnik.data.settings

import android.content.Context
import com.jakubmeysner.legitnik.data.settings.SettingsProto.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import dagger.hilt.android.qualifiers.ApplicationContext

enum class CategoryType {
    NOTIFICATION,
    ONGOING
}

class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
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
            when (category) {
                CategoryType.NOTIFICATION -> currentSettings.toBuilder()
                    .setNotificationCategoryEnabled(isEnabled)
                    .build()
                CategoryType.ONGOING -> currentSettings.toBuilder()
                    .setOngoingCategoryEnabled(isEnabled)
                    .build()
            }
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
}
