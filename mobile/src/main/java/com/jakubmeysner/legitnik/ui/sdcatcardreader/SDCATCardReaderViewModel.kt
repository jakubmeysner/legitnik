package com.jakubmeysner.legitnik.ui.sdcatcardreader

import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acs.smartcard.Reader
import com.jakubmeysner.legitnik.util.ClassSimpleNameLoggingTag
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
    USB_UNSUPPORTED_DEVICE,
}

data class SDCATCardReaderUiState(
    val selectedInterface: SDCATCardReaderInterface = SDCATCardReaderInterface.NFC,
    val snackbar: SDCATCardReaderSnackbar? = null,
    val selectedUsbDevice: UsbDevice? = null,
)

@HiltViewModel
class SDCATCardReaderViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel(), ClassSimpleNameLoggingTag {
    private val _selectedInterface = savedStateHandle.getStateFlow(
        SELECTED_INTERFACE_KEY, SDCATCardReaderInterface.NFC
    )

    private val _snackbar = MutableStateFlow<SDCATCardReaderSnackbar?>(null)

    private var usbReader: Reader? = null
    private var _selectedUsbDevice = MutableStateFlow<UsbDevice?>(null)

    val uiState: StateFlow<SDCATCardReaderUiState> = combine(
        _selectedInterface,
        _snackbar,
        _selectedUsbDevice,
    ) { selectedInterface, snackbar, selectedUsbDevice ->
        SDCATCardReaderUiState(
            selectedInterface = selectedInterface,
            snackbar = snackbar,
            selectedUsbDevice = selectedUsbDevice,
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

    private fun onCardInserted(slotNum: Int) {
        Log.d(tag, "Card inserted into slot $slotNum")
    }

    fun createUsbReader(usbManager: UsbManager) {
        usbReader = Reader(usbManager).apply {
            setOnStateChangeListener { slotNum, previousState, currentState ->
                if (previousState == Reader.CARD_ABSENT && currentState == Reader.CARD_PRESENT) {
                    onCardInserted(slotNum)
                }
            }
        }
    }

    fun selectUsbDevice(usbDevice: UsbDevice?) {
        if (usbDevice == null) {
            usbReader?.close()
            _selectedUsbDevice.update { null }
            return
        }

        val reader = usbReader

        if (reader == null || !reader.isSupported(usbDevice)) {
            _snackbar.update { SDCATCardReaderSnackbar.USB_UNSUPPORTED_DEVICE }
            return
        }

        try {
            reader.open(usbDevice)
            _selectedUsbDevice.update { usbDevice }
        } catch (e: Exception) {
            Log.e(tag, "An exception occurred while trying to open USB device", e)
            _snackbar.update { SDCATCardReaderSnackbar.USB_UNSUPPORTED_DEVICE }
        }
    }

    fun onShownSnackbar() {
        _snackbar.update { null }
    }

    override fun onCleared() {
        if (usbReader?.isOpened == true) {
            usbReader?.close()
        }
    }

    companion object {
        private const val SELECTED_INTERFACE_KEY = "selectedInterface"
    }
}
