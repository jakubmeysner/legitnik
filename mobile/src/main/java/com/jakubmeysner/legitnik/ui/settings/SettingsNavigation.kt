package com.jakubmeysner.legitnik.ui.settings

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object Settings

fun NavGraphBuilder.settingsDestination() {
    composable<Settings> {
        SettingsScreen()
    }
}
