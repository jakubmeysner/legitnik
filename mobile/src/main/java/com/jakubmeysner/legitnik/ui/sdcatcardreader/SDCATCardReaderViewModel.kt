package com.jakubmeysner.legitnik.ui.sdcatcardreader

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.Serializable
import javax.inject.Inject

@Serializable
enum class SDCATCardReaderInterface {
    NFC,
    USB
}

data class SDCATCardReaderUiState(
    val selectedInterface: SDCATCardReaderInterface,
)

@HiltViewModel
class SDCATCardReaderViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _selectedInterface = savedStateHandle.getStateFlow(
        SELECTED_INTERFACE_KEY, SDCATCardReaderInterface.NFC
    )

    val uiState: StateFlow<SDCATCardReaderUiState> = _selectedInterface.map { selectedInterface ->
        SDCATCardReaderUiState(
            selectedInterface = selectedInterface
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SDCATCardReaderUiState(
            selectedInterface = SDCATCardReaderInterface.NFC
        )
    )

    fun selectInterface(inter: SDCATCardReaderInterface) {
        savedStateHandle[SELECTED_INTERFACE_KEY] = inter
    }

    companion object {
        private const val SELECTED_INTERFACE_KEY = "selectedInterface"
    }
}
