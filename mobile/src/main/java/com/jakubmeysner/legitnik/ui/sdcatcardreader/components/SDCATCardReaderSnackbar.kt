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
fun SDCATCardReaderSnackbar(
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

    val readingInterfaceErrorMessage = stringResource(
        R.string.sdcat_card_reader_snackbar_reading_interface_error
    )

    val readingErrorMessage = stringResource(
        R.string.sdcat_card_reader_snackbar_reading_error
    )

    val validationErrorMessage = stringResource(
        R.string.sdcat_card_reader_snackbar_validation_error
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

                        SDCATCardReaderSnackbar.READING_INTERFACE_ERROR -> SnackbarVisualsData(
                            message = readingInterfaceErrorMessage,
                        )

                        SDCATCardReaderSnackbar.READING_ERROR -> SnackbarVisualsData(
                            message = readingErrorMessage,
                        )

                        SDCATCardReaderSnackbar.VALIDATION_ERROR -> SnackbarVisualsData(
                            message = validationErrorMessage,
                        )
                    }
                )
            }

            onShownSnackbar()
        }
    }
}
