package com.jakubmeysner.legitnik.ui.parking.details.components

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.jakubmeysner.legitnik.ui.theme.LegitnikTheme

class ParkingLotDetailsDataUnavailableScreenshotComponentTest {
    @Preview(showBackground = true)
    @Composable
    fun Default() {
        LegitnikTheme {
            Surface {
                ParkingLotDetailsDataUnavailable(
                    onReload = {},
                )
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultDarkTheme() {
        LegitnikTheme(darkTheme = true) {
            Surface {
                ParkingLotDetailsDataUnavailable(
                    onReload = {},
                )
            }
        }
    }
}
