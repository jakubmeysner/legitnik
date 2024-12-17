package com.jakubmeysner.legitnik.ui.settings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakubmeysner.legitnik.ui.parking.list.ParkingLotUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.jakubmeysner.legitnik.data.parking.ParkingLotRepository
import com.jakubmeysner.legitnik.data.settings.*

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val parkingLotRepository: ParkingLotRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ParkingLotUiState())
    val uiState: StateFlow<ParkingLotUiState> = _uiState

    init {
        loadParkingLots()
    }

    private fun loadParkingLots(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(loading = true, error = false) }
                val parkingLots = parkingLotRepository.getParkingLots(forceRefresh)
                _uiState.update { it.copy(loading = false, parkingLots = parkingLots, error = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(loading = false, error = true) }
            }
        }
    }

    fun isCategoryEnabled(category: SettingCategory): Flow<Boolean> =
        settingsRepository.isCategoryEnabled(category)

    fun toggleCategory(category: SettingCategory, isEnabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.toggleCategory(category, isEnabled)
        }
    }

    fun isSettingEnabled(label: String, category: SettingCategory): Flow<Boolean> =
        settingsRepository.isSettingEnabled(label, category)

    fun toggleSetting(label: String, category: SettingCategory, isEnabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.toggleSetting(label, category, isEnabled)
        }
    }

    fun getLabelsFromSettingsCategory(category: SettingCategory): Flow<List<String>> {
        return settingsRepository.getSavedLabelsForCategory(category)
    }
}


