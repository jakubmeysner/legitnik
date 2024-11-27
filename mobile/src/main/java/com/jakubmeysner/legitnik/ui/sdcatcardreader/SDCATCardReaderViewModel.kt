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
import com.jakubmeysner.legitnik.domain.apdu.ApduTransceiver
import com.jakubmeysner.legitnik.domain.apdu.IsoDepApduTransceiver
import com.jakubmeysner.legitnik.domain.apdu.UsbReader
import com.jakubmeysner.legitnik.domain.apdu.UsbReaderTransceiver
import com.jakubmeysner.legitnik.domain.sdcatcard.SDCATCardData
import com.jakubmeysner.legitnik.domain.sdcatcard.readSDCATCard
import com.jakubmeysner.legitnik.domain.sdcatcard.toParsed
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

enum class SDCATCardReaderSnackbar {
    NFC_UNSUPPORTED_CARD,
    USB_UNSUPPORTED_DEVICE,
    READING_INTERFACE_ERROR,
    READING_ERROR,
}

data class SDCATCardReaderUiState(
    val selectedInterface: SDCATCardReaderInterface = SDCATCardReaderInterface.NFC,
    val snackbar: SDCATCardReaderSnackbar? = null,
    val selectedUsbDevice: UsbDevice? = null,
    val reading: Boolean = false,
    val cardData: SDCATCardData? = null,
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

    private var _reading = MutableStateFlow(false)

    private var _cardData = MutableStateFlow<SDCATCardData?>(null)

    val uiState: StateFlow<SDCATCardReaderUiState> = combine(
        _selectedInterface,
        _snackbar,
        _selectedUsbDevice,
        _reading,
        _cardData,
    ) { selectedInterface, snackbar, selectedUsbDevice, reading, cardData ->
        SDCATCardReaderUiState(
            selectedInterface = selectedInterface,
            snackbar = snackbar,
            selectedUsbDevice = selectedUsbDevice,
            reading = reading,
            cardData = cardData,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SDCATCardReaderUiState(),
    )

    fun selectInterface(inter: SDCATCardReaderInterface) {
        savedStateHandle[SELECTED_INTERFACE_KEY] = inter
    }

    fun onTagDiscovered(nfcTag: Tag) {
        try {
            _reading.update { true }
            val isoDepAvailable = nfcTag.techList.contains(IsoDep::class.qualifiedName)

            if (!isoDepAvailable) {
                _snackbar.update { SDCATCardReaderSnackbar.NFC_UNSUPPORTED_CARD }
                return
            }

            IsoDep.get(nfcTag).use { isoDep ->
                isoDep.connect()
                val apduTransceiver = IsoDepApduTransceiver(isoDep)
                scanCard(apduTransceiver)
            }
        } catch (exception: Exception) {
            Log.e(tag, "An exception occurred while trying to read card", exception)
            _snackbar.update { SDCATCardReaderSnackbar.READING_INTERFACE_ERROR }
        } finally {
            _reading.update { false }
        }
    }

    private fun onCardInserted(slotNum: Int) {
        try {
            Log.d(tag, "Card inserted into slot $slotNum")
            val reader = usbReader ?: return

            if (_selectedInterface.value != SDCATCardReaderInterface.USB) {
                return
            }

            _reading.update { true }

            reader.power(slotNum, Reader.CARD_COLD_RESET)
            reader.setProtocol(slotNum, Reader.PROTOCOL_T0 or Reader.PROTOCOL_T1)

            val apduTransceiver = UsbReaderTransceiver(reader, slotNum)
            scanCard(apduTransceiver)

            reader.power(slotNum, Reader.CARD_POWER_DOWN)
        } catch (exception: Exception) {
            Log.e(tag, "An exception occurred while trying to read card", exception)
            _snackbar.update { SDCATCardReaderSnackbar.READING_INTERFACE_ERROR }
        } finally {
            _reading.update { false }
        }
    }

    private fun scanCard(apduTransceiver: ApduTransceiver) {
        try {
            val rawData = readSDCATCard(apduTransceiver)
            val parsedData = rawData.toParsed()
            _cardData.update { SDCATCardData(rawData, parsedData) }
        } catch (exception: Exception) {
            Log.e(tag, "An exception occurred while reading card", exception)
            _snackbar.update { SDCATCardReaderSnackbar.READING_ERROR }
        }

    }

    fun createUsbReader(usbManager: UsbManager) {
        usbReader = UsbReader(usbManager).apply {
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
