package com.jakubmeysner.legitnik.ui.sdcatcardreader

import android.nfc.Tag
import android.nfc.tech.IsoDep
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.serialization.Serializable
import javax.inject.Inject

@Serializable
enum class SDCATCardReaderInterface {
    NFC,
    USB
}

@Serializable
enum class SDCATCardReaderSnackbar {
    NFC_UNSUPPORTED_CARD,
}

data class SDCATCardReaderUiState(
    val selectedInterface: SDCATCardReaderInterface = SDCATCardReaderInterface.NFC,
    val snackbar: SDCATCardReaderSnackbar? = null,
)

@HiltViewModel
class SDCATCardReaderViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _selectedInterface = savedStateHandle.getStateFlow(
        SELECTED_INTERFACE_KEY, SDCATCardReaderInterface.NFC
    )

    private val _snackbar = MutableStateFlow<SDCATCardReaderSnackbar?>(null)

    val uiState: StateFlow<SDCATCardReaderUiState> = combine(
        _selectedInterface,
        _snackbar,
    ) { selectedInterface, snackbar ->
        SDCATCardReaderUiState(
            selectedInterface = selectedInterface,
            snackbar = snackbar,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SDCATCardReaderUiState(),
    )

    fun selectInterface(inter: SDCATCardReaderInterface) {
        savedStateHandle[SELECTED_INTERFACE_KEY] = inter
    }

    fun onTagDiscovered(tag: Tag) {
        val isoDepAvailable = tag.techList.contains(IsoDep::class.qualifiedName)

        if (!isoDepAvailable) {
            _snackbar.update { SDCATCardReaderSnackbar.NFC_UNSUPPORTED_CARD }
            return
        }
    }

    fun onShownSnackbar() {
        _snackbar.update { null }
    }

    companion object {
        private const val SELECTED_INTERFACE_KEY = "selectedInterface"
    }
}
