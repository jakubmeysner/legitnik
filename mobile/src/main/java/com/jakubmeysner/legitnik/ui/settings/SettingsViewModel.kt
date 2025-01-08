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
        loadSavedParkingLabels()
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

    private fun loadSavedParkingLabels() {
        viewModelScope.launch {
            combine(
                settingsRepository.getSavedLabelsForCategory(CategoryType.NOTIFICATION),
                settingsRepository.getSavedLabelsForCategory(CategoryType.ONGOING)
            ) { notificationLabels, ongoingLabels ->
                (notificationLabels + ongoingLabels).distinct()
            }.collect { combinedLabels ->
                _uiState.update { it.copy(savedParkingLabels = combinedLabels) }
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

    private fun isSettingEnabled(label: String, category: CategoryType): Flow<Boolean> =
        settingsRepository.isSettingEnabled(label, category)

    fun toggleSetting(label: String, category: CategoryType, isEnabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.toggleSetting(label, category, isEnabled)
        }
    }

    fun getSettingsStateForCategory(category: CategoryType, labels: List<String>): Flow<Map<String, Boolean>> {
        return combine(
            labels.map { label ->
                isSettingEnabled(label, category).map { state -> label to state }
            }
        ) { states -> states.toMap() }
    }
}
