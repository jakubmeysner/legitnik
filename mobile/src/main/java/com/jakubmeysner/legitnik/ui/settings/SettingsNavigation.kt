package com.jakubmeysner.legitnik.ui.settings

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object SettingsRoute

fun NavGraphBuilder.settingsDestination() {
    composable<SettingsRoute> {
        SettingsScreen()
    }
}
