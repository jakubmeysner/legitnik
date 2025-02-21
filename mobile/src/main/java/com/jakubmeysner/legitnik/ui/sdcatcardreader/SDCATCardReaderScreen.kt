package com.jakubmeysner.legitnik.ui.sdcatcardreader

import android.app.Activity
import android.hardware.usb.UsbDevice
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jakubmeysner.legitnik.ui.sdcatcard.SDCATCardCard
import com.jakubmeysner.legitnik.ui.sdcatcard.SDCATCardValidationDetailsDialog
import com.jakubmeysner.legitnik.ui.sdcatcardreader.components.SDCATCardReaderInterfaceNfc
import com.jakubmeysner.legitnik.ui.sdcatcardreader.components.SDCATCardReaderInterfaceSelect
import com.jakubmeysner.legitnik.ui.sdcatcardreader.components.SDCATCardReaderInterfaceUsb
import com.jakubmeysner.legitnik.ui.sdcatcardreader.components.SDCATCardReaderSnackbar

@Composable
fun SDCATCardReaderScreen(
    viewModel: SDCATCardReaderViewModel = hiltViewModel(),
    onShowSnackbar: suspend (visuals: SnackbarVisuals) -> SnackbarResult,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SDCATCardReaderScreen(
        uiState = uiState,
        selectInterface = viewModel::selectInterface,
        enableNfcAdapterReaderMode = viewModel::enableNfcAdapterReaderMode,
        disableNfcAdapterReaderMode = viewModel::disableNfcAdapterReaderMode,
        selectUsbDevice = viewModel::selectUsbDevice,
        saveCard = viewModel::saveCard,
        removeCard = viewModel::removeCard,
        openCardValidationDetailsDialog = viewModel::openCardValidationDetailsDialog,
        closeCardValidationDetailsDialog = viewModel::closeCardValidationDetailsDialog,
        onShownSnackbar = viewModel::onShownSnackbar,
        onShowSnackbar = onShowSnackbar,
    )
}

@Composable
fun SDCATCardReaderScreen(
    uiState: SDCATCardReaderUiState,
    selectInterface: (inter: SDCATCardReaderInterface) -> Unit,
    enableNfcAdapterReaderMode: (activity: Activity) -> Unit,
    disableNfcAdapterReaderMode: (activity: Activity) -> Unit,
    selectUsbDevice: (usbDevice: UsbDevice?) -> Unit,
    saveCard: () -> Unit,
    removeCard: () -> Unit,
    openCardValidationDetailsDialog: () -> Unit,
    closeCardValidationDetailsDialog: () -> Unit,
    onShownSnackbar: () -> Unit,
    onShowSnackbar: suspend (visuals: SnackbarVisuals) -> SnackbarResult,
) {
    val cardData = uiState.cardData
    val validationResult = uiState.cardValidationResult
    val showPrompt = !uiState.reading && cardData == null
    val isSaved = uiState.cardId != null

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(space = 16.dp),
    ) {
        SDCATCardReaderInterfaceSelect(
            selectedInterface = uiState.selectedInterface,
            onSelectInterface = selectInterface,
            enabled = !uiState.reading,
        )

        when (uiState.selectedInterface) {
            SDCATCardReaderInterface.NFC -> {
                SDCATCardReaderInterfaceNfc(
                    hasNfcFeature = uiState.hasNfcFeature,
                    showPrompt = showPrompt,
                    enableNfcAdapterReaderMode = enableNfcAdapterReaderMode,
                    disableNfcAdapterReaderMode = disableNfcAdapterReaderMode,
                )
            }

            SDCATCardReaderInterface.USB -> {
                SDCATCardReaderInterfaceUsb(
                    hasUsbHostFeature = uiState.hasUsbHostFeature,
                    showPrompt = showPrompt,
                    deviceSelectEnabled = !uiState.reading,
                    selectedUsbDeviceName = uiState.selectedUsbDeviceName,
                    selectUsbDevice = selectUsbDevice,
                )
            }
        }



        if (uiState.reading) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(size = 64.dp),
                )
            }
        } else if (cardData != null) {
            Box(modifier = Modifier.padding(top = 16.dp)) {
                SDCATCardCard(
                    content = cardData.parsedData.content,
                    valid = validationResult?.valid,
                    isSaved = isSaved,
                    saveCard = saveCard,
                    removeCard = removeCard,
                    onShowValidationDetails = openCardValidationDetailsDialog,
                )
            }

            if (uiState.cardValidationDetailsDialogOpened && validationResult != null) {
                SDCATCardValidationDetailsDialog(
                    certificate = cardData.parsedData.certificate,
                    validationResult = validationResult,
                    onClose = closeCardValidationDetailsDialog,
                )
            }
        }
    }

    SDCATCardReaderSnackbar(
        onShowSnackbar = onShowSnackbar,
        snackbar = uiState.snackbar,
        onShownSnackbar = { onShownSnackbar() },
    )
}
