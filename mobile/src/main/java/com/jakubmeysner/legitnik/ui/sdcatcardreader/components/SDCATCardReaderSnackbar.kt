package com.jakubmeysner.legitnik.ui.sdcatcardreader.components

import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.jakubmeysner.legitnik.R
import com.jakubmeysner.legitnik.ui.sdcatcardreader.SDCATCardReaderSnackbar
import com.jakubmeysner.legitnik.util.SnackbarVisualsData
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun SDCATCardReaderNfcUnsupportedCardSnackbar(
    onShowSnackbar: suspend (visuals: SnackbarVisuals) -> SnackbarResult,
    snackbar: SDCATCardReaderSnackbar?,
    onShownSnackbar: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    var job by remember { mutableStateOf<Job?>(null) }

    val nfcUnsupportedCardSnackbarMessage = stringResource(
        R.string.sdcat_card_reader_snackbar_nfc_unsupported_card
    )

    val usbUnsupportedDeviceMessage = stringResource(
        R.string.sdcat_card_reader_snackbar_usb_unsupported_device
    )

    LaunchedEffect(snackbar) {
        if (snackbar != null) {
            job?.cancel()

            job = scope.launch {
                onShowSnackbar(
                    when (snackbar) {
                        SDCATCardReaderSnackbar.NFC_UNSUPPORTED_CARD -> SnackbarVisualsData(
                            message = nfcUnsupportedCardSnackbarMessage,
                        )

                        SDCATCardReaderSnackbar.USB_UNSUPPORTED_DEVICE -> SnackbarVisualsData(
                            message = usbUnsupportedDeviceMessage,
                        )
                    }
                )
            }

            onShownSnackbar()
        }
    }
}
