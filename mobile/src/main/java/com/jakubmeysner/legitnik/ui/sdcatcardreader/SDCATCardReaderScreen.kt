package com.jakubmeysner.legitnik.ui.sdcatcardreader

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
    val cardData = uiState.cardData
    val validationResult = uiState.cardValidationResult
    val showPrompt = !uiState.reading && cardData == null

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(space = 16.dp),
    ) {
        SDCATCardReaderInterfaceSelect(
            selectedInterface = uiState.selectedInterface,
            onSelectInterface = viewModel::selectInterface,
            enabled = !uiState.reading,
        )

        if (uiState.selectedInterface == SDCATCardReaderInterface.NFC) {
            SDCATCardReaderInterfaceNfc(
                showPrompt = showPrompt,
                onTagDiscovered = viewModel::onTagDiscovered,
            )
        }

        SDCATCardReaderInterfaceUsb(
            visible = uiState.selectedInterface == SDCATCardReaderInterface.USB,
            showPrompt = showPrompt,
            deviceSelectEnabled = !uiState.reading,
            createReader = viewModel::createUsbReader,
            selectedUsbDevice = uiState.selectedUsbDevice,
            selectUsbDevice = viewModel::selectUsbDevice,
        )

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
                    onShowValidationDetails = viewModel::openCardValidationDetailsDialog,
                )
            }

            if (uiState.cardValidationDetailsDialogOpened && validationResult != null) {
                SDCATCardValidationDetailsDialog(
                    certificate = cardData.parsedData.certificate,
                    validationResult = validationResult,
                    onClose = viewModel::closeCardValidationDetailsDialog,
                )
            }
        }
    }

    SDCATCardReaderSnackbar(
        onShowSnackbar = onShowSnackbar,
        snackbar = uiState.snackbar,
        onShownSnackbar = { viewModel.onShownSnackbar() },
    )
}
