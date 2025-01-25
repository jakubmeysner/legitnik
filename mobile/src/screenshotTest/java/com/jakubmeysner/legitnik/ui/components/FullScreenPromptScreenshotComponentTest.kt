package com.jakubmeysner.legitnik.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.jakubmeysner.legitnik.ui.theme.LegitnikTheme

class FullScreenPromptScreenshotComponentTest {
    @Preview(showBackground = true)
    @Composable
    fun WithoutSubtitle() {
        LegitnikTheme {
            Surface {
                FullScreenPrompt(
                    icon = Icons.Default.CheckCircle,
                    title = "Hello 123!",
                )
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun WithoutSubtitleDarkTheme() {
        LegitnikTheme(darkTheme = true) {
            Surface {
                FullScreenPrompt(
                    icon = Icons.Default.AddCircle,
                    title = "Hello 456!",
                )
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun WithSubtitle() {
        LegitnikTheme {
            Surface {
                FullScreenPrompt(
                    icon = Icons.Default.AccountCircle,
                    title = "Goodbye 123!",
                    subtitle = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec tristique dolor eu libero laoreet mattis."
                )
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun WithSubtitleDarkTheme() {
        LegitnikTheme(darkTheme = true) {
            Surface {
                FullScreenPrompt(
                    icon = Icons.Default.AccountBox,
                    title = "Goodbye 456!",
                    subtitle = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec tristique dolor eu libero laoreet mattis.",
                )
            }
        }
    }
}
