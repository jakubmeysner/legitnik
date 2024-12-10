package com.jakubmeysner.legitnik.ui.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakubmeysner.legitnik.ui.parking.list.ParkingLotUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.jakubmeysner.legitnik.data.parking.ParkingLotRepository

val Context.dataStore by preferencesDataStore(name = "settings")

enum class SettingCategory {
    NOTIFICATION,
    ONGOING
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context,
    private val parkingLotRepository: ParkingLotRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ParkingLotUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadParkingLots()
    }

    private fun loadParkingLots(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            try {
                _uiState.update { currentUiState ->
                    currentUiState.copy(loading = true, error = false)
                }
                val parkingLots = parkingLotRepository.getParkingLots(forceRefresh)
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        loading = false,
                        parkingLots = parkingLots,
                        error = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        error = true,
                        loading = false
                    )
                }
            }
        }
    }

    private fun categoryKey(category: SettingCategory) =
        booleanPreferencesKey("${category.name.lowercase()}_category_enabled")

    private fun settingKey(label: String, category: SettingCategory) =
        booleanPreferencesKey("${category.name.lowercase()}_$label")

    fun isCategoryEnabled(category: SettingCategory): Flow<Boolean> {
        return context.dataStore.data
            .map { preferences -> preferences[categoryKey(category)] ?: false }
            .distinctUntilChanged()
    }

    fun toggleCategory(isEnabled: Boolean, category: SettingCategory) {
        viewModelScope.launch {
            context.dataStore.edit { preferences ->
                preferences[categoryKey(category)] = isEnabled
            }
        }
    }

    fun isSettingEnabled(label: String, category: SettingCategory): Flow<Boolean> {
        return context.dataStore.data
            .map { preferences -> preferences[settingKey(label, category)] ?: false }
            .distinctUntilChanged()
    }

    fun toggleSetting(label: String, isEnabled: Boolean, category: SettingCategory) {
        viewModelScope.launch {
            context.dataStore.edit { preferences ->
                preferences[settingKey(label, category)] = isEnabled
            }
        }
    }
}

