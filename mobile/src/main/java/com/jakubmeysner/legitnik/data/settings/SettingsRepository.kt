package com.jakubmeysner.legitnik.data.settings

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.dataStore by preferencesDataStore(name = "settings")

enum class SettingCategory {
    NOTIFICATION,
    ONGOING
}

class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private fun categoryKey(category: SettingCategory): Preferences.Key<Boolean> =
        booleanPreferencesKey("${category.name.lowercase()}_category_enabled")

    private fun settingKey(label: String, category: SettingCategory): Preferences.Key<Boolean> =
        booleanPreferencesKey("${category.name.lowercase()}_$label")

    fun isCategoryEnabled(category: SettingCategory): Flow<Boolean> =
        context.dataStore.data.map { preferences ->
            preferences[categoryKey(category)] ?: false
        }

    suspend fun toggleCategory(category: SettingCategory, isEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[categoryKey(category)] = isEnabled
        }
    }

    fun isSettingEnabled(label: String, category: SettingCategory): Flow<Boolean> =
        context.dataStore.data.map { preferences ->
            preferences[settingKey(label, category)] ?: false
        }

    suspend fun toggleSetting(label: String, category: SettingCategory, isEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[settingKey(label, category)] = isEnabled
        }
    }

    fun getSavedLabelsForCategory(category: SettingCategory): Flow<List<String>> {
        return context.dataStore.data.map { preferences ->
            preferences.asMap().keys
                .filter { it.name.startsWith("${category.name.lowercase()}_") && !it.name.endsWith("_category_enabled") }
                .map { it.name.substringAfter("${category.name.lowercase()}_") }
        }
    }
}
