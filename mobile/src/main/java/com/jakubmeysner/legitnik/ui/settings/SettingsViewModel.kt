package com.jakubmeysner.legitnik.ui.settings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakubmeysner.legitnik.data.parking.ParkingLotRepository
import com.jakubmeysner.legitnik.data.settings.CategoryType
import com.jakubmeysner.legitnik.data.settings.SettingsRepository
import com.jakubmeysner.legitnik.ui.parking.list.ParkingLotListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val parkingLotRepository: ParkingLotRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ParkingLotListUiState())
    val uiState: StateFlow<ParkingLotListUiState> = _uiState

    init {
        loadParkingLots()
        loadSavedParkingIds()
    }

    private fun loadParkingLots(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(loading = true, error = false) }
                val parkingLots = parkingLotRepository.getParkingLots(forceRefresh)
                _uiState.update {
                    it.copy(
                        loading = false,
                        parkingLots = parkingLots,
                        error = false
                    )
                }
                settingsRepository.cacheParkingLotSymbols(parkingLots)
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

    fun getSettingsStateForCategory(
        category: CategoryType,
        ids: List<String>,
    ): Flow<Map<String, Boolean>> {
        return combine(
            ids.map { id ->
                isSettingEnabled(id, category).map { state -> id to state }
            }
        ) { states -> states.toMap() }
    }

    suspend fun getLabelFromCache(id: String): String {
        val cachedSymbol = settingsRepository.getCachedParkingLotSymbol(id).firstOrNull()
        return cachedSymbol ?: id
    }

    fun getLabelFromId(id: String): String {
        val parkingLot = _uiState.value.parkingLots?.find { it.id == id }
        return parkingLot?.symbol ?: id
    }
}
