package com.jakubmeysner.legitnik.ui.sdcatcardreader

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteConstraintException
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.nfc.NfcAdapter
import android.nfc.NfcManager
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
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.util.UUID
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
    SAVING_SUCCESS,
    REMOVING_SUCCESS
}

data class SDCATCardReaderUiState(
    val selectedInterface: SDCATCardReaderInterface = SDCATCardReaderInterface.NFC,
    val snackbar: SDCATCardReaderSnackbar? = null,
    val selectedUsbDeviceName: String? = null,
    val reading: Boolean = false,
    val cardId: UUID? = null,
    val cardData: SDCATCardData? = null,
    val cardValidationResult: SDCATCardValidationResult? = null,
    val cardValidationDetailsDialogOpened: Boolean = false,
    val hasNfcFeature: Boolean = false,
    val hasUsbHostFeature: Boolean = false,
)

@HiltViewModel
class SDCATCardReaderViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context,
    private val cardValidator: SDCATCardValidator,
    private val cardRepository: SDCATCardRepository,
) : ViewModel(), ClassSimpleNameLoggingTag {
    private val nfcManager = context.getSystemService(NfcManager::class.java)
    private val usbManager = context.getSystemService(UsbManager::class.java)

    private val nfcAdapter: NfcAdapter? = nfcManager.defaultAdapter

    private val hasUsbHostFeature = context.packageManager.hasSystemFeature(
        PackageManager.FEATURE_USB_HOST
    )

    private var usbReader = UsbReader(usbManager).apply {
        setOnStateChangeListener(::onUsbReaderStateChange)
    }

    private val usbBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            onUsbBroadcastReceive(intent)
        }
    }.apply {
        context.registerReceiver(
            this,
            IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED),
            Context.RECEIVER_EXPORTED,
        )
    }

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
            hasNfcFeature = nfcAdapter != null,
            hasUsbHostFeature = hasUsbHostFeature,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SDCATCardReaderUiState(),
    )

    fun selectInterface(inter: SDCATCardReaderInterface) {
        savedStateHandle[SELECTED_INTERFACE_KEY] = inter
    }

    fun enableNfcAdapterReaderMode(activity: Activity) {
        nfcAdapter?.enableReaderMode(
            activity,
            ::onTagDiscovered,
            NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_NFC_B,
            null
        )
    }

    fun disableNfcAdapterReaderMode(activity: Activity) {
        nfcAdapter?.disableReaderMode(activity)
    }

    fun onTagDiscovered(nfcTag: Tag) {
        try {
            _uiState.update { it.copy(reading = true) }
            val isoDepAvailable = nfcTag.techList.contains(IsoDep::class.qualifiedName)

            if (!isoDepAvailable) {
                _uiState.update {
                    it.copy(
                        reading = false,
                        snackbar = SDCATCardReaderSnackbar.NFC_UNSUPPORTED_CARD,
                    )
                }
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

    private fun onUsbBroadcastReceive(intent: Intent) {
        val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)

        when (intent.action) {
            UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                if (device?.deviceName == _uiState.value.selectedUsbDeviceName) {
                    selectUsbDevice(null)
                }
            }
        }
    }

    fun saveCard() {
        viewModelScope.launch {
            try {
                val rawData = _uiState.value.cardData?.rawData
                if (rawData != null) {
                    try {
                        cardRepository.addCard(rawData, default = true)
                    } catch (e: SQLiteConstraintException) {
                        cardRepository.addCard(rawData)
                    }

                    _uiState.update {
                        it.copy(
                            snackbar = SDCATCardReaderSnackbar.SAVING_SUCCESS,
                            cardId = cardRepository.getCardByHash(rawData.getHash())?.id
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

    fun removeCard() {
        viewModelScope.launch {
            val rawData = _uiState.value.cardData?.rawData
            val cardUUID = _uiState.value.cardId
            if (rawData != null && cardUUID != null) {
                cardRepository.removeCard(cardUUID)
                _uiState.update {
                    it.copy(
                        cardId = null,
                        snackbar = SDCATCardReaderSnackbar.REMOVING_SUCCESS
                    )
                }
            }
        }
    }

    private fun checkIfCardIsSaved() {
        viewModelScope.launch {
            val rawData = _uiState.value.cardData?.rawData
            if (rawData != null) {
                val card = cardRepository.getCardByHash(rawData.getHash())
                if (card != null) {
                    _uiState.update {
                        it.copy(
                            cardId = card.id,
                        )
                    }
                }
            }
        }
    }


    private fun onCardInserted(slotNum: Int) {
        try {
            Log.d(tag, "Card inserted into slot $slotNum")

            if (_selectedInterface.value != SDCATCardReaderInterface.USB) {
                return
            }

            _uiState.update { it.copy(reading = true) }

            usbReader.power(slotNum, Reader.CARD_COLD_RESET)
            usbReader.setProtocol(slotNum, Reader.PROTOCOL_T0 or Reader.PROTOCOL_T1)

            val apduTransceiver = UsbReaderTransceiver(usbReader, slotNum)
            scanCard(apduTransceiver)

            usbReader.power(slotNum, Reader.CARD_POWER_DOWN)
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

    private fun onUsbReaderStateChange(slotNum: Int, previousState: Int, currentState: Int) {
        if (previousState == Reader.CARD_ABSENT && currentState == Reader.CARD_PRESENT) {
            onCardInserted(slotNum)
        }
    }

    fun selectUsbDevice(usbDevice: UsbDevice?) {
        if (usbDevice == null) {
            usbReader.close()
            _uiState.update { it.copy(selectedUsbDeviceName = null) }
            return
        }

        val reader = usbReader

        if (!reader.isSupported(usbDevice)) {
            _uiState.update { it.copy(snackbar = SDCATCardReaderSnackbar.USB_UNSUPPORTED_DEVICE) }
            return
        }

        try {
            reader.open(usbDevice)
            _uiState.update { it.copy(selectedUsbDeviceName = usbDevice.deviceName) }
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
        if (usbReader.isOpened) {
            usbReader.close()
        }

        context.unregisterReceiver(usbBroadcastReceiver)
    }

    companion object {
        private const val SELECTED_INTERFACE_KEY = "selectedInterface"
    }
}
