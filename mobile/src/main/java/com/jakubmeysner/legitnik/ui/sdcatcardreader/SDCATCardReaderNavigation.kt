package com.jakubmeysner.legitnik.ui.sdcatcardreader

import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object SDCATCardReaderRoute

fun NavGraphBuilder.sdcatCardReaderDestination(
    onShowSnackbar: suspend (visuals: SnackbarVisuals) -> SnackbarResult,
) {
    composable<SDCATCardReaderRoute> {
        SDCATCardReaderScreen(onShowSnackbar = onShowSnackbar)
    }
}
