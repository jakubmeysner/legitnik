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
        loadSavedParkingIds()
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

    private fun loadSavedParkingIds() {
        viewModelScope.launch {
            combine(
                settingsRepository.getSavedIdsForCategory(CategoryType.NOTIFICATION),
                settingsRepository.getSavedIdsForCategory(CategoryType.ONGOING)
            ) { notificationIds, ongoingIds ->
                (notificationIds + ongoingIds).distinct()
            }.collect { combinedIds ->
                _uiState.update { it.copy(savedParkingIds = combinedIds) }
            }
        }
    }

    fun isCategoryEnabled(category: CategoryType): Flow<Boolean> =
        settingsRepository.getCategoryState(category)

    fun toggleCategory(category: CategoryType, isEnabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.toggleCategory(category, isEnabled)
        }
    }

    private fun isSettingEnabled(id: String, category: CategoryType): Flow<Boolean> =
        settingsRepository.isSettingEnabled(id, category)

    fun toggleSetting(id: String, category: CategoryType, isEnabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.toggleSetting(id, category, isEnabled)
        }
    }

    fun getSettingsStateForCategory(category: CategoryType, ids: List<String>): Flow<Map<String, Boolean>> {
        return combine(
            ids.map { id ->
                isSettingEnabled(id, category).map { state -> id to state }
            }
        ) { states -> states.toMap() }
    }

    fun getLabelFromId(id: String): String {
        val parkingLot = _uiState.value.parkingLots?.find { it.id == id }
        return parkingLot?.symbol ?: id
    }
}
