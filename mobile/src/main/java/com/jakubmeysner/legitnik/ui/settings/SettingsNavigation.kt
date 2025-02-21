package com.jakubmeysner.legitnik.ui.settings

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.jakubmeysner.legitnik.ui.Route
import kotlinx.serialization.Serializable

@Serializable
object SettingsRoute : Route

fun NavGraphBuilder.settingsDestination() {
    composable<SettingsRoute> {
        SettingsScreen()
    }
}
