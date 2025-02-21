package com.jakubmeysner.legitnik.ui.sdcatcardreader

import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.jakubmeysner.legitnik.data.sdcatcard.SDCATCardData
import com.jakubmeysner.legitnik.data.sdcatcard.SDCATCardParsedContent
import com.jakubmeysner.legitnik.data.sdcatcard.SDCATCardParsedData
import com.jakubmeysner.legitnik.data.sdcatcard.SDCATCardRawData
import com.jakubmeysner.legitnik.data.sdcatcard.SDCATCardType
import com.jakubmeysner.legitnik.data.sdcatcard.SDCATCardValidationResult
import com.jakubmeysner.legitnik.ui.ScreenPreviewScaffold
import io.mockk.mockk
import java.util.Date

@Composable
private fun PreviewSDCATCardReaderScreen(
    uiState: SDCATCardReaderUiState,
) {
    ScreenPreviewScaffold(
        selectedTopLevelRoute = SDCATCardReaderRoute,
    ) {
        SDCATCardReaderScreen(
            uiState = uiState,
            selectInterface = {},
            enableNfcAdapterReaderMode = {},
            disableNfcAdapterReaderMode = {},
            selectUsbDevice = {},
            saveCard = {},
            removeCard = {},
            openCardValidationDetailsDialog = {},
            closeCardValidationDetailsDialog = {},
            onShownSnackbar = {},
            onShowSnackbar = {
                SnackbarResult.Dismissed
            },
        )
    }
}

@Composable
@Preview(showBackground = true)
fun SDCATCardReaderScreenPromptPreview() {
    PreviewSDCATCardReaderScreen(uiState = PROMPT_UI_STATE)
}

private val PROMPT_UI_STATE = SDCATCardReaderUiState(
    hasNfcFeature = true,
)

@Composable
@Preview(showBackground = true)
fun SDCATCardReaderScannedPreview() {
    PreviewSDCATCardReaderScreen(uiState = SCANNED_UI_STATE)
}

private val SCANNED_UI_STATE = SDCATCardReaderUiState(
    cardData = SDCATCardData(
        rawData = SDCATCardRawData(
            type = SDCATCardType.STUDENT,
            rawMessage = emptyList(),
            rawCertificate = emptyList(),
        ),
        parsedData = SDCATCardParsedData(
            message = mockk(),
            content = SDCATCardParsedContent.StudentCardParsedBasicContent(
                version = 1,
                chipSerialNumber = "QWERTY123",
                universityOrIssuerName = "Politechnika Wrocławska",
                surname = listOf("Kowalski"),
                givenNames = listOf("Jan"),
                albumOrCardNumber = "123456",
                editionNumber = "A",
                peselNumber = "00000000000",
                expiryDate = Date(2025, 5, 5)
            ),
            certificate = mockk(),
        )
    ),
    cardValidationResult = SDCATCardValidationResult(
        signatureValidationReports = mockk(),
        signatureValid = true,
        issuerMatchesCertificateSubject = true,
        certificateSubjectAuthorized = true,
        notExpired = true,
    ),
)

@Composable
@Preview(showBackground = true)
fun SDCATCardReaderScreenScannedInvalidPreview() {
    PreviewSDCATCardReaderScreen(uiState = SCANNED_INVALID_UI_STATE)
}

private val SCANNED_INVALID_UI_STATE = SCANNED_UI_STATE.copy(
    cardValidationResult = SCANNED_UI_STATE.cardValidationResult?.copy(
        signatureValid = false,
    )
)

@Composable
@Preview(showBackground = true)
fun SDCATCardReaderScreenValidationDetailsPreview() {
    PreviewSDCATCardReaderScreen(uiState = VALIDATION_DETAILS_UI_STATE)
}

private val VALIDATION_DETAILS_UI_STATE = SCANNED_UI_STATE.copy(

)
