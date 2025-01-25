package com.jakubmeysner.legitnik.ui.sdcatcard

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.jakubmeysner.legitnik.data.sdcatcard.SDCATCardParsedContent
import com.jakubmeysner.legitnik.ui.theme.LegitnikTheme
import java.util.Date

class SDCATCardCardScreenshotComponentTest {
    @Composable
    fun Base(
        darkTheme: Boolean = false,
        valid: Boolean = true,
        default: Boolean? = null,
        defaultToggleable: Boolean = false,
    ) {
        LegitnikTheme(darkTheme = darkTheme) {
            Surface {
                SDCATCardCard(
                    content = SDCATCardParsedContent.StudentCardParsedBasicContent(
                        version = 1,
                        chipSerialNumber = "ABC123",
                        universityOrIssuerName = "University of Earth",
                        surname = listOf("Kowalski"),
                        givenNames = listOf("Jan"),
                        albumOrCardNumber = "123456",
                        editionNumber = "A",
                        peselNumber = "1234567890",
                        expiryDate = Date(2025, 1, 1),
                    ),
                    valid = valid,
                    isSaved = false,
                    default = default,
                    saveCard = {},
                    removeCard = {},
                    toggleDefault = if (defaultToggleable) ({}) else null,
                    onShowValidationDetails = {},
                )
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun Valid() {
        Base()
    }

    @Preview(showBackground = true)
    @Composable
    fun ValidDarkTheme() {
        Base(darkTheme = true)
    }

    @Preview(showBackground = true)
    @Composable
    fun Invalid() {
        Base(valid = false)
    }

    @Preview(showBackground = true)
    @Composable
    fun InvalidDarkTheme() {
        Base(darkTheme = true, valid = false)
    }

    @Preview(showBackground = true)
    @Composable
    fun Default() {
        Base(default = true)
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultDarkTheme() {
        Base(darkTheme = true, default = true)
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultToggleable() {
        Base(default = true, defaultToggleable = true)
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultToggleableDarkTheme() {
        Base(darkTheme = true, default = true, defaultToggleable = true)
    }
}
