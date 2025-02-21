package com.jakubmeysner.legitnik.ui.sdcatcardsaved.list

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.jakubmeysner.legitnik.data.sdcatcard.SDCATCardDataEntity
import com.jakubmeysner.legitnik.data.sdcatcard.SDCATCardParsedContent
import com.jakubmeysner.legitnik.data.sdcatcard.SDCATCardParsedData
import com.jakubmeysner.legitnik.data.sdcatcard.SDCATCardType
import com.jakubmeysner.legitnik.data.sdcatcard.database.SDCATCardRawDataEntity
import com.jakubmeysner.legitnik.ui.ScreenPreviewScaffold
import com.jakubmeysner.legitnik.ui.sdcatcardsaved.SDCATCardSavedRoute
import io.mockk.mockk
import java.util.Date
import java.util.UUID

@Composable
@Preview(showBackground = true)
fun SDCATCardSavedListScreenPreview() {
    ScreenPreviewScaffold(
        selectedTopLevelRoute = SDCATCardSavedRoute,
    ) {
        SDCATCardSavedListScreen(
            uiState = UI_STATE,
            navigateToSDCATCardSavedDetails = {},
        )
    }
}

private val UI_STATE = SDCATCardSavedListUiState(
    cards = listOf(
        SDCATCardDataEntity(
            rawData = SDCATCardRawDataEntity(
                id = UUID.randomUUID(),
                hash = emptyList(),
                type = SDCATCardType.STUDENT,
                rawMessage = emptyList(),
                rawCertificate = emptyList(),
                default = true,
                active = false,
            ),
            parsedData = SDCATCardParsedData(
                message = mockk(),
                content = SDCATCardParsedContent.StudentCardParsedBasicContent(
                    version = 1,
                    chipSerialNumber = "QWERTY123",
                    universityOrIssuerName = "Politechnika Wrocławska",
                    surname = listOf("Kowalski"),
                    givenNames = listOf("Jan"),
                    albumOrCardNumber = "456789",
                    editionNumber = "A",
                    peselNumber = "01234567890",
                    expiryDate = Date(2025, 5, 5),
                ),
                certificate = mockk(),
            )
        ),
        SDCATCardDataEntity(
            rawData = SDCATCardRawDataEntity(
                id = UUID.randomUUID(),
                hash = emptyList(),
                type = SDCATCardType.DOCTORAL_CANDIDATE,
                rawMessage = emptyList(),
                rawCertificate = emptyList(),
                default = false,
                active = false,
            ),
            parsedData = SDCATCardParsedData(
                message = mockk(),
                content = SDCATCardParsedContent.DoctoralCandidateCardParsedContent(
                    version = 1,
                    chipSerialNumber = "QWERTY123",
                    universityOrIssuerName = "Uniwersytet Wrocławski",
                    surname = listOf("Kowalski"),
                    givenNames = listOf("Jan"),
                    albumOrCardNumber = "123456",
                    editionNumber = "A",
                    peselNumber = "01234567890",
                    expiryDate = Date(2025, 5, 5),
                ),
                certificate = mockk(),
            )
        ),
    ),
)
