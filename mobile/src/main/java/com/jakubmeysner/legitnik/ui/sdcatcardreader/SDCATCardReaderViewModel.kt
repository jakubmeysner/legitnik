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
import com.jakubmeysner.legitnik.data.sdcatcard.SDCATCardData
import com.jakubmeysner.legitnik.data.sdcatcard.SDCATCardRepository
import com.jakubmeysner.legitnik.data.sdcatcard.SDCATCardValidationResult
import com.jakubmeysner.legitnik.data.sdcatcard.SDCATCardValidator
import com.jakubmeysner.legitnik.data.sdcatcard.getHash
import com.jakubmeysner.legitnik.domain.apdu.ApduTransceiver
import com.jakubmeysner.legitnik.domain.apdu.IsoDepApduTransceiver
import com.jakubmeysner.legitnik.domain.apdu.UsbReader
import com.jakubmeysner.legitnik.domain.apdu.UsbReaderTransceiver
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
import kotlinx.coroutines.launch
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
    VALIDATION_ERROR,
    SAVING_ERROR,
    SAVING_ERROR_DUPLICATE,
    SAVING_SUCCESS
}

data class SDCATCardReaderUiState(
    val selectedInterface: SDCATCardReaderInterface = SDCATCardReaderInterface.NFC,
    val snackbar: SDCATCardReaderSnackbar? = null,
    val selectedUsbDevice: UsbDevice? = null,
    val reading: Boolean = false,
    val isSaved: Boolean = false,
    val cardData: SDCATCardData? = null,
    val cardValidationResult: SDCATCardValidationResult? = null,
    val cardValidationDetailsDialogOpened: Boolean = false,
)

@HiltViewModel
class SDCATCardReaderViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val cardValidator: SDCATCardValidator,
    private val cardRepository: SDCATCardRepository,
) : ViewModel(), ClassSimpleNameLoggingTag {
    private var usbReader: Reader? = null

    private val _selectedInterface = savedStateHandle.getStateFlow(
        SELECTED_INTERFACE_KEY, SDCATCardReaderInterface.NFC
    )

    private var _uiState = MutableStateFlow(SDCATCardReaderUiState())

    val uiState: StateFlow<SDCATCardReaderUiState> = combine(
        _uiState,
        _selectedInterface,
    ) { uiState, selectedInterface ->
        uiState.copy(
            selectedInterface = selectedInterface,
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
            _uiState.update { it.copy(reading = true) }
            val isoDepAvailable = nfcTag.techList.contains(IsoDep::class.qualifiedName)

            if (!isoDepAvailable) {
                _uiState.update { it.copy(snackbar = SDCATCardReaderSnackbar.NFC_UNSUPPORTED_CARD) }
                return
            }

            IsoDep.get(nfcTag).use { isoDep ->
                isoDep.connect()
                val apduTransceiver = IsoDepApduTransceiver(isoDep)
                scanCard(apduTransceiver)
            }

            _uiState.update { it.copy(reading = false) }
            validateCard()
        } catch (exception: Exception) {
            Log.e(tag, "An exception occurred while trying to read card", exception)
            _uiState.update {
                it.copy(
                    reading = false,
                    snackbar = SDCATCardReaderSnackbar.READING_INTERFACE_ERROR,
                )
            }
        }
    }

    fun saveCard() {
        viewModelScope.launch {
            try {
                val rawData = _uiState.value.cardData?.rawData
                if (rawData != null) {
                    cardRepository.addCard(rawData)
                    _uiState.update {
                        it.copy(
                            snackbar = SDCATCardReaderSnackbar.SAVING_SUCCESS,
                            isSaved = true
                        )
                    }
                }
            } catch (exception: Exception) {
                Log.e(tag, "An exception occurred while trying to save card", exception)
                _uiState.update {
                    it.copy(
                        snackbar = SDCATCardReaderSnackbar.SAVING_ERROR,
                    )
                }
            }
        }
    }

    private fun checkIfCardIsSaved() {
        viewModelScope.launch {
            val rawData = _uiState.value.cardData?.rawData
            if (rawData != null)
                if (cardRepository.getCardByHash(rawData.getHash()) != null) {
                    _uiState.update {
                        it.copy(
                            isSaved = true,
                            snackbar = SDCATCardReaderSnackbar.SAVING_ERROR_DUPLICATE
                        )
                    }
                }
        }
    }


    private fun onCardInserted(slotNum: Int) {
        try {
            Log.d(tag, "Card inserted into slot $slotNum")
            val reader = usbReader ?: return

            if (_selectedInterface.value != SDCATCardReaderInterface.USB) {
                return
            }

            _uiState.update { it.copy(reading = true) }

            if (!reader.isOpened && uiState.value.selectedUsbDevice != null) {
                reader.open(uiState.value.selectedUsbDevice)
            }

            reader.power(slotNum, Reader.CARD_COLD_RESET)
            reader.setProtocol(slotNum, Reader.PROTOCOL_T0 or Reader.PROTOCOL_T1)

            val apduTransceiver = UsbReaderTransceiver(reader, slotNum)
            scanCard(apduTransceiver)

            reader.power(slotNum, Reader.CARD_POWER_DOWN)
            _uiState.update { it.copy(reading = false) }
            validateCard()
        } catch (exception: Exception) {
            Log.e(tag, "An exception occurred while trying to read card", exception)
            _uiState.update {
                it.copy(
                    reading = false,
                    snackbar = SDCATCardReaderSnackbar.READING_INTERFACE_ERROR,
                )
            }
        }
    }

    private fun scanCard(apduTransceiver: ApduTransceiver) {
        try {
            val rawData = readSDCATCard(apduTransceiver)
            val parsedData = rawData.toParsed()
            _uiState.update {
                it.copy(
                    cardData = SDCATCardData(rawData, parsedData),
                )
            }
            checkIfCardIsSaved()
        } catch (exception: Exception) {
            Log.e(tag, "An exception occurred while reading card", exception)
            _uiState.update { it.copy(snackbar = SDCATCardReaderSnackbar.READING_ERROR) }
        }
    }

    private fun validateCard() {
        try {
            _uiState.update {
                it.copy(
                    cardValidationResult = null,
                    cardValidationDetailsDialogOpened = false,
                )
            }

            val data = _uiState.value.cardData ?: return
            val result = cardValidator.getValidationResult(data)
            _uiState.update { it.copy(cardValidationResult = result) }
        } catch (exception: Exception) {
            Log.e(tag, "An exception occurred while validating the card", exception)
            _uiState.update { it.copy(snackbar = SDCATCardReaderSnackbar.VALIDATION_ERROR) }
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
            _uiState.update { it.copy(selectedUsbDevice = null) }
            return
        }

        val reader = usbReader

        if (reader == null || !reader.isSupported(usbDevice)) {
            _uiState.update { it.copy(snackbar = SDCATCardReaderSnackbar.USB_UNSUPPORTED_DEVICE) }
            return
        }

        try {
            reader.open(usbDevice)
            _uiState.update { it.copy(selectedUsbDevice = usbDevice) }
        } catch (e: Exception) {
            Log.e(tag, "An exception occurred while trying to open USB device", e)
            _uiState.update { it.copy(snackbar = SDCATCardReaderSnackbar.USB_UNSUPPORTED_DEVICE) }
        }
    }

    fun onShownSnackbar() {
        _uiState.update { it.copy(snackbar = null) }
    }

    fun openCardValidationDetailsDialog() {
        _uiState.update { it.copy(cardValidationDetailsDialogOpened = true) }
    }

    fun closeCardValidationDetailsDialog() {
        _uiState.update { it.copy(cardValidationDetailsDialogOpened = false) }
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
