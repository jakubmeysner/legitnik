package com.jakubmeysner.legitnik.ui.sdcatcardreader

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object SDCATCardReader

fun NavGraphBuilder.sdcatCardReaderDestination() {
    composable<SDCATCardReader> {
        SDCATCardReaderScreen()
    }
}
